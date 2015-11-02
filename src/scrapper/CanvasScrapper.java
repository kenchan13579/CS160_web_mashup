package scrapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.Certificate;
import model.Course;
import model.CourseDetails;

public class CanvasScrapper implements CourseScrapper {
	
	String homepage = "https://www.canvas.net";
	String site = "Canvas";
	LocalDate timeScraped;
	DateTimeFormatter canvasDateFormatter = DateTimeFormatter.ofPattern("MMM. d, yyyy");
	public CanvasScrapper() {
		timeScraped = LocalDate.now();
	}
	@Override
	public List<Course> scrapeCourses() {
		try {
			Document catalogDoc = Jsoup.connect(homepage).get();
			System.out.println(catalogDoc.title());
			Elements courseStubElements = catalogDoc.select(".course-title.product-tile");
			System.out.println(courseStubElements.toString());
			return courseStubElements.parallelStream() // parallel for efficency
					.map(courseStub -> parseCourseStub(courseStub)) // parse
					.filter(out -> out != null) // remove all null results.
					.collect(Collectors.toList());

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
	private Course parseCourseStub(Element courseStub) {

		

		// Extract all possible info from the stub
		String title = getTitleFromStub(courseStub);
		String shortDescription = getShortDescFromStub(courseStub);
		String courseLink = getCourseLinkFromStub(courseStub);
		String language = null; // Canvas has English only
		String courseImage = getCourseImageFromStub(courseStub);

		// //Follow to the full course description
		Document fullCourseDoc = null;
		try {
			fullCourseDoc = Jsoup.connect(courseLink).get();
		} catch (IOException e) {
			System.out.println("Unable to connect to full course at: " + courseLink);
			System.out.println("Exception is: " + e.toString());
			e.printStackTrace();
			// Return what course info we have so far.
			return new Course(title, shortDescription, null, courseLink, null, null, 0, courseImage, null, courseLink,
					0, null, null, null, timeScraped, null);
		}

		// Now that we have the full course page, parse the rest of the
		// information.
		String longDescription = getLongDescriptionFromCourse(fullCourseDoc);
		String videoLink = getVideoLinkFromCourse(fullCourseDoc); // null
		LocalDate startDate = getStartDateFromCourse(fullCourseDoc);
		int courseLength = getCourseLengthFromCourse(fullCourseDoc);
		String categorey = getCategoryFromCourse(fullCourseDoc);
		int courseFee = getCourseFeeFromCourse(fullCourseDoc);
		String university = getUniversityFromCourse(fullCourseDoc);
		Certificate certficate = getCertificateFromCourse(fullCourseDoc);
		CourseDetails details = getCourseDetailsFromCourse(fullCourseDoc);

		return new Course(title, shortDescription, longDescription, courseLink, videoLink, startDate, courseLength,
				courseImage, categorey, university, courseFee, language, university, certficate, startDate, details);
	}

	

	private String getTitleFromStub(Element courseStub) {
		return courseStub.select("h3[class=product-title]").text();
	}

	private String getShortDescFromStub(Element courseStub) {
		return courseStub.select("a[class=sr-only]").text();
	}

	private String getCourseLinkFromStub(Element courseStub) {
		return courseStub.select("a[class=hover-link]").get(0).attr("abs:href");
	}

	/*private String getLanguageFromStub(Element courseStub) {
		return courseStub.select("li[title=Language]").select("span").text();
	}*/

	private String getCourseImageFromStub(Element courseStub) {
		String style = courseStub.select("span[class=image-wrapper]").get(0).attr("style");
		// Format: "background-image: url(<SOME IMAGE URL>)"
		// Only want the URL value between the parentheses
		return style.substring(style.indexOf("(") + 1, style.indexOf(")"));
	}

	private String getLongDescriptionFromCourse(Document fullCourseDoc) {
		StringBuilder longDesc = new StringBuilder();
		Elements paragraphs =  fullCourseDoc.select("div[class=course-details] p");
		for ( Element p : paragraphs) {
			longDesc.append(p.text());
		}
		return longDesc.toString();
	}

	private String getVideoLinkFromCourse(Document fullCourseDoc) {
		Element videoElement = fullCourseDoc.select("#course-video-iframe").first();
		if (videoElement == null) {
			System.out.println("No video found for course.");
			return null;
		} else {
			return videoElement.attr("src");
		}

	}

	/*
	 * Iversity lists both present and past courses. This will be either the
	 * start or possibly end date.
	 */
	private LocalDate getStartDateFromCourse(Document fullCourseDoc) {
		String date =  fullCourseDoc.select("div[class=course-details] h5").text();
		//  date string format -> "Starts mm dd,yyyy"
		int from = "Starts ".length();
		date = date.substring(from);
		// Parse the date from iveristy's format.
		return LocalDate.parse(date, canvasDateFormatter);
	}

	/*
	 * Returning the number of weeks for the course length, since it has to be
	 * an int for some reason.
	 */
	private int getCourseLengthFromCourse(Document fullCourseDoc) {
		Element courseLengthIcon = fullCourseDoc.select("i[class=fa fa-bell fa-fw]").first();
		if (courseLengthIcon == null) {
			System.out.println("Warning: No course length found for course.");
			return 0;
		}

		String courseLength = courseLengthIcon.parent().text();
		// courseLength includes weeks. Ex: 10 weeks.
		// Only want the number of weeks.
		return Integer.parseInt(courseLength.split("\\s")[0]);
	}

	private String getCategoryFromCourse(Document fullCourseDoc) {
		//return fullCourseDoc.select("i[class=fa fa-bookmark fa-fw]").get(0).parent().text();
		return null;
	}

	/*
	 Canvas courses all seem to be free
	 */
	private int getCourseFeeFromCourse(Document fullCourseDoc) {
		return 0;
	}

	/*
	 * Fortunately Canvas has alt & title tags :D
	 */
	private String getUniversityFromCourse(Document fullCourseDoc) {
		return fullCourseDoc.select("img.product-account-logo").attr("alt");
	}

	/*
	 * Some courses offer certificates, others don't. I'm doing a pretty basic
	 * search to establish if the certificate option is there or not.
	 */
	private Certificate getCertificateFromCourse(Document fullCourseDoc) {
		boolean hasCertOption = fullCourseDoc.select("h2[class=truncate]").text().contains("Certificate Track");
		if (hasCertOption) {
			return Certificate.YES;
		} else {
			return Certificate.NO;
		}
	}

	/*
	 * Many courses have more than one Professor. I'm only getting the first.
	 */
	private CourseDetails getCourseDetailsFromCourse(Document fullCourseDoc) {
		Element instructor = fullCourseDoc.select(".instructors").first();
		String profName = instructor.select("h3").text();
		String profImgUrl = instructor.select("img").attr("src");
		return new CourseDetails(profName,profImgUrl);
	}
}
