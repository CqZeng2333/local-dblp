package connect_database;

import java.sql.*;

public class Connector {
	// Change the parameters accordingly.
	private static String dbUrl = "jdbc:mysql://localhost:3306/dblp?serverTimezone=GMT";
	private static String user = "root";
	private static String password = "000000";

	public static Connection getConn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(dbUrl, user, password);
		} catch (Exception e) {
			System.out.println("Error while opening a conneciton to database server: "
								+ e.getMessage());
			return null;
		}
	}
}
