//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.typemapping;

public class DatabaseTypeUtils {
	public DatabaseTypeUtils() {
	}

	public static String getDatabaseTypeByJdbcDriver(String driver) {
		if (driver == null) {
			return null;
		} else {
			driver = driver.toLowerCase();
			if (driver.indexOf("mysql") >= 0) {
				return "mysql";
			} else if (driver.indexOf("oracle") >= 0) {
				return "oracle";
			} else if (driver.indexOf("com.microsoft.sqlserver.jdbc.sqlserverdriver") >= 0) {
				return "sqlserver2005";
			} else if (driver.indexOf("microsoft") < 0 && driver.indexOf("jtds") < 0) {
				if (driver.indexOf("postgresql") >= 0) {
					return "postgresql";
				} else if (driver.indexOf("sybase") >= 0) {
					return "sybase";
				} else if (driver.indexOf("db2") >= 0) {
					return "db2";
				} else if (driver.indexOf("hsqldb") >= 0) {
					return "hsqldb";
				} else if (driver.indexOf("derby") >= 0) {
					return "derby";
				} else {
					return driver.indexOf("h2") >= 0 ? "h2" : driver;
				}
			} else {
				return "sqlserver";
			}
		}
	}
}
