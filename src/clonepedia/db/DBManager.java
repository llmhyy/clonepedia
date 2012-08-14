package clonepedia.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBManager {
	
	private static Connection connection;
	private String configurationFile = "configurations/db.properties";
	
	private DBManager(){
		Properties properties = new Properties();
		InputStream ifs;
		try {
			//System.out.println(System.getProperty("user.dir"));
			ifs = new FileInputStream(configurationFile);
			properties.load(ifs);
			ifs.close();
			
			String driverClassName = properties.getProperty("driverClassName");
			String url = properties.getProperty("url");
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");
			
			Class.forName(driverClassName);
			connection = DriverManager.getConnection(url, username, password);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public static Connection getConnection(){
		if(connection == null){
			new DBManager();
			return connection;
		}
		else
			return connection;
	}
}
