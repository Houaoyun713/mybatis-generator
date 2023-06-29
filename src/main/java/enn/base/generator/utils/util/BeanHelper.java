//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanHelper {
	public BeanHelper() {
	}

	public static Map describe(Object obj, String... ignoreProperties) {
		if (obj instanceof Map) {
			return (Map)obj;
		} else {
			Map map = new HashMap();
			PropertyDescriptor[] descriptors = getPropertyDescriptors(obj.getClass());

			for(int i = 0; i < descriptors.length; ++i) {
				String name = descriptors[i].getName();
				if (!contains(ignoreProperties, name)) {
					Method readMethod = descriptors[i].getReadMethod();
					if (readMethod != null) {
						try {
							long start = System.currentTimeMillis();
							map.put(name, readMethod.invoke(obj));
							long var9 = start - System.currentTimeMillis();
						} catch (Exception var11) {
							GLogger.warn("error get property value,name:" + name + " on bean:" + obj, var11);
						}
					}
				}
			}

			return map;
		}
	}

	private static boolean contains(String[] array, String str) {
		if (array == null) {
			return false;
		} else if (str == null) {
			return false;
		} else {
			String[] arr$ = array;
			int len$ = array.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				String ignore = arr$[i$];
				if (str.equals(ignore)) {
					return true;
				}
			}

			return false;
		}
	}

	public static PropertyDescriptor getPropertyDescriptor(Class beanClass, String propertyName, boolean ignoreCase) {
		PropertyDescriptor[] arr$ = getPropertyDescriptors(beanClass);
		int len$ = arr$.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			PropertyDescriptor pd = arr$[i$];
			if (ignoreCase) {
				if (pd.getName().equalsIgnoreCase(propertyName)) {
					return pd;
				}
			} else if (pd.getName().equals(propertyName)) {
				return pd;
			}
		}

		return null;
	}

	public static PropertyDescriptor[] getPropertyDescriptors(Class beanClass) {
		BeanInfo beanInfo = null;

		try {
			beanInfo = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException var3) {
			return new PropertyDescriptor[0];
		}

		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		if (descriptors == null) {
			descriptors = new PropertyDescriptor[0];
		}

		return descriptors;
	}

	public static void copyProperties(Object target, Object source) {
		copyProperties(target, source, false);
	}

	public static void copyProperties(Object target, Object source, boolean ignoreCase) {
		copyProperties(target, source, ignoreCase, (String[])null);
	}

	public static void copyProperties(Object target, Map source) {
		copyProperties(target, source, false);
	}

	public static void copyProperties(Object target, Map source, boolean ignoreCase) {
		Set<String> keys = source.keySet();
		Iterator i$ = keys.iterator();

		while(i$.hasNext()) {
			String key = (String)i$.next();
			PropertyDescriptor pd = getPropertyDescriptor(target.getClass(), key, ignoreCase);
			if (pd == null) {
				throw new IllegalArgumentException("not found property:'" + key + "' on class:" + target.getClass());
			}

			setProperty(target, pd, source.get(key));
		}

	}

	public static void copyProperties(Object target, Object source, boolean ignoreCase, String[] ignoreProperties) {
		if (target instanceof Map) {
			throw new UnsupportedOperationException("target is Map unsuported");
		} else {
			PropertyDescriptor[] targetPds = getPropertyDescriptors(target.getClass());
			List<String> ignoreList = ignoreProperties != null ? Arrays.asList(ignoreProperties) : null;

			for(int i = 0; i < targetPds.length; ++i) {
				PropertyDescriptor targetPd = targetPds[i];
				if (targetPd.getWriteMethod() != null && (ignoreProperties == null || !ignoreList.contains(targetPd.getName()))) {
					try {
						Object value;
						if (source instanceof Map) {
							Map map = (Map)source;
							if (MapHelper.containsKey(map, targetPd.getName(), ignoreCase)) {
								value = MapHelper.getValue(map, targetPd.getName(), ignoreCase);
								setProperty(target, targetPd, value);
							}
						} else {
							PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName(), ignoreCase);
							if (sourcePd != null && sourcePd.getReadMethod() != null) {
								value = getProperty(source, sourcePd);
								setProperty(target, targetPd, value);
							}
						}
					} catch (Throwable var10) {
						throw new IllegalArgumentException("Could not copy properties on:" + targetPd.getDisplayName(), var10);
					}
				}
			}

		}
	}

	private static Object getProperty(Object source, PropertyDescriptor sourcePd) throws IllegalAccessException, InvocationTargetException {
		Method readMethod = sourcePd.getReadMethod();
		if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
			readMethod.setAccessible(true);
		}

		Object value = readMethod.invoke(source);
		return value;
	}

	public static void setProperty(Object target, String propertyName, Object value) {
		PropertyDescriptor pd = getPropertyDescriptor(target.getClass(), propertyName, false);
		if (pd == null) {
			throw new IllegalArgumentException("not found property:" + propertyName + " on class:" + target.getClass());
		} else {
			setProperty(target, pd, value);
		}
	}

	public static void setProperty(Object target, String propertyName, Object value, boolean ignoreCase) {
		PropertyDescriptor pd = getPropertyDescriptor(target.getClass(), propertyName, ignoreCase);
		if (pd == null) {
			throw new IllegalArgumentException("not found property:" + propertyName + " on class:" + target.getClass());
		} else {
			setProperty(target, pd, value);
		}
	}

	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException var2) {
			throw new RuntimeException(var2);
		} catch (IllegalAccessException var3) {
			throw new RuntimeException(var3);
		}
	}

	private static void setProperty(Object target, PropertyDescriptor propertyDescriptor, Object value) {
		if (propertyDescriptor == null) {
			throw new IllegalArgumentException("propertyDescriptor must be not null");
		} else if (target == null) {
			throw new IllegalArgumentException("target must be not null");
		} else {
			Method writeMethod = propertyDescriptor.getWriteMethod();
			if (writeMethod == null) {
				throw new IllegalArgumentException("not found write method for property:" + propertyDescriptor.getName() + " on class:" + target.getClass());
			} else {
				if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
					writeMethod.setAccessible(true);
				}

				try {
					writeMethod.invoke(target, convert(value, writeMethod.getParameterTypes()[0]));
				} catch (Exception var5) {
					throw new RuntimeException("error set property:" + propertyDescriptor.getName() + " on class:" + target.getClass(), var5);
				}
			}
		}
	}

	private static Object convert(Object value, Class<?> targetType) {
		if (value == null) {
			return null;
		} else {
			return targetType == String.class ? value.toString() : convert(value.toString(), targetType);
		}
	}

	private static Object convert(String value, Class<?> targetType) {
		if (targetType != Byte.class && targetType != Byte.TYPE) {
			if (targetType != Short.class && targetType != Short.TYPE) {
				if (targetType != Integer.class && targetType != Integer.TYPE) {
					if (targetType != Long.class && targetType != Long.TYPE) {
						if (targetType != Float.class && targetType != Float.TYPE) {
							if (targetType != Double.class && targetType != Double.TYPE) {
								if (targetType == BigDecimal.class) {
									return new BigDecimal(value);
								} else if (targetType == BigInteger.class) {
									return BigInteger.valueOf(Long.parseLong(value));
								} else if (targetType != Boolean.class && targetType != Boolean.TYPE) {
									if (targetType == Boolean.TYPE) {
										return new Boolean(value);
									} else if (targetType == Character.TYPE) {
										return value.charAt(0);
									} else if (DateHelper.isDateType(targetType)) {
										return DateHelper.parseDate(value, targetType, new String[]{"yyyyMMdd", "yyyy-MM-dd", "yyyyMMddHHmmSS", "yyyy-MM-dd HH:mm:ss", "HH:mm:ss"});
									} else {
										throw new IllegalArgumentException("cannot convert value:" + value + " to targetType:" + targetType);
									}
								} else {
									return new Boolean(value);
								}
							} else {
								return new Double(value);
							}
						} else {
							return new Float(value);
						}
					} else {
						return new Long(value);
					}
				} else {
					return new Integer(value);
				}
			} else {
				return new Short(value);
			}
		} else {
			return new Byte(value);
		}
	}

	static class MapHelper {
		MapHelper() {
		}

		public static Object getValue(Map map, String property, boolean ignoreCase) {
			if (ignoreCase) {
				Iterator i$ = map.keySet().iterator();

				Object key;
				do {
					if (!i$.hasNext()) {
						return null;
					}

					key = i$.next();
				} while(!property.equalsIgnoreCase(key.toString()));

				return map.get(key);
			} else {
				return map.get(property);
			}
		}

		public static boolean containsKey(Map map, String property, boolean ignoreCase) {
			if (ignoreCase) {
				Iterator i$ = map.keySet().iterator();

				Object key;
				do {
					if (!i$.hasNext()) {
						return false;
					}

					key = i$.next();
				} while(!property.equalsIgnoreCase(key.toString()));

				return true;
			} else {
				return map.containsKey(property);
			}
		}
	}
}
