//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import enn.base.generator.utils.util.PropertyPlaceholderHelper.PropertyPlaceholderConfigurerResolver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

public class PropertiesHelper {
	boolean isSearchSystemProperty = false;
	Properties p;
	static PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}", ":", false);

	public PropertiesHelper(Properties p) {
		this.p = p;
	}

	public PropertiesHelper(Properties p, boolean isSearchSystemProperty) {
		this.p = resolveProperties(p);
		this.isSearchSystemProperty = isSearchSystemProperty;
	}

	public Properties getProperties() {
		return this.p;
	}

	public String getProperty(String key, String defaultValue) {
		String value = null;
		if (this.isSearchSystemProperty) {
			value = System.getProperty(key);
		}

		if (value == null || "".equals(value.trim())) {
			value = this.getProperties().getProperty(key);
		}

		return value != null && !"".equals(value.trim()) ? value : defaultValue;
	}

	public String getProperty(String key) {
		return this.getProperty(key, (String)null);
	}

	public String getRequiredProperty(String key) {
		String value = this.getProperty(key);
		if (value != null && !"".equals(value.trim())) {
			return value;
		} else {
			throw new IllegalStateException("required property is blank by key=" + key);
		}
	}

	public Integer getInt(String key) {
		return this.getProperty(key) == null ? null : Integer.parseInt(this.getRequiredProperty(key));
	}

	public int getInt(String key, int defaultValue) {
		return this.getProperty(key) == null ? defaultValue : Integer.parseInt(this.getRequiredProperty(key));
	}

	public int getRequiredInt(String key) {
		return Integer.parseInt(this.getRequiredProperty(key));
	}

	public String[] getStringArray(String key) {
		String v = this.getProperty(key);
		return v == null ? new String[0] : StringHelper.tokenizeToStringArray(v, ", \t\n\r\f");
	}

	public int[] getIntArray(String key) {
		String[] array = this.getStringArray(key);
		int[] result = new int[array.length];

		for(int i = 0; i < array.length; ++i) {
			result[i] = Integer.parseInt(array[i]);
		}

		return result;
	}

	public Boolean getBoolean(String key) {
		return this.getProperty(key) == null ? null : Boolean.parseBoolean(this.getRequiredProperty(key));
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return this.getProperty(key) == null ? defaultValue : Boolean.parseBoolean(this.getRequiredProperty(key));
	}

	public boolean getRequiredBoolean(String key) {
		return Boolean.parseBoolean(this.getRequiredProperty(key));
	}

	public String getNullIfBlank(String key) {
		String value = this.getProperty(key);
		return value != null && !"".equals(value.trim()) ? value : null;
	}

	public PropertiesHelper setProperty(String key, String value) {
		value = resolveProperty(value, this.getProperties());
		key = resolveProperty(key, this.getProperties());
		this.p.setProperty(key, value);
		return this;
	}

	public PropertiesHelper putAll(Properties props) {
		this.p.putAll(resolveProperties(props));
		return this;
	}

	public void clear() {
		this.p.clear();
	}

	public Set<Entry<Object, Object>> entrySet() {
		return this.p.entrySet();
	}

	public Enumeration<?> propertyNames() {
		return this.p.propertyNames();
	}

	public static Properties load(String... files) throws InvalidPropertiesFormatException, IOException {
		Properties properties = new Properties();
		String[] arr$ = files;
		int len$ = files.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			String f = arr$[i$];
			File file = FileHelper.getFile(f);
			FileInputStream input = new FileInputStream(file);

			try {
				if (file.getPath().endsWith(".xml")) {
					properties.loadFromXML(input);
				} else {
					properties.load(input);
				}

				properties.putAll(properties);
			} finally {
				input.close();
			}
		}

		return properties;
	}

	public static String[] loadAllPropertiesFromClassLoader(Properties properties, String... resourceNames) throws IOException {
		List successLoadProperties = new ArrayList();
		String[] arr$ = resourceNames;
		int len$ = resourceNames.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			String resourceName = arr$[i$];
			Enumeration urls = ClassHelper.getDefaultClassLoader().getResources(resourceName);

			while(urls.hasMoreElements()) {
				URL url = (URL)urls.nextElement();
				successLoadProperties.add(url.getFile());
				InputStream input = null;

				try {
					URLConnection con = url.openConnection();
					con.setUseCaches(false);
					input = con.getInputStream();
					if (resourceName.endsWith(".xml")) {
						properties.loadFromXML(input);
					} else {
						properties.load(input);
					}
				} finally {
					if (input != null) {
						input.close();
					}

				}
			}
		}

		return (String[])((String[])successLoadProperties.toArray(new String[0]));
	}

	private static Properties resolveProperties(Properties props) {
		Properties result = new Properties();
		Iterator i$ = props.keySet().iterator();

		while(i$.hasNext()) {
			Object s = i$.next();
			String sourceKey = s.toString();
			String key = resolveProperty(sourceKey, props);
			String value = resolveProperty(props.getProperty(sourceKey), props);
			result.setProperty(key, value);
		}

		return result;
	}

	private static String resolveProperty(String v, Properties props) {
		PropertyPlaceholderConfigurerResolver propertyPlaceholderConfigurerResolver = new PropertyPlaceholderConfigurerResolver(props);
		return propertyPlaceholderHelper.replacePlaceholders(v, propertyPlaceholderConfigurerResolver);
	}
}
