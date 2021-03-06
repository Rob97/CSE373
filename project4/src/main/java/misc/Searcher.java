package misc;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;

public class Searcher {
    /**
     * This method takes the input list and returns the top k elements
     * in sorted order.
     *
     * So, the first element in the output list should be the "smallest"
     * element; the last element should be the "biggest".
     *
     * If the input list contains fewer then 'k' elements, return
     * a list containing all input.length elements in sorted order.
     *
     * This method must not modify the input list.
     *
     * @throws IllegalArgumentException  if k < 0
     */
    public static <T extends Comparable<T>> IList<T> topKSort(int k, IList<T> input) {
    		// Implementation notes:
    		//
    		// - This static method is a _generic method_. A generic method is similar to
    		//   the generic methods we covered in class, except that the generic parameter
    		//   is used only within this method.
    		//
    		//   You can implement a generic method in basically the same way you implement
    		//   generic classes: just use the 'T' generic type as if it were a regular type.
    		//
    		// - You should implement this method by using your ArrayHeap for the sake of
    		//   efficiency.

    	
    		if (null == input) {
    			throw new NullPointerException();
    		} else if (k < 0) {
    			throw new IllegalArgumentException();
    		}
    		
    		IPriorityQueue<T> heap = new ArrayHeap<T>();
    		IList<T> result = new DoubleLinkedList<T>();
    		
    		
    		if (0 == k) {
    			// Return an empty list
    			return result;
    		}
    		
    		for (T item : input) {
    			if (heap.size() < k) {
    				// Add the first K elements of the list into the ArrayHeap
    				heap.insert(item);
    			} else {
    				// Compare existing items against the contents of the heap
    				// Replace them if necessary
    				T minVal = heap.peekMin();
    				if (minVal.compareTo(item) < 0) {
    					// item is greater than minVal
    					heap.removeMin(); // Remove existing min from the heap
    					heap.insert(item); // Place newer greater item onto the heap
    				}
    			}
    		}
    		
    		boolean flag = (k > input.size());
    		if (flag) {
    			for (int i = 0; i < input.size(); i++) {
    				result.add(heap.removeMin());
    			}
    		} else {
    			for (int i = 0; i < k; i++) {
    				result.add(heap.removeMin());
    			}
    		}
    		
    		return result;
    }
}
