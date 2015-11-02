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
				// json parsing begins
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
			System.out.println("Unable to connect to Canvas homepage at: " + homepage);
			System.out.println("Exception is: " + e.toString());
			e.printStackTrace();
			return res;
		}

	}

	/**
	 * With the json object otained from Canvas web service
	 * All info about each course will be parsed and passed to the our <Course> Object
	 * @param json a `product` json object with title,teaser,image,url,etc
	 */
	private void parseCourseStub(JsonElement json) {
		JsonObject obj = json.getAsJsonObject();

		// Extract all possible info from the stub
		String title = obj.get("title").getAsString();
		String shortDescription = obj.get("teaser").getAsString();
		String courseLink = obj.get("url").getAsString();
		String language = null; // Canvas has English only
		String courseImage = obj.get("image").getAsString();
		String categorey = obj.get("type").getAsString();
		String lang = "English"; 
		String university = obj.get("logo").getAsJsonObject().get("label").getAsString();
		String courseLength = 0 ; // don't see any course length info so far. Need to check 
		String videoLink = null;  // a few page might have a youtube player. Didn't do implementation regards those. Need to check
		String longDescription = null;
		LocalDate startDate = null;
		Certificate certficate = Certificate.NO;// canvas doesn't have
		CourseDetails details = null;
		int courseFee = 0;
		if (!obj.get("free").getAsBoolean()) {
			// free -> true , else false
			String priceWithPriceTag = obj.get("priceWithCurrency").getAsString();
			// format "$[0-9]*"
			courseFee = Integer.valueOf(priceWithPriceTag.substring(1));
		}
		Document fullCourseDoc = null;
		try {
			// Attemp to get to the course page 
			fullCourseDoc = Jsoup.connect(courseLink).get();
		} catch (IOException e) {
			System.out.println("Unable to connect to full course at: " + courseLink);
			System.out.println("Exception is: " + e.toString());
			e.printStackTrace();
			// Failed. Return what course info we have so far.
			res.add(new Course(title, shortDescription, longDescription, courseLink, videoLink, startDate, courseLength, courseImage, categorey, site,
					courseFee, lang, university, certficate, timeScraped, details));
			return;
		}
		// Now that we have the full course page, parse the rest of the information.
		longDescription = getLongDescriptionFromCourse(fullCourseDoc);
		startDate = getStartDateFromCourse(fullCourseDoc);
		details = getCourseDetailsFromCourse(fullCourseDoc);
		res.add(new Course(title, shortDescription, longDescription, courseLink, videoLink, startDate, courseLength,
				courseImage, categorey, site, courseFee, lang, university, certficate, timeScraped, details));
	}
	/**
	 * Some .course-detail p has a youtube player so it might have unexpected parse result
	 * We'll look at the data  later
	 * @param  fullCourseDoc dom element
	 * @return  full description for the course
	 */
	private String getLongDescriptionFromCourse(Document fullCourseDoc) {
		StringBuilder longDesc = new StringBuilder();
		Elements paragraphs = fullCourseDoc.select("div[class=course-details] p");
		for (Element p : paragraphs) {
			longDesc.append(p.text());
			longDesc.append("\n");
		}
		return longDesc.toString();
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
	 * Some courses have more than one Professor. I'm only getting the first as well.
	 */
	private CourseDetails getCourseDetailsFromCourse(Document fullCourseDoc) {
		Element instructor = fullCourseDoc.select(".instructors").first();
		String profName = instructor.select("h3").text();
		String profImgUrl = instructor.select("img").attr("src");
		return new CourseDetails(profName, profImgUrl);
	}
}
