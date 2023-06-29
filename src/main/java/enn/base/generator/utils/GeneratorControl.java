//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils;

import enn.base.generator.utils.provider.db.DataSourceProvider;
import enn.base.generator.utils.util.DBHelper;
import enn.base.generator.utils.util.FileHelper;
import enn.base.generator.utils.util.GLogger;
import enn.base.generator.utils.util.IOHelper;
import enn.base.generator.utils.util.SqlExecutorHelper;
import enn.base.generator.utils.util.StringHelper;
import enn.base.generator.utils.util.SystemHelper;
import enn.base.generator.utils.util.XMLHelper;
import freemarker.ext.dom.NodeModel;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.xml.sax.InputSource;

public class GeneratorControl {
	private boolean isOverride;
	private boolean isAppend;
	private boolean ignoreOutput;
	private boolean isMergeIfExists;
	private String mergeLocation;
	private String outRoot;
	private String outputEncoding;
	private String sourceFile;
	private String sourceDir;
	private String sourceFileName;
	private String sourceEncoding;
	private String outputFile;
	boolean deleteGeneratedFile;

	public GeneratorControl() {
		this.isOverride = GeneratorProperties.getBoolean(GeneratorConstants.GG_IS_OVERRIDE);
		this.isAppend = false;
		this.ignoreOutput = false;
		this.isMergeIfExists = true;
		this.deleteGeneratedFile = false;
	}

	public NodeModel loadXml(String file) {
		return this.loadXml(file, true);
	}

	public NodeModel loadXml(String file, boolean removeXmlNamespace) {
		try {
			if (removeXmlNamespace) {
				InputStream forEncodingInput = FileHelper.getInputStream(file);
				String encoding = XMLHelper.getXMLEncoding(forEncodingInput);
				forEncodingInput.close();
				InputStream input = FileHelper.getInputStream(file);
				String xml = IOHelper.toString(encoding, input);
				xml = XMLHelper.removeXmlns(xml);
				input.close();
				return NodeModel.parse(new InputSource(new StringReader(xml.trim())));
			} else {
				return NodeModel.parse(new InputSource(FileHelper.getInputStream(file)));
			}
		} catch (Exception var7) {
			throw new IllegalArgumentException("loadXml error,file:" + file, var7);
		}
	}

	public Properties loadProperties(String file) {
		try {
			Properties p = new Properties();
			InputStream in = FileHelper.getInputStream(file);
			if (file.endsWith(".xml")) {
				p.loadFromXML(in);
			} else {
				p.load(in);
			}

			in.close();
			return p;
		} catch (Exception var4) {
			throw new IllegalArgumentException("loadProperties error,file:" + file, var4);
		}
	}

	public void generateFile(String outputFile, String content) {
		this.generateFile(outputFile, content, false);
	}

	public void generateFile(String outputFile, String content, boolean append) {
		try {
			String realOutputFile = null;
			if ((new File(outputFile)).isAbsolute()) {
				realOutputFile = outputFile;
			} else {
				realOutputFile = (new File(this.getOutRoot(), outputFile)).getAbsolutePath();
			}

			if (this.deleteGeneratedFile) {
				GLogger.println("[delete gg.generateFile()] file:" + realOutputFile + " by template:" + this.getSourceFile());
				(new File(realOutputFile)).delete();
			} else {
				File file = new File(realOutputFile);
				FileHelper.parentMkdir(file);
				GLogger.println("[gg.generateFile()] outputFile:" + realOutputFile + " append:" + append + " by template:" + this.getSourceFile());
				IOHelper.saveFile(file, content, this.getOutputEncoding(), append);
			}

		} catch (Exception var6) {
			GLogger.warn("gg.generateFile() occer error,outputFile:" + outputFile + " caused by:" + var6, var6);
			throw new RuntimeException("gg.generateFile() occer error,outputFile:" + outputFile + " caused by:" + var6, var6);
		}
	}

	public boolean isOverride() {
		return this.isOverride;
	}

	public void setOverride(boolean isOverride) {
		this.isOverride = isOverride;
	}

	public boolean isIgnoreOutput() {
		return this.ignoreOutput;
	}

	public void setIgnoreOutput(boolean ignoreOutput) {
		this.ignoreOutput = ignoreOutput;
	}

	public boolean isMergeIfExists() {
		return this.isMergeIfExists;
	}

	public void setMergeIfExists(boolean isMergeIfExists) {
		this.isMergeIfExists = isMergeIfExists;
	}

