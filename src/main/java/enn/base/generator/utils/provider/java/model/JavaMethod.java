//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.java.model;

import enn.base.generator.utils.provider.java.model.MethodParameter.JavaSourceFileMethodParametersParser;
import enn.base.generator.utils.util.GLogger;
import enn.base.generator.utils.util.StringHelper;
import enn.base.generator.utils.util.typemapping.JavaImport;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaMethod {
	Method method;
	private JavaClass clazz;

	public JavaMethod(Method method, JavaClass clazz) {
		if (method == null) {
			throw new IllegalArgumentException("method must be not null");
		} else if (clazz == null) {
			throw new IllegalArgumentException("clazz must be not null");
		} else {
			this.method = method;
			this.clazz = clazz;
		}
	}

	public JavaClass getClazz() {
		return this.clazz;
	}

	public String getMethodName() {
		return this.method.getName();
	}

	public JavaClass getReturnType() {
		return new JavaClass(this.method.getReturnType());
	}

	public Annotation[] getAnnotations() {
		return this.method.getAnnotations();
	}

	public boolean isBridge() {
		return this.method.isBridge();
	}

	public List<JavaClass> getExceptionTypes() {
		List<JavaClass> result = new ArrayList();
		Class[] arr$ = this.method.getExceptionTypes();
		int len$ = arr$.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			Class c = arr$[i$];
			result.add(new JavaClass(c));
		}

		return result;
	}

	public boolean isSynthetic() {
		return this.method.isSynthetic();
	}

	public boolean isVarArgs() {
		return this.method.isVarArgs();
	}

	public Set<JavaClass> getImportClasses() {
		Set<JavaClass> set = new LinkedHashSet();
		JavaImport.addImportClass(set, this.method.getParameterTypes());
		JavaImport.addImportClass(set, this.method.getExceptionTypes());
		JavaImport.addImportClass(set, new Class[]{this.method.getReturnType()});
		return set;
	}

	public List<MethodParameter> getParameters() {
		Class[] parameters = this.method.getParameterTypes();
		List<MethodParameter> results = new ArrayList();

		for(int i = 0; i < parameters.length; ++i) {
			results.add(new MethodParameter(i + 1, this, new JavaClass(parameters[i])));
		}

		return results;
	}

	public String getMethodNameUpper() {
		return StringHelper.capitalize(this.getMethodName());
	}

	public int hashCode() {
		boolean prime = true;
		int result = 1;
		result = 31 * result + (this.method == null ? 0 : this.method.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (this.getClass() != obj.getClass()) {
			return false;
		} else {
			JavaMethod other = (JavaMethod)obj;
			if (this.method == null) {
				if (other.method != null) {
					return false;
				}
			} else if (!this.method.equals(other.method)) {
				return false;
			}

			return true;
		}
	}

	public boolean isPropertyMethod() {
		return this.getMethodName().startsWith("set") || this.getMethodName().startsWith("get") || this.getMethodName().startsWith("is") && this.getReturnType().isBooleanType();
	}

	public List<FieldMethodInvocation> getFieldMethodInvocationSequences() {
		if (StringHelper.isNotBlank(this.clazz.getMavenJavaSourceFileContent())) {
			try {
				JavaMethodInvokeSequencesParser cmd = new JavaMethodInvokeSequencesParser(this, this.clazz.getMavenJavaSourceFileContent());
				cmd.execute();
				return cmd.getMethodInvokeSequences();
			} catch (Exception var2) {
				GLogger.warn("getFieldMethodInvocationSequences() occer error on method:" + this.method.toString(), var2);
				return new ArrayList(0);
			}
		} else {
			return new ArrayList(0);
		}
	}

	public String toString() {
		return this.clazz.getJavaType() + "." + this.getMethodName() + "()";
	}

	public static class JavaMethodInvokeSequencesParser {
		public static String fieldMethodInvokeRegex = "(\\w+)\\.(\\w+)\\(";
		private JavaMethod method;
		private String javaSourceContent;
		private JavaClass clazz;
		boolean executed = false;
		private List<FieldMethodInvocation> methodInvokeFlows = new ArrayList();

		public JavaMethodInvokeSequencesParser(JavaMethod method, String javaSourceContent) {
			if (StringHelper.isBlank(javaSourceContent)) {
				throw new IllegalArgumentException("'javaSourceContent' must be not blank");
			} else {
				this.method = method;
				this.javaSourceContent = javaSourceContent;
				this.clazz = method.getClazz();
			}
		}

		public List<FieldMethodInvocation> getMethodInvokeSequences() {
			if (this.executed) {
				return this.methodInvokeFlows;
			} else {
				throw new IllegalStateException("please invoke execute() method before getMethodInvokeFlows()");
			}
		}

		public void execute() {
			this.executed = true;
			if (this.declaredMethodsContains()) {
				if (this.method.getMethodName().indexOf("$") < 0) {
					String javaSourceContent = this.removeSomeThings();
					String methodBody = this.getMethodBody(javaSourceContent);
					Pattern p = Pattern.compile(fieldMethodInvokeRegex);
					Matcher m = p.matcher(methodBody);

					while(m.find()) {
						String field = m.group(1);
						String methodName = m.group(2);
						this.addFieldMethodInvocation(field, methodName);
					}

				}
			}
		}

		private boolean declaredMethodsContains() {
			Method[] arr$ = this.clazz.getClazz().getDeclaredMethods();
			int len$ = arr$.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				Method m = arr$[i$];
				if (m.equals(this.method.method)) {
					return true;
				}
			}

			return false;
		}

		private void addFieldMethodInvocation(String field, String methodName) {
			try {
				JavaField javaField = this.clazz.getField(field);
				JavaClass fieldType = javaField.getType();
				JavaMethod method = fieldType.getMethod(methodName);
				if (method != null) {
					this.methodInvokeFlows.add(new FieldMethodInvocation(javaField, method));
				}
			} catch (NoSuchFieldException var6) {
				;
			}

		}

		public static String modifierToString(int mod) {
			if ((mod & 1) != 0) {
				return "public";
			} else if ((mod & 4) != 0) {
				return "protected";
			} else {
				return (mod & 2) != 0 ? "private" : "";
			}
		}

		private String getMethodBody(String javaSourceContent) {
			String methodStartPattern = "(?s)\\s+" + this.method.getMethodName() + "\\s*\\(" + JavaSourceFileMethodParametersParser.getSimpleParamsPattern(this.method.method) + "\\)\\s*";
			int methodStart = StringHelper.indexOfByRegex(javaSourceContent, methodStartPattern);
			if (methodStart == -1) {
				throw new IllegalArgumentException("cannot get method body by pattern:" + methodStartPattern + " methodName:" + this.method.getMethodName() + "\n javaSource:" + javaSourceContent);
			} else {
				try {
					String methodEnd = javaSourceContent.substring(methodStart);
					int[] beginAndEnd = findWrapCharEndLocation(methodEnd, '{', '}');
					if (beginAndEnd == null) {
						return "";
					} else {
						String methodBody = methodEnd.substring(beginAndEnd[0], beginAndEnd[1]);
						return methodBody;
					}
				} catch (RuntimeException var7) {
					throw new IllegalArgumentException("cannot get method body by pattern:" + methodStartPattern + "\n javaSource:" + javaSourceContent, var7);
				}
			}
		}

		private String removeSomeThings() {
			String javaSourceContent = removeJavaComments(this.javaSourceContent);
			javaSourceContent = replaceString2EmptyString(javaSourceContent);
			return javaSourceContent;
		}

		public static String replaceString2EmptyString(String str) {
			if (str == null) {
				return null;
			} else {
				str = str.replaceAll("\".*?\"", "");
				return str;
			}
		}

		public static String removeJavaComments(String str) {
			if (str == null) {
				return null;
			} else {
				str = str.replaceAll("//.*", "");
				str = str.replaceAll("(?s)/\\*.*?\\*/", "");
				return str;
			}
		}

		public static int[] findWrapCharEndLocation(String str, char begin, char end) {
			int count = 0;
			boolean foundEnd = false;
			boolean foundBegin = false;
			int[] beginAndEnd = new int[2];

			for(int i = 0; i < str.length(); ++i) {
				char c = str.charAt(i);
				if (c == begin) {
					if (!foundBegin) {
						beginAndEnd[0] = i;
					}

					foundBegin = true;
					++count;
				}

				if (c == end) {
					foundEnd = true;
					--count;
				}

				if (count == 0 && foundBegin && foundEnd) {
					beginAndEnd[1] = i;
					return beginAndEnd;
				}
			}

			return null;
		}
	}

	public static class FieldMethodInvocation {
		JavaField field;
		JavaMethod method;

		public FieldMethodInvocation(JavaField field, JavaMethod method) {
			this.field = field;
			this.method = method;
		}

		public JavaField getField() {
			return this.field;
		}

		public void setField(JavaField field) {
			this.field = field;
		}

		public JavaMethod getMethod() {
			return this.method;
		}

		public void setMethod(JavaMethod method) {
			this.method = method;
		}

		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else if (!(obj instanceof FieldMethodInvocation)) {
				return false;
			} else {
				FieldMethodInvocation other = (FieldMethodInvocation)obj;
				return this.field.equals(other.field) && this.method.equals(other.method);
			}
		}

		public int hashCode() {
			return this.field.hashCode() + this.method.hashCode();
		}

		public String toString() {
			return this.field.getFieldName() + "." + this.method.getMethodName() + "()";
		}
	}
}
