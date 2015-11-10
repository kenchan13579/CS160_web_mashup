package scrapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
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
			java.sql.Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moocs160", "root", "");
			System.out.println("Connected to moocs160 as root");

			// now build the SQL statement
			PreparedStatement statement;
			
			String query = "";
			for (Course c : courses) {
				// start_date format is bad.. had to alter table to accept null
				query = "INSERT INTO course_data (title,short_desc,long_desc,course_link,video_link,start_date,course_length"
						+ ",course_image,category,site,course_fee,language,certificate,university,time_scraped)"
						+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"+")";
				statement = connection.prepareStatement(query);
				statement.setString(1, c.getTitle());
				statement.setString(2, c.getShortDescription());
				statement.setString(3, c.getLongDescription());
				statement.setString(4, c.getCourseLink());
				statement.setString(5, c.getVideoLink());
				if ( c.getStartDate()!=null) {
					statement.setDate(6, Date.valueOf(c.getStartDate()));
				} else {
					// current date instead of null 
					statement.setDate(6,new Date(Calendar.getInstance().getTimeInMillis()));
				}
				statement.setInt(7, c.getCourseLength());
				statement.setString(8, c.getCourseImage());
				statement.setString(9, c.getCategorey());
				statement.setString(10, c.getCourseLink());
				statement.setInt(11, c.getCourseFee());
				statement.setString(12, c.getLanguage());
				statement.setString(13, c.getCertficate().toString());
				statement.setString(14, c.getUniversity());
				statement.setDate(15, Date.valueOf(c.getTimeScraped()));
				try{
				statement.executeUpdate();
				statement.close();
				//System.out.println(query);
				}catch (SQLException e){
					// this will catch the sql error if database doesnt take course data
					//e.printStackTrace();
					System.out.println(c.getTitle() + " : had an SQL error - " + e.getMessage());
				}
			}
			
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
