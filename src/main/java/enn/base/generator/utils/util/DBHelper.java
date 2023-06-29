//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {
	public DBHelper() {
	}

	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception var2) {
				;
			}
		}

	}

	public static void close(PreparedStatement s) {
		if (s != null) {
			try {
				s.close();
			} catch (Exception var2) {
				;
			}
		}

	}

	public static void close(Statement s) {
		if (s != null) {
			try {
				s.close();
			} catch (Exception var2) {
				;
			}
		}

	}

	public static void close(ResultSet s) {
		if (s != null) {
			try {
				s.close();
			} catch (Exception var2) {
				;
			}
		}

	}

	public static void close(Connection conn, ResultSet rs) {
		close(conn);
		close(rs);
	}

	public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
		close(conn);
		close(ps);
		close(rs);
	}

	public static void close(PreparedStatement ps, ResultSet rs) {
		close(ps);
		close(rs);
	}

	public static void close(Connection conn, Statement s, ResultSet rs) {
		close(conn);
		close(s);
		close(rs);
	}

	public static void close(Statement s, ResultSet rs) {
		close(s);
		close(rs);
	}

	public static void rollback(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (SQLException var2) {
				throw new RuntimeException("rollback occer error", var2);
			}
		}

	}
}
