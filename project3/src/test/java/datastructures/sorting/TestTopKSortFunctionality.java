package datastructures.sorting;

import misc.BaseTest;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import misc.Searcher;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestTopKSortFunctionality extends BaseTest {
    @Test(timeout=SECOND)
    public void testSimpleUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(5, list);
        assertEquals(5, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }
    }
    
   
    @Test(timeout=SECOND)
    public void testEmptyList() {
    		IList<Integer> list = new DoubleLinkedList<>();
   
    		try  {
    			Searcher.topKSort(5, list);
        } catch (IllegalArgumentException ex) {
        		// This is good
        }
    }
    
    
    @Test(timeout=SECOND)
    public void testInvalidK() {
    		IList<Integer> list = new DoubleLinkedList<>();
   
    		try  {
    			Searcher.topKSort(-1, list);
        } catch (IllegalArgumentException ex) {
        		// This is good
        }
    }
    
    @Test(timeout=SECOND)
    public void testKisZero() {
    		IList<Integer> list = new DoubleLinkedList<>();
    		list.add(0);
    		list.add(1);
    		list.add(2);
    		IList<Integer> top = Searcher.topKSort(0, list);
    		assertEquals(0, top.size());
    }
    
    
    @Test(timeout=SECOND)
    public void testNullList() {
    		try  {
    			Searcher.topKSort(-1, null);
        } catch (NullPointerException ex) {
        		// This is good
        }
    }
    
    
    @Test(timeout=SECOND)
    public void testKlargerThanList() {
    		int dummyVal = 5;
    	
    		IList<Integer> list = new DoubleLinkedList<>();
    		for(int i = 0; i < dummyVal; i++) {
    			list.add(i);
    		}
    		try  {
    			Searcher.topKSort(dummyVal + 1, list);
        } catch (IllegalArgumentException ex) {
        		// This is good
        }
    }
    
    @Test(timeout=SECOND)
    public void testListWithDuplicateValues() {
		IList<Integer> list = new DoubleLinkedList<>();
		for(int i = 0; i < 20; i++) {
			list.add(i % 4);
		}
		
		IList<Integer> top = Searcher.topKSort(5, list);
		assertEquals(5, top.size());
		for(int i = 0; i < 5; i++) {
			assertEquals(3, top.get(i));
		}
		
		top = Searcher.topKSort(10, list);
		assertEquals(10, top.size());
		for(int i = 0; i < 10; i++) {
			if(i > 4) {
				assertEquals(3, top.get(i));
			} else {
				assertEquals(2, top.get(i));
			}
		}
    }
    
    @Test(timeout=SECOND)
    public void testRandomInts() {
    		// Test random integer input from 0 - 1000
    		// On a list of size 1000
    		// Compare against collections.sort
    		IList<Integer> list = new DoubleLinkedList<>();
    		List<Integer> sortList = new LinkedList<>();
    		Random rand = new Random();
    		for(int i = 0; i < 1000; i++) {
    			int n = rand.nextInt(1000);
    			list.add(n);
    			sortList.add(n);
    		}
    		
    		IList<Integer> top = Searcher.topKSort(1000, list);
    		Collections.sort(sortList);
    		
    		assertEquals(top.size(), sortList.size());
    		for(int i = 0; i < 1000; i++) {
    			assertEquals(top.get(i), sortList.get(i));
    		}
    		
    }
    
    @Test(timeout=SECOND)
    public void testBasicString() {
    		IList<String> list = new DoubleLinkedList<>();
    		for(Character c = 'z'; c >= 'a'; c--) {
    			list.add(c.toString());
    		}
    		
    		IList<String> top = Searcher.topKSort(5, list);
    		assertEquals(5, top.size());
    		assertEquals("z", top.get(4));
    		assertEquals("y", top.get(3));
    		assertEquals("x", top.get(2));
    		assertEquals("w", top.get(1));
    		assertEquals("v", top.get(0));
    }
    
    
}
