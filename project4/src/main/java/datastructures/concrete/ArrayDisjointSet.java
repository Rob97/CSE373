package datastructures.concrete;


import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;


/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private IDictionary<T, Integer> map;
    private int size;
    private final int INITIAL_SIZE = 20;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        this.size = 0;
        this.map = new ChainedHashDictionary<T, Integer>();
        this.pointers = new int[INITIAL_SIZE];
    }

    @Override
    public void makeSet(T item) {
    		if (this.map.containsKey(item)) {
    			throw new IllegalArgumentException();
    		} else {
    			this.map.put(item, this.size);
    			this.pointers[this.size] = -1;
    			this.size++;
    		}
    		
    		if(this.size == this.pointers.length) {
    			// RESIZE
    			int[] newArray = new int[this.size * 2];
    			for (int i = 0; i < this.size; i++) {
    				newArray[i] = this.pointers[i];
    			}
    			this.pointers = newArray;
    		}
    }

    @Override
    public int findSet(T item) {
		if (!this.map.containsKey(item)) {
			throw new IllegalArgumentException();
		} 
		
		int value = this.map.get(item); // Gets the size index of the item
		if (this.pointers[value] < 0) {
			return value;
		} else {
			while (this.pointers[value] >= 0) {
				value = this.pointers[value];
			}
			return value;
		}
    }

    @Override
    public void union(T item1, T item2) {
		if (!this.map.containsKey(item1) || !this.map.containsKey(item2)) {
			throw new IllegalArgumentException();
		}
		int cellVal1 = findSet(item1);
		int cellVal2 = findSet(item2);
		
		if(cellVal1 == cellVal2) {
			throw new IllegalArgumentException("Both items in the same set");
		}
		
		if (this.pointers[cellVal2] < this.pointers[cellVal1]) {
			// item 2 belongs to higher rank than item1
			this.pointers[cellVal1] = cellVal2;
			this.pointers[cellVal2]--;
		} else {
			this.pointers[cellVal2] = cellVal1;
			this.pointers[cellVal1]--;
		}
		
    }
}
