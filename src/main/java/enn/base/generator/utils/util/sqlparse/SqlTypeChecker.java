//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.sqlparse;

import enn.base.generator.utils.util.StringHelper;

public class SqlTypeChecker {
	public SqlTypeChecker() {
	}

	public static boolean isSelectSql(String sourceSql) {
		return StringHelper.removeXMLCdataTag(SqlParseHelper.removeSqlComments(sourceSql)).trim().toLowerCase().matches("(?is)\\s*select\\s.*\\sfrom\\s+.*");
	}

	public static boolean isUpdateSql(String sourceSql) {
		return StringHelper.removeXMLCdataTag(SqlParseHelper.removeSqlComments(sourceSql)).trim().toLowerCase().matches("(?is)\\s*update\\s+.*\\sset\\s.*");
	}

	public static boolean isDeleteSql(String sourceSql) {
		String processedSql = StringHelper.removeXMLCdataTag(SqlParseHelper.removeSqlComments(sourceSql)).trim().toLowerCase();
		return processedSql.matches("(?is)\\s*delete\\s+from\\s.*") || processedSql.matches("(?is)\\s*delete\\s+.*");
	}

	public static boolean isInsertSql(String sourceSql) {
		return StringHelper.removeXMLCdataTag(SqlParseHelper.removeSqlComments(sourceSql)).trim().toLowerCase().matches("(?is)\\s*insert\\s+into\\s+.*");
	}
}
