//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public class SqlExecutorHelper {
	public SqlExecutorHelper() {
	}

	public static List<Map> queryForList(Connection conn, String sql, int limit) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql.trim());
		ps.setMaxRows(limit);
		ps.setFetchDirection(1000);
		ResultSet rs = ps.executeQuery();

		List var6;
		try {
			List result = toListMap(limit, rs);
			var6 = result;
		} finally {
			DBHelper.close(rs);
		}

		return var6;
	}

	public static boolean execute(DataSource ds, String sql) {
		Connection conn = null;

		boolean var4;
		try {
			conn = ds.getConnection();
			Statement s = conn.createStatement();
			var4 = s.execute(sql);
		} catch (SQLException var8) {
			throw new RuntimeException(var8.getMessage() + " errorCode:" + var8.getErrorCode() + " SQLState:" + var8.getSQLState());
		} finally {
			DBHelper.close(conn);
		}

		return var4;
	}

	public static List<Map> toListMap(int limit, ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = 0;
		ArrayList list = new ArrayList();

		while(rs.next()) {
			Map row = new HashMap();

			for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
				row.put(rsmd.getColumnName(i), rs.getObject(i));
			}

			list.add(row);
			++count;
			if (count >= limit) {
				break;
			}
		}

		return list;
	}
}
