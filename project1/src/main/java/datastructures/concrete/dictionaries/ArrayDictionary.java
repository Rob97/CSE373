package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NotYetImplementedException;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;
    private int size;
    // You're encouraged to add extra fields (and helper methods) though!

    public ArrayDictionary() {
        this.pairs = makeArrayOfPairs(8);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);

    }

    private void resize() {   
        // make a new array double the size, copy over old KV pairs, and adjust size
        Pair<K, V>[] newPairs = makeArrayOfPairs(this.pairs.length*2);
        for(int i = 0; i < this.size; i++) {
            newPairs[i] = this.pairs[i];
        }
        this.pairs = newPairs;   
    }
    
    @Override
    public V get(K key) {
        // traverse the array looking for a matching pair
        for(int i = 0; i < this.size; i++) {
            if(this.pairs[i].key == key || this.pairs[i].key.equals(key)) {
                return this.pairs[i].value;
            }
        }
        // throw exception if key does not exist in dictionary
        throw new NoSuchKeyException();
        
    }

    @Override
    public void put(K key, V value) {
        // if dictionary contains key, find it and overwrite value
        if(this.containsKey(key)) {
            for(int i = 0; i < this.size; i++) {
                if(this.pairs[i].key == key || this.pairs[i].key.equals(key)) {
                    this.pairs[i].value = value;
                    return;
                }
            }
            
        }
        
        // resize array if full
        if(this.pairs.length == this.size) {
            this.resize();
        }
        // insert KV pair into next open slot
        this.pairs[this.size] = new Pair<K, V>(key,value);
        this.size++;
    }

    @Override
    public V remove(K key) {      
        // traverse pairs to find matching key
        for(int i = 0; i < this.size; i++) {
            if(this.pairs[i].key == key || this.pairs[i].key.equals(key)) {           
                V retValue = this.pairs[i].value;
                // shift all KV pairs to the left
                while(i < this.size) {
                    this.pairs[i] = this.pairs[i+1];

                    i++;
                }
                this.size--;
                return retValue;
            }
        }
        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {     
        // traverse the array looking for a matching pair
        for(int i = 0; i < this.size; i++) {
            if(this.pairs[i].key == key || this.pairs[i].key.equals(key)) {
                return true;
            }
        }
        // return false, no key found
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
