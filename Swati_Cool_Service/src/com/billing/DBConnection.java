package com.billing;

import java.sql.*;

public class DBConnection {
	public static Connection getConnection() throws Exception {
	    String url = "jdbc:mysql://localhost:3306/swati_cool_service";
	    String user = "root";
	    String pass = "9821";
	    Class.forName("com.mysql.cj.jdbc.Driver");
	    return DriverManager.getConnection(url, user, pass);
	}

}
