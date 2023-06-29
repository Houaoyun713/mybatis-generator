//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util.sqlparse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class NamedParameterUtils {
	private static final char[] PARAMETER_SEPARATORS = new char[]{'"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^'};
	private static final String[] START_SKIP = new String[]{"'", "\"", "--", "/*"};
	private static final String[] STOP_SKIP = new String[]{"'", "\"", "\n", "*/"};

	public NamedParameterUtils() {
	}

	public static ParsedSql parseSqlStatement(String sql) throws IllegalArgumentException {
		if (sql == null) {
			throw new IllegalArgumentException("SQL must not be null");
		} else {
			Set<String> namedParameters = new HashSet();
			ParsedSql parsedSql = new ParsedSql(sql);
			char[] statement = sql.toCharArray();
			int namedParameterCount = 0;
			int unnamedParameterCount = 0;
			int totalParameterCount = 0;
			int i = 0;

			while(i < statement.length) {
				int skipToPosition = skipCommentsAndQuotes(statement, i);
				if (i != skipToPosition) {
					if (skipToPosition >= statement.length) {
						break;
					}

					i = skipToPosition;
				}

				char c = statement[i];
				if (c != ':' && c != '&' && c != '#' && c != '$') {
					if (c == '?') {
						++unnamedParameterCount;
						++totalParameterCount;
					}
				} else {
					int j = i + 1;
					if (j < statement.length && statement[j] == ':' && c == ':') {
						i += 2;
						continue;
					}

					while(j < statement.length && !isParameterSeparator(statement[j])) {
						++j;
					}

					if (j - i > 1) {
						String parameter = sql.substring(i + 1, j);
						if (!namedParameters.contains(parameter)) {
							namedParameters.add(parameter);
							++namedParameterCount;
						}

						String removedPrefixAndSuffixParameter = removePrefixAndSuffix(c, parameter, sql);
						parsedSql.addNamedParameter(removedPrefixAndSuffixParameter, c + parameter, i, j);
						++totalParameterCount;
					}

					i = j - 1;
				}

				++i;
			}

			parsedSql.setNamedParameterCount(namedParameterCount);
			parsedSql.setUnnamedParameterCount(unnamedParameterCount);
			parsedSql.setTotalParameterCount(totalParameterCount);
			return parsedSql;
		}
	}

	private static String removePrefixAndSuffix(char startPrifix, String parameter, String sql) {
		if (startPrifix != ':' && startPrifix != '&') {
			if (!parameter.startsWith("{") && !parameter.endsWith("}")) {
				if (startPrifix == '#') {
					if (parameter.endsWith("#")) {
						parameter = parameter.substring(0, parameter.length() - 1);
						return parameter.endsWith("[]") ? parameter.substring(0, parameter.length() - 2) : parameter;
					} else {
						throw new IllegalArgumentException("parameter error:" + parameter + ",must wrap with #param#,sql:" + sql);
					}
				} else if (startPrifix == '$') {
					if (parameter.endsWith("$")) {
						parameter = parameter.substring(0, parameter.length() - 1);
						return parameter;
					} else {
						throw new IllegalArgumentException("parameter error:" + parameter + ",must wrap with $param$,sql:" + sql);
					}
				} else {
					throw new IllegalArgumentException("cannot reach this");
				}
			} else if (parameter.startsWith("{") && parameter.endsWith("}")) {
				parameter = parameter.substring(1, parameter.length() - 1);
				return parameter.replaceAll("\\[.*?\\]", "");
			} else {
				throw new IllegalArgumentException("parameter error:" + parameter + ",must wrap with {param},sql:" + sql);
			}
		} else {
			return parameter;
		}
	}

	private static int skipCommentsAndQuotes(char[] statement, int position) {
		for(int i = 0; i < START_SKIP.length; ++i) {
			if (statement[position] == START_SKIP[i].charAt(0)) {
				boolean match = true;

				int offset;
				for(offset = 1; offset < START_SKIP[i].length(); ++offset) {
					if (statement[position + offset] != START_SKIP[i].charAt(offset)) {
						match = false;
						break;
					}
				}

				if (match) {
					offset = START_SKIP[i].length();

					for(int m = position + offset; m < statement.length; ++m) {
						if (statement[m] == STOP_SKIP[i].charAt(0)) {
							boolean endMatch = true;
							int endPos = m;

							for(int n = 1; n < STOP_SKIP[i].length(); ++n) {
								if (m + n >= statement.length) {
									return statement.length;
								}

								if (statement[m + n] != STOP_SKIP[i].charAt(n)) {
									endMatch = false;
									break;
								}

								endPos = m + n;
							}

							if (endMatch) {
								return endPos + 1;
							}
						}
					}

					return statement.length;
				}
			}
		}

		return position;
	}

	public static String substituteNamedParameters(ParsedSql parsedSql) {
		String originalSql = parsedSql.getOriginalSql();
		StringBuilder actualSql = new StringBuilder();
		List paramNames = parsedSql.getParameterNames();
		int lastIndex = 0;

		for(int i = 0; i < paramNames.size(); ++i) {
			String paramName = (String)paramNames.get(i);
			int[] indexes = parsedSql.getParameterIndexes(i);
			int startIndex = indexes[0];
			int endIndex = indexes[1];
			actualSql.append(originalSql.substring(lastIndex, startIndex));
			actualSql.append("?");
			lastIndex = endIndex;
		}

		actualSql.append(originalSql.substring(lastIndex, originalSql.length()));
		return actualSql.toString();
	}

	private static boolean isParameterSeparator(char c) {
		if (Character.isWhitespace(c)) {
			return true;
		} else {
			char[] arr$ = PARAMETER_SEPARATORS;
			int len$ = arr$.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				char separator = arr$[i$];
				if (c == separator) {
					return true;
				}
			}

			return false;
		}
	}
}
