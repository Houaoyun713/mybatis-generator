//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import enn.base.generator.utils.GeneratorProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class GLogger {
	public static final int TRACE = 60;
	public static final int DEBUG = 70;
	public static final int INFO = 80;
	public static final int WARN = 90;
	public static final int ERROR = 100;
	public static int logLevel = 80;
	public static PrintStream out;
	public static PrintStream err;
	public static int perfLogLevel;

	public GLogger() {
	}

	public static void trace(String s) {
		if (logLevel <= 60) {
			out.println("[Generator TRACE] " + s);
		}

	}

	public static void debug(String s) {
		if (logLevel <= 70) {
			out.println("[Generator DEBUG] " + s);
		}

	}

	public static void info(String s) {
		if (logLevel <= 80) {
			out.println("[Generator INFO] " + s);
		}

	}

	public static void warn(String s) {
		if (logLevel <= 90) {
			err.println("[Generator WARN] " + s);
		}

	}

	public static void warn(String s, Throwable e) {
		if (logLevel <= 90) {
			err.println("[Generator WARN] " + s + " cause:" + e);
			e.printStackTrace(err);
		}

	}

	public static void error(String s) {
		if (logLevel <= 100) {
			err.println("[Generator ERROR] " + s);
		}

	}

	public static void error(String s, Throwable e) {
		if (logLevel <= 100) {
			err.println("[Generator ERROR] " + s + " cause:" + e);
			e.printStackTrace(err);
		}

	}

	public static void perf(String s) {
		if (perfLogLevel <= 80) {
			out.println("[Generator Performance] () " + s);
		}

	}

	public static void println(String s) {
		if (logLevel <= 80) {
			out.println(s);
		}

	}

	public static void init_with_log4j_config() {
		Properties props = loadLog4jProperties();
		logLevel = toLogLevel(props.getProperty("GLogger", "INFO"));
		perfLogLevel = toLogLevel(props.getProperty("GLogger.perf", "ERROR"));
	}

	public static int toLogLevel(String level) {
		if ("TRACE".equalsIgnoreCase(level)) {
			return 60;
		} else if ("DEBUG".equalsIgnoreCase(level)) {
			return 70;
		} else if ("INFO".equalsIgnoreCase(level)) {
			return 80;
		} else if ("WARN".equalsIgnoreCase(level)) {
			return 90;
		} else if ("ERROR".equalsIgnoreCase(level)) {
			return 100;
		} else if ("FATAL".equalsIgnoreCase(level)) {
			return 100;
		} else {
			return "ALL".equalsIgnoreCase(level) ? 100 : 90;
		}
	}

	public static void debug(String string, Map templateModel) {
		if (logLevel <= 70) {
			println(string);
			if (templateModel == null) {
				return;
			}

			Iterator i$ = templateModel.keySet().iterator();

			while(i$.hasNext()) {
				Object key = i$.next();
				if (!System.getProperties().containsKey(key) && !GeneratorProperties.getProperties().containsKey(key) && !key.toString().endsWith("_dir")) {
					println(key + " = " + templateModel.get(key));
				}
			}
		}

	}

	private static Properties loadLog4jProperties() {
		try {
			File file = FileHelper.getFileByClassLoader("log4j.properties");
			Properties props = new Properties();
			FileInputStream in = new FileInputStream(file);

			try {
				props.load(in);
			} finally {
				IOHelper.close(in, null);
			}

			return props;
		} catch (FileNotFoundException var8) {
			warn("not found log4j.properties, cause:" + var8);
			return new Properties();
		} catch (IOException var9) {
			warn("load log4j.properties occer error, cause:" + var9);
			return new Properties();
		}
	}

	static {
		out = System.out;
		err = System.err;
		perfLogLevel = 60;
		init_with_log4j_config();
	}
}
