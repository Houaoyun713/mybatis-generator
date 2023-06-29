//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.java.model;

import enn.base.generator.utils.util.typemapping.ActionScriptDataTypesUtils;
import enn.base.generator.utils.util.typemapping.JavaPrimitiveTypeMapping;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class JavaProperty {
	PropertyDescriptor propertyDescriptor;
	JavaClass clazz;

	public JavaProperty(PropertyDescriptor pd, JavaClass javaClass) {
		this.propertyDescriptor = pd;
		this.clazz = javaClass;
	}

	public String getName() {
		return this.propertyDescriptor.getName();
	}

	public String getJavaType() {
		return this.propertyDescriptor.getPropertyType().getName();
	}

	public String getPrimitiveJavaType() {
		return JavaPrimitiveTypeMapping.getPrimitiveType(this.getJavaType());
	}

	public JavaClass getPropertyType() {
		return new JavaClass(this.propertyDescriptor.getPropertyType());
	}

	public String getDisplayName() {
		return this.propertyDescriptor.getDisplayName();
	}

	public JavaMethod getReadMethod() {
		return new JavaMethod(this.propertyDescriptor.getReadMethod(), this.clazz);
	}

	public JavaMethod getWriteMethod() {
		return new JavaMethod(this.propertyDescriptor.getWriteMethod(), this.clazz);
	}

	public boolean isHasReadMethod() {
		return this.propertyDescriptor.getReadMethod() != null;
	}

	public boolean isHasWriteMethod() {
		return this.propertyDescriptor.getWriteMethod() != null;
	}

	public String getAsType() {
		return ActionScriptDataTypesUtils.getPreferredAsType(this.propertyDescriptor.getPropertyType().getName());
	}

	public boolean isPk() {
		return JPAUtils.isPk(this.propertyDescriptor.getReadMethod());
	}

	public JavaClass getClazz() {
		return this.clazz;
	}

	public String toString() {
		return "JavaClass:" + this.clazz + " JavaProperty:" + this.getName();
	}

	public static class JPAUtils {
		private static boolean isJPAClassAvaiable = false;

		public JPAUtils() {
		}

		public static boolean isPk(Method readMethod) {
			return isJPAClassAvaiable && readMethod != null && readMethod.isAnnotationPresent(classForName("javax.persistence.Id"));
		}

		private static Class classForName(String clazz) {
			try {
				return Class.forName(clazz);
			} catch (ClassNotFoundException var2) {
				return null;
			}
		}

		static {
			try {
				Class.forName("javax.persistence.Table");
				isJPAClassAvaiable = true;
			} catch (ClassNotFoundException var1) {
				;
			}

		}
	}
}
