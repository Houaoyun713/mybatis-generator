//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.db.sql.model;

import enn.base.generator.utils.GeneratorConstants;
import enn.base.generator.utils.GeneratorProperties;
import enn.base.generator.utils.provider.db.sql.SqlFactory;
import enn.base.generator.utils.provider.db.table.model.Column;
import enn.base.generator.utils.provider.db.table.model.ColumnSet;
import enn.base.generator.utils.provider.db.table.model.Table;
import enn.base.generator.utils.util.StringHelper;
import enn.base.generator.utils.util.sqlparse.SqlParseHelper;
import enn.base.generator.utils.util.sqlparse.SqlTypeChecker;
import enn.base.generator.utils.util.typemapping.JavaPrimitiveTypeMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class Sql {
	public static String MULTIPLICITY_ONE = "one";
	public static String MULTIPLICITY_MANY = "many";
	public static String MULTIPLICITY_PAGING = "paging";
	public static String PARAMTYPE_PRIMITIVE = "primitive";
	public static String PARAMTYPE_OBJECT = "object";
	String operation = null;
	String resultClass;
	String parameterClass;
	String remarks;
	String multiplicity;
	boolean paging;
	String sqlmap;
	String resultMap;
	LinkedHashSet<Column> columns;
	LinkedHashSet<SqlParameter> params;
	String sourceSql;
	String executeSql;
	private String paramType;
	private List<SqlSegment> sqlSegments;
	private String ibatisSql;
	private String mybatisSql;

	public Sql() {
		this.multiplicity = MULTIPLICITY_ONE;
		this.paging = false;
		this.resultMap = null;
		this.columns = new LinkedHashSet();
		this.params = new LinkedHashSet();
		this.paramType = PARAMTYPE_PRIMITIVE;
		this.sqlSegments = new ArrayList();
	}

	public boolean isColumnsInSameTable() {
		if (this.columns != null && !this.columns.isEmpty()) {
			Collection<SqlParseHelper.NameWithAlias> tableNames = SqlParseHelper.getTableNamesByQuery(this.executeSql);
			if (tableNames.size() > 1) {
				return false;
			} else {
				Table t = SqlFactory.getTableFromCache(((SqlParseHelper.NameWithAlias)tableNames.iterator().next()).getName());
				Iterator i$ = this.columns.iterator();

				Column fromTableColumn;
				do {
					if (!i$.hasNext()) {
						return true;
					}

					Column c = (Column)i$.next();
					fromTableColumn = (new ColumnSet(t.getColumns())).getBySqlName(c.getSqlName());
				} while(fromTableColumn != null);

				return false;
			}
		} else {
			return false;
		}
	}

	public String getResultClass() {
		String resultClass = this._getResultClass();
		return !this.isPaging() && !MULTIPLICITY_MANY.equals(this.multiplicity) ? resultClass : JavaPrimitiveTypeMapping.getWrapperType(resultClass);
	}

	private String _getResultClass() {
		if (StringHelper.isNotBlank(this.resultClass)) {
			return this.resultClass;
		} else if (this.columns.size() == 1) {
			return ((Column)this.columns.iterator().next()).getSimpleJavaType();
		} else if (this.isColumnsInSameTable()) {
			Collection<SqlParseHelper.NameWithAlias> tableNames = SqlParseHelper.getTableNamesByQuery(this.executeSql);
			Table t = SqlFactory.getTableFromCache(((SqlParseHelper.NameWithAlias)tableNames.iterator().next()).getName());
			return t.getClassName();
		} else {
			return this.operation == null ? null : StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(this.operation)) + GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_SQL_RESULTCLASS_SUFFIX);
		}
	}

	public void setResultClass(String queryResultClass) {
		this.resultClass = queryResultClass;
	}

	public boolean isHasCustomResultClass() {
		return StringHelper.isNotBlank(this.resultClass);
	}

	public boolean isHasResultMap() {
		return StringHelper.isNotBlank(this.resultMap);
	}

	public String getResultClassName() {
		int lastIndexOf = this.getResultClass().lastIndexOf(".");
		return lastIndexOf >= 0 ? this.getResultClass().substring(lastIndexOf + 1) : this.getResultClass();
	}

	public String getParameterClass() {
		if (StringHelper.isNotBlank(this.parameterClass)) {
			return this.parameterClass;
		} else if (StringHelper.isBlank(this.operation)) {
			return null;
		} else {
			return this.isSelectSql() ? StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(this.operation)) + "Query" : StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(this.operation)) + "Parameter";
		}
	}

	public void setParameterClass(String parameterClass) {
		this.parameterClass = parameterClass;
	}

	public String getParameterClassName() {
		int lastIndexOf = this.getParameterClass().lastIndexOf(".");
		return lastIndexOf >= 0 ? this.getParameterClass().substring(lastIndexOf + 1) : this.getParameterClass();
	}

	public int getColumnsCount() {
		return this.columns.size();
	}

	public void addColumn(Column c) {
		this.columns.add(c);
	}

	public String getOperation() {
		return this.operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getOperationFirstUpper() {
		return StringHelper.capitalize(this.getOperation());
	}

	public String getMultiplicity() {
		return this.multiplicity;
	}

	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}

	public LinkedHashSet<Column> getColumns() {
		return this.columns;
	}

	public void setColumns(LinkedHashSet<Column> columns) {
		this.columns = columns;
	}

	public LinkedHashSet<SqlParameter> getParams() {
		return this.params;
	}

	public void setParams(LinkedHashSet<SqlParameter> params) {
		this.params = params;
	}

	public SqlParameter getParam(String paramName) {
		Iterator i$ = this.getParams().iterator();

		SqlParameter p;
		do {
			if (!i$.hasNext()) {
				return null;
			}

			p = (SqlParameter)i$.next();
		} while(!p.getParamName().equals(paramName));

		return p;
	}

	public String getSourceSql() {
		return this.sourceSql;
	}

	public void setSourceSql(String sourceSql) {
		this.sourceSql = sourceSql;
	}

	public String getSqlmap() {
		return this.getSqlmap(this.getParamNames());
	}

	public void setSqlmap(String sqlmap) {
		if (StringHelper.isNotBlank(sqlmap)) {
			sqlmap = StringHelper.replace(sqlmap, "${cdata-start}", "<![CDATA[");
			sqlmap = StringHelper.replace(sqlmap, "${cdata-end}", "]]>");
		}

		this.sqlmap = sqlmap;
	}

	private List<String> getParamNames() {
		List<String> paramNames = new ArrayList();
		Iterator i$ = this.params.iterator();

		while(i$.hasNext()) {
			SqlParameter p = (SqlParameter)i$.next();
			paramNames.add(p.getParamName());
		}

		return paramNames;
	}

	private String getSqlmap(List<String> params) {
		if (params != null && params.size() != 0) {
			String result = this.sqlmap;
			if (params.size() == 1) {
				return StringHelper.replace(result, "${param1}", "value");
			} else {
				for(int i = 0; i < params.size(); ++i) {
					result = StringHelper.replace(result, "${param" + (i + 1) + "}", (String)params.get(i));
				}

				return result;
			}
		} else {
			return this.sqlmap;
		}
	}

	public boolean isHasSqlMap() {
		return StringHelper.isNotBlank(this.sqlmap);
	}

	public String getResultMap() {
		return this.resultMap;
	}

	public void setResultMap(String resultMap) {
		this.resultMap = resultMap;
	}

	public String getExecuteSql() {
		return this.executeSql;
	}

	public void setExecuteSql(String executeSql) {
		this.executeSql = executeSql;
	}

	public String getCountHql() {
		return toCountSqlForPaging(this.getHql());
	}

	public String getCountSql() {
		return toCountSqlForPaging(this.getSql());
	}

	public String getIbatisCountSql() {
		return toCountSqlForPaging(this.getIbatisSql());
	}

	public String getMybatisCountSql() {
		return toCountSqlForPaging(this.getMybatisSql());
	}

	public String getSqlmapCountSql() {
		return toCountSqlForPaging(this.getSqlmap());
	}

	public String getSql() {
		return this.replaceWildcardWithColumnsSqlName(this.sourceSql);
	}

	public static String toCountSqlForPaging(String sql) {
		if (sql == null) {
			return null;
		} else {
			return SqlTypeChecker.isSelectSql(sql) ? SqlParseHelper.toCountSqlForPaging(sql, "select count(*) ") : sql;
		}
	}

	public String getSpringJdbcSql() {
		return SqlParseHelper.convert2NamedParametersSql(this.getSql(), ":", "");
	}

	public String getHql() {
		return SqlParseHelper.convert2NamedParametersSql(this.getSql(), ":", "");
	}

	public String getIbatisSql() {
		return StringHelper.isBlank(this.ibatisSql) ? SqlParseHelper.convert2NamedParametersSql(this.getSql(), "#", "#") : this.ibatisSql;
	}

	public String getMybatisSql() {
		return StringHelper.isBlank(this.mybatisSql) ? SqlParseHelper.convert2NamedParametersSql(this.getSql(), "#{", "}") : this.mybatisSql;
	}

	public void setIbatisSql(String ibatisSql) {
		this.ibatisSql = ibatisSql;
	}

	public void setMybatisSql(String mybatisSql) {
		this.mybatisSql = mybatisSql;
	}

	private String joinColumnsSqlName() {
		StringBuffer sb = new StringBuffer();
		Iterator it = this.columns.iterator();

		while(it.hasNext()) {
			Column c = (Column)it.next();
			sb.append(c.getSqlName());
			if (it.hasNext()) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

	public String replaceWildcardWithColumnsSqlName(String sql) {
		return SqlTypeChecker.isSelectSql(sql) && SqlParseHelper.getSelect(SqlParseHelper.removeSqlComments(sql)).indexOf("*") >= 0 && SqlParseHelper.getSelect(SqlParseHelper.removeSqlComments(sql)).indexOf("count(") < 0 ? SqlParseHelper.getPrettySql("select " + this.joinColumnsSqlName() + " " + SqlParseHelper.removeSelect(sql)) : sql;
	}

	public List<SqlSegment> getSqlSegments() {
		return this.sqlSegments;
	}

	public void setSqlSegments(List<SqlSegment> includeSqls) {
		this.sqlSegments = includeSqls;
	}

	public SqlSegment getSqlSegment(String id) {
		Iterator i$ = this.sqlSegments.iterator();

		SqlSegment seg;
		do {
			if (!i$.hasNext()) {
				return null;
			}

			seg = (SqlSegment)i$.next();
		} while(!seg.getId().equals(id));

		return seg;
	}

	public List<SqlParameter> getFilterdWithSqlSegmentParams() {
		List<SqlParameter> result = new ArrayList();
		Iterator i$ = this.getParams().iterator();

		while(i$.hasNext()) {
			SqlParameter p = (SqlParameter)i$.next();
			if (!this.isSqlSegementContainsParam(p.getParamName())) {
				result.add(p);
			}
		}

		return result;
	}

	private boolean isSqlSegementContainsParam(String paramName) {
		Iterator i$ = this.getSqlSegments().iterator();

		SqlSegment seg;
		do {
			if (!i$.hasNext()) {
				return false;
			}

			seg = (SqlSegment)i$.next();
		} while(!seg.getParamNames().contains(paramName));

		return true;
	}

	public boolean isSelectSql() {
		return SqlTypeChecker.isSelectSql(this.sourceSql);
	}

	public boolean isUpdateSql() {
		return SqlTypeChecker.isUpdateSql(this.sourceSql);
	}

	public boolean isDeleteSql() {
		return SqlTypeChecker.isDeleteSql(this.sourceSql);
	}

	public boolean isInsertSql() {
		return SqlTypeChecker.isInsertSql(this.sourceSql);
	}

	public String getRemarks() {
		return this.remarks;
	}

	public String getParamType() {
		return this.paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public void setRemarks(String comments) {
		this.remarks = comments;
	}

	public boolean isPaging() {
		return MULTIPLICITY_PAGING.equalsIgnoreCase(this.multiplicity) ? true : this.paging;
	}

	public void setPaging(boolean paging) {
		this.paging = paging;
	}

	public Column getColumnBySqlName(String sqlName) {
		Iterator i$ = this.getColumns().iterator();

		Column c;
		do {
			if (!i$.hasNext()) {
				return null;
			}

			c = (Column)i$.next();
		} while(!c.getSqlName().equalsIgnoreCase(sqlName));

		return c;
	}

	public Column getColumnByName(String name) {
		Column c = this.getColumnBySqlName(name);
		if (c == null) {
			c = this.getColumnBySqlName(StringHelper.toUnderscoreName(name));
		}

		return c;
	}

	public void afterPropertiesSet() {
		Iterator i$ = this.sqlSegments.iterator();

		while(i$.hasNext()) {
			SqlSegment seg = (SqlSegment)i$.next();
			seg.setParams(seg.getParams(this));
		}

	}

	public String toString() {
		return "sourceSql:\n" + this.sourceSql + "\nsql:" + this.getSql();
	}
}
