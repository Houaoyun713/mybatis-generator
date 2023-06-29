//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.db.table.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TableSet implements Serializable {
	private static final long serialVersionUID = -6500047411657968878L;
	private List<Table> tables = new ArrayList();

	public TableSet() {
	}

	public List<Table> getTables() {
		return this.tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	public void addTable(Table c) {
		this.tables.add(c);
	}

	public Table getBySqlName(String name) {
		Iterator i$ = this.tables.iterator();

		Table c;
		do {
			if (!i$.hasNext()) {
				return null;
			}

			c = (Table)i$.next();
		} while(!name.equalsIgnoreCase(c.getSqlName()));

		return c;
	}

	public Table getByClassName(String name) {
		Iterator i$ = this.tables.iterator();

		Table c;
		do {
			if (!i$.hasNext()) {
				return null;
			}

			c = (Table)i$.next();
		} while(!name.equals(c.getClassName()));

		return c;
	}
}
