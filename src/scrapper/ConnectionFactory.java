package scrapper;

import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.jdbc.Connection;

public class ConnectionFactory {
	/**
	 *
	 * @return jdbc Connection object to access the database
	 */
	public static Connection getConnection(){
		Login login = getConfig();
		try {
			System.out.println("Connecting to " + login.hostname+"/"+ login.db  + " ...");
			Connection connection = (Connection) DriverManager.
					getConnection("jdbc:mysql://" + login.hostname + "/" + login.db, login.username, login.password);
			System.out.println("Successfully connected to " + login.db +" as "+ login.username);

			return connection;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed:" + e.getMessage() );
			return null;
		}


	}
	/*
	 * get the login info of the database
	 */
	private static Login getConfig(){
		JsonParser parser = new JsonParser();
		try {
			Object obj = parser.parse(new FileReader("config.json"));
			JsonObject json = (JsonObject) obj;
			String hostname = json.get("hostname").getAsString();
			String username = json.get("username").getAsString();
			String password = json.get("password").getAsString();
			String database = json.get("db").getAsString();
			return  new Login(hostname,username,password,database);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * an instance stores login information
	 * @author Ken
	 *
	 */
	private static class Login {
		public String hostname;
		public String username;
		public String password;
		public String db;
		public Login(String h , String u, String p, String db) {
			this.hostname = h;
			this.username = u;
			this.password = p;
			this.db = db;
		}
		@Override
		public String toString() {
			return "Login [hostname=" + hostname + ", username=" + username + ", password=" + password + ", db=" + db
					+ "]";
		}
	}
}
