package mine.fanjh.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://119.23.231.0:3306/im?useUnicode=true&zeroDateTimeBehavior=convertToNull","root","123456");
        connection.setAutoCommit(false);
        return connection;
	}
	
	public static void close(Connection connection) {
		if(null != connection) {
			try {
				connection.close();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public static void rollback(Connection connection) {
		if(null != connection) {
			try {
				connection.rollback();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
}
