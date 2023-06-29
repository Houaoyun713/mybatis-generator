//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.typemapping;

import enn.base.generator.utils.provider.java.model.JavaClass;
import enn.base.generator.utils.util.StringHelper;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

public class JavaImport {
	TreeSet<String> imports = new TreeSet();

	public JavaImport() {
	}

	public void addImport(String javaType) {
		if (isNeedImport(javaType)) {
			this.imports.add(javaType.replace("$", "."));
		}

	}

	public void addImport(JavaImport javaImport) {
		if (javaImport != null) {
			this.imports.addAll(javaImport.getImports());
		}

	}

	public TreeSet<String> getImports() {
		return this.imports;
	}

	public static void addImportClass(Set<JavaClass> set, Class... clazzes) {
		if (clazzes != null) {
			Class[] arr$ = clazzes;
			int len$ = clazzes.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				Class c = arr$[i$];
				if (c != null && !c.getName().startsWith("java.lang.") && !c.isPrimitive() && !"void".equals(c.getName()) && !c.isAnonymousClass() && Modifier.isPublic(c.getModifiers()) && isNeedImport(c.getName())) {
					set.add(new JavaClass(c));
				}
			}

		}
	}

	public static boolean isNeedImport(String type) {
		if (StringHelper.isBlank(type)) {
			return false;
		} else if ("void".equals(type)) {
			return false;
		} else if (type.startsWith("java.lang.")) {
			return false;
		} else if (JavaPrimitiveTypeMapping.getPrimitiveTypeOrNull(type) != null) {
			return false;
		} else {
			return type.indexOf(".") >= 0 && !Character.isLowerCase(StringHelper.getJavaClassSimpleName(type).charAt(0));
		}
	}
}
