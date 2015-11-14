package scrapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

public class IversityScrapper implements CourseScrapper {

	String homepage = "https://iversity.org/en/courses";
	String site = "Iversity";
	LocalDate timeScraped;
	DateTimeFormatter iversityDateFormatter = DateTimeFormatter.ofPattern("d MMM. yyyy");
	List<Course> res;

	public IversityScrapper() {
		timeScraped = LocalDate.now();
		res = new ArrayList<>();
	}

	@Override
	public List<Course> scrapeCourses() {
		try {
			Document catalogDoc = Jsoup.connect(homepage).get();
			Elements courseStubElements = catalogDoc.select("article[class=courses-list-item]");
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
		String language = getLanguageFromStub(courseStub);
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
					0, language, null, null, timeScraped, null);
		}

		// Now that we have the full course page, parse the rest of the
		// information.
		String longDescription = getLongDescriptionFromCourse(fullCourseDoc);
		String videoLink = getVideoLinkFromCourse(fullCourseDoc);
		LocalDate startDate = getStartDateFromCourse(fullCourseDoc);
		int courseLength = getCourseLengthFromCourse(fullCourseDoc);
		String categorey = getCategoryFromCourse(fullCourseDoc);
		int courseFee = getCourseFeeFromCourse(fullCourseDoc);
		String university = getUniversityFromCourse(fullCourseDoc);
		Certificate certficate = getCertificateFromCourse(fullCourseDoc);
		CourseDetails details = getCourseDetailsFromCourse(fullCourseDoc);

		// Update values if "Pro" course
		if (isProCourse(courseStub)) {
			// pro courses are all "business" category
			categorey = "Business";
			// pro courses cost 399 Euro
			courseFee = 399;
			// pro courses offer certficates
			certficate = Certificate.YES;
			// pro courses take 5 weeks
			courseLength = 5;
		}

		System.out.println("Parsed Iversity course: " + title);

		return new Course(title, shortDescription, longDescription, courseLink, videoLink, startDate, courseLength,
				courseImage, categorey, university, courseFee, language, university, certficate, timeScraped, details);
	}

	private boolean isProCourse(Element courseStub) {
		return courseStub.select("div[class=ribbon-content]").text().trim().equalsIgnoreCase("PRO");
	}

	private String getTitleFromStub(Element courseStub) {
		return courseStub.select("h2[class=truncate]").text();
	}

	private String getShortDescFromStub(Element courseStub) {
		return courseStub.select("p[class=description]").text();
	}

	private String getCourseLinkFromStub(Element courseStub) {
		return courseStub.select("a[class=hover-link]").get(0).attr("abs:href");
	}

	private String getLanguageFromStub(Element courseStub) {
		return courseStub.select("li[title=Language]").select("span").text();
	}

	private String getCourseImageFromStub(Element courseStub) {
		String style = courseStub.select("div[class=course-cover]").get(0).attr("style");
		// Format: "background-image: url(<SOME IMAGE URL>)"
		// Only want the URL value between the parentheses
		return style.substring(style.indexOf("(") + 1, style.indexOf(")"));
	}

	private String getLongDescriptionFromCourse(Document fullCourseDoc) {
		return fullCourseDoc.select("div[class=col-sm-9]").text();
	}

	private String getVideoLinkFromCourse(Document fullCourseDoc) {
		Element videoElement = fullCourseDoc.select("#course-video-iframe").first();
		if (videoElement == null) {
			// System.out.println("No video found for course.");
			return "";
		} else {
			return videoElement.attr("src");
		}

	}

	/*
	 * Iversity lists both present and past courses. This will be either the
	 * start or possibly end date.
	 */
	private LocalDate getStartDateFromCourse(Document fullCourseDoc) {
		String date = fullCourseDoc.select("li[title=Start date]").text();
		if (date == null || date.isEmpty()) {
			// No start date found. Look for End Date
			date = fullCourseDoc.select("li[title=End date]").text();
		}
		// Parse the date from iveristy's format.
		try {
			// this might have invalid date format. need investigate.
			LocalDate res = LocalDate.parse(date, iversityDateFormatter);
			return res;
		} catch (Exception e) {

			return LocalDate.now(); // return now for startdate
		}

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

	private String getCategoryFromCourse(Document fullCourseDoc) {
		Elements ele = fullCourseDoc.select("i[class=fa fa-bookmark fa-fw]");
		if (!ele.isEmpty()) {
			return ele.get(0).parent().text();
		} else {
			return "";
		}
	}

	/*
	 * This is tricky because iversity offers different kinds of courses. They
	 * all have a free audit option, but some also have certifications that cost
	 * more.
	 *
	 * I'm being lazy and calling them all free.
	 *
	 */
	private int getCourseFeeFromCourse(Document fullCourseDoc) {
		return 0;
	}

	/*
	 * This information is only contained in an image, and I'm not going to
	 * bother trying to read text from an image. Maybe there is a better way.
	 */
	private String getUniversityFromCourse(Document fullCourseDoc) {
		return "";
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
		Elements profElements = fullCourseDoc.select("img[class=avatar]");
		if (profElements.isEmpty()) {
			// System.out.println("Warning: No professor found for course.");
			return new CourseDetails();
		} else {
			Element profElement = profElements.first();
			String profName = profElement.attr("alt");
			String profImage = profElement.attr("src");
			return new CourseDetails(profName, profImage);
		}
	}

}
