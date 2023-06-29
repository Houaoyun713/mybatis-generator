//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.db.sql.model;

import enn.base.generator.utils.provider.db.table.model.Column;
import enn.base.generator.utils.util.BeanHelper;
import enn.base.generator.utils.util.StringHelper;
import enn.base.generator.utils.util.typemapping.JavaPrimitiveTypeMapping;

public class SqlParameter extends Column {
	String parameterClass;
	String paramName;
	boolean isListParam = false;

	public SqlParameter() {
	}

	public SqlParameter(Column param) {
		super(param);
		BeanHelper.copyProperties(this, param);
	}

	public SqlParameter(SqlParameter param) {
		super(param);
		this.isListParam = param.isListParam;
		this.paramName = param.paramName;
	}

	public String getParameterClass() {
		return StringHelper.isNotBlank(this.parameterClass) ? this.parameterClass : this.getPossibleShortJavaType();
	}

	public void setParameterClass(String parameterClass) {
		this.parameterClass = parameterClass;
	}

	public String getPreferredParameterJavaType() {
		return this.toListParam(this.getParameterClass());
	}

	String toListParam(String parameterClassName) {
		if (this.isListParam) {
			if (parameterClassName.indexOf("[]") >= 0) {
				return parameterClassName;
			} else if (parameterClassName.indexOf("List") >= 0) {
				return parameterClassName;
			} else {
				return parameterClassName.indexOf("Set") >= 0 ? parameterClassName : "java.util.List<" + JavaPrimitiveTypeMapping.getWrapperType(parameterClassName) + ">";
			}
		} else {
			return JavaPrimitiveTypeMapping.getWrapperType(parameterClassName);
		}
	}

	public String getParamName() {
		return this.paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public boolean isListParam() {
		return this.isListParam;
	}

	public void setListParam(boolean isListParam) {
		this.isListParam = isListParam;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof SqlParameter) {
			SqlParameter other = (SqlParameter)obj;
			return this.paramName.equals(other.getParamName());
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.paramName.hashCode();
	}

	public String toString() {
		return "paramName:" + this.paramName + " preferredParameterJavaType:" + this.getPreferredParameterJavaType();
	}
}