	public String getMergeLocation() {
		return this.mergeLocation;
	}

	public void setMergeLocation(String mergeLocation) {
		this.mergeLocation = mergeLocation;
	}

	public String getOutRoot() {
		return this.outRoot;
	}

	public void setOutRoot(String outRoot) {
		this.outRoot = outRoot;
	}

	public String getOutputEncoding() {
		return this.outputEncoding;
	}

	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	public String getSourceFile() {
		return this.sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getSourceDir() {
		return this.sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public String getSourceFileName() {
		return this.sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getSourceEncoding() {
		return this.sourceEncoding;
	}

	public void setSourceEncoding(String sourceEncoding) {
		this.sourceEncoding = sourceEncoding;
	}

	public String getOutputFile() {
		if (this.outputFile == null) {
			return null;
		} else {
			return (new File(this.outputFile)).isAbsolute() ? this.outputFile : (new File(this.getOutRoot(), this.outputFile)).getAbsolutePath();
		}
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public boolean isExistsOutputFile() {
		return (new File(this.outRoot, this.outputFile)).exists();
	}

	public boolean outputFileMatchs(String regex) throws IOException {
		if (this.isExistsOutputFile()) {
			String content = IOHelper.readFile(new File(this.outRoot, this.outputFile), this.sourceEncoding);
			if (StringHelper.indexOfByRegex(content, regex) >= 0) {
				return true;
			}
		}

		return false;
	}

	public boolean outputFileContains(String s) throws IOException {
		if (this.isExistsOutputFile()) {
			String content = IOHelper.readFile(new File(this.outRoot, this.outputFile), this.sourceEncoding);
			return content.contains(s);
		} else {
			return false;
		}
	}

	public String getProperty(String key, String defaultValue) {
		return GeneratorProperties.getProperty(key, defaultValue);
	}

	public String insertAfter(String compareToken, String str) throws IOException {
		String content = IOHelper.readFile((new File(this.outRoot, this.outputFile)).getAbsoluteFile(), this.sourceEncoding);
		if (StringHelper.isBlank(content)) {
			throw new IllegalArgumentException((new File(this.outRoot, this.outputFile)).getAbsolutePath() + " is blank");
		} else {
			return StringHelper.insertAfter(content, compareToken, str);
		}
	}

	public String insertBefore(String compareToken, String str) throws IOException {
		String content = IOHelper.readFile(new File(this.outRoot, this.outputFile), this.sourceEncoding);
		if (StringHelper.isBlank(content)) {
			throw new IllegalArgumentException((new File(this.outRoot, this.outputFile)).getAbsolutePath() + " is blank");
		} else {
			return StringHelper.insertBefore(content, compareToken, str);
		}
	}

	public String append(String str) throws IOException {
		String content = IOHelper.readFile(new File(this.outRoot, this.outputFile), this.sourceEncoding);
		if (StringHelper.isBlank(content)) {
			throw new IllegalArgumentException((new File(this.outRoot, this.outputFile)).getAbsolutePath() + " is blank");
		} else {
			return content + str;
		}
	}

	public String prepend(String str) throws IOException {
		String content = IOHelper.readFile(new File(this.outRoot, this.outputFile), this.sourceEncoding);
		if (StringHelper.isBlank(content)) {
			throw new IllegalArgumentException((new File(this.outRoot, this.outputFile)).getAbsolutePath() + " is blank");
		} else {
			return (new StringBuffer(content)).insert(0, str).toString();
		}
	}

	public String getInputProperty(String key) throws IOException {
		return this.getInputProperty(key, "Please input value for " + key + ":");
	}

	public String getInputProperty(String key, String message) throws IOException {
		String v = GeneratorProperties.getProperty(key);
		if (v == null) {
			if (SystemHelper.isWindowsOS) {
				v = JOptionPane.showInputDialog((Component)null, message, "template:" + this.getSourceFileName(), 0);
			} else {
				System.out.print("template:" + this.getSourceFileName() + "," + message);
				v = (new BufferedReader(new InputStreamReader(System.in))).readLine();
			}

			GeneratorProperties.setProperty(key, v);
		}

		return v;
	}

	public List<Map> queryForList(String sql, int limit) throws SQLException {
		Connection conn = DataSourceProvider.getConnection();

		List var5;
		try {
			List<Map> result = SqlExecutorHelper.queryForList(conn, sql, limit);
			var5 = result;
		} finally {
			DBHelper.close(conn);
		}

		return var5;
	}
}
