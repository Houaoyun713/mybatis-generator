//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.paranamer;

import enn.base.generator.utils.util.IOHelper;
import enn.base.generator.utils.util.StringHelper;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSourceParanamer implements Paranamer {
	private ClassLoader classLoader;

	public JavaSourceParanamer(ClassLoader classLoader) {
		if (classLoader == null) {
			throw new IllegalArgumentException("'classLoader' must be not null");
		} else {
			this.classLoader = classLoader;
		}
	}

	public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
		return this.lookupParameterNames(methodOrConstructor, true);
	}

	public String[] lookupParameterNames(AccessibleObject methodOrConstructor, boolean throwExceptionIfMissing) {
		try {
			JavaSourceFileMethodParametersParser parser = new JavaSourceFileMethodParametersParser();
			String javaSource = null;
			if (methodOrConstructor instanceof Method) {
				Method m = (Method)methodOrConstructor;
				javaSource = m.getDeclaringClass().getName().replace('.', '/') + ".java";
			} else {
				if (!(methodOrConstructor instanceof Constructor)) {
					throw new IllegalArgumentException("unknow AccessibleObject" + methodOrConstructor + ",must be Method or Constructor");
				}

				Constructor c = (Constructor)methodOrConstructor;
				javaSource = c.getDeclaringClass().getName().replace('.', '/') + ".java";
			}

			InputStream javaSourceInputStream = this.classLoader.getResourceAsStream(javaSource);

			String[] var6;
			try {
				if (javaSourceInputStream == null) {
					var6 = EMPTY_NAMES;
					return var6;
				}

				var6 = parser.parseJavaFileForParamNames(methodOrConstructor, IOHelper.toString(javaSourceInputStream));
			} finally {
				if (javaSourceInputStream != null) {
					javaSourceInputStream.close();
				}

			}

			return var6;
		} catch (IOException var11) {
			if (throwExceptionIfMissing) {
				throw new RuntimeException("IOException while reading javasource,method:" + methodOrConstructor, var11);
			} else {
				return EMPTY_NAMES;
			}
		}
	}

	public static class JavaSourceFileMethodParametersParser {
		public JavaSourceFileMethodParametersParser() {
		}

		public String[] parseJavaFileForParamNames(Constructor<?> constructor, String content) {
			return this.parseJavaFileForParamNames(content, constructor.getName(), constructor.getParameterTypes());
		}

		public String[] parseJavaFileForParamNames(Method method, String content) {
			return this.parseJavaFileForParamNames(content, method.getName(), method.getParameterTypes());
		}

		public String[] parseJavaFileForParamNames(AccessibleObject methodOrConstructor, String content) {
			if (methodOrConstructor instanceof Method) {
				return this.parseJavaFileForParamNames((Method)methodOrConstructor, content);
			} else if (methodOrConstructor instanceof Constructor) {
				return this.parseJavaFileForParamNames((Constructor)methodOrConstructor, content);
			} else {
				throw new IllegalArgumentException("unknow AccessibleObject" + methodOrConstructor + ",must be Method or Constructor");
			}
		}

		private String[] parseJavaFileForParamNames(String content, String name, Class<?>[] parameterTypes) {
			Pattern methodPattern = Pattern.compile("(?s)" + name + "\\s*\\(" + this.getParamsPattern(parameterTypes) + "\\)\\s*\\{");
			Matcher m = methodPattern.matcher(content);
			List<String> paramNames = new ArrayList();
			if (!m.find()) {
				return null;
			} else {
				for(int i = 1; i <= parameterTypes.length; ++i) {
					paramNames.add(m.group(i));
				}

				return (String[])((String[])paramNames.toArray(new String[0]));
			}
		}

		private String getParamsPattern(Class<?>[] parameterTypes) {
			List paramPatterns = new ArrayList();

			for(int i = 0; i < parameterTypes.length; ++i) {
				Class type = parameterTypes[i];
				String paramPattern = ".*" + type.getSimpleName() + ".*\\s+(\\w+).*";
				paramPatterns.add(paramPattern);
			}

			return StringHelper.join(paramPatterns, ",");
		}
	}
}
