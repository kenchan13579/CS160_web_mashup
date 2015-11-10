package scrapper;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class ConnectionFactory {
	public static Connection getConnection(){
		
		try {
			System.out.println("Connecting to Database ...");
			java.sql.Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moocs160", "root", "");
			System.out.println("Connected to moocs160 as root");
			return (Connection) connection;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed:" + e.getMessage() );
			return null;
		}
			
	}
}
