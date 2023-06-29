//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class DateHelper {
	public DateHelper() {
	}

	public static Date parseDate(String value, Class targetType, String... formats) {
		String[] arr$ = formats;
		int len$ = formats.length;
		int i$ = 0;

		while(i$ < len$) {
			String format = arr$[i$];

			try {
				long v = (new SimpleDateFormat(format)).parse(value).getTime();
				return (Date)targetType.getConstructor(Long.TYPE).newInstance(v);
			} catch (ParseException var10) {
				try {
					return (Date)targetType.getConstructor(String.class).newInstance(value);
				} catch (Exception var9) {
					++i$;
				}
			} catch (Exception var11) {
				throw new RuntimeException(var11);
			}
		}

		throw new IllegalArgumentException("cannot parse:" + value + " for date by formats:" + Arrays.asList(formats));
	}

	public static boolean isDateType(Class<?> targetType) {
		if (targetType == null) {
			return false;
		} else {
			return targetType == Date.class || targetType == Timestamp.class || targetType == java.sql.Date.class || targetType == Time.class;
		}
	}
}
