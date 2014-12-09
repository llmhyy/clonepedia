package clonepedia.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import clonepedia.Activator;
import clonepedia.filepraser.FileParser;
import clonepedia.util.Settings;

public class DBManager {
	
	private static Connection connection;
	private String configurationFile = "configurations/db.properties";
	private String tableFile = "gen_table.xml";
	
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	
	private DBManager(){
		Properties properties = new Properties();
		InputStream ifs;
		try {
			//System.out.println(System.getProperty("user.dir"));
			ifs = new FileInputStream(configurationFile);
			properties.load(ifs);
			ifs.close();
			
			this.driverClassName = properties.getProperty("driverClassName");
			this.url = properties.getProperty("url");
			this.username = properties.getProperty("username");
			this.password = properties.getProperty("password");
			
			Class.forName(driverClassName);
			connection = createConnectionForSpecificProject(DriverManager.getConnection(url, username, password));
			
			generateDatabaseTables();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * A new project is stored in a new database in DBMS, which is easy for debugging.
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private Connection createConnectionForSpecificProject(Connection connection) throws SQLException, ClassNotFoundException{
		/**
		 * The URL should depend on the project name.
		 */
		String databaseName = "Clonepedia_" + Settings.projectName;
		this.url = this.url + databaseName;
		
		Statement statement = connection.createStatement();
		statement.executeUpdate("create database if not exists " + databaseName);
		
		statement.close();
		connection.close();
		
		Class.forName(driverClassName);
		return DriverManager.getConnection(url, username, password);
	}
	
	/**
	 * Create the corresponding database tables for project-specific database.
	 * @throws XPathExpressionException
	 * @throws SQLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	private void generateDatabaseTables() throws XPathExpressionException, SQLException, FileNotFoundException, IOException, URISyntaxException{
		FileParser parser = new FileParser(){}; 
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL dbFileConfigURL = bundle.getEntry("dbSchema/gen_table.xml");
		
		File dbFileConfigFile = new File(FileLocator.resolve(dbFileConfigURL).toURI());
		Document doc = parser.getDocument(dbFileConfigFile);
		
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
		Node root = doc.getElementsByTagName("sql-tables").item(0);
		NodeList tableList = (NodeList)xpath.evaluate("child::table", root, XPathConstants.NODESET);
		
		ScriptRunner runner = new ScriptRunner(connection, false, true);
		/*URL tablesCleaningURL = bundle.getEntry("dbSchema/clean.sql");
		File tablesCleaningFile = new File(FileLocator.resolve(tablesCleaningURL).toURI());
		runner.runScript(new BufferedReader(new FileReader(tablesCleaningFile)));*/
		
		for(int i=0; i<tableList.getLength(); i++){
			
			Node table = tableList.item(i);
			String tableScript = parser.getAttributeValue(table, "script");
			URL tableScriptURL = bundle.getEntry("dbSchema/" + tableScript);
			File tableScriptFile = new File(FileLocator.resolve(tableScriptURL).toURI());
			runner.runScript(new BufferedReader(new FileReader(tableScriptFile)));
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
