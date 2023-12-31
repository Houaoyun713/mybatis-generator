//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ListHashtable extends Hashtable {
	protected List orderedKeys = new ArrayList();

	public ListHashtable() {
	}

	public synchronized void clear() {
		super.clear();
		this.orderedKeys = new ArrayList();
	}

	public synchronized Object put(Object aKey, Object aValue) {
		if (this.orderedKeys.contains(aKey)) {
			int pos = this.orderedKeys.indexOf(aKey);
			this.orderedKeys.remove(pos);
			this.orderedKeys.add(pos, aKey);
		} else if (aKey instanceof Integer) {
			Integer key = (Integer)aKey;
			int pos = this.getFirstKeyGreater(key.intValue());
			if (pos >= 0) {
				this.orderedKeys.add(pos, aKey);
			} else {
				this.orderedKeys.add(aKey);
			}
		} else {
			this.orderedKeys.add(aKey);
		}

		return super.put(aKey, aValue);
	}

	private int getFirstKeyGreater(int aKey) {
		int pos = 0;
		int numKeys = this.getOrderedKeys().size();

		for(int i = 0; i < numKeys; ++i) {
			Integer key = (Integer)this.getOrderedKey(i);
			int keyval = key.intValue();
			if (keyval >= aKey) {
				break;
			}

			++pos;
		}

		if (pos >= numKeys) {
			pos = -1;
		}

		return pos;
	}

	public synchronized Object remove(Object aKey) {
		if (this.orderedKeys.contains(aKey)) {
			int pos = this.orderedKeys.indexOf(aKey);
			this.orderedKeys.remove(pos);
		}

		return super.remove(aKey);
	}

	public void reorderIntegerKeys() {
		List keys = this.getOrderedKeys();
		int numKeys = keys.size();
		if (numKeys > 0) {
			if (this.getOrderedKey(0) instanceof Integer) {
				List newKeys = new ArrayList();
				List newValues = new ArrayList();

				int l;
				for(l = 0; l < numKeys; ++l) {
					Integer key = (Integer)this.getOrderedKey(l);
					Object val = this.getOrderedValue(l);
					int numNew = newKeys.size();
					int pos = 0;

					for(int j = 0; j < numNew; ++j) {
						Integer newKey = (Integer)newKeys.get(j);
						if (newKey.intValue() >= key.intValue()) {
							break;
						}

						++pos;
					}

					if (pos >= numKeys) {
						newKeys.add(key);
						newValues.add(val);
					} else {
						newKeys.add(pos, key);
						newValues.add(pos, val);
					}
				}

				this.clear();

				for(l = 0; l < numKeys; ++l) {
					this.put(newKeys.get(l), newValues.get(l));
				}

			}
		}
	}

	public String toString() {
		StringBuffer x = new StringBuffer();
		x.append("Ordered Keys: ");
		int numKeys = this.orderedKeys.size();
		x.append("[");

		int j;
		for(j = 0; j < numKeys; ++j) {
			x.append(this.orderedKeys.get(j) + " ");
		}

		x.append("]\n");
		x.append("Ordered Values: ");
		x.append("[");

		for(j = 0; j < numKeys; ++j) {
			x.append(this.getOrderedValue(j) + " ");
		}

		x.append("]\n");
		return x.toString();
	}

	public void merge(ListHashtable newTable) {
		int num = newTable.size();

		for(int i = 0; i < num; ++i) {
			Object aKey = newTable.getOrderedKey(i);
			Object aVal = newTable.getOrderedValue(i);
			this.put(aKey, aVal);
		}

	}

	public List getOrderedKeys() {
		return this.orderedKeys;
	}

	public Object getOrderedKey(int i) {
		return this.getOrderedKeys().get(i);
	}

	public Object getKeyForValue(Object aValue) {
		int num = this.getOrderedValues().size();

		for(int i = 0; i < num; ++i) {
			Object tmpVal = this.getOrderedValue(i);
			if (tmpVal.equals(aValue)) {
				return this.getOrderedKey(i);
			}
		}

		return null;
	}

	public List getOrderedValues() {
		List values = new ArrayList();
		int numKeys = this.orderedKeys.size();

		for(int i = 0; i < numKeys; ++i) {
			values.add(this.get(this.getOrderedKey(i)));
		}

		return values;
	}

	public Object getOrderedValue(int i) {
		return this.get(this.getOrderedKey(i));
	}
}
