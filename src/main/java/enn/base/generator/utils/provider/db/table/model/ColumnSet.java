//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.db.table.model;

import enn.base.generator.utils.util.StringHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class ColumnSet implements Serializable {
	private static final long serialVersionUID = -6500047411657968878L;
	private LinkedHashSet<Column> columns = new LinkedHashSet();

	public ColumnSet() {
	}

	public ColumnSet(Collection<? extends Column> columns) {
		this.columns = new LinkedHashSet(columns);
	}

	public LinkedHashSet<Column> getColumns() {
		return this.columns;
	}

	public void setColumns(LinkedHashSet<Column> columns) {
		this.columns = columns;
	}

	public void addColumn(Column c) {
		this.columns.add(c);
	}

	public Column getBySqlName(String name, int sqlType) {
		Iterator i$ = this.columns.iterator();

		Column c;
		do {
			if (!i$.hasNext()) {
				return null;
			}

			c = (Column)i$.next();
		} while(!name.equalsIgnoreCase(c.getSqlName()) || c.getSqlType() != sqlType);

		return c;
	}

	public Column getBySqlName(String name) {
		if (name == null) {
			return null;
		} else {
			Iterator i$ = this.columns.iterator();

			Column c;
			do {
				if (!i$.hasNext()) {
					return null;
				}

				c = (Column)i$.next();
			} while(!name.equalsIgnoreCase(c.getSqlName()));

			return c;
		}
	}

	public Column getByName(String name) {
		if (name == null) {
			return null;
		} else {
			Column c = this.getBySqlName(name);
			if (c == null) {
				c = this.getBySqlName(StringHelper.toUnderscoreName(name));
			}

			return c;
		}
	}

	public Column getByName(String name, int sqlType) {
		Column c = this.getBySqlName(name, sqlType);
		if (c == null) {
			c = this.getBySqlName(StringHelper.toUnderscoreName(name), sqlType);
		}

		return c;
	}

	public Column getByColumnName(String name) {
		if (name == null) {
			return null;
		} else {
			Iterator i$ = this.columns.iterator();

			Column c;
			do {
				if (!i$.hasNext()) {
					return null;
				}

				c = (Column)i$.next();
			} while(!name.equals(c.getColumnName()));

			return c;
		}
	}

	public List<Column> getPkColumns() {
		List results = new ArrayList();
		Iterator i$ = this.getColumns().iterator();

		while(i$.hasNext()) {
			Column c = (Column)i$.next();
			if (c.isPk()) {
				results.add(c);
			}
		}

		return results;
	}

	public List<Column> getNotPkColumns() {
		List results = new ArrayList();
		Iterator i$ = this.getColumns().iterator();

		while(i$.hasNext()) {
			Column c = (Column)i$.next();
			if (!c.isPk()) {
				results.add(c);
			}
		}

		return results;
	}

	public int getPkCount() {
		int pkCount = 0;
		Iterator i$ = this.columns.iterator();

		while(i$.hasNext()) {
			Column c = (Column)i$.next();
			if (c.isPk()) {
				++pkCount;
			}
		}

		return pkCount;
	}

	public Column getPkColumn() {
		return this.getPkColumns().isEmpty() ? null : (Column)this.getPkColumns().get(0);
	}

	public List<Column> getEnumColumns() {
		List results = new ArrayList();
		Iterator i$ = this.getColumns().iterator();

		while(i$.hasNext()) {
			Column c = (Column)i$.next();
			if (!c.isEnumColumn()) {
				results.add(c);
			}
		}

		return results;
	}
}
