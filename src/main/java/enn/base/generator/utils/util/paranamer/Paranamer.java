//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.paranamer;

import java.lang.reflect.AccessibleObject;

public interface Paranamer {
	String[] EMPTY_NAMES = new String[0];

	String[] lookupParameterNames(AccessibleObject var1);

	String[] lookupParameterNames(AccessibleObject var1, boolean var2);
}
