//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.provider.db.table.model;

import enn.base.generator.utils.util.ListHashtable;

import java.io.Serializable;

public class ForeignKeys implements Serializable {
	protected Table parentTable;
	protected ListHashtable associatedTables;

	public ForeignKeys(Table aTable) {
		this.parentTable = aTable;
		this.associatedTables = new ListHashtable();
	}

	public void addForeignKey(String tableName, String columnName, String parentColumn, Integer seq) {
		ForeignKey tbl = null;
		if (this.associatedTables.containsKey(tableName)) {
			tbl = (ForeignKey)this.associatedTables.get(tableName);
		} else {
			tbl = new ForeignKey(this.parentTable, tableName);
			this.associatedTables.put(tableName, tbl);
		}

		tbl.addColumn(columnName, parentColumn, seq);
	}

	public ListHashtable getAssociatedTables() {
		return this.associatedTables;
	}

	public int getSize() {
		return this.getAssociatedTables().size();
	}

	public boolean getHasImportedKeyColumn(String aColumn) {
		boolean isFound = false;
		int numKeys = this.getAssociatedTables().size();

		for(int i = 0; i < numKeys; ++i) {
			ForeignKey aKey = (ForeignKey)this.getAssociatedTables().getOrderedValue(i);
			if (aKey.getHasImportedKeyColumn(aColumn)) {
				isFound = true;
				break;
			}
		}

		return isFound;
	}

	public ForeignKey getAssociatedTable(String name) {
		Object fkey = this.getAssociatedTables().get(name);
		return fkey != null ? (ForeignKey)fkey : null;
	}

	public Table getParentTable() {
		return this.parentTable;
	}

	public boolean getHasImportedKeyParentColumn(String aColumn) {
		boolean isFound = false;
		int numKeys = this.getAssociatedTables().size();

		for(int i = 0; i < numKeys; ++i) {
			ForeignKey aKey = (ForeignKey)this.getAssociatedTables().getOrderedValue(i);
			if (aKey.getHasImportedKeyParentColumn(aColumn)) {
				isFound = true;
				break;
			}
		}

		return isFound;
	}

	public ForeignKey getImportedKeyParentColumn(String aColumn) {
		ForeignKey aKey = null;
		int numKeys = this.getAssociatedTables().size();

		for(int i = 0; i < numKeys; ++i) {
			aKey = (ForeignKey)this.getAssociatedTables().getOrderedValue(i);
			if (aKey.getHasImportedKeyParentColumn(aColumn)) {
				break;
			}
		}

		return aKey;
	}
}
