package com.soham.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

public class ConnectionFactoryBuilder {

	private final String FILE_NAME = "db.properties";
	private StringBuilder DB_URL = null;
	private String DB_SCHEMA = null;
	private String DB_USERNAME = null;
	private String DB_PASSWORD = null;
	private String DB_DRIVER = null;
	private String DB_URL_LINK = null;
	
	//private ObjectPool connectionPool = null;
	private DataSource ds = null;
	private ObjectPool connectionPool = null;
	
	private ConnectionFactoryBuilder() {
		//ds = loadDatasource();
		try {
			readProperties();
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());;
		}
		try {
			ds = buildConnectionFactory();
		} catch (ClassNotFoundException e) {
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	public Connection getConnection() throws SQLException {
		//System.out.println(connectionPool.getNumActive());
		return ds.getConnection();
	}
	
	public static class JDBCConnectionFactory {
		public static ConnectionFactoryBuilder sharedInstance = new ConnectionFactoryBuilder();
	}
	
	private void readProperties() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_NAME);
		
		Properties prop = new Properties();
		prop.load(is);
		
		DB_SCHEMA = readEachProperty(prop, "jdbc.components.db.schema");
		DB_DRIVER = readEachProperty(prop, "jdbc.components.db.driver");
		DB_USERNAME = readEachProperty(prop, "jdbc.components.db.username");
		DB_PASSWORD = readEachProperty(prop, "jdbc.components.db.password");
		DB_URL_LINK = readEachProperty(prop, "jdbc.components.db.url");
		DB_URL = new StringBuilder();
		DB_URL.append("jdbc:mysql://")
				.append(DB_URL_LINK)
				.append("/")
				.append(DB_SCHEMA)
				.append("?")
				.append("useUnicode=true&characterEncoding=UTF-8");
				//.append("&username=").append(DB_USERNAME)
				//.append("&password=").append(DB_PASSWORD);
	}
	
	private String readEachProperty(Properties prop, String propKey) {
		if (prop.getProperty(propKey) == null) {
			throw new IllegalArgumentException("Error occured while reading key - "+propKey);
		} else {
			return prop.getProperty(propKey);
		}
	}
	
	private DataSource buildConnectionFactory() throws ClassNotFoundException {
		
		Class.forName(DB_DRIVER);
		
		/**
		 * first create connection factory which will lend collection objects
		 */
		ConnectionFactory conFactory = new DriverManagerConnectionFactory(DB_URL.toString(), DB_USERNAME, DB_PASSWORD);
		
		/**
		 * Now we are creating object pool for connection objects
		 */
		connectionPool = new GenericObjectPool();
		GenericObjectPool genObjPool = (GenericObjectPool) connectionPool;
		genObjPool.setMaxActive(30);
		genObjPool.setMaxIdle(10);
		genObjPool.setMaxWait(60);
		genObjPool.setMinEvictableIdleTimeMillis(216000);
		
		/**
		 * now binding the connection factory and connection pool to the poolable connection factory for Poolable functionality.
		 */
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(conFactory, connectionPool, null, "SELECT 1", null, true, true);
		
		/**
		 * setting the object pool with the factory for connection building.
		 */
		connectionPool.setFactory(poolableConnectionFactory);
		
		/**
		 * passing the poolable driver 
		 */
		PoolingDataSource dataSource = new PoolingDataSource(connectionPool);
		return dataSource;
	}
}
