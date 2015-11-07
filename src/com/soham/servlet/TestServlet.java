package com.soham.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soham.connection.ConnectionFactoryBuilder;
import com.soham.connection.ConnectionFactoryBuilder.JDBCConnectionFactory;

/**
 * Servlet implementation class TestServlet
 */
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public TestServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ConnectionFactoryBuilder cf = JDBCConnectionFactory.sharedInstance;
		String sql = "SELECT * FROM users";
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<String[]> list = null;
		try {
			con = cf.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			
			list = new ArrayList<String[]>();
			
			while (rs.next()) {
				String[] sqq = new String[2];
				sqq[0] = rs.getString(1);
				sqq[1] = rs.getString(2);
				//System.out.println(rs.getString(1)+" - "+rs.getString(2));
				list.add(sqq);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			stmt.close();
			rs.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//list = cf.getData();
		
		//for (Iterator<String[]> it = list.iterator(); it.hasNext();) {
		//	String[] strings = (String[]) it.next();
		//	System.out.println(strings[0]+", "+strings[1]);
		//}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
