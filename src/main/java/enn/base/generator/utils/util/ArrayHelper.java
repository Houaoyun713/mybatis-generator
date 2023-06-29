//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

public class ArrayHelper {
	public ArrayHelper() {
	}

	public static String getValue(String[] array, int indexOf) {
		return getValue(array, indexOf, (String)null);
	}

	public static String getValue(String[] array, int indexOf, String defaultValue) {
		return array.length - 1 >= indexOf ? array[indexOf] : defaultValue;
	}
}
