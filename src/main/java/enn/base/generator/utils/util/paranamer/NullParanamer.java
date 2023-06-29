//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.paranamer;

import java.lang.reflect.AccessibleObject;

public class NullParanamer implements Paranamer {
	public NullParanamer() {
	}

	public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
		return new String[0];
	}

	public String[] lookupParameterNames(AccessibleObject methodOrConstructor, boolean throwExceptionIfMissing) {
		if (throwExceptionIfMissing) {
			throw new ParameterNamesNotFoundException("NullParanamer implementation predictably finds no parameter names");
		} else {
			return new String[0];
		}
	}
}
