//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.paranamer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class BytecodeReadingParanamer implements Paranamer {
	private static final Map<String, String> primitives = new HashMap<String, String>() {
		{
			this.put("int", "I");
			this.put("boolean", "Z");
			this.put("char", "C");
			this.put("short", "B");
			this.put("float", "F");
			this.put("long", "J");
			this.put("double", "D");
		}
	};

	public BytecodeReadingParanamer() {
	}

	public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
		return this.lookupParameterNames(methodOrConstructor, true);
	}

	public String[] lookupParameterNames(AccessibleObject methodOrCtor, boolean throwExceptionIfMissing) {
		Class<?>[] types = null;
		Class<?> declaringClass = null;
		String name = null;
		if (methodOrCtor instanceof Method) {
			Method method = (Method)methodOrCtor;
			types = method.getParameterTypes();
			name = method.getName();
			declaringClass = method.getDeclaringClass();
		} else {
			Constructor<?> constructor = (Constructor)methodOrCtor;
			types = constructor.getParameterTypes();
			declaringClass = constructor.getDeclaringClass();
			name = "<init>";
		}

		if (types.length == 0) {
			return EMPTY_NAMES;
		} else {
			InputStream byteCodeStream = this.getClassAsStream(declaringClass);
			if (byteCodeStream == null) {
				if (throwExceptionIfMissing) {
					throw new ParameterNamesNotFoundException("Unable to get class bytes");
				} else {
					return EMPTY_NAMES;
				}
			} else {
				try {
					ClassReader reader = new ClassReader(byteCodeStream, null);
					TypeCollector visitor = new TypeCollector(name, types, throwExceptionIfMissing, null);
					reader.accept(visitor);
					String[] parameterNamesForMethod = visitor.getParameterNamesForMethod();

					try {
						byteCodeStream.close();
					} catch (IOException var11) {
						;
					}

					return parameterNamesForMethod;
				} catch (IOException var12) {
					if (throwExceptionIfMissing) {
						throw new ParameterNamesNotFoundException("IoException while reading class bytes", var12);
					} else {
						return EMPTY_NAMES;
					}
				}
			}
		}
	}

	private InputStream getClassAsStream(Class<?> clazz) {
		ClassLoader classLoader = clazz.getClassLoader();
		if (classLoader == null) {
			classLoader = ClassLoader.getSystemClassLoader();
		}

		return this.getClassAsStream(classLoader, clazz.getName());
	}

	private InputStream getClassAsStream(ClassLoader classLoader, String className) {
		String name = className.replace('.', '/') + ".class";
		InputStream asStream = classLoader.getResourceAsStream(name);
		if (asStream == null) {
			asStream = BytecodeReadingParanamer.class.getResourceAsStream(name);
		}

		return asStream;
	}

	private static class Type {
		private static final int VOID = 0;
		private static final int BOOLEAN = 1;
		private static final int CHAR = 2;
		private static final int BYTE = 3;
		private static final int SHORT = 4;
		private static final int INT = 5;
		private static final int FLOAT = 6;
		private static final int LONG = 7;
		private static final int DOUBLE = 8;
		private static final int ARRAY = 9;
		private static final int OBJECT = 10;
		private static final Type VOID_TYPE = new Type(0, (char[])null, 1443168256, 1);
		private static final Type BOOLEAN_TYPE = new Type(1, (char[])null, 1509950721, 1);
		private static final Type CHAR_TYPE = new Type(2, (char[])null, 1124075009, 1);
		private static final Type BYTE_TYPE = new Type(3, (char[])null, 1107297537, 1);
		private static final Type SHORT_TYPE = new Type(4, (char[])null, 1392510721, 1);
		private static final Type INT_TYPE = new Type(5, (char[])null, 1224736769, 1);
		private static final Type FLOAT_TYPE = new Type(6, (char[])null, 1174536705, 1);
		private static final Type LONG_TYPE = new Type(7, (char[])null, 1241579778, 1);
		private static final Type DOUBLE_TYPE = new Type(8, (char[])null, 1141048066, 1);
		private final int sort;
		private char[] buf;
		private int off;
		private final int len;

		private Type(int sort) {
			this.sort = sort;
			this.len = 1;
		}

		private Type(int sort, char[] buf, int off, int len) {
			this.sort = sort;
			this.buf = buf;
			this.off = off;
			this.len = len;
		}

		private static Type[] getArgumentTypes(String methodDescriptor) {
			char[] buf = methodDescriptor.toCharArray();
			int off = 1;
			int size = 0;

			while(true) {
				while(true) {
					char car = buf[off++];
					if (car == ')') {
						Type[] args = new Type[size];
						off = 1;

						for(size = 0; buf[off] != ')'; ++size) {
							args[size] = getType(buf, off);
							off += args[size].len + (args[size].sort == 10 ? 2 : 0);
						}

						return args;
					}

					if (car == 'L') {
						while(buf[off++] != ';') {
							;
						}

						++size;
					} else if (car != '[') {
						++size;
					}
				}
			}
		}

		private static Type getType(char[] buf, int off) {
			int len;
			switch(buf[off]) {
				case 'B':
					return BYTE_TYPE;
				case 'C':
					return CHAR_TYPE;
				case 'D':
					return DOUBLE_TYPE;
				case 'E':
				case 'G':
				case 'H':
				case 'K':
				case 'L':
				case 'M':
				case 'N':
				case 'O':
				case 'P':
				case 'Q':
				case 'R':
				case 'T':
				case 'U':
				case 'W':
				case 'X':
				case 'Y':
				default:
					for(len = 1; buf[off + len] != ';'; ++len) {
						;
					}

					return new Type(10, buf, off + 1, len - 1);
				case 'F':
					return FLOAT_TYPE;
				case 'I':
					return INT_TYPE;
				case 'J':
					return LONG_TYPE;
				case 'S':
					return SHORT_TYPE;
				case 'V':
					return VOID_TYPE;
				case 'Z':
					return BOOLEAN_TYPE;
				case '[':
					for(len = 1; buf[off + len] == '['; ++len) {
						;
					}

					if (buf[off + len] == 'L') {
						++len;

						while(buf[off + len] != ';') {
							++len;
						}
					}

					return new Type(9, buf, off, len + 1);
			}
		}

		private int getDimensions() {
			int i;
			for(i = 1; this.buf[this.off + i] == '['; ++i) {
				;
			}

			return i;
		}

		private Type getElementType() {
			return getType(this.buf, this.off + this.getDimensions());
		}

		private String getClassName() {
			switch(this.sort) {
				case 0:
					return "void";
				case 1:
					return "boolean";
				case 2:
					return "char";
				case 3:
					return "byte";
				case 4:
					return "short";
				case 5:
					return "int";
				case 6:
					return "float";
				case 7:
					return "long";
				case 8:
					return "double";
				case 9:
					StringBuffer b = new StringBuffer(this.getElementType().getClassName());

					for(int i = this.getDimensions(); i > 0; --i) {
						b.append("[]");
					}

					return b.toString();
				default:
					return (new String(this.buf, this.off, this.len)).replace('/', '.');
			}
		}
	}

	private static class ClassReader {
		public final byte[] b;
		private final int[] items;
		private final String[] strings;
		private final int maxStringLength;
		public final int header;
		static final int FIELD = 9;
		static final int METH = 10;
		static final int IMETH = 11;
		static final int INT = 3;
		static final int FLOAT = 4;
		static final int LONG = 5;
		static final int DOUBLE = 6;
		static final int NAME_TYPE = 12;
		static final int UTF8 = 1;

		private ClassReader(byte[] b) {
			this(b, 0);
		}

		private ClassReader(byte[] b, int off) {
			this.b = b;
			this.items = new int[this.readUnsignedShort(off + 8)];
			int n = this.items.length;
			this.strings = new String[n];
			int max = 0;
			int index = off + 10;

			for(int i = 1; i < n; ++i) {
				this.items[i] = index + 1;
				int size;
				switch(b[index]) {
					case 1:
						size = 3 + this.readUnsignedShort(index + 1);
						if (size > max) {
							max = size;
						}
						break;
					case 2:
					case 7:
					case 8:
					default:
						size = 3;
						break;
					case 3:
					case 4:
					case 9:
					case 10:
					case 11:
					case 12:
						size = 5;
						break;
					case 5:
					case 6:
						size = 9;
						++i;
				}

				index += size;
			}

			this.maxStringLength = max;
			this.header = index;
		}

		private ClassReader(InputStream byteCodeStream, InputStream is) throws IOException {
			this(readClass(is));
		}

		private static byte[] readClass(InputStream is) throws IOException {
			if (is == null) {
				throw new IOException("Class not found");
			} else {
				byte[] b = new byte[is.available()];
				int len = 0;

				while(true) {
					int n = is.read(b, len, b.length - len);
					if (n == -1) {
						if (len < b.length) {
							byte[] c = new byte[len];
							System.arraycopy(b, 0, c, 0, len);
							b = c;
						}

						return b;
					}

					len += n;
					if (len == b.length) {
						int last = is.read();
						if (last < 0) {
							return b;
						}

						byte[] c = new byte[b.length + 1000];
						System.arraycopy(b, 0, c, 0, len);
						c[len++] = (byte)last;
						b = c;
					}
				}
			}
		}

		private void accept(TypeCollector classVisitor) {
			char[] c = new char[this.maxStringLength];
			int anns = 0;
			int ianns = 0;
			int u = this.header;
			int v = this.items[this.readUnsignedShort(u + 4)];
			int len = this.readUnsignedShort(u + 6);
			int w = 0;
			u += 8;

			int i;
			for(i = 0; i < len; ++i) {
				u += 2;
			}

			i = this.readUnsignedShort(u);

			int j;
			for(v = u + 2; i > 0; --i) {
				j = this.readUnsignedShort(v + 6);

				for(v += 8; j > 0; --j) {
					v += 6 + this.readInt(v + 2);
				}
			}

			i = this.readUnsignedShort(v);

			for(v += 2; i > 0; --i) {
				j = this.readUnsignedShort(v + 6);

				for(v += 8; j > 0; --j) {
					v += 6 + this.readInt(v + 2);
				}
			}

			i = this.readUnsignedShort(v);

			for(v += 2; i > 0; --i) {
				v += 6 + this.readInt(v + 2);
			}

			i = this.readUnsignedShort(u);

			for(u += 2; i > 0; --i) {
				j = this.readUnsignedShort(u + 6);

				for(u += 8; j > 0; --j) {
					u += 6 + this.readInt(u + 2);
				}
			}

			i = this.readUnsignedShort(u);

			for(u += 2; i > 0; --i) {
				u = this.readMethod(classVisitor, c, u);
			}

		}

		private int readMethod(TypeCollector classVisitor, char[] c, int u) {
			int access = this.readUnsignedShort(u);
			String name = this.readUTF8(u + 2, c);
			String desc = this.readUTF8(u + 4, c);
			int v = 0;
			int w = 0;
			int j = this.readUnsignedShort(u + 6);

			String attrName;
			for(u += 8; j > 0; --j) {
				attrName = this.readUTF8(u, c);
				int attrSize = this.readInt(u + 2);
				u += 6;
				if (attrName.equals("Code")) {
					v = u;
				}

				u += attrSize;
			}

			//int w;
			if (w != 0) {
				w = w + 2;

				for(j = 0; j < this.readUnsignedShort(w); ++j) {
					w += 2;
				}
			}

			MethodCollector mv = classVisitor.visitMethod(access, name, desc);
			if (mv != null && v != 0) {
				int codeLength = this.readInt(v + 4);
				v += 8;
				int codeEnd = v + codeLength;
				j = this.readUnsignedShort(codeEnd);

				for(v = codeEnd + 2; j > 0; --j) {
					v += 8;
				}

				int varTable = 0;
				int varTypeTable = 0;
				j = this.readUnsignedShort(v);

				for(v += 2; j > 0; --j) {
					attrName = this.readUTF8(v, c);
					if (attrName.equals("LocalVariableTable")) {
						varTable = v + 6;
					} else if (attrName.equals("LocalVariableTypeTable")) {
						varTypeTable = v + 6;
					}

					v += 6 + this.readInt(v + 2);
				}

				if (varTable != 0) {
					int k;
					if (varTypeTable != 0) {
						k = this.readUnsignedShort(varTypeTable) * 3;
						w = varTypeTable + 2;

						for(int[] typeTable = new int[k]; k > 0; w += 10) {
							--k;
							typeTable[k] = w + 6;
							--k;
							typeTable[k] = this.readUnsignedShort(w + 8);
							--k;
							typeTable[k] = this.readUnsignedShort(w);
						}
					}

					k = this.readUnsignedShort(varTable);

					for(w = varTable + 2; k > 0; --k) {
						int index = this.readUnsignedShort(w + 8);
						mv.visitLocalVariable(this.readUTF8(w + 4, c), index);
						w += 10;
					}
				}
			}

			return u;
		}

		private int readUnsignedShort(int index) {
			byte[] b = this.b;
			return (b[index] & 255) << 8 | b[index + 1] & 255;
		}

		private int readInt(int index) {
			byte[] b = this.b;
			return (b[index] & 255) << 24 | (b[index + 1] & 255) << 16 | (b[index + 2] & 255) << 8 | b[index + 3] & 255;
		}

		private String readUTF8(int index, char[] buf) {
			int item = this.readUnsignedShort(index);
			String s = this.strings[item];
			if (s != null) {
				return s;
			} else {
				index = this.items[item];
				return this.strings[item] = this.readUTF(index + 2, this.readUnsignedShort(index), buf);
			}
		}

		private String readUTF(int index, int utfLen, char[] buf) {
			int endIndex = index + utfLen;
			byte[] b = this.b;
			int strLen = 0;
			int st = 0;
			char cc = 0;

			while(true) {
				while(index < endIndex) {
					int c = b[index++];
					switch(st) {
						case 0:
							c = c & 255;
							if (c < 128) {
								buf[strLen++] = (char)c;
							} else {
								if (c < 224 && c > 191) {
									cc = (char)(c & 31);
									st = 1;
									continue;
								}

								cc = (char)(c & 15);
								st = 2;
							}
							break;
						case 1:
							buf[strLen++] = (char)(cc << 6 | c & 63);
							st = 0;
							break;
						case 2:
							cc = (char)(cc << 6 | c & 63);
							st = 1;
					}
				}

				return new String(buf, 0, strLen);
			}
		}
	}

	private static class MethodCollector {
		private final int paramCount;
		private final int ignoreCount;
		private int currentParameter;
		private final StringBuffer result;
		private boolean debugInfoPresent;

		private MethodCollector(int ignoreCount, int paramCount, Object o) {
			this.ignoreCount = ignoreCount;
			this.paramCount = paramCount;
			this.result = new StringBuffer();
			this.currentParameter = 0;
			this.debugInfoPresent = paramCount == 0;
		}

		public void visitLocalVariable(String name, int index) {
			if (index >= this.ignoreCount && index < this.ignoreCount + this.paramCount) {
				if (!name.equals("arg" + this.currentParameter)) {
					this.debugInfoPresent = true;
				}

				this.result.append(',');
				this.result.append(name);
				++this.currentParameter;
			}

		}

		private String getResult() {
			return this.result.length() != 0 ? this.result.substring(1) : "";
		}

		private boolean isDebugInfoPresent() {
			return this.debugInfoPresent;
		}
	}

	private static class TypeCollector {
		private static final String COMMA = ",";
		private final String methodName;
		private final Class<?>[] parameterTypes;
		private final boolean throwExceptionIfMissing;
		private MethodCollector collector;

		private TypeCollector(String methodName, Class<?>[] parameterTypes, boolean throwExceptionIfMissing, Object o) {
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
			this.throwExceptionIfMissing = throwExceptionIfMissing;
			this.collector = null;
		}

		public MethodCollector visitMethod(int access, String name, String desc) {
			if (this.collector != null) {
				return null;
			} else if (!name.equals(this.methodName)) {
				return null;
			} else {
				Type[] argumentTypes = Type.getArgumentTypes(desc);
				int longOrDoubleQuantity = 0;
				Type[] arr$ = argumentTypes;
				int i = argumentTypes.length;

				for(int i$ = 0; i$ < i; ++i$) {
					Type t = arr$[i$];
					if (t.getClassName().equals("long") || t.getClassName().equals("double")) {
						++longOrDoubleQuantity;
					}
				}

				int paramCount = argumentTypes.length;
				if (paramCount != this.parameterTypes.length) {
					return null;
				} else {
					for(i = 0; i < argumentTypes.length; ++i) {
						if (!this.correctTypeName(argumentTypes, i).equals(this.parameterTypes[i].getName())) {
							return null;
						}
					}

					this.collector = new MethodCollector(Modifier.isStatic(access) ? 0 : 1, argumentTypes.length + longOrDoubleQuantity, null);
					return this.collector;
				}
			}
		}

		private String correctTypeName(Type[] argumentTypes, int i) {
			String s = argumentTypes[i].getClassName();
			if (s.endsWith("[]")) {
				String prefix = s.substring(0, s.length() - 2);
				if (BytecodeReadingParanamer.primitives.containsKey(prefix)) {
					s = "[" + (String)BytecodeReadingParanamer.primitives.get(prefix);
				} else {
					s = "[L" + prefix + ";";
				}
			}

			return s;
		}

		private String[] getParameterNamesForMethod() {
			if (this.collector == null) {
				return EMPTY_NAMES;
			} else if (!this.collector.isDebugInfoPresent()) {
				if (this.throwExceptionIfMissing) {
					throw new ParameterNamesNotFoundException("Parameter names not found for " + this.methodName);
				} else {
					return EMPTY_NAMES;
				}
			} else {
				return this.collector.getResult().split(",");
			}
		}
	}
}
