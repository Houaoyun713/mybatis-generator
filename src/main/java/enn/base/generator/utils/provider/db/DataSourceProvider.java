//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.db;

import enn.base.generator.utils.GeneratorConstants;
import enn.base.generator.utils.GeneratorProperties;
import enn.base.generator.utils.util.GLogger;
import enn.base.generator.utils.util.StringHelper;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceProvider {
	private static Connection connection;
	private static DataSource dataSource;

	public DataSourceProvider() {
	}

	public static synchronized Connection getNewConnection() {
		try {
			return getDataSource().getConnection();
		} catch (SQLException var1) {
			throw new RuntimeException(var1);
		}
	}

	public static synchronized Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = getDataSource().getConnection();
			}

			return connection;
		} catch (SQLException var1) {
			throw new RuntimeException(var1);
		}
	}

	public static void setDataSource(DataSource dataSource) {
		dataSource = dataSource;
	}

	public static synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = lookupJndiDataSource(GeneratorProperties.getProperty(GeneratorConstants.DATA_SOURCE_JNDI_NAME));
			if (dataSource == null) {
				dataSource = new DriverManagerDataSource();
			}
		}

		return dataSource;
	}

	private static DataSource lookupJndiDataSource(String name) {
		if (StringHelper.isBlank(name)) {
			return null;
		} else {
			try {
				Context context = new InitialContext();
				return (DataSource)context.lookup(name);
			} catch (NamingException var2) {
				GLogger.warn("lookup generator dataSource fail by name:" + name + " cause:" + var2.toString() + ",retry by jdbc_url again");
				return null;
			}
		}
	}

	public static class DriverManagerDataSource implements DataSource {
		private static void loadJdbcDriver(String driverClass) {
			try {
				if (driverClass != null && !"".equals(driverClass.trim())) {
					Class.forName(driverClass.trim());
				} else {
					throw new IllegalArgumentException("jdbc 'driverClass' must not be empty");
				}
			} catch (ClassNotFoundException var2) {
				throw new RuntimeException("not found jdbc driver class:[" + driverClass + "]", var2);
			}
		}

		public DriverManagerDataSource() {
		}

		public Connection getConnection() throws SQLException {
			loadJdbcDriver(this.getDriverClass());
			return DriverManager.getConnection(this.getUrl(), this.getUsername(), this.getPassword());
		}

		public Connection getConnection(String username, String password) throws SQLException {
			loadJdbcDriver(this.getDriverClass());
			return DriverManager.getConnection(this.getUrl(), username, password);
		}

		public PrintWriter getLogWriter() throws SQLException {
			throw new UnsupportedOperationException("getLogWriter");
		}

		public int getLoginTimeout() throws SQLException {
			throw new UnsupportedOperationException("getLoginTimeout");
		}

		@Override
		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return null;
		}

		public void setLogWriter(PrintWriter out) throws SQLException {
			throw new UnsupportedOperationException("setLogWriter");
		}

		public void setLoginTimeout(int seconds) throws SQLException {
			throw new UnsupportedOperationException("setLoginTimeout");
		}

		public <T> T unwrap(Class<T> iface) throws SQLException {
			if (iface == null) {
				throw new IllegalArgumentException("Interface argument must not be null");
			} else if (!DataSource.class.equals(iface)) {
				throw new SQLException("DataSource of type [" + this.getClass().getName() + "] can only be unwrapped as [javax.sql.DataSource], not as [" + iface.getName());
			} else {
				return (T) this;
			}
		}

		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return DataSource.class.equals(iface);
		}

		private String getUrl() {
			return GeneratorProperties.getRequiredProperty(GeneratorConstants.JDBC_URL);
		}

		private String getUsername() {
			return GeneratorProperties.getRequiredProperty(GeneratorConstants.JDBC_USERNAME);
		}

		private String getPassword() {
			return GeneratorProperties.getProperty(GeneratorConstants.JDBC_PASSWORD);
		}

		private String getDriverClass() {
			return GeneratorProperties.getRequiredProperty(GeneratorConstants.JDBC_DRIVER);
		}

		public String toString() {
			return "DataSource: url=" + this.getUrl() + " username=" + this.getUsername();
		}
	}
}
