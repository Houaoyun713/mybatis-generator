//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

public class ClassHelper {
	public ClassHelper() {
	}

	public static Object newInstance(Class<?> c) {
		try {
			return c.newInstance();
		} catch (Exception var2) {
			throw new IllegalArgumentException("cannot new instance with class:" + c.getName(), var2);
		}
	}

	public static Object newInstance(String className) {
		try {
			return newInstance(Class.forName(className));
		} catch (Exception var2) {
			throw new IllegalArgumentException("cannot new instance with className:" + className, var2);
		}
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;

		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable var2) {
			;
		}

		if (cl == null) {
			cl = ClassHelper.class.getClassLoader();
		}

		return cl;
	}
}
