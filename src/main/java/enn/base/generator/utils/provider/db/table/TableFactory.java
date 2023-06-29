//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.db.table;

import enn.base.generator.utils.GeneratorConstants;
import enn.base.generator.utils.GeneratorProperties;
import enn.base.generator.utils.provider.db.DataSourceProvider;
import enn.base.generator.utils.provider.db.table.model.Column;
import enn.base.generator.utils.provider.db.table.model.Table;
import enn.base.generator.utils.util.BeanHelper;
import enn.base.generator.utils.util.DBHelper;
import enn.base.generator.utils.util.FileHelper;
import enn.base.generator.utils.util.GLogger;
import enn.base.generator.utils.util.StringHelper;
import enn.base.generator.utils.util.XMLHelper;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TableFactory {
	private static TableFactory instance = null;
	private String schema;
	private String catalog;
	private List<TableFactoryListener> tableFactoryListeners = new ArrayList();

	private TableFactory(String schema, String catalog) {
		this.schema = schema;
		this.catalog = catalog;
	}

	public static synchronized TableFactory getInstance() {
		if (instance == null) {
			instance = new TableFactory(GeneratorProperties.getNullIfBlank(GeneratorConstants.JDBC_SCHEMA), GeneratorProperties.getNullIfBlank(GeneratorConstants.JDBC_CATALOG));
		}

		return instance;
	}

	public List<TableFactoryListener> getTableFactoryListeners() {
		return this.tableFactoryListeners;
	}

	public void setTableFactoryListeners(List<TableFactoryListener> tableFactoryListeners) {
		this.tableFactoryListeners = tableFactoryListeners;
	}

	public boolean addTableFactoryListener(TableFactoryListener o) {
		return this.tableFactoryListeners.add(o);
	}

	public void clearTableFactoryListener() {
		this.tableFactoryListeners.clear();
	}

	public boolean removeTableFactoryListener(TableFactoryListener o) {
		return this.tableFactoryListeners.remove(o);
	}

	public String getCatalog() {
		return this.catalog;
	}

	public String getSchema() {
		return this.schema;
	}

	public List getAllTables() {
		Connection conn = DataSourceProvider.getConnection();

		try {
			List<Table> tables = (new TableCreateProcessor(conn, this.getSchema(), this.getCatalog())).getAllTables();
			Iterator i$ = tables.iterator();

			while(i$.hasNext()) {
				Table t = (Table)i$.next();
				this.dispatchOnTableCreatedEvent(t);
			}

			List var10 = tables;
			return var10;
		} catch (Exception var8) {
			throw new RuntimeException(var8);
		} finally {
			DBHelper.close(conn);
		}
	}

	private void dispatchOnTableCreatedEvent(Table t) {
		Iterator i$ = this.tableFactoryListeners.iterator();

		while(i$.hasNext()) {
			TableFactoryListener listener = (TableFactoryListener)i$.next();
			listener.onTableCreated(t);
		}

	}

	public Table getTable(String tableName) {
		return this.getTable(this.getSchema(), tableName);
	}

	private Table getTable(String schema, String tableName) {
		return this.getTable(this.getCatalog(), schema, tableName);
	}

	private Table getTable(String catalog, String schema, String tableName) {
		Table t = null;

		try {
			t = this._getTable(catalog, schema, tableName);
			if (t == null && !tableName.equals(tableName.toUpperCase())) {
				t = this._getTable(catalog, schema, tableName.toUpperCase());
			}

			if (t == null && !tableName.equals(tableName.toLowerCase())) {
				t = this._getTable(catalog, schema, tableName.toLowerCase());
			}
		} catch (Exception var10) {
			throw new RuntimeException(var10);
		}

		if (t == null) {
			Connection conn = DataSourceProvider.getConnection();

			try {
				throw new NotFoundTableException("not found table with give name:" + tableName + (DatabaseMetaDataUtils.isOracleDataBase(DatabaseMetaDataUtils.getMetaData(conn)) ? " \n databaseStructureInfo:" + DatabaseMetaDataUtils.getDatabaseStructureInfo(DatabaseMetaDataUtils.getMetaData(conn), schema, catalog) : "") + "\n current " + DataSourceProvider.getDataSource() + " current schema:" + this.getSchema() + " current catalog:" + this.getCatalog());
			} finally {
				DBHelper.close(conn);
			}
		} else {
			this.dispatchOnTableCreatedEvent(t);
			return t;
		}
	}

	private Table _getTable(String catalog, String schema, String tableName) throws SQLException {
		if (tableName != null && tableName.trim().length() != 0) {
			catalog = StringHelper.defaultIfEmpty(catalog, (String)null);
			schema = StringHelper.defaultIfEmpty(schema, (String)null);
			Connection conn = DataSourceProvider.getConnection();
			DatabaseMetaData dbMetaData = conn.getMetaData();
			ResultSet rs = dbMetaData.getTables(catalog, schema, tableName, (String[])null);

			Table var8;
			try {
				if (!rs.next()) {
					return null;
				}

				Table table = (new TableCreateProcessor(conn, this.getSchema(), this.getCatalog())).createTable(rs);
				var8 = table;
			} finally {
				DBHelper.close(conn, rs);
			}

			return var8;
		} else {
			throw new IllegalArgumentException("tableName must be not empty");
		}
	}

	public static class DatabaseMetaDataUtils {
		public DatabaseMetaDataUtils() {
		}

		public static boolean isOracleDataBase(DatabaseMetaData metadata) {
			try {
				boolean ret = false;
				ret = metadata.getDatabaseProductName().toLowerCase().indexOf("oracle") != -1;
				return ret;
			} catch (SQLException var2) {
				return false;
			}
		}

		public static boolean isHsqlDataBase(DatabaseMetaData metadata) {
			try {
				boolean ret = false;
				ret = metadata.getDatabaseProductName().toLowerCase().indexOf("hsql") != -1;
				return ret;
			} catch (SQLException var2) {
				return false;
			}
		}

		public static boolean isMysqlDataBase(DatabaseMetaData metadata) {
			try {
				boolean ret = false;
				ret = metadata.getDatabaseProductName().toLowerCase().indexOf("mysql") != -1;
				return ret;
			} catch (SQLException var2) {
				return false;
			}
		}

		public static DatabaseMetaData getMetaData(Connection connection) {
			try {
				return connection.getMetaData();
			} catch (SQLException var2) {
				throw new RuntimeException("cannot get DatabaseMetaData", var2);
			}
		}

		public static String getDatabaseStructureInfo(DatabaseMetaData metadata, String schema, String catalog) {
			ResultSet schemaRs = null;
			ResultSet catalogRs = null;
			String nl = System.getProperty("line.separator");
			StringBuffer sb = new StringBuffer(nl);
			sb.append("Configured schema:").append(schema).append(nl);
			sb.append("Configured catalog:").append(catalog).append(nl);

			try {
				schemaRs = metadata.getSchemas();
				sb.append("Available schemas:").append(nl);

				while(schemaRs.next()) {
					sb.append("  ").append(schemaRs.getString("TABLE_SCHEM")).append(nl);
				}
			} catch (SQLException var20) {
				GLogger.warn("Couldn't get schemas", var20);
				sb.append("  ?? Couldn't get schemas ??").append(nl);
			} finally {
				DBHelper.close(schemaRs);
			}

			try {
				catalogRs = metadata.getCatalogs();
				sb.append("Available catalogs:").append(nl);

				while(catalogRs.next()) {
					sb.append("  ").append(catalogRs.getString("TABLE_CAT")).append(nl);
				}
			} catch (SQLException var18) {
				GLogger.warn("Couldn't get catalogs", var18);
				sb.append("  ?? Couldn't get catalogs ??").append(nl);
			} finally {
				DBHelper.close(catalogRs);
			}

			return sb.toString();
		}
	}

	static class ExecuteSqlHelper {
		ExecuteSqlHelper() {
		}

		public static String queryForString(Connection conn, String sql) {
			Statement s = null;
			ResultSet rs = null;

			String var4;
			try {
				s = conn.createStatement();
				rs = s.executeQuery(sql);
				if (rs.next()) {
					var4 = rs.getString(1);
					return var4;
				}

				var4 = null;
			} catch (SQLException var9) {
				var9.printStackTrace();
				Object var5 = null;
				return (String)var5;
			} finally {
				DBHelper.close((Connection)null, s, rs);
			}

			return var4;
		}
	}

	public static class TableOverrideValuesProvider {
		public TableOverrideValuesProvider() {
		}

		private static Map getTableConfigValues(String tableSqlName) {
			XMLHelper.NodeData nd = getTableConfigXmlNodeData(tableSqlName);
			if (nd == null) {
				return new HashMap();
			} else {
				return (Map)(nd == null ? new HashMap() : nd.attributes);
			}
		}

		private static Map getColumnConfigValues(Table table, Column column) {
			XMLHelper.NodeData root = getTableConfigXmlNodeData(table.getSqlName());
			if (root != null) {
				Iterator i$ = root.childs.iterator();

				while(i$.hasNext()) {
					XMLHelper.NodeData item = (XMLHelper.NodeData)i$.next();
					if (item.nodeName.equals("column") && column.getSqlName().equalsIgnoreCase((String)item.attributes.get("sqlName"))) {
						return item.attributes;
					}
				}
			}

			return new HashMap();
		}

		private static XMLHelper.NodeData getTableConfigXmlNodeData(String tableSqlName) {
			XMLHelper.NodeData nd = getTableConfigXmlNodeData0(tableSqlName);
			if (nd == null) {
				nd = getTableConfigXmlNodeData0(tableSqlName.toLowerCase());
				if (nd == null) {
					nd = getTableConfigXmlNodeData0(tableSqlName.toUpperCase());
				}
			}

			return nd;
		}

		private static XMLHelper.NodeData getTableConfigXmlNodeData0(String tableSqlName) {
			try {
				File file = FileHelper.getFileByClassLoader("generator_config/table/" + tableSqlName + ".xml");
				GLogger.trace("getTableConfigXml() load nodeData by tableSqlName:" + tableSqlName + ".xml");
				return (new XMLHelper()).parseXML(file);
			} catch (Exception var2) {
				GLogger.trace("not found config xml for table:" + tableSqlName + ", exception:" + var2);
				return null;
			}
		}
	}

	public static class TableCreateProcessor {
		private Connection connection;
		private String catalog;
		private String schema;

		public String getCatalog() {
			return this.catalog;
		}

		public String getSchema() {
			return this.schema;
		}

		public TableCreateProcessor(Connection connection, String schema, String catalog) {
			this.connection = connection;
			this.schema = schema;
			this.catalog = catalog;
		}

		public Table createTable(ResultSet rs) throws SQLException {
			long start = System.currentTimeMillis();
			String tableName = null;

			Table var16;
			try {
				ResultSetMetaData rsMetaData = rs.getMetaData();
				if (rs.getString("TABLE_SCHEM") == null) {
					String var10000 = "";
				} else {
					rs.getString("TABLE_SCHEM");
				}

				tableName = rs.getString("TABLE_NAME");
				String tableType = rs.getString("TABLE_TYPE");
				String remarks = rs.getString("REMARKS");
				if (remarks == null && DatabaseMetaDataUtils.isOracleDataBase(this.connection.getMetaData())) {
					remarks = this.getOracleTableComments(tableName);
				}

				Table table = new Table();
				table.setSchema(this.schema);
				table.setCatalog(this.catalog);
				table.setSqlName(tableName);
				table.setRemarks(remarks);
				if ("SYNONYM".equals(tableType) && DatabaseMetaDataUtils.isOracleDataBase(this.connection.getMetaData())) {
					String[] ownerAndTableName = this.getSynonymOwnerAndTableName(tableName);
					table.setOwnerSynonymName(ownerAndTableName[0]);
					table.setTableSynonymName(ownerAndTableName[1]);
				}

				this.retriveTableColumns(table);
				table.initExportedKeys(this.connection.getMetaData());
				table.initImportedKeys(this.connection.getMetaData());
				BeanHelper.copyProperties(table, TableOverrideValuesProvider.getTableConfigValues(table.getSqlName()));
				var16 = table;
			} catch (SQLException var14) {
				throw new RuntimeException("create table object error,tableName:" + tableName, var14);
			} finally {
				GLogger.perf("createTable() cost:" + (System.currentTimeMillis() - start) + " tableName:" + tableName);
			}

			return var16;
		}

		private List<Table> getAllTables() throws SQLException {
			DatabaseMetaData dbMetaData = this.connection.getMetaData();
			ResultSet rs = dbMetaData.getTables(this.getCatalog(), this.getSchema(), (String)null, (String[])null);

			try {
				ArrayList tables = new ArrayList();

				while(rs.next()) {
					tables.add(this.createTable(rs));
				}

				ArrayList var4 = tables;
				return var4;
			} finally {
				DBHelper.close(rs);
			}
		}

		private String[] getSynonymOwnerAndTableName(String synonymName) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			String[] ret = new String[2];

			try {
				ps = this.connection.prepareStatement("select table_owner,table_name from sys.all_synonyms where synonym_name=? and owner=?");
				ps.setString(1, synonymName);
				ps.setString(2, this.getSchema());
				rs = ps.executeQuery();
				if (!rs.next()) {
					String databaseStructure = DatabaseMetaDataUtils.getDatabaseStructureInfo(this.getMetaData(), this.schema, this.catalog);
					throw new RuntimeException("Wow! Synonym " + synonymName + " not found. How can it happen? " + databaseStructure);
				}

				ret[0] = rs.getString(1);
				ret[1] = rs.getString(2);
			} catch (SQLException var10) {
				String databaseStructure = DatabaseMetaDataUtils.getDatabaseStructureInfo(this.getMetaData(), this.schema, this.catalog);
				GLogger.error(var10.getMessage(), var10);
				throw new RuntimeException("Exception in getting synonym owner " + databaseStructure);
			} finally {
				DBHelper.close((Connection)null, ps, rs);
			}

			return ret;
		}

		private DatabaseMetaData getMetaData() {
			return DatabaseMetaDataUtils.getMetaData(this.connection);
		}

		private void retriveTableColumns(Table table) throws SQLException {
			GLogger.trace("-------setColumns(" + table.getSqlName() + ")");
			List primaryKeys = this.getTablePrimaryKeys(table);
			table.setPrimaryKeyColumns(primaryKeys);
			List indices = new LinkedList();
			Map uniqueIndices = new HashMap();
			Map uniqueColumns = new HashMap();
			ResultSet indexRs = null;

			try {
				if (table.getOwnerSynonymName() != null) {
					indexRs = this.getMetaData().getIndexInfo(this.getCatalog(), table.getOwnerSynonymName(), table.getTableSynonymName(), false, true);
				} else {
					indexRs = this.getMetaData().getIndexInfo(this.getCatalog(), this.getSchema(), table.getSqlName(), false, true);
				}

				while(indexRs.next()) {
					String columnName = indexRs.getString("COLUMN_NAME");
					if (columnName != null) {
						GLogger.trace("index:" + columnName);
						indices.add(columnName);
					}

					String indexName = indexRs.getString("INDEX_NAME");
					boolean nonUnique = indexRs.getBoolean("NON_UNIQUE");
					if (!nonUnique && columnName != null && indexName != null) {
						List l = (List)uniqueColumns.get(indexName);
						if (l == null) {
							l = new ArrayList();
							uniqueColumns.put(indexName, l);
						}

						((List)l).add(columnName);
						uniqueIndices.put(columnName, indexName);
						GLogger.trace("unique:" + columnName + " (" + indexName + ")");
					}
				}
			} catch (Throwable var14) {
				;
			} finally {
				DBHelper.close(indexRs);
			}

			List columns = this.getTableColumns(table, primaryKeys, indices, uniqueIndices, uniqueColumns);
			Iterator i = columns.iterator();

			while(i.hasNext()) {
				Column column = (Column)i.next();
				table.addColumn(column);
			}

			if (primaryKeys.size() == 0) {
				GLogger.warn("WARNING: The JDBC driver didn't report any primary key columns in " + table.getSqlName());
			}

		}

		private List getTableColumns(Table table, List primaryKeys, List indices, Map uniqueIndices, Map uniqueColumns) throws SQLException {
			List columns = new LinkedList();
			ResultSet columnRs = this.getColumnsResultSet(table);

			try {
				while(columnRs.next()) {
					int sqlType = columnRs.getInt("DATA_TYPE");
					String sqlTypeName = columnRs.getString("TYPE_NAME");
					String columnName = columnRs.getString("COLUMN_NAME");
					String columnDefaultValue = columnRs.getString("COLUMN_DEF");
					String remarks = columnRs.getString("REMARKS");
					if (remarks == null && DatabaseMetaDataUtils.isOracleDataBase(this.connection.getMetaData())) {
						remarks = this.getOracleColumnComments(table.getSqlName(), columnName);
					}

					boolean isNullable = 1 == columnRs.getInt("NULLABLE");
					int size = columnRs.getInt("COLUMN_SIZE");
					int decimalDigits = columnRs.getInt("DECIMAL_DIGITS");
					boolean isPk = primaryKeys.contains(columnName);
					boolean isIndexed = indices.contains(columnName);
					String uniqueIndex = (String)uniqueIndices.get(columnName);
					List columnsInUniqueIndex = null;
					if (uniqueIndex != null) {
						columnsInUniqueIndex = (List)uniqueColumns.get(uniqueIndex);
					}

					boolean isUnique = columnsInUniqueIndex != null && columnsInUniqueIndex.size() == 1;
					if (isUnique) {
						GLogger.trace("unique column:" + columnName);
					}

					Column column = new Column(table, sqlType, sqlTypeName, columnName, size, decimalDigits, isPk, isNullable, isIndexed, isUnique, columnDefaultValue, remarks);
					BeanHelper.copyProperties(column, TableOverrideValuesProvider.getColumnConfigValues(table, column));
					columns.add(column);
				}
			} finally {
				DBHelper.close(columnRs);
			}

			return columns;
		}

		private ResultSet getColumnsResultSet(Table table) throws SQLException {
			ResultSet columnRs = null;
			if (table.getOwnerSynonymName() != null) {
				columnRs = this.getMetaData().getColumns(this.getCatalog(), table.getOwnerSynonymName(), table.getTableSynonymName(), (String)null);
			} else {
				columnRs = this.getMetaData().getColumns(this.getCatalog(), this.getSchema(), table.getSqlName(), (String)null);
			}

			return columnRs;
		}

		private List<String> getTablePrimaryKeys(Table table) throws SQLException {
			List primaryKeys = new LinkedList();
			ResultSet primaryKeyRs = null;

			try {
				if (table.getOwnerSynonymName() != null) {
					primaryKeyRs = this.getMetaData().getPrimaryKeys(this.getCatalog(), table.getOwnerSynonymName(), table.getTableSynonymName());
				} else {
					primaryKeyRs = this.getMetaData().getPrimaryKeys(this.getCatalog(), this.getSchema(), table.getSqlName());
				}

				while(primaryKeyRs.next()) {
					String columnName = primaryKeyRs.getString("COLUMN_NAME");
					GLogger.trace("primary key:" + columnName);
					primaryKeys.add(columnName);
				}
			} finally {
				DBHelper.close(primaryKeyRs);
			}

			return primaryKeys;
		}

		private String getOracleTableComments(String table) {
			String sql = "SELECT comments FROM user_tab_comments WHERE table_name='" + table + "'";
			return ExecuteSqlHelper.queryForString(this.connection, sql);
		}

		private String getOracleColumnComments(String table, String column) {
			String sql = "SELECT comments FROM user_col_comments WHERE table_name='" + table + "' AND column_name = '" + column + "'";
			return ExecuteSqlHelper.queryForString(this.connection, sql);
		}
	}

	public static class NotFoundTableException extends RuntimeException {
		private static final long serialVersionUID = 5976869128012158628L;

		public NotFoundTableException(String message) {
			super(message);
		}
	}
}
