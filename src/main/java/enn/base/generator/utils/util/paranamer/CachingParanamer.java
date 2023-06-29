//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.paranamer;

import java.lang.reflect.AccessibleObject;
import java.util.WeakHashMap;

public class CachingParanamer implements Paranamer {
	public static final String __PARANAMER_DATA = "v1.0 \ncom.thoughtworks.paranamer.CachingParanamer <init> com.thoughtworks.paranamer.Paranamer delegate \ncom.thoughtworks.paranamer.CachingParanamer lookupParameterNames java.lang.AccessibleObject methodOrConstructor \ncom.thoughtworks.paranamer.CachingParanamer lookupParameterNames java.lang.AccessibleObject, boolean methodOrCtor,throwExceptionIfMissing \n";
	private final Paranamer delegate;
	private final WeakHashMap<AccessibleObject, String[]> methodCache;

	public CachingParanamer() {
		this(new DefaultParanamer());
	}

	public CachingParanamer(Paranamer delegate) {
		this.methodCache = new WeakHashMap();
		this.delegate = delegate;
	}

	public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
		return this.lookupParameterNames(methodOrConstructor, true);
	}

	public String[] lookupParameterNames(AccessibleObject methodOrCtor, boolean throwExceptionIfMissing) {
		if (this.methodCache.containsKey(methodOrCtor)) {
			return (String[])this.methodCache.get(methodOrCtor);
		} else {
			String[] names = this.delegate.lookupParameterNames(methodOrCtor, throwExceptionIfMissing);
			this.methodCache.put(methodOrCtor, names);
			return names;
		}
	}
}
