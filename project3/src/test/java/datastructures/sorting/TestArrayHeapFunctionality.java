package datastructures.sorting;

import static org.junit.Assert.assertTrue;

import misc.BaseTest;
import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import org.junit.Test;
import misc.exceptions.EmptyContainerException;
import java.util.*;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestArrayHeapFunctionality extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout=SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(1, heap.size());
        assertTrue(!heap.isEmpty());
    }
    
    @Test(timeout=SECOND)
    public void testBasicEdges() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        assertTrue(heap.isEmpty());
     
        heap.insert(-1);
        heap.insert(0);
        heap.insert(0);
        heap.insert(5);
        
        assertEquals(4, heap.size()); //check that duplicates arent erased
        
        assertEquals(-1, heap.peekMin()); //check negative numbers are properly handled
        assertEquals(-1, heap.peekMin()); //check peekMin doesnt remove values
        
        //check proper ordering
        assertEquals(-1, heap.removeMin()); 
        assertEquals(0, heap.removeMin());
        assertEquals(0, heap.removeMin());
        assertEquals(5, heap.removeMin());
    }

    
    @Test(timeout=SECOND)
    public void testNull() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        // test for proper null handling
        try  {
            heap.insert(null);
        } catch (IllegalArgumentException e) {
            return;
        }
        assertTrue(false); // only gets here if no exception caught
    }
    
    @Test(timeout=SECOND)
    public void testRemoveEmpty() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        // test for proper null handling
        try  {
            heap.removeMin();
        } catch (EmptyContainerException e) {
            return;
        }
        assertTrue(false); // only gets here if no exception caught
    }
    
    @Test(timeout=SECOND)
    public void testPeekEmpty() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        // test for proper null handling
        try  {
            heap.peekMin();
        } catch (EmptyContainerException e) {
            return;
        }
        assertTrue(false); // only gets here if no exception caught
    }
    
    @Test(timeout=SECOND)
    public void testBasicOrderedFill() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        int size = 0;
        for (int i = 1; i < 10; i++) {
            for (int j = 0; j < i; j++) {
                heap.insert(i);
                size++;
            }
        }
        
        assertEquals(heap.size(), size);
        
        for (int i = 1; i < 10; i++) {
            for (int j = 0; j < i; j++) {
                assertEquals(i, heap.removeMin());
            }
        }
        assertTrue(heap.isEmpty());      
    }
    
    @Test(timeout=SECOND)
    public void testRandomFill() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        LinkedList<Integer> testList = new LinkedList<Integer>();
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            int n = rand.nextInt(10);
            testList.add(n);
            heap.insert(n);
        }
        Collections.sort(testList);
        while(!heap.isEmpty()) {
            assertEquals(heap.removeMin(), testList.pop());
        }
        
        
    }
    
    @Test(timeout=SECOND)
    public void testRandomStringFill() {
        IPriorityQueue<String> heap = this.makeInstance();
        LinkedList<String> testList = new LinkedList<String>();
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            int n = rand.nextInt(100) + 1;
            
            String randString = "";
            for(int j = 0; j < n;j++) {
                randString += (char)(rand.nextInt(26) + 'a');
            }
            heap.insert(randString);
            testList.add(randString);
        }
        Collections.sort(testList);
        while(!heap.isEmpty()) {
            assertEquals(heap.removeMin(), testList.pop());
        }
        
        
    }
    
}
