//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils;

import enn.base.generator.utils.util.AntPathMatcher;
import enn.base.generator.utils.util.BeanHelper;
import enn.base.generator.utils.util.FileHelper;
import enn.base.generator.utils.util.FreemarkerHelper;
import enn.base.generator.utils.util.GLogger;
import enn.base.generator.utils.util.GeneratorException;
import enn.base.generator.utils.util.IOHelper;
import enn.base.generator.utils.util.StringHelper;
import enn.base.generator.utils.util.ZipUtils;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Generator {
	private static final String GENERATOR_INSERT_LOCATION = "generator-insert-location";
	private ArrayList<File> templateRootDirs = new ArrayList();
	private String outRootDir;
	private boolean ignoreTemplateGenerateException = true;
	private String removeExtensions;
	private boolean isCopyBinaryFile;
	private String includes;
	private String excludes;
	private String sourceEncoding;
	private String outputEncoding;

	public Generator() {
		this.removeExtensions = GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_REMOVE_EXTENSIONS);
		this.isCopyBinaryFile = true;
		this.includes = GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_INCLUDES);
		this.excludes = GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_EXCLUDES);
		this.sourceEncoding = GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_SOURCE_ENCODING);
		this.outputEncoding = GeneratorProperties.getProperty(GeneratorConstants.GENERATOR_OUTPUT_ENCODING);
	}

	public void setTemplateRootDir(File templateRootDir) {
		this.setTemplateRootDirs(templateRootDir);
	}

	public void setTemplateRootDir(String templateRootDir) {
		this.setTemplateRootDirs(StringHelper.tokenizeToStringArray(templateRootDir, ","));
	}

	public void setTemplateRootDirs(File... templateRootDirs) {
		this.templateRootDirs = new ArrayList(Arrays.asList(templateRootDirs));
	}

	public void setTemplateRootDirs(String... templateRootDirs) {
		ArrayList<File> tempDirs = new ArrayList();
		String[] arr$ = templateRootDirs;
		int len$ = templateRootDirs.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			String dir = arr$[i$];
			tempDirs.add(FileHelper.getFile(dir));
		}

		this.templateRootDirs = tempDirs;
	}

	public void addTemplateRootDir(File file) {
		this.templateRootDirs.add(file);
	}

	public void addTemplateRootDir(String file) {
		this.templateRootDirs.add(FileHelper.getFile(file));
	}

	public boolean isIgnoreTemplateGenerateException() {
		return this.ignoreTemplateGenerateException;
	}

	public void setIgnoreTemplateGenerateException(boolean ignoreTemplateGenerateException) {
		this.ignoreTemplateGenerateException = ignoreTemplateGenerateException;
	}

	public boolean isCopyBinaryFile() {
		return this.isCopyBinaryFile;
	}

	public void setCopyBinaryFile(boolean isCopyBinaryFile) {
		this.isCopyBinaryFile = isCopyBinaryFile;
	}

	public String getSourceEncoding() {
		return this.sourceEncoding;
	}

	public void setSourceEncoding(String sourceEncoding) {
		if (StringHelper.isBlank(sourceEncoding)) {
			throw new IllegalArgumentException("sourceEncoding must be not empty");
		} else {
			this.sourceEncoding = sourceEncoding;
		}
	}

	public String getOutputEncoding() {
		return this.outputEncoding;
	}

	public void setOutputEncoding(String outputEncoding) {
		if (StringHelper.isBlank(outputEncoding)) {
			throw new IllegalArgumentException("outputEncoding must be not empty");
		} else {
			this.outputEncoding = outputEncoding;
		}
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

	public void setOutRootDir(String rootDir) {
		if (rootDir == null) {
			throw new IllegalArgumentException("outRootDir must be not null");
		} else {
			this.outRootDir = rootDir;
		}
	}

	public String getOutRootDir() {
		return this.outRootDir;
	}

	public void setRemoveExtensions(String removeExtensions) {
		this.removeExtensions = removeExtensions;
	}

	public void deleteOutRootDir() throws IOException {
		if (StringHelper.isBlank(this.getOutRootDir())) {
			throw new IllegalStateException("'outRootDir' property must be not null.");
		} else {
			GLogger.println("[delete dir]    " + this.getOutRootDir());
			FileHelper.deleteDirectory(new File(this.getOutRootDir()));
		}
	}

	public Generator generateBy(Map templateModel, Map filePathModel) throws Exception {
		this.processTemplateRootDirs(templateModel, filePathModel, false);
		return this;
	}

	public Generator deleteBy(Map templateModel, Map filePathModel) throws Exception {
		this.processTemplateRootDirs(templateModel, filePathModel, true);
		return this;
	}

	private void processTemplateRootDirs(Map templateModel, Map filePathModel, boolean isDelete) throws Exception {
		if (StringHelper.isBlank(this.getOutRootDir())) {
			throw new IllegalStateException("'outRootDir' property must be not empty.");
		} else if (this.templateRootDirs != null && this.templateRootDirs.size() != 0) {
			GLogger.debug("******* Template reference variables *********", templateModel);
			GLogger.debug("\n\n******* FilePath reference variables *********", filePathModel);
			templateModel.putAll(GeneratorHelper.getDirValuesMap(templateModel));
			filePathModel.putAll(GeneratorHelper.getDirValuesMap(filePathModel));
			GeneratorException ge = new GeneratorException("generator occer error, Generator BeanInfo:" + BeanHelper.describe(this, new String[0]));
			List<File> processedTemplateRootDirs = this.processTemplateRootDirs();

			for(int i = 0; i < processedTemplateRootDirs.size(); ++i) {
				File templateRootDir = (File)processedTemplateRootDirs.get(i);
				System.out.println("templateRootDir="+templateRootDir+" ");
				List<Exception> exceptions = this.scanTemplatesAndProcess(templateRootDir, processedTemplateRootDirs, templateModel, filePathModel, isDelete);
				ge.addAll(exceptions);
			}

			if (!ge.exceptions.isEmpty()) {
				throw ge;
			}
		} else {
			throw new IllegalStateException("'templateRootDirs'  must be not empty");
		}
	}

	protected List<File> processTemplateRootDirs() throws Exception {
		return this.unzipIfTemplateRootDirIsZipFile();
	}

	private List<File> unzipIfTemplateRootDirIsZipFile() throws MalformedURLException {
		List<File> unzipIfTemplateRootDirIsZipFile = new ArrayList();

		for(int i = 0; i < this.templateRootDirs.size(); ++i) {
			File file = (File)this.templateRootDirs.get(i);
			String templateRootDir = FileHelper.toFilePathIfIsURL(file);
			String subFolder = "";
			int zipFileSeperatorIndexOf = templateRootDir.indexOf("!");
			if (zipFileSeperatorIndexOf >= 0) {
				subFolder = templateRootDir.substring(zipFileSeperatorIndexOf + 1);
				templateRootDir = templateRootDir.substring(0, zipFileSeperatorIndexOf);
			}

			if ((new File(templateRootDir)).isFile()) {
				File tempDir = ZipUtils.unzip2TempDir(new File(templateRootDir), "tmp_generator_template_folder_for_zipfile");
				unzipIfTemplateRootDirIsZipFile.add(new File(tempDir, subFolder));
			} else {
				unzipIfTemplateRootDirIsZipFile.add(new File(templateRootDir, subFolder));
			}
		}

		return unzipIfTemplateRootDirIsZipFile;
	}

	private List<Exception> scanTemplatesAndProcess(File templateRootDir, List<File> templateRootDirs, Map templateModel, Map filePathModel, boolean isDelete) throws Exception {
		if (templateRootDir == null) {
			throw new IllegalStateException("'templateRootDir' must be not null");
		} else {
			GLogger.println("-------------------load template from templateRootDir = '" + templateRootDir.getAbsolutePath() + "' outRootDir:" + (new File(this.outRootDir)).getAbsolutePath());
			List srcFiles = FileHelper.searchAllNotIgnoreFile(templateRootDir);
			List<Exception> exceptions = new ArrayList();

			for(int i = 0; i < srcFiles.size(); ++i) {
				File srcFile = (File)srcFiles.get(i);

				try {
					if (isDelete) {
						(new TemplateProcessor(templateRootDirs)).executeDelete(templateRootDir, templateModel, filePathModel, srcFile);
					} else {
						long start = System.currentTimeMillis();
						(new TemplateProcessor(templateRootDirs)).executeGenerate(templateRootDir, templateModel, filePathModel, srcFile);
						GLogger.perf("genereate by tempate cost time:" + (System.currentTimeMillis() - start) + "ms");
					}
				} catch (Exception var12) {
					if (!this.ignoreTemplateGenerateException) {
						throw var12;
					}

					GLogger.warn("iggnore generate error,template is:" + srcFile + " cause:" + var12);
					exceptions.add(var12);
				}
			}

			return exceptions;
		}
	}

	public static class GeneratorModel implements Serializable {
		private static final long serialVersionUID = -6430787906037836995L;
		public Map templateModel = new HashMap();
		public Map filePathModel = new HashMap();

		public GeneratorModel() {
		}

		public GeneratorModel(Map templateModel, Map filePathModel) {
			this.templateModel = templateModel;
			this.filePathModel = filePathModel;
		}
	}

	static class GeneratorHelper {
		GeneratorHelper() {
		}

		public static Map getDirValuesMap(Map map) {
			Map dirValues = new HashMap();
			Set<Object> keys = map.keySet();
			Iterator i$ = keys.iterator();

			while(i$.hasNext()) {
				Object key = i$.next();
				Object value = map.get(key);
				if (key instanceof String && value instanceof String) {
					String dirKey = key + "_dir";
					String dirValue = value.toString().replace('.', '/');
					dirValues.put(dirKey, dirValue);
				}
			}

			return dirValues;
		}

		public static boolean isIgnoreTemplateProcess(File srcFile, String templateFile, String includes, String excludes) {
			if (!srcFile.isDirectory() && !srcFile.isHidden()) {
				if (templateFile.trim().equals("")) {
					return true;
				} else if (srcFile.getName().toLowerCase().endsWith(".include")) {
					GLogger.println("[skip]\t\t endsWith '.include' template:" + templateFile);
					return true;
				} else {
					templateFile = templateFile.replace('\\', '/');
					String[] arr$ = StringHelper.tokenizeToStringArray(excludes, ",");
					int len$ = arr$.length;

					int i$;
					String include;
					for(i$ = 0; i$ < len$; ++i$) {
						include = arr$[i$];
						if ((new AntPathMatcher()).match(include.replace('\\', '/'), templateFile)) {
							return true;
						}
					}

					if (StringHelper.isBlank(includes)) {
						return false;
					} else {
						arr$ = StringHelper.tokenizeToStringArray(includes, ",");
						len$ = arr$.length;

						for(i$ = 0; i$ < len$; ++i$) {
							include = arr$[i$];
							if ((new AntPathMatcher()).match(include.replace('\\', '/'), templateFile)) {
								return false;
							}
						}

						return true;
					}
				}
			} else {
				return true;
			}
		}

		private static boolean isFoundInsertLocation(GeneratorControl gg, Template template, Map model, File outputFile, StringWriter newFileContent) throws IOException, TemplateException {
			LineNumberReader reader = new LineNumberReader(new FileReader(outputFile));
			String line = null;
			boolean isFoundInsertLocation = false;
			PrintWriter writer = new PrintWriter(newFileContent);

			while((line = reader.readLine()) != null) {
				writer.println(line);
				if (!isFoundInsertLocation && line.indexOf(gg.getMergeLocation()) >= 0) {
					template.process(model, writer);
					writer.println();
					isFoundInsertLocation = true;
				}
			}

			writer.close();
			reader.close();
			return isFoundInsertLocation;
		}

		public static Configuration newFreeMarkerConfiguration(List<File> templateRootDirs, String defaultEncoding, String templateName) throws IOException {
			Configuration conf = new Configuration();
			FileTemplateLoader[] templateLoaders = new FileTemplateLoader[templateRootDirs.size()];

			for(int i = 0; i < templateRootDirs.size(); ++i) {
				templateLoaders[i] = new FileTemplateLoader((File)templateRootDirs.get(i));
			}

			MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoaders);
			conf.setTemplateLoader(multiTemplateLoader);
			conf.setNumberFormat("###############");
			conf.setBooleanFormat("true,false");
			conf.setDefaultEncoding(defaultEncoding);
			List<String> autoIncludes = getParentPaths(templateName, "macro.include");
			List<String> availableAutoInclude = FreemarkerHelper.getAvailableAutoInclude(conf, autoIncludes);
			conf.setAutoIncludes(availableAutoInclude);
			GLogger.trace("set Freemarker.autoIncludes:" + availableAutoInclude + " for templateName:" + templateName + " autoIncludes:" + autoIncludes);
			return conf;
		}

		public static List<String> getParentPaths(String templateName, String suffix) {
			String[] array = StringHelper.tokenizeToStringArray(templateName, "\\/");
			List<String> list = new ArrayList();
			list.add(suffix);
			list.add(File.separator + suffix);
			String path = "";

			for(int i = 0; i < array.length; ++i) {
				path = path + File.separator + array[i];
				list.add(path + File.separator + suffix);
			}

			return list;
		}
	}

	private class TemplateProcessor {
		private GeneratorControl gg = new GeneratorControl();
		private List<File> templateRootDirs = new ArrayList();

		public TemplateProcessor(List<File> var1) {
			this.templateRootDirs = var1;
		}

		private void executeGenerate(File templateRootDir, Map templateModel, Map filePathModel, File srcFile) throws SQLException, IOException, TemplateException {
			String templateFile = FileHelper.getRelativePath(templateRootDir, srcFile);
			if (!GeneratorHelper.isIgnoreTemplateProcess(srcFile, templateFile, Generator.this.includes, Generator.this.excludes)) {
				String outputFilepath;
				if (Generator.this.isCopyBinaryFile && FileHelper.isBinaryFile(srcFile)) {
					outputFilepath = this.proceeForOutputFilepath(filePathModel, templateFile);
					File outputFile = new File(Generator.this.getOutRootDir(), outputFilepath);
					GLogger.println("[copy binary file by extention] from:" + srcFile + " => " + outputFile);
					FileHelper.parentMkdir(outputFile);
					IOHelper.copyAndClose(new FileInputStream(srcFile), new FileOutputStream(outputFile));
				} else {
					try {
						outputFilepath = this.proceeForOutputFilepath(filePathModel, templateFile);
						this.initGeneratorControlProperties(srcFile, outputFilepath);
						this.processTemplateForGeneratorControl(templateModel, templateFile);
						if (this.gg.isIgnoreOutput()) {
							GLogger.println("[not generate] by gg.isIgnoreOutput()=true on template:" + templateFile);
						} else {
							if (StringHelper.isNotBlank(this.gg.getOutputFile())) {
								this.generateNewFileOrInsertIntoFile(templateFile, this.gg.getOutputFile(), templateModel);
							}

						}
					} catch (Exception var8) {
						throw new RuntimeException("generate oucur error,templateFile is:" + templateFile + " => " + this.gg.getOutputFile() + " cause:" + var8, var8);
					}
				}
			}
		}

		private void executeDelete(File templateRootDir, Map templateModel, Map filePathModel, File srcFile) throws SQLException, IOException, TemplateException {
			String templateFile = FileHelper.getRelativePath(templateRootDir, srcFile);
			if (!GeneratorHelper.isIgnoreTemplateProcess(srcFile, templateFile, Generator.this.includes, Generator.this.excludes)) {
				String outputFilepath = this.proceeForOutputFilepath(filePathModel, templateFile);
				this.initGeneratorControlProperties(srcFile, outputFilepath);
				this.gg.deleteGeneratedFile = true;
				this.processTemplateForGeneratorControl(templateModel, templateFile);
				GLogger.println("[delete file] file:" + (new File(this.gg.getOutputFile())).getAbsolutePath());
				(new File(this.gg.getOutputFile())).delete();
			}
		}

		private void initGeneratorControlProperties(File srcFile, String outputFile) throws SQLException {
			this.gg.setSourceFile(srcFile.getAbsolutePath());
			this.gg.setSourceFileName(srcFile.getName());
			this.gg.setSourceDir(srcFile.getParent());
			this.gg.setOutRoot(Generator.this.getOutRootDir());
			this.gg.setOutputEncoding(Generator.this.outputEncoding);
			this.gg.setSourceEncoding(Generator.this.sourceEncoding);
			this.gg.setMergeLocation("generator-insert-location");
			if(outputFile.contains("\\vo\\")){
				String name = StringUtils.substringAfterLast(outputFile,"\\");
				String path = name;
				if(name.contains("ListVo")){
					path = name.substring(0, name.indexOf("ListVo"));
				}else if(name.contains("ReqVo")){
					path = name.substring(0, name.indexOf("ReqVo"));
				}else if(name.contains("Vo")){
					path = name.substring(0, name.indexOf("Vo"));
				}else{
					path = name.substring(0, name.indexOf("Vo"));
				}

				outputFile = outputFile.substring(0, outputFile.lastIndexOf("\\"))+"\\"+toLowerCaseFirstOne(path)+"\\"+name;
			}
			this.gg.setOutputFile(outputFile);
		}
		public  String toLowerCaseFirstOne(String s){
			if(Character.isLowerCase(s.charAt(0)))
				return s;
			else
				return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
		}
		private void processTemplateForGeneratorControl(Map templateModel, String templateFile) throws IOException, TemplateException {
			templateModel.put("gg", this.gg);
			Template template = this.getFreeMarkerTemplate(templateFile);
			template.process(templateModel, IOHelper.NULL_WRITER);
		}

		private String proceeForOutputFilepath(Map filePathModel, String templateFile) throws IOException {
			String outputFilePath = templateFile;
			boolean testExpressionIndex = true;
			int testExpressionIndexx;
			if ((testExpressionIndexx = templateFile.indexOf(64)) != -1) {
				outputFilePath = templateFile.substring(0, testExpressionIndexx);
				String testExpressionKey = templateFile.substring(testExpressionIndexx + 1);
				Object expressionValue = filePathModel.get(testExpressionKey);
				if (expressionValue == null) {
					System.err.println("[not-generate] WARN: test expression is null by key:[" + testExpressionKey + "] on template:[" + templateFile + "]");
					return null;
				}

				if (!"true".equals(String.valueOf(expressionValue))) {
					GLogger.println("[not-generate]\t test expression '@" + testExpressionKey + "' is false,template:" + templateFile);
					return null;
				}
			}

			String[] arr$ = Generator.this.removeExtensions.split(",");
			int len$ = arr$.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				String removeExtension = arr$[i$];
				if (outputFilePath.endsWith(removeExtension)) {
					outputFilePath = outputFilePath.substring(0, outputFilePath.length() - removeExtension.length());
					break;
				}
			}

			Configuration conf = GeneratorHelper.newFreeMarkerConfiguration(this.templateRootDirs, Generator.this.sourceEncoding, "/filepath/processor/");
			outputFilePath = outputFilePath.replace('^', '?');
			return FreemarkerHelper.processTemplateString(outputFilePath, filePathModel, conf);
		}

		private Template getFreeMarkerTemplate(String templateName) throws IOException {
			return GeneratorHelper.newFreeMarkerConfiguration(this.templateRootDirs, Generator.this.sourceEncoding, templateName).getTemplate(templateName);
		}

		private void generateNewFileOrInsertIntoFile(String templateFile, String outputFilepath, Map templateModel) throws Exception {
			Template template = this.getFreeMarkerTemplate(templateFile);
			template.setOutputEncoding(this.gg.getOutputEncoding());
			File absoluteOutputFilePath = FileHelper.parentMkdir(outputFilepath);
			if (absoluteOutputFilePath.exists()) {
				StringWriter newFileContentCollector = new StringWriter();
				if (GeneratorHelper.isFoundInsertLocation(this.gg, template, templateModel, absoluteOutputFilePath, newFileContentCollector)) {
					GLogger.println("[insert]\t generate content into:" + outputFilepath);
					IOHelper.saveFile(absoluteOutputFilePath, newFileContentCollector.toString(), this.gg.getOutputEncoding());
					return;
				}
			}

			if (absoluteOutputFilePath.exists() && !this.gg.isOverride()) {
				GLogger.println("[not generate]\t by gg.isOverride()=false and outputFile exist:" + outputFilepath);
			} else {
				if (absoluteOutputFilePath.exists()) {
					GLogger.println("[override]\t template:" + templateFile + " ==> " + outputFilepath);
				} else {
					GLogger.println("[generate]\t template:" + templateFile + " ==> " + outputFilepath);
				}

				FreemarkerHelper.processTemplate(template, templateModel, absoluteOutputFilePath, this.gg.getOutputEncoding());
			}
		}
	}
}
