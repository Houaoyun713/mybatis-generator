//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.db.sql;

import enn.base.generator.utils.provider.db.DataSourceProvider;
import enn.base.generator.utils.provider.db.sql.model.Sql;
import enn.base.generator.utils.provider.db.sql.model.SqlParameter;
import enn.base.generator.utils.provider.db.table.TableFactory;
import enn.base.generator.utils.provider.db.table.model.Column;
import enn.base.generator.utils.provider.db.table.model.Table;
import enn.base.generator.utils.util.BeanHelper;
import enn.base.generator.utils.util.DBHelper;
import enn.base.generator.utils.util.GLogger;
import enn.base.generator.utils.util.StringHelper;
import enn.base.generator.utils.util.sqlerrorcode.SQLErrorCodeSQLExceptionTranslator;
import enn.base.generator.utils.util.sqlparse.BasicSqlFormatter;
import enn.base.generator.utils.util.sqlparse.NamedParameterUtils;
import enn.base.generator.utils.util.sqlparse.ParsedSql;
import enn.base.generator.utils.util.sqlparse.ResultSetMetaDataHolder;
import enn.base.generator.utils.util.sqlparse.SqlParseHelper;
import enn.base.generator.utils.util.sqlparse.StatementCreatorUtils;
import enn.base.generator.utils.util.typemapping.JdbcType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlFactory {
	public static Map<String, Table> cache = new HashMap();

	public SqlFactory() {
	}

	public Sql parseSql(String sourceSql) {
		if (StringHelper.isBlank(sourceSql)) {
			throw new IllegalArgumentException("sourceSql must be not empty");
		} else {
			String beforeProcessedSql = this.beforeParseSql(sourceSql);
			String namedSql = SqlParseHelper.convert2NamedParametersSql(beforeProcessedSql, ":", "");
			ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(namedSql);
			String executeSql = (new BasicSqlFormatter()).format(NamedParameterUtils.substituteNamedParameters(parsedSql));
			Sql sql = new Sql();
			sql.setSourceSql(sourceSql);
			sql.setExecuteSql(executeSql);
			GLogger.debug("\n*******************************");
			GLogger.debug("sourceSql  :" + sql.getSourceSql());
			GLogger.debug("namedSql  :" + namedSql);
			GLogger.debug("executeSql :" + sql.getExecuteSql());
			GLogger.debug("*********************************");
			Connection conn = null;
			PreparedStatement ps = null;

			Sql var11;
			try {
				conn = DataSourceProvider.getNewConnection();
				conn.setAutoCommit(false);
				ps = conn.prepareStatement(SqlParseHelper.removeOrders(executeSql));
				SqlParametersParser sqlParametersParser = new SqlParametersParser();
				sqlParametersParser.execute(parsedSql, sql);
				ResultSetMetaData resultSetMetaData = this.executeSqlForResultSetMetaData(executeSql, ps, sqlParametersParser.allParams);
				sql.setColumns((new SelectColumnsParser()).convert2Columns(sql, resultSetMetaData));
				sql.setParams(sqlParametersParser.params);
				var11 = this.afterProcessedSql(sql);
			} catch (SQLException var31) {
				throw new RuntimeException("execute sql occer error,\nexecutedSql:" + SqlParseHelper.removeOrders(executeSql), var31);
			} catch (Exception var32) {
				throw new RuntimeException("sql parse error,\nexecutedSql:" + SqlParseHelper.removeOrders(executeSql), var32);
			} finally {
				try {
					DBHelper.rollback(conn);
				} finally {
					DBHelper.close(conn, ps, (ResultSet)null);
				}
			}

			return var11;
		}
	}

	protected Sql afterProcessedSql(Sql sql) {
		return sql;
	}

	protected String beforeParseSql(String sourceSql) {
		return sourceSql;
	}

	private ResultSetMetaData executeSqlForResultSetMetaData(String sql, PreparedStatement ps, List<SqlParameter> params) throws SQLException {
		StatementCreatorUtils.setRandomParamsValueForPreparedStatement(sql, ps, params);

		try {
			ps.setMaxRows(3);
			ps.setFetchSize(3);
			ps.setQueryTimeout(20);
			ResultSet rs = null;
			if (ps.execute()) {
				rs = ps.getResultSet();
				return rs.getMetaData();
			} else {
				return null;
			}
		} catch (SQLException var6) {
			if (this.isDataIntegrityViolationException(var6)) {
				GLogger.warn("ignore executeSqlForResultSetMetaData() SQLException,errorCode:" + var6.getErrorCode() + " sqlState:" + var6.getSQLState() + " message:" + var6.getMessage() + "\n executedSql:" + sql);
				return null;
			} else {
				String message = "errorCode:" + var6.getErrorCode() + " SQLState:" + var6.getSQLState() + " errorCodeTranslatorDataBaaseName:" + this.getErrorCodeTranslatorDataBaaseName() + " " + var6.getMessage();
				throw new SQLException(message, var6.getSQLState(), var6.getErrorCode());
			}
		}
	}

	private String getErrorCodeTranslatorDataBaaseName() {
		SQLErrorCodeSQLExceptionTranslator transaltor = SQLErrorCodeSQLExceptionTranslator.getSQLErrorCodeSQLExceptionTranslator(DataSourceProvider.getDataSource());
		return transaltor.getSqlErrorCodes() == null ? "null" : Arrays.toString(transaltor.getSqlErrorCodes().getDatabaseProductNames());
	}

	protected boolean isDataIntegrityViolationException(SQLException sqlEx) {
		SQLErrorCodeSQLExceptionTranslator transaltor = SQLErrorCodeSQLExceptionTranslator.getSQLErrorCodeSQLExceptionTranslator(DataSourceProvider.getDataSource());
		return transaltor.isDataIntegrityViolation(sqlEx);
	}

	public static Table getTableFromCache(String tableSqlName) {
		if (tableSqlName == null) {
			throw new IllegalArgumentException("tableSqlName must be not null");
		} else {
			Table table = (Table)cache.get(tableSqlName.toLowerCase());
			if (table == null) {
				table = TableFactory.getInstance().getTable(tableSqlName);
				cache.put(tableSqlName.toLowerCase(), table);
			}

			return table;
		}
	}

	public static class SqlParametersParser {
		private static Map<String, Column> specialParametersMapping = new HashMap();
		public LinkedHashSet<SqlParameter> params;
		public List<SqlParameter> allParams;

		public SqlParametersParser() {
			specialParametersMapping.put("offset", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "offset", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("limit", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "limit", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("pageSize", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "pageSize", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("pageNo", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "pageNo", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("pageNumber", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "pageNumber", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("pageNum", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "pageNumber", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("page", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "page", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("beginRow", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "beginRow", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("beginRows", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "beginRows", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("startRow", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "startRow", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("startRows", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "startRows", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("endRow", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "endRow", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("endRows", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "endRows", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("lastRow", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "lastRow", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("lastRows", new Column((Table)null, JdbcType.INTEGER.TYPE_CODE, "INTEGER", "lastRows", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("orderBy", new Column((Table)null, JdbcType.VARCHAR.TYPE_CODE, "VARCHAR", "orderBy", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("orderby", new Column((Table)null, JdbcType.VARCHAR.TYPE_CODE, "VARCHAR", "orderby", 0, 0, false, false, false, false, (String)null, (String)null));
			specialParametersMapping.put("sortColumns", new Column((Table)null, JdbcType.VARCHAR.TYPE_CODE, "VARCHAR", "sortColumns", 0, 0, false, false, false, false, (String)null, (String)null));
			this.params = new LinkedHashSet();
			this.allParams = new ArrayList();
		}

		private void execute(ParsedSql parsedSql, Sql sql) throws Exception {
			long start = System.currentTimeMillis();

			for(int i = 0; i < parsedSql.getParameterNames().size(); ++i) {
				String paramName = (String)parsedSql.getParameterNames().get(i);
				Column column = this.findColumnByParamName(parsedSql, sql, paramName);
				if (column == null) {
					column = (Column)specialParametersMapping.get(paramName);
					if (column == null) {
						column = new Column((Table)null, JdbcType.UNDEFINED.TYPE_CODE, "UNDEFINED", paramName, 0, 0, false, false, false, false, (String)null, (String)null);
					}
				}

				SqlParameter param = new SqlParameter(column);
				param.setParamName(paramName);
				if (this.isMatchListParam(sql.getSourceSql(), paramName)) {
					param.setListParam(true);
				}

				this.params.add(param);
				this.allParams.add(param);
			}

			GLogger.perf("parseForSqlParameters() cost:" + (System.currentTimeMillis() - start));
		}

		public boolean isMatchListParam(String sql, String paramName) {
			return sql.matches("(?s).*\\sin\\s*\\([:#\\$&]\\{?" + paramName + "\\}?[$#}]?\\).*") || sql.matches("(?s).*[#$]" + paramName + "\\[]\\.?\\w*[#$].*") || sql.matches("(?s).*[#$]\\{" + paramName + "\\[[$\\{\\}\\w]+]\\}*.*");
		}

		private Column findColumnByParamName(ParsedSql parsedSql, Sql sql, String paramName) throws Exception {
			Column column = sql.getColumnByName(paramName);
			if (column == null) {
				String leftColumn = SqlParseHelper.getColumnNameByRightCondition(parsedSql.toString(), paramName);
				if (leftColumn != null) {
					column = this.findColumnByParseSql(parsedSql, leftColumn);
				}
			}

			if (column == null) {
				column = this.findColumnByParseSql(parsedSql, paramName);
			}

			return column;
		}

		private Column findColumnByParseSql(ParsedSql sql, String paramName) throws Exception {
			if (paramName == null) {
				throw new NullPointerException("'paramName' must be not null");
			} else {
				try {
					Collection<SqlParseHelper.NameWithAlias> tableNames = SqlParseHelper.getTableNamesByQuery(sql.toString());
					Iterator i$ = tableNames.iterator();

					while(i$.hasNext()) {
						SqlParseHelper.NameWithAlias tableName = (SqlParseHelper.NameWithAlias)i$.next();
						Table t = SqlFactory.getTableFromCache(tableName.getName());
						if (t != null) {
							Column column = t.getColumnByName(paramName);
							if (column != null) {
								return column;
							}
						}
					}

					return null;
				} catch (TableFactory.NotFoundTableException var8) {
					throw new IllegalArgumentException("get tableNamesByQuery occer error:" + sql.toString(), var8);
				}
			}
		}
	}

	public static class SelectColumnsParser {
		public SelectColumnsParser() {
		}

		private LinkedHashSet<Column> convert2Columns(Sql sql, ResultSetMetaData metadata) throws SQLException, Exception {
			if (metadata == null) {
				return new LinkedHashSet();
			} else {
				LinkedHashSet<Column> columns = new LinkedHashSet();

				for(int i = 1; i <= metadata.getColumnCount(); ++i) {
					Column c = this.convert2Column(sql, metadata, i);
					if (c == null) {
						throw new IllegalStateException("column must be not null");
					}

					columns.add(c);
				}

				return columns;
			}
		}

		private Column convert2Column(Sql sql, ResultSetMetaData metadata, int i) throws SQLException, Exception {
			ResultSetMetaDataHolder m = new ResultSetMetaDataHolder(metadata, i);
			if (StringHelper.isNotBlank(m.getTableName())) {
				Table table = this.foundTableByTableNameOrTableAlias(sql, m.getTableName());
				if (table == null) {
					return this.newColumn((Table)null, m);
				} else {
					Column column = table.getColumnBySqlName(m.getColumnLabelOrName());
					if (column != null && column.getSqlType() == m.getColumnType()) {
						GLogger.trace("found column:" + m.getColumnLabelOrName() + " on table:" + table.getSqlName() + " " + BeanHelper.describe(column, new String[0]));
					} else {
						column = this.newColumn(table, m);
						GLogger.trace("not found column:" + m.getColumnLabelOrName() + " on table:" + table.getSqlName() + " " + BeanHelper.describe(column, new String[0]));
					}

					return column;
				}
			} else {
				return this.newColumn((Table)null, m);
			}
		}

		private Column newColumn(Table table, ResultSetMetaDataHolder m) {
			Column column = new Column((Table)null, m.getColumnType(), m.getColumnTypeName(), m.getColumnLabelOrName(), m.getColumnDisplaySize(), m.getScale(), false, false, false, false, (String)null, (String)null);
			GLogger.trace("not found on table by table emtpty:" + BeanHelper.describe(column, new String[0]));
			return column;
		}

		private Table foundTableByTableNameOrTableAlias(Sql sql, String tableNameId) throws Exception {
			try {
				return SqlFactory.getTableFromCache(tableNameId);
			} catch (TableFactory.NotFoundTableException var7) {
				Set<SqlParseHelper.NameWithAlias> tableNames = SqlParseHelper.getTableNamesByQuery(sql.getExecuteSql());
				Iterator i$ = tableNames.iterator();

				SqlParseHelper.NameWithAlias tableName;
				do {
					if (!i$.hasNext()) {
						return null;
					}

					tableName = (SqlParseHelper.NameWithAlias)i$.next();
				} while(!tableName.getAlias().equalsIgnoreCase(tableNameId));

				return SqlFactory.getTableFromCache(tableName.getName());
			}
		}
	}
}
