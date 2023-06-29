//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.test;

import enn.base.generator.utils.Generator;
import enn.base.generator.utils.GeneratorFacade;
import enn.base.generator.utils.provider.db.sql.model.Sql;
import enn.base.generator.utils.util.FileHelper;
import enn.base.generator.utils.util.StringHelper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class GeneratorTestHelper {
	private static AtomicLong count = new AtomicLong(System.currentTimeMillis());

	public GeneratorTestHelper() {
	}

	public static String generateBy(GeneratorFacade gf, Generator.GeneratorModel... models) throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateBy(models);
		return readEntireDirectoryContentAndDelete(tempDir, gf.getGenerator().getOutputEncoding());
	}

	public static String generateByAllTable(GeneratorFacade gf) throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateByAllTable();
		return readEntireDirectoryContentAndDelete(tempDir, gf.getGenerator().getOutputEncoding());
	}

	public static String generateByClass(GeneratorFacade gf, Class... clazzes) throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateByClass(clazzes);
		return readEntireDirectoryContentAndDelete(tempDir, gf.getGenerator().getOutputEncoding());
	}

	public static String generateByMap(GeneratorFacade gf, Map... maps) throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateByMap(maps);
		return readEntireDirectoryContentAndDelete(tempDir, gf.getGenerator().getOutputEncoding());
	}

	public static String generateBySql(GeneratorFacade gf, Sql... sqls) throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateBySql(sqls);
		return readEntireDirectoryContentAndDelete(tempDir, gf.getGenerator().getOutputEncoding());
	}

	public static String generateByTable(GeneratorFacade gf, String... tableNames) throws Exception {
		File tempDir = getOutputTempDir();
		gf.getGenerator().setOutRootDir(tempDir.getPath());
		gf.generateByTable(tableNames);
		return readEntireDirectoryContentAndDelete(tempDir, gf.getGenerator().getOutputEncoding());
	}

	public static String generateBy(Generator g, Map templateModel) throws Exception {
		return generateBy(g, templateModel, templateModel);
	}

	public static String generateBy(Generator g, Map templateModel, Map filePathModel) throws Exception {
		File tempDir = getOutputTempDir();
		g.setOutRootDir(tempDir.getPath());
		g.generateBy(templateModel, filePathModel);
		return readEntireDirectoryContentAndDelete(tempDir, g.getOutputEncoding());
	}

	private static String readEntireDirectoryContentAndDelete(File tempDir, String encoding) {
		String result = FileHelper.readEntireDirectoryContent(tempDir, encoding);
		List<File> files = FileHelper.searchAllNotIgnoreFile(tempDir);
		Iterator i$ = files.iterator();

		while(i$.hasNext()) {
			File f = (File)i$.next();
			if (!f.isDirectory()) {
				String relativePath = FileHelper.getRelativePath(tempDir, f).replace('\\', '/');
				if (!StringHelper.isBlank(relativePath)) {
					result = result + "\n" + "file:" + relativePath;
				}
			}
		}

		try {
			FileHelper.deleteDirectory(tempDir);
		} catch (IOException var7) {
			var7.printStackTrace();
		}

		return result;
	}

	private static File getOutputTempDir() {
		File tempDir = new File(FileHelper.getTempDir(), "GeneratorTestHelper/" + count.incrementAndGet() + ".tmp");
		tempDir.deleteOnExit();
		return tempDir;
	}
}
