package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * See IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;

    // Feel free to add more fields and constants.
    private static final int INITIAL_SIZE = 8;
    private int heapSize;
  

    public ArrayHeap() {
        heapSize = 0;
        heap = makeArrayOfT(INITIAL_SIZE);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int size) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[size]);
    }

    @Override
    public T removeMin() {
        if (this.isEmpty()) {
            throw new EmptyContainerException();
        }
        // get minimum, move last element to top and percolate down
        T retVal = heap[0];
        heap[0] = heap[heapSize-1];
        heap[heapSize - 1] = null;
        heapSize--;
        percolateDown(0);
        return retVal;
        
    }

    @Override
    public T peekMin() {
        if (this.isEmpty()) {
            throw new EmptyContainerException();
        }
        return heap[0];

    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        
        if(heapSize == heap.length) {
            resize();
        }
        
        heap[heapSize] = item;
        heapSize++;
        percolateUp(heapSize-1);
    }

    @Override
    public int size() {
        return heapSize;
    }
    
    // returns the index of the parent of i
    private int parentOf(int i) {
        return (i - 1) / NUM_CHILDREN;
    }
    
    // returns the index of jth child of i
    private int childOf(int i, int j) {
        return (NUM_CHILDREN*i) + j + 1;
    }
    
    
    /*Equation for size of array based on levels : 
     * 1/3[4^(a+1) - 1 ]
     * 
     */
    private void resize() {
        T[] newHeap = makeArrayOfT(heap.length * 2);
        for(int i = 0; i < heapSize; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
        
    }
    
    private void percolateUp(int i) {
        int parent_index = parentOf(i);
        // return if parent is equal to or smaller
        if (heap[i].compareTo(heap[parent_index]) >= 0)  {
            return;
        }
        //else swap places with parent
        T swap = heap[parent_index];
        heap[parent_index] = heap[i];
        heap[i] = swap;
        //keep percolatin
        percolateUp(parent_index);
    }
    
    private void percolateDown(int i) {
        //if the current node has no children, stop
        if(childOf(i,0) >= heapSize) {
            return;
        }
        
        // find the smallest child
        int smallChild = 0;
        for(int j = 1; (j < NUM_CHILDREN) && (childOf(i,j) < heapSize); j++) {
            if (heap[childOf(i,smallChild)].compareTo(heap[childOf(i,j)]) > 0) {
                smallChild = j;
            }
        }
        
        int swapIndex = childOf(i,smallChild);
        //swap with the smallest child, or not
        if(heap[i].compareTo(heap[swapIndex]) <= 0) {
            return;
        }
        T swap = heap[i];
        heap[i] = heap[swapIndex];
        heap[swapIndex] = swap;
        percolateDown(swapIndex);
    }
}
