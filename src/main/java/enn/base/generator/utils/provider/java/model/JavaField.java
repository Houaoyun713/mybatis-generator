//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.java.model;

import enn.base.generator.utils.util.typemapping.ActionScriptDataTypesUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class JavaField {
	private Field field;
	private JavaClass clazz;

	public JavaField(Field field, JavaClass clazz) {
		this.field = field;
		this.clazz = clazz;
	}

	public String getFieldName() {
		return this.field.getName();
	}

	public JavaClass getType() {
		return new JavaClass(this.field.getType());
	}

	public boolean isAccessible() {
		return this.field.isAccessible();
	}

	public boolean isEnumConstant() {
		return this.field.isEnumConstant();
	}

	public String toGenericString() {
		return this.field.toGenericString();
	}

	public JavaClass getClazz() {
		return this.clazz;
	}

	public String getJavaType() {
		return this.field.getType().getName();
	}

	public String getAsType() {
		return ActionScriptDataTypesUtils.getPreferredAsType(this.getJavaType());
	}

	public Annotation[] getAnnotations() {
		return this.field.getAnnotations();
	}

	public boolean getIsDateTimeField() {
		return this.getJavaType().equalsIgnoreCase("java.util.Date") || this.getJavaType().equalsIgnoreCase("java.sql.Date") || this.getJavaType().equalsIgnoreCase("java.sql.Timestamp") || this.getJavaType().equalsIgnoreCase("java.sql.Time");
	}

	public int hashCode() {
		boolean prime = true;
		int result = 1;
		result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
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
			JavaField other = (JavaField)obj;
			if (this.field == null) {
				if (other.field != null) {
					return false;
				}
			} else if (!this.field.equals(other.field)) {
				return false;
			}

			return true;
		}
	}

	public String toString() {
		return "JavaClass:" + this.clazz + " JavaField:" + this.getFieldName();
	}
}
