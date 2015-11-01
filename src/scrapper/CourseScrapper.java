package scrapper;

import java.util.List;

import model.Course;

public interface CourseScrapper {
	/**
	 * Pull the HTML from the website, scrape the HTML, and find the courses
	 * available.
	 * 
	 * @return A list of courses that have been gathered from the website.
	 */
	public List<Course> scrapeCourses();
}
