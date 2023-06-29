//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.sqlerrorcode;

import enn.base.generator.utils.util.DBHelper;
import enn.base.generator.utils.util.GLogger;
import enn.base.generator.utils.util.PatternMatchHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.sql.DataSource;

public class SQLErrorCodesFactory {
	private static final SQLErrorCodesFactory instance = new SQLErrorCodesFactory();
	private final Map<String, SQLErrorCodes> errorCodesMap = new LinkedHashMap();
	private final Map<DataSource, SQLErrorCodes> dataSourceCache = new WeakHashMap(16);

	public static SQLErrorCodesFactory getInstance() {
		return instance;
	}

	public SQLErrorCodesFactory() {
		this.errorCodesMap.put("DB2", this.newSQLErrorCodes(false, "DB2*", "-407,-530,-531,-532,-543,-544,-545,-603,-667"));
		this.errorCodesMap.put("Derby", this.newSQLErrorCodes(true, "Apache Derby", "22001,22005,23502,23503,23513,X0Y32"));
		this.errorCodesMap.put("H2", this.newSQLErrorCodes(false, "H2", "22003,22012,22025,23000,23001,23002,23003"));
		this.errorCodesMap.put("HSQL", this.newSQLErrorCodes(false, "HSQL Database Engine", "-9"));
		this.errorCodesMap.put("Informix", this.newSQLErrorCodes(false, "Informix", "-692,-11030"));
		this.errorCodesMap.put("MS-SQL", this.newSQLErrorCodes(false, "Microsoft SQL Server", "2627,8114,8115"));
		this.errorCodesMap.put("MySQL", this.newSQLErrorCodes(false, "MySQL", "1217,1218,1452,1453,1062,1406,1048,630,839,840,893,1169,1215,1216,1217,1218,1451,1452,1453,1557,1264"));
		this.errorCodesMap.put("Oracle", this.newSQLErrorCodes(false, "Oracle", "2291,2292,1400,1722,12899,1,2290,1461,1438"));
		this.errorCodesMap.put("PostgreSQL", this.newSQLErrorCodes(true, "PostgreSQL", "23001,23503,23514"));
		this.errorCodesMap.put("Sybase", this.newSQLErrorCodes(false, "Sybase SQL Server,SQL Server,Adaptive Server Enterprise,ASE,sql server", "233,423,511,515,530,547,2615,2714"));
	}

	public SQLErrorCodes newSQLErrorCodes(boolean useStateCodeForTranslation, String databaseProductNames, String dataIntegrityViolationCodes) {
		SQLErrorCodes r = new SQLErrorCodes();
		r.setDatabaseProductNames(databaseProductNames.split(","));
		r.setDataIntegrityViolationCodes(dataIntegrityViolationCodes.split(","));
		r.setUseSqlStateForTranslation(useStateCodeForTranslation);
		return r;
	}

	public SQLErrorCodes getErrorCodes(String dbName) {
		if (dbName == null) {
			throw new IllegalArgumentException("Database product name must not be null");
		} else {
			SQLErrorCodes sec = (SQLErrorCodes)this.errorCodesMap.get(dbName);
			if (sec == null) {
				Iterator i$ = this.errorCodesMap.values().iterator();

				while(i$.hasNext()) {
					SQLErrorCodes candidate = (SQLErrorCodes)i$.next();
					if (PatternMatchHelper.simpleMatch(candidate.getDatabaseProductNames(), dbName)) {
						sec = candidate;
						break;
					}
				}
			}

			if (sec != null) {
				GLogger.debug("SQL error codes for '" + dbName + "' found");
				return sec;
			} else {
				GLogger.debug("SQL error codes for '" + dbName + "' not found");
				return new SQLErrorCodes();
			}
		}
	}

	public SQLErrorCodes getErrorCodes(DataSource dataSource) {
		Map var2 = this.dataSourceCache;
		synchronized(this.dataSourceCache) {
			SQLErrorCodes sec = (SQLErrorCodes)this.dataSourceCache.get(dataSource);
			if (sec != null) {
				return sec;
			} else {
				Connection conn = null;

				try {
					conn = dataSource.getConnection();
					String dbName = conn.getMetaData().getDatabaseProductName();
					if (dbName != null) {
						GLogger.debug("Database product name cached for DataSource [" + dataSource.getClass().getName() + '@' + Integer.toHexString(dataSource.hashCode()) + "]: name is '" + dbName + "'");
						sec = this.getErrorCodes(dbName);
						this.dataSourceCache.put(dataSource, sec);
						SQLErrorCodes var10000 = sec;
						return var10000;
					} else {
						return new SQLErrorCodes();
					}
				} catch (SQLException var7) {
					DBHelper.close(conn);
					throw new IllegalStateException("canot getErrorCodes by dataSource", var7);
				}
			}
		}
	}

	public String getDatabaseType(DataSource ds) {
		Connection conn = null;

		try {
			conn = ds.getConnection();
			String dbName = conn.getMetaData().getDatabaseProductName();
			Iterator i$ = this.errorCodesMap.keySet().iterator();

			String database;
			SQLErrorCodes candidate;
			do {
				if (!i$.hasNext()) {
					return null;
				}

				database = (String)i$.next();
				candidate = (SQLErrorCodes)this.errorCodesMap.get(database);
			} while(!database.equals(dbName) && !PatternMatchHelper.simpleMatch(candidate.getDatabaseProductNames(), dbName));

			return database;
		} catch (SQLException var7) {
			DBHelper.close(conn);
			throw new IllegalStateException("canot get database type by dataSource", var7);
		}
	}
}
