package scrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.Course;

public class ScrapperRunner {

	public static void main(String[] args) {
		ScrapperRunner runner = new ScrapperRunner();
		List<Course> courses = runner.scrape();

		// courses should be dumped in db here.

		System.out.println(courses);

	}

	private List<Course> scrape() {
		List<CourseScrapper> scrapers = new ArrayList<>();
		scrapers.add(new CanvasScrapper());
		scrapers.add(new IversityScrapper());

		return scrapers.stream().flatMap(scraper -> scraper.scrapeCourses().stream()).collect(Collectors.toList());
	}

}
