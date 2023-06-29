//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.sqlerrorcode;

import enn.base.generator.utils.GeneratorConstants;
import enn.base.generator.utils.GeneratorProperties;
import java.sql.SQLException;
import java.util.Arrays;
import javax.sql.DataSource;

public class SQLErrorCodeSQLExceptionTranslator {
	private SQLErrorCodes sqlErrorCodes;

	public SQLErrorCodeSQLExceptionTranslator() {
	}

	public SQLErrorCodeSQLExceptionTranslator(DataSource dataSource) {
		this.setDataSource(dataSource);
	}

	public SQLErrorCodeSQLExceptionTranslator(String dbName) {
		this.setDatabaseProductName(dbName);
	}

	public SQLErrorCodeSQLExceptionTranslator(SQLErrorCodes sec) {
		this.sqlErrorCodes = sec;
	}

	public void setDataSource(DataSource dataSource) {
		this.sqlErrorCodes = SQLErrorCodesFactory.getInstance().getErrorCodes(dataSource);
	}

	public void setDatabaseProductName(String dbName) {
		this.sqlErrorCodes = SQLErrorCodesFactory.getInstance().getErrorCodes(dbName);
	}

	public void setSqlErrorCodes(SQLErrorCodes sec) {
		this.sqlErrorCodes = sec;
	}

	public SQLErrorCodes getSqlErrorCodes() {
		return this.sqlErrorCodes;
	}

	public boolean isDataIntegrityViolation(SQLException e) {
		if (this.sqlErrorCodes != null) {
			String errorCode = null;
			if (this.sqlErrorCodes.isUseSqlStateForTranslation()) {
				errorCode = e.getSQLState();
			} else {
				errorCode = Integer.toString(e.getErrorCode());
			}

			if (this.ignoreByCustom(errorCode)) {
				return true;
			}

			if (errorCode != null) {
				if (Arrays.asList(GeneratorProperties.getStringArray(GeneratorConstants.SQLPARSE_IGNORE_SQL_EXCEPTION_ERROR_CODES)).contains(errorCode)) {
					return true;
				}

				if (Arrays.asList(this.sqlErrorCodes.getDataIntegrityViolationCodes()).contains(errorCode)) {
					return true;
				}
			}
		}

		return false;
	}

	protected boolean ignoreByCustom(String errorCode) {
		return false;
	}

	public static SQLErrorCodeSQLExceptionTranslator getSQLErrorCodeSQLExceptionTranslator(DataSource ds) {
		SQLErrorCodeSQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator();
		translator.setDataSource(ds);
		return translator;
	}
}
