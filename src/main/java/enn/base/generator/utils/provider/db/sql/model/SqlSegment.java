//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.db.sql.model;

import enn.base.generator.utils.util.StringHelper;
import enn.base.generator.utils.util.sqlparse.NamedParameterUtils;
import enn.base.generator.utils.util.sqlparse.ParsedSql;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SqlSegment {
	public String id;
	public String rawIncludeSql;
	public String parsedIncludeSql;
	public Set<SqlParameter> params;

	public SqlSegment() {
	}

	public SqlSegment(String id, String rawIncludeSql, String parsedIncludeSql) {
		this.setId(id);
		this.rawIncludeSql = rawIncludeSql;
		this.parsedIncludeSql = parsedIncludeSql;
	}

	public Set<SqlParameter> getParams(Sql sql) {
		Set<SqlParameter> result = new LinkedHashSet();
		Iterator i$ = this.getParamNames().iterator();

		while(i$.hasNext()) {
			String paramName = (String)i$.next();
			SqlParameter p = sql.getParam(paramName);
			if (p == null) {
				throw new IllegalArgumentException("not found param on sql:" + this.parsedIncludeSql + " with name:" + paramName + " for sqlSegment:" + this.id);
			}

			result.add(p);
		}

		return result;
	}

	public List<String> getParamNames() {
		ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(this.parsedIncludeSql);
		return parsedSql.getParameterNames();
	}

	public String getClassName() {
		return StringHelper.toJavaClassName(this.id.replace(".", "_").replace("-", "_"));
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		if (StringHelper.isBlank(id)) {
			throw new IllegalArgumentException("id must be not blank");
		} else {
			this.id = id;
		}
	}

	public String getRawIncludeSql() {
		return this.rawIncludeSql;
	}

	public void setRawIncludeSql(String rawIncludeSql) {
		this.rawIncludeSql = rawIncludeSql;
	}

	public String getParsedIncludeSql() {
		return this.parsedIncludeSql;
	}

	public void setParsedIncludeSql(String parsedIncludeSql) {
		this.parsedIncludeSql = parsedIncludeSql;
	}

	public Set<SqlParameter> getParams() {
		return this.params;
	}

	public void setParams(Set<SqlParameter> params) {
		this.params = params;
	}

	public int hashCode() {
		return this.id.hashCode();
	}

	public boolean isGenerateParameterObject() {
		return this.getParamNames().size() > 1;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (this.getClass() != obj.getClass()) {
			return false;
		} else {
			SqlSegment other = (SqlSegment)obj;
			if (this.id == null) {
				if (other.id != null) {
					return false;
				}
			} else if (!this.id.equals(other.id)) {
				return false;
			}

			return true;
		}
	}
}
