//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils;

import enn.base.generator.utils.provider.db.sql.model.Sql;
import enn.base.generator.utils.provider.db.table.TableFactory;
import enn.base.generator.utils.provider.db.table.model.Table;
import enn.base.generator.utils.provider.java.model.JavaClass;
import enn.base.generator.utils.util.BeanHelper;
import enn.base.generator.utils.util.ClassHelper;
import enn.base.generator.utils.util.GLogger;
import enn.base.generator.utils.util.GeneratorException;
import enn.base.generator.utils.util.StringHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneratorFacade {
	private Generator generator = new Generator();

	public GeneratorFacade() {
		if (StringHelper.isNotBlank(GeneratorProperties.getProperty("outRoot"))) {
			this.generator.setOutRootDir(GeneratorProperties.getProperty("outRoot"));
		}

	}

	public static void printAllTableNames() throws Exception {
		PrintUtils.printAllTableNames(TableFactory.getInstance().getAllTables());
	}

	public void deleteOutRootDir() throws IOException {
		this.generator.deleteOutRootDir();
	}

	public void generateByMap(Map... maps) throws Exception {
		Map[] arr$ = maps;
		int len$ = maps.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			Map map = arr$[i$];
			(new ProcessUtils()).processByMap(map, false);
		}

	}

	public void deleteByMap(Map... maps) throws Exception {
		Map[] arr$ = maps;
		int len$ = maps.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			Map map = arr$[i$];
			(new ProcessUtils()).processByMap(map, true);
		}

	}

	public void generateBy(Generator.GeneratorModel... models) throws Exception {
		Generator.GeneratorModel[] arr$ = models;
		int len$ = models.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			Generator.GeneratorModel model = arr$[i$];
			(new ProcessUtils()).processByGeneratorModel(model, false);
		}

	}

	public void deleteBy(Generator.GeneratorModel... models) throws Exception {
		Generator.GeneratorModel[] arr$ = models;
		int len$ = models.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			Generator.GeneratorModel model = arr$[i$];
			(new ProcessUtils()).processByGeneratorModel(model, true);
		}

	}

	public void generateByAllTable() throws Exception {
		(new ProcessUtils()).processByAllTable(false);
	}

	public void deleteByAllTable() throws Exception {
		(new ProcessUtils()).processByAllTable(true);
	}

	public void generateByTable(String... tableNames) throws Exception {
		String[] arr$ = tableNames;
		int len$ = tableNames.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			String tableName = arr$[i$];
			(new ProcessUtils()).processByTable(tableName, false);
		}

	}

	public void deleteByTable(String... tableNames) throws Exception {
		String[] arr$ = tableNames;
		int len$ = tableNames.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			String tableName = arr$[i$];
			(new ProcessUtils()).processByTable(tableName, true);
		}

	}

	public void generateByClass(Class... clazzes) throws Exception {
		Class[] arr$ = clazzes;
		int len$ = clazzes.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			Class clazz = arr$[i$];
			(new ProcessUtils()).processByClass(clazz, false);
		}

	}

	public void deleteByClass(Class... clazzes) throws Exception {
		Class[] arr$ = clazzes;
		int len$ = clazzes.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			Class clazz = arr$[i$];
			(new ProcessUtils()).processByClass(clazz, true);
		}

	}

	public void generateBySql(Sql... sqls) throws Exception {
		Sql[] arr$ = sqls;
		int len$ = sqls.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			Sql sql = arr$[i$];
			(new ProcessUtils()).processBySql(sql, false);
		}

	}

	public void deleteBySql(Sql... sqls) throws Exception {
		Sql[] arr$ = sqls;
		int len$ = sqls.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			Sql sql = arr$[i$];
			(new ProcessUtils()).processBySql(sql, true);
		}

	}

	public Generator getGenerator() {
		return this.generator;
	}

	public void setGenerator(Generator generator) {
		this.generator = generator;
	}

	private static class PrintUtils {
		private PrintUtils() {
		}

		private static void printExceptionsSumary(String msg, String outRoot, List<Exception> exceptions) throws FileNotFoundException {
			File errorFile = new File(outRoot, "generator_error.log");
			if (exceptions != null && exceptions.size() > 0) {
				System.err.println("[Generate Error Summary] : " + msg);
				errorFile.getParentFile().mkdirs();
				PrintStream output = new PrintStream(new FileOutputStream(errorFile));

				for(int i = 0; i < exceptions.size(); ++i) {
					Exception e = (Exception)exceptions.get(i);
					System.err.println("[GENERATE ERROR]:" + e);
					if (i == 0) {
						e.printStackTrace();
					}

					e.printStackTrace(output);
				}

				output.close();
				System.err.println("***************************************************************");
				System.err.println("* * 输出目录已经生成generator_error.log用于查看错误 ");
				System.err.println("***************************************************************");
			}

		}

		private static void printBeginProcess(String displayText, boolean isDatele) {
			GLogger.println("***************************************************************");
			GLogger.println("* BEGIN " + (isDatele ? " delete by " : " generate by ") + displayText);
			GLogger.println("***************************************************************");
		}

		public static void printAllTableNames(List<Table> tables) throws Exception {
			GLogger.println("\n----All TableNames BEGIN----");

			for(int i = 0; i < tables.size(); ++i) {
				String sqlName = ((Table)tables.get(i)).getSqlName();
				GLogger.println(sqlName);
			}

			GLogger.println("----All TableNames END----");
		}
	}

	public static class GeneratorModelUtils {
		public GeneratorModelUtils() {
		}

		public static Generator.GeneratorModel newGeneratorModel(String key, Object valueObject) {
			Generator.GeneratorModel gm = newDefaultGeneratorModel();
			gm.templateModel.put(key, valueObject);
			gm.filePathModel.putAll(BeanHelper.describe(valueObject, new String[0]));
			return gm;
		}

		public static Generator.GeneratorModel newFromMap(Map params) {
			Generator.GeneratorModel gm = newDefaultGeneratorModel();
			gm.templateModel.putAll(params);
			gm.filePathModel.putAll(params);
			return gm;
		}

		public static Generator.GeneratorModel newDefaultGeneratorModel() {
			Map templateModel = new HashMap();
			templateModel.putAll(getShareVars());
			Map filePathModel = new HashMap();
			filePathModel.putAll(getShareVars());
			return new Generator.GeneratorModel(templateModel, filePathModel);
		}

		public static Map getShareVars() {
			Map templateModel = new HashMap();
			templateModel.putAll(System.getProperties());
			templateModel.putAll(GeneratorProperties.getProperties());
			templateModel.put("env", System.getenv());
			templateModel.put("now", new Date());
			templateModel.put(GeneratorConstants.DATABASE_TYPE.code, GeneratorProperties.getDatabaseType(GeneratorConstants.DATABASE_TYPE.code));
			templateModel.putAll(GeneratorContext.getContext());
			templateModel.putAll(getToolsMap());
			return templateModel;
		}

		private static Map getToolsMap() {
			Map toolsMap = new HashMap();
			String[] tools = GeneratorProperties.getStringArray(GeneratorConstants.GENERATOR_TOOLS_CLASS);
			String[] arr$ = tools;
			int len$ = tools.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				String className = arr$[i$];

				try {
					Object instance = ClassHelper.newInstance(className);
					toolsMap.put(Class.forName(className).getSimpleName(), instance);
					GLogger.debug("put tools class:" + className + " with key:" + Class.forName(className).getSimpleName());
				} catch (Exception var7) {
					GLogger.error("cannot load tools by className:" + className + " cause:" + var7);
				}
			}

			return toolsMap;
		}
	}

	public class ProcessUtils {
		public ProcessUtils() {
		}

		public void processByGeneratorModel(Generator.GeneratorModel model, boolean isDelete) throws Exception, FileNotFoundException {
			Generator g = GeneratorFacade.this.getGenerator();
			Generator.GeneratorModel targetModel = GeneratorModelUtils.newDefaultGeneratorModel();
			targetModel.filePathModel.putAll(model.filePathModel);
			targetModel.templateModel.putAll(model.templateModel);
			this.processByGeneratorModel(isDelete, g, targetModel);
		}

		public void processByMap(Map params, boolean isDelete) throws Exception, FileNotFoundException {
			Generator g = GeneratorFacade.this.getGenerator();
			Generator.GeneratorModel m = GeneratorModelUtils.newFromMap(params);
			this.processByGeneratorModel(isDelete, g, m);
		}

		public void processBySql(Sql sql, boolean isDelete) throws Exception {
			Generator g = GeneratorFacade.this.getGenerator();
			Generator.GeneratorModel m = GeneratorModelUtils.newGeneratorModel("sql", sql);
			PrintUtils.printBeginProcess("sql:" + sql.getSourceSql(), isDelete);
			this.processByGeneratorModel(isDelete, g, m);
		}

		public void processByClass(Class clazz, boolean isDelete) throws Exception, FileNotFoundException {
			Generator g = GeneratorFacade.this.getGenerator();
			Generator.GeneratorModel m = GeneratorModelUtils.newGeneratorModel("clazz", new JavaClass(clazz));
			PrintUtils.printBeginProcess("JavaClass:" + clazz.getSimpleName(), isDelete);
			this.processByGeneratorModel(isDelete, g, m);
		}

		private void processByGeneratorModel(boolean isDelete, Generator g, Generator.GeneratorModel m) throws Exception, FileNotFoundException {
			try {
				if (isDelete) {
					g.deleteBy(m.templateModel, m.filePathModel);
				} else {
					g.generateBy(m.templateModel, m.filePathModel);
				}

			} catch (GeneratorException var5) {
				PrintUtils.printExceptionsSumary(var5.getMessage(), GeneratorFacade.this.getGenerator().getOutRootDir(), var5.getExceptions());
				throw var5;
			}
		}

		public void processByTable(String tableName, boolean isDelete) throws Exception {
			if ("*".equals(tableName)) {
				if (isDelete) {
					GeneratorFacade.this.deleteByAllTable();
				} else {
					GeneratorFacade.this.generateByAllTable();
				}

			} else {
				Generator g = GeneratorFacade.this.getGenerator();
				Table table = TableFactory.getInstance().getTable(tableName);

				try {
					this.processByTable(g, table, isDelete);
				} catch (GeneratorException var6) {
					PrintUtils.printExceptionsSumary(var6.getMessage(), GeneratorFacade.this.getGenerator().getOutRootDir(), var6.getExceptions());
					throw var6;
				}
			}
		}

		public void processByAllTable(boolean isDelete) throws Exception {
			List<Table> tables = TableFactory.getInstance().getAllTables();
			List exceptions = new ArrayList();

			for(int i = 0; i < tables.size(); ++i) {
				try {
					this.processByTable(GeneratorFacade.this.getGenerator(), (Table)tables.get(i), isDelete);
				} catch (GeneratorException var6) {
					exceptions.addAll(var6.getExceptions());
				}
			}

			PrintUtils.printExceptionsSumary("", GeneratorFacade.this.getGenerator().getOutRootDir(), exceptions);
			if (!exceptions.isEmpty()) {
				throw new GeneratorException("batch generate by all table occer error", exceptions);
			}
		}

		public void processByTable(Generator g, Table table, boolean isDelete) throws Exception {
			Generator.GeneratorModel m = GeneratorModelUtils.newGeneratorModel("table", table);
			PrintUtils.printBeginProcess(table.getSqlName() + " => " + table.getClassName(), isDelete);
			if (isDelete) {
				g.deleteBy(m.templateModel, m.filePathModel);
			} else {
				g.generateBy(m.templateModel, m.filePathModel);
			}

		}
	}
}
