package scrapper;

import model.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import com.mysql.jdbc.Connection;

public class SQLoperation {
	Connection con;
	public SQLoperation(Connection con) {
		this.con = con;
	}
	public void addCourseData(List<Course> courses){
		
		
		for (Course c : courses) {
			// start_date format is bad.. had to alter table to accept null
			String query = "INSERT INTO course_data (title,short_desc,long_desc,course_link,video_link,start_date,course_length"
					+ ",course_image,category,site,course_fee,language,certificate,university,time_scraped)"
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"+")";
			try {
				
			
			PreparedStatement statement = con.prepareStatement(query);
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
			
			}catch (SQLException e){
				// this will catch the sql error if database doesnt take course data
				//e.printStackTrace();
				System.out.println(c.getTitle() + " : had an SQL error - " + e.getMessage());
			}
			} catch (SQLException e) {
				
			}
		}
		System.out.println("We found " + courses.size() + " courses.");
	}
	

	
}
