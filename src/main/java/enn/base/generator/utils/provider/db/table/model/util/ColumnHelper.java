//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.db.table.model.util;

import enn.base.generator.utils.provider.db.table.model.Column;
import enn.base.generator.utils.util.StringHelper;
import enn.base.generator.utils.util.typemapping.DatabaseDataTypesUtils;

public class ColumnHelper {
	public ColumnHelper() {
	}

	public static String[] removeHibernateValidatorSpecialTags(String str) {
		return str != null && str.trim().length() != 0 ? str.trim().replaceAll("@", "").replaceAll("\\(.*?\\)", "").trim().split("\\s+") : new String[0];
	}

	public static String getHibernateValidatorExpression(Column c) {
		if (!c.isPk() && !c.isNullable()) {
			return DatabaseDataTypesUtils.isString(c.getJavaType()) ? "@NotBlank " + getNotRequiredHibernateValidatorExpression(c) : "@NotNull " + getNotRequiredHibernateValidatorExpression(c);
		} else {
			return getNotRequiredHibernateValidatorExpression(c);
		}
	}

	public static String getNotRequiredHibernateValidatorExpression(Column c) {
		String result = "";
		if (c.getSqlName().indexOf("mail") >= 0) {
			result = result + "@Email ";
		}

		if (DatabaseDataTypesUtils.isString(c.getJavaType()) && c.getSize() > 0) {
			result = result + String.format("@Length(max=%s)", c.getSize());
		}

		if (DatabaseDataTypesUtils.isIntegerNumber(c.getJavaType())) {
			String javaType = DatabaseDataTypesUtils.getPreferredJavaType(c.getSqlType(), c.getSize(), c.getDecimalDigits());
			if (javaType.toLowerCase().indexOf("short") >= 0) {
				result = result + " @Max(32767)";
			} else if (javaType.toLowerCase().indexOf("byte") >= 0) {
				result = result + " @Max(127)";
			} else if (c.getSize() > 0) {
				try {
					long maxValue = Long.parseLong(StringHelper.repeat("9", c.getSize()));
					result = result + " @Max(" + maxValue + "L)";
				} catch (NumberFormatException var5) {
					result = result + " @Max(9223372036854775807L)";
				}
			}
		}

		return result.trim();
	}

	public static String getRapidValidation(Column c) {
		String result = "";
		if (c.getSqlName().indexOf("mail") >= 0) {
			result = result + "validate-email ";
		}

		if (DatabaseDataTypesUtils.isFloatNumber(c.getJavaType())) {
			result = result + "validate-number ";
		}

		if (DatabaseDataTypesUtils.isIntegerNumber(c.getJavaType())) {
			result = result + "validate-integer ";
			if (c.getJavaType().toLowerCase().indexOf("short") >= 0) {
				result = result + "max-value-32767";
			} else if (c.getJavaType().toLowerCase().indexOf("integer") >= 0) {
				result = result + "max-value-2147483647";
			} else if (c.getJavaType().toLowerCase().indexOf("byte") >= 0) {
				result = result + "max-value-127";
			}
		}

		return result;
	}
}
