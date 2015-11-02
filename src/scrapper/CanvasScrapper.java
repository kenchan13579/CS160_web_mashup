package scrapper;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import com.google.gson.*;
import jdk.nashorn.internal.parser.JSONParser;
import model.*;

public class CanvasScrapper implements CourseScrapper {

	String homepage = "https://www.canvas.net";
	String site = "Canvas";
	LocalDate timeScraped;
	DateTimeFormatter canvasDateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy").withLocale(Locale.US);
	List<Course> res;

	public CanvasScrapper() {
		timeScraped = LocalDate.now();
		res = new ArrayList<>();
	}

	@Override
	public List<Course> scrapeCourses() {
		try {
			int remaining = Integer.MAX_VALUE;
			int pageNum = 1;
			
			while (remaining > 0) {
				// parsing Canvas' json data
				URL url = new URL(homepage + "/products.json?page=" + pageNum + "&query=");
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.setRequestMethod("GET");
				request.connect();
				// read response data from stream into string			
				BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
				StringBuilder jsonstr = new StringBuilder();
				String temp;
				while ((temp = in.readLine()) != null) {
					jsonstr.append(temp);
				}
				// jsonify begins
				JsonParser jp = new JsonParser();
				JsonElement je = jp.parse(jsonstr.toString());
				JsonObject jsonObj = je.getAsJsonObject();
				JsonArray products = jsonObj.getAsJsonArray("products");
				for (JsonElement j : products) {
					parseCourseStub(j);
				}
				remaining = jsonObj.get("remaining").getAsInt();
				pageNum++;
				request.disconnect();
			}
			return res;
		} catch (IOException e) {
			System.out.println("Unable to connect to Iversity homepage at: " + homepage);
			System.out.println("Exception is: " + e.toString());
			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	/**
	 * Using the coursestub from the homepage, parse some info, then follow the
	 * link to the full course description, and parse the rest of the available
	 * information.
	 * 
	 * Note: This currently ignores the "pro" courses since they are in a
	 * different format.
	 * 
	 * TODO: include those Pro courses.
	 * 
	 * @param courseStub
	 *            This is the DOM element of the course listed on the homepage.
	 *            This contains some information, but not all of the information
	 *            we need.
	 * @return the parsed Course
	 */
	private void parseCourseStub(JsonElement json) {
		JsonObject obj = json.getAsJsonObject();

		// Extract all possible info from the stub
		String title = obj.get("title").getAsString();
		String shortDescription = obj.get("teaser").getAsString();
		String courseLink = obj.get("url").getAsString();
		String language = null; // Canvas has English only
		String courseImage = obj.get("image").getAsString();

		// //Follow to the full course description
		Document fullCourseDoc = null;
		try {

			fullCourseDoc = Jsoup.connect(courseLink).get();
		} catch (IOException e) {
			System.out.println("Unable to connect to full course at: " + courseLink);
			System.out.println("Exception is: " + e.toString());
			e.printStackTrace();
			// Return what course info we have so far.
			res.add(new Course(title, shortDescription, null, courseLink, null, null, 0, courseImage, null, courseLink,
					0, null, null, null, timeScraped, null));
			return;
		}

		// Now that we have the full course page, parse the rest of the
		// information.
		String longDescription = getLongDescriptionFromCourse(fullCourseDoc);
		String videoLink = getVideoLinkFromCourse(fullCourseDoc); // null
		LocalDate startDate = getStartDateFromCourse(fullCourseDoc);
		int courseLength = getCourseLengthFromCourse(fullCourseDoc);
		String categorey = obj.get("type").getAsString();
		int courseFee = 0;
		if (!obj.get("free").getAsBoolean()) {
			// free -> true , else false
			String priceWithPriceTag = obj.get("priceWithCurrency").getAsString();
			// format "$[0-9]*"
			courseFee = Integer.valueOf(priceWithPriceTag.substring(1));
		}
		String university = obj.get("logo").getAsJsonObject().get("label").getAsString();
		Certificate certficate = null;// canvas doesn't have
		CourseDetails details = getCourseDetailsFromCourse(fullCourseDoc);
		res.add(new Course(title, shortDescription, longDescription, courseLink, videoLink, startDate, courseLength,
				courseImage, categorey, university, courseFee, language, university, certficate, startDate, details));
	}

	private String getLongDescriptionFromCourse(Document fullCourseDoc) {
		StringBuilder longDesc = new StringBuilder();
		Elements paragraphs = fullCourseDoc.select("div[class=course-details] p");
		for (Element p : paragraphs) {
			longDesc.append(p.text());
		}
		return longDesc.toString();
	}

	private String getVideoLinkFromCourse(Document fullCourseDoc) {
		Element videoElement = fullCourseDoc.select("#course-video-iframe").first();
		if (videoElement == null) {
			// System.out.println("No video found for course.");
			return null;
		} else {
			return videoElement.attr("src");
		}

	}
	// regex pattern to match the date format
	private final Pattern r = Pattern.compile("(\\w*\\s)?([a-zA-z]*?\\s[0-9]{1,2},\\s[0-9]{4})(.*)?");
	private LocalDate getStartDateFromCourse(Document fullCourseDoc) {
		String date = fullCourseDoc.select("div[class=course-details] h5").text();
		Matcher m = r.matcher(date);
		if (m.find()) {
			date = m.group(2);
		} else {
			// either is empty or is "Self-paced"
			return null;
		}
		return LocalDate.parse(date, canvasDateFormatter);
	}

	/*
	 * Returning the number of weeks for the course length, since it has to be
	 * an int for some reason.
	 */
	private int getCourseLengthFromCourse(Document fullCourseDoc) {
		Element courseLengthIcon = fullCourseDoc.select("i[class=fa fa-bell fa-fw]").first();
		if (courseLengthIcon == null) {
			// System.out.println("Warning: No course length found for
			// course.");
			return 0;
		}

		String courseLength = courseLengthIcon.parent().text();
		// courseLength includes weeks. Ex: 10 weeks.
		// Only want the number of weeks.
		return Integer.parseInt(courseLength.split("\\s")[0]);
	}

	/*
	 * Many courses have more than one Professor. I'm only getting the first.
	 */
	private CourseDetails getCourseDetailsFromCourse(Document fullCourseDoc) {
		Element instructor = fullCourseDoc.select(".instructors").first();
		String profName = instructor.select("h3").text();
		String profImgUrl = instructor.select("img").attr("src");
		return new CourseDetails(profName, profImgUrl);
	}
}
