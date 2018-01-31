package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;


import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
	
	// Initial # of buckets
	private final int initialSize = 17;
	
	// You may not change or rename this field: we will be inspecting
	// it using our private tests.
	private IDictionary<K, V>[] chains;
    
    // For determining load factor
    private int occupancy;
    
    public ChainedHashDictionary() {
        this.chains = makeArrayOfChains(initialSize);
        this.occupancy = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
    		// Get the index
    		int bucketIndex = getHash(key);
    		if (null == chains[bucketIndex]) {
    			throw new NoSuchKeyException();
    		} else if (!chains[bucketIndex].containsKey(key)) {
    			throw new NoSuchKeyException();
    		}
    		return chains[bucketIndex].get(key);
    	}

    @Override
    public void put(K key, V value) {
    		if (this.occupancy == chains.length) {
    			// When our load factor gets to one, resize
    			this.resize();
    		}
    	
    		// Get the index
    		int bucketIndex = getHash(key);
    		
    		// Now analyze the current state of the bucket
    		if (null == chains[bucketIndex]) {
    			// No array dictionary currently exists there
    			chains[bucketIndex] = new ArrayDictionary<K, V>();
    		}
    		
    		if (!chains[bucketIndex].containsKey(key)) {
    			this.occupancy++;
    		}
    		chains[bucketIndex].put(key, value);
    
    }
    
    private void resize() {
    		// Create a new bucket system that is double the length
    		IDictionary<K, V>[] newChains = makeArrayOfChains(this.chains.length * 2);
    		IDictionary<K, V>[] oldChains = this.chains;
    		this.chains = newChains;
    		this.occupancy = 0;
    		for (int i = 0; i < oldChains.length; i++) {
    			if (!(oldChains[i] == null)) {
    		         for (KVPair<K, V> pair : oldChains[i]) {
    		                K key = pair.getKey();
    		                V value = pair.getValue();
    		                this.put(key, value);
    		                
    		            }
    			}
    		}
    }
    
    /**
     * assigns positive hash value to a key based off of the current number of buckets
     * assigns a hash value of 0 to null keys
     * 
     * @param key
     * @return the hash value for this specific key
     */
    private int getHash(K key) {
    		int bucketIndex = (null == key) ? 0 : (key.hashCode() % chains.length);
    		if (bucketIndex < 0) {
    			bucketIndex *= -1;
    		}
    		return bucketIndex;
    }

    @Override
    public V remove(K key) {
        int bucketIndex = getHash(key);
		if (null == chains[bucketIndex] || !chains[bucketIndex].containsKey(key)) {
			throw new NoSuchKeyException();
		}
		
		V var = chains[bucketIndex].remove(key);
		int dictSize = chains[bucketIndex].size();
		if (0 == dictSize) {
			// We'll need to set this index back to null after getting the value
			chains[bucketIndex] = null;
		}
		occupancy--;
		return var;
    }

    @Override
    public boolean containsKey(K key) {
        int bucketIndex = getHash(key);
        if (null == chains[bucketIndex]) {
        		return false;
        } else {
        		return chains[bucketIndex].containsKey(key);
        }
    }

    @Override
    public int size() {
        return this.occupancy;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Think about what exactly your *invariants* are. Once you've
     *    decided, write them down in a comment somewhere to help you
     *    remember.
     *
     * 3. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 4. Think about what exactly your *invariants* are. As a 
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        int currChain;
        Iterator<KVPair<K, V>> currIt;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.currChain = -1;
            moveToNextChain();

        }

        @Override
        public boolean hasNext() {
            if (currIt == null || currChain == chains.length) {
                return false;
            } else if (!currIt.hasNext()) {
                    return moveToNextChain();
                } else {
                    return true;
                }
        }

        @Override
        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            } else {
                return this.currIt.next();
            }
            
        }
        
        // helper function to automatically find the next 
        // valid dictionary chain to iterate over
        // returns true if it successfully moved to a new, valid
        // chain.
        private boolean moveToNextChain() {
            for (int i = currChain + 1; i < chains.length; i++) {
                if (chains[i] != null && chains[i].size() > 0) {
                    currChain = i;
                    currIt = chains[i].iterator();
                    return true;
                }
            }
            currChain = chains.length;
            currIt = null;
            return false;
        }
    
    }
}
