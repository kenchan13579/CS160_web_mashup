package scrapper;

import model.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

import com.mysql.jdbc.Connection;

public class SQLoperation {
	Connection con;

	public SQLoperation(Connection con) {
		this.con = con;
	}

	public void addCourseData(List<Course> courses) {

		int count = 1;
		for (Course c : courses) {
			// start_date format is bad.. had to alter table to accept null
			String query = "INSERT INTO course_data (title,short_desc,long_desc,course_link,video_link,start_date,course_length"
					+ ",course_image,category,site,course_fee,language,certificate,university,time_scraped)"
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" + ")";
			try {

				PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, c.getTitle());
				statement.setString(2, c.getShortDescription());
				statement.setString(3, c.getLongDescription());
				statement.setString(4, c.getCourseLink());
				statement.setString(5, c.getVideoLink());
				if (c.getStartDate() != null) {
					statement.setDate(6, Date.valueOf(c.getStartDate()));
				} else {
					// current date instead of null
					statement.setDate(6, new Date(Calendar.getInstance().getTimeInMillis()));
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
				try {
					statement.executeUpdate();
					ResultSet rs = statement.getGeneratedKeys();
					int id = 0;
					if (rs != null && rs.next()) {
						id = rs.getInt(1);
						rs.close();
					} else {
						throw new SQLException("DB INSERT FAILED for: " + c.getTitle());
					}
					statement.close();
					insertCourseDetails(count, c.getDetails(), id);
					count++;

				} catch (SQLException e) {
					// this will catch the sql error if database doesnt take
					// course data
					// e.printStackTrace();
					System.out.println(c.getTitle() + " : had an SQL error - " + e.getMessage());
				}
			} catch (SQLException e) {

			}
		}
		System.out.println("We found " + courses.size() + " courses.");
	}

	private void insertCourseDetails(int count, CourseDetails details, int id) throws SQLException {
		String query = "INSERT INTO coursedetails (id, profname, profimage, course_id)" + " VALUES (?, ?,?,?)";
		PreparedStatement statement = con.prepareStatement(query);
		statement.setInt(1, count);
		// Column only supports 30 characters, no null.
		String profName = details.getProfName() == null ? ""
				: details.getProfName().substring(0, Integer.min(details.getProfName().length(), 30));
		statement.setString(2, profName);
		// No null
		String profImage = details.getProfImage() == null ? "" : details.getProfImage();
		statement.setString(3, profImage);
		statement.setInt(4, id);
		statement.executeUpdate();
		statement.close();
	}

}
