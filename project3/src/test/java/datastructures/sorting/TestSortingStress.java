package datastructures.sorting;

import misc.BaseTest;
import misc.Searcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestSortingStress extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }
	
	@Test(timeout = 20 * SECOND)
    public void testLargeListSort() {
    	IList<Integer> list = new DoubleLinkedList<>();
    		for (int i = 100000; i > 0; i--) {
    			list.add(i);
    		}
    		
    		IList<Integer> top = Searcher.topKSort(1000, list);
    		assertEquals(1000, top.size());
    		for (int i = 0; i < 1000; i++) {
    			assertEquals(top.get(i), 99000 + i + 1);
    		}
    }
    
    @Test(timeout = 10 * SECOND)
    public void testLargeArrayHeap() {
    		IPriorityQueue<String> heap = this.makeInstance();
        List<String> testList = new LinkedList<String>();
        Random rand = new Random();
        for (int i = 0; i < 400000; i++) {
            int n = rand.nextInt(100) + 1;
            
            String randString = "";
            for (int j = 0; j < n; j++) {
                randString += (char) (rand.nextInt(26) + 'a');
            }
            heap.insert(randString);
            testList.add(randString);
        }
        Collections.sort(testList);
        while (!heap.isEmpty()) {
            assertEquals(heap.removeMin(), testList.remove(0));
        }
    }
    
    @Test(timeout = 10 * SECOND)
    public void testLargeRandomSort() {
		// Test random integer input from 0 - 1000
		// On a list of size 1000
		// Compare against collections.sort
		IList<Integer> list = new DoubleLinkedList<>();
		List<Integer> sortList = new ArrayList<>();
		Random rand = new Random();
		for (int i = 0; i < 50000; i++) {
			int n = rand.nextInt(1000);
			list.add(n);
			sortList.add(n);
		}
		
		IList<Integer> top = Searcher.topKSort(1000, list);
		Collections.sort(sortList);
		
		assertEquals(top.size(), 1000);
		for (int i = 0; i < 1000; i++) {
			assertEquals(top.get(i), sortList.get(i + 49000));
		}
    }
}
