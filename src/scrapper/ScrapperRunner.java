package scrapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import com.mysql.jdbc.Connection;

import model.Course;

public class ScrapperRunner {

	public static void main(String[] args) {
		ScrapperRunner runner = new ScrapperRunner();
		List<Course> courses = runner.scrape();
		Connection c = ConnectionFactory.getConnection();
		SQLoperation sql = new SQLoperation(c);
		sql.addCourseData(courses);
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// courses should be dumped in db here.
		// connect to MySql
		// The following few lines of code are used to connect to a database so
		// the scraped course content can be stored.
		// make sure you create a database named moocs160 in your local mysql
		// database before running this code
		// default mysql database in your local machine is ID:root with no
		// password
		// you can download moocs.sql database template from your Canvas
		// account->modules->Team Project area
		
	}

	private List<Course> scrape() {
		List<CourseScrapper> scrapers = new ArrayList<>();
		scrapers.add(new CanvasScrapper());
		scrapers.add(new IversityScrapper());
		return scrapers.parallelStream().flatMap(scraper -> scraper.scrapeCourses().stream())
				.collect(Collectors.toList());
	}

}
