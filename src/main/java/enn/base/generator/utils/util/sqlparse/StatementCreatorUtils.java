//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.sqlparse;

import enn.base.generator.utils.provider.db.sql.model.SqlParameter;
import enn.base.generator.utils.util.StringHelper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

public class StatementCreatorUtils {
	static Random random = new Random(System.currentTimeMillis());

	public StatementCreatorUtils() {
	}

	public static Object getRandomValue(int sqlType, Integer scale) {
		switch(sqlType) {
			case -2147482648:
			case -10:
				return null;
			case -15:
			case -9:
			case 2011:
				return null;
			case -7:
			case -6:
			case 5:
				return (byte)randomNumber();
			case -5:
			case 2:
			case 3:
			case 4:
			case 6:
			case 7:
			case 8:
				return randomNumber();
			case -4:
			case -3:
			case -2:
				return null;
			case -1:
			case 2005:
				return null;
			case 0:
			case 1111:
			case 2004:
				return null;
			case 1:
			case 12:
				return randomString(scale);
			case 16:
				return false;
			case 91:
			case 92:
			case 93:
				return now();
			default:
				return null;
		}
	}

	public static int randomNumber() {
		return Math.abs(random.nextInt());
	}

	public static Timestamp now() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static String randomString(Integer count) {
		if (count == null || count.intValue() <= 0) {
			count = Integer.valueOf(1);
		}

		String result = "";

		for(int i = 0; i < count.intValue(); ++i) {
			result = result + "" + Math.abs(randomNumber()) % 9;
		}

		return result;
	}

	public static void setParameterRandomValue(PreparedStatement ps, int parameterIndex, int sqlType, Integer scale) throws SQLException {
		Object v = getRandomValue(sqlType, scale);
		if (v == null) {
			ps.setNull(parameterIndex, 0);
		} else if (v instanceof String) {
			ps.setString(parameterIndex, (String)v);
		} else if (v instanceof Timestamp) {
			ps.setTimestamp(parameterIndex, (Timestamp)v);
		} else if (v instanceof Number) {
			ps.setByte(parameterIndex, ((Number)v).byteValue());
		} else {
			ps.setObject(parameterIndex, v);
		}

	}

	public static void setRandomParamsValueForPreparedStatement(String executeSql, PreparedStatement ps, List<SqlParameter> params) throws SQLException {
		int count = StringHelper.containsCount(SqlParseHelper.removeOrders(executeSql), "?");
		if (count != 0) {
			SqlParameter[] sqlParameters = (SqlParameter[])params.toArray(new SqlParameter[0]);

			for(int parameterIndex = 1; parameterIndex <= count; ++parameterIndex) {
				int index = parameterIndex - 1;
				if (index < sqlParameters.length) {
					SqlParameter parameter = sqlParameters[index];
					setParameterRandomValue(ps, parameterIndex, parameter.getSqlType(), parameter.getSize());
				} else {
					ps.setObject(parameterIndex, (Object)null);
				}
			}

		}
	}
}
