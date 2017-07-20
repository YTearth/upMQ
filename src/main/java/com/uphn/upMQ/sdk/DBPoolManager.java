package com.uphn.upMQ.sdk;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

/**
 * tomcat数据库连接池管理类<br>
 * 使用为tomcat部署环境<br>
 * 需要在类路径下准备数据库连接配置文件dbcp.properties
 * 
 * @author tianyin
 * @time 2017-07-19
 * 
 */
public class DBPoolManager {
	private static final Logger logger = Logger.getLogger(MessageQueue.class);
	private static final String configFile = "dbcp.properties";
	private static DataSource dataSource;
	static {
		Properties dbProperties = new Properties();
		try {
			dbProperties.load(DBPoolManager.class.getClassLoader().getResourceAsStream(configFile));
			dataSource = BasicDataSourceFactory.createDataSource(dbProperties);
			Connection conn = getConnection();
			DatabaseMetaData mdm = conn.getMetaData();
			logger.info("Connected to " + mdm.getDatabaseProductName() + " " + mdm.getDatabaseProductVersion());
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			logger.error("init db pool failed：" + e);
		}
	}

	private DBPoolManager() {
	}

	/**
	 * 获取链接，用完后记得关闭
	 * 
	 * @see {@link DBPoolManager#closeConn(Connection)}
	 * @return
	 */
	public static final Connection getConnection() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			logger.error("get db Connection failed：" + e);
		}
		return conn;
	}

	/**
	 * 关闭连接
	 * 
	 * @param conn
	 *            需要关闭的连接
	 */
	public static void closeConnection(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (SQLException e) {
			logger.error("close db Connection failed：" + e);
		}
	}
}
