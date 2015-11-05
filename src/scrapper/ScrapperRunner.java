package scrapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.Course;

public class ScrapperRunner {

	public static void main(String[] args) {
		ScrapperRunner runner = new ScrapperRunner();
		List<Course> courses = runner.scrape();

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
		try {
			System.out.println("Connecting to Database ...");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			java.sql.Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/moocs160", "root", "");
			System.out.println("Connected to moocs160 as root");

			// now build the SQL statement
			Statement statement = connection.createStatement();
			String query = "";
			for (Course c : courses) {
				// start_date format is bad.. had to alter table to accept null
				query = "insert into course_data values" + "(null,'" + c.getTitle() + "','" + c.getShortDescription()
						+ "','" + c.getLongDescription() + "','" + c.getCourseLink() + "','" + c.getVideoLink()
						+ "',"/* +c.getStartDate() */
						+ "null," + c.getCourseLength() + ",'" + c.getCourseImage() + "','" + c.getCategorey() + "','"
						+ c.getSite() + "','" + c.getCourseFee() + "','" + c.getLanguage() + "','" + c.getCertficate()
						+ "','" + c.getUniversity() + "','" + c.getTimeScraped() + "')";
				try{
				statement.executeUpdate(query);
				System.out.println(query);
				}catch (SQLException e){
					// this will catch the sql error if database doesnt take course data
					//e.printStackTrace();
					System.out.println(c.getTitle() + " : had an SQL error");
				}
			}
			statement.close();
			connection.close();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		System.out.println("We found " + courses.size() + " courses.");

	}

	private List<Course> scrape() {
		List<CourseScrapper> scrapers = new ArrayList<>();
		scrapers.add(new CanvasScrapper());
		scrapers.add(new IversityScrapper());

		return scrapers.parallelStream().flatMap(scraper -> scraper.scrapeCourses().stream())
				.collect(Collectors.toList());
	}

}
