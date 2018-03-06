package datastructures.concrete;

import datastructures.interfaces.IList;
import java.util.Iterator;

import java.util.NoSuchElementException;
import misc.exceptions.EmptyContainerException;


/**
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
    		if (this.size == 0) {
    			// If the list currently has nothing in it
	    		Node<T> newNode = new Node<T>(item);
	    		this.front = newNode;
	    		this.back = newNode;
    		} else {
    			// If the list has items in it already
    			Node<T> newNode = new Node<T>(item);
    			newNode.prev = this.back;	// Set the new Node's prev field to the previous list end
    			this.back.next = newNode;	// Add a reference from previous end to the new node 
    			this.back = newNode;			// Set the back of the list to point to the new node
    		}
    		
    		this.size++;						// Increase the size of the list
    }

    @Override
    public T remove() {
    		Node<T> current;
	    	if (this.size < 1) {
	    			throw new EmptyContainerException();
	    		} else if (this.size == 1) {
	    			current = this.front;
	    			this.front = null;
	    			this.back = null;
	    		} else {
	    			current = this.back;			// Set Node to remove to list end
	    			this.back = current.prev;	// Set new list end to current's previous Node
	    			// Set new list end's next field to null, as it is now the end of the list
	    			this.back.next = null;
	    		}
	    		
	    	this.size--;							// Decrease the size of the list
	    	return current.data;
    }

    @Override
    public T get(int index) {
		Node<T> current = this.front;
    		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException();
		} else if (this.size < 1) {
			throw new EmptyContainerException();
		} else {
			int curIndex = 0;
			while (curIndex < index) {
				current = current.next;
				curIndex++;
			}
		}
    		return current.data;
    }

    @Override
    public void set(int index, T item) {
		Node<T> current = this.front;
		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException();
		} else if (this.size < 1) {
			throw new EmptyContainerException();
		} else if (this.size == 1) {	 // If there is only one item in the list, switch it out
			Node<T> newNode = new Node<T>(item);
			this.front = newNode;
			this.back = newNode;
		} else if (index == 0) {	 // If we're setting at the beginning of the list
			Node<T> newNode = new Node<T>(null, item, this.front.next);
			this.front.next.prev = newNode;
			this.front = newNode;
		} else if (index == this.size - 1) {	 // If we're setting at the ending of the list
			Node<T> newNode = new Node<T>(this.back.prev, item, null);
			this.back.prev.next = newNode;
			this.back = newNode;
		} else {	 // If we're setting otherwise
			int curIndex = 0;
			while (curIndex < index) {
				current = current.next;
				curIndex++;
			}
			Node<T> newNode = new Node<T>(current.prev, item, current.next);
			current.prev.next = newNode;
			current.next.prev = newNode;
		}
    }

    @Override
    public void insert(int index, T item) {
    		if (index < 0 || index >= this.size + 1) {
    			throw new IndexOutOfBoundsException();
    		} else if (index == 0) {
    			if (this.size == 0) {
    				// There is nothing in the list currently
    				// Add this item as normal
    				this.add(item);
    			} else {
    				// Place this at the front of the list
    				Node<T> newNode = new Node<T>(null, item, this.front);
    				this.front.prev = newNode;
    				this.front = newNode;
    				this.size++;
    			}
    		} else if (index == this.size) {
    			// Add this item to the end of the list
    			this.add(item);
    		} else {
    			int curIndex;
    			if (index <= this.size / 2) {
    		  // Start from the beginning if index is closer to the beginning
    				curIndex = 0;
    				Node<T> current = this.front;
    				while (curIndex < index) {
    					current = current.next;
    					curIndex++;
    				}
    				Node<T> newNode = new Node<T>(current.prev, item, current);
    				current.prev.next = newNode;
    				current.prev = newNode;
    				this.size++;
    			} else {
    			 // Start from the end if index is closer to the end
    				curIndex = this.size - 1;
    				Node<T> current = this.back;
    				while (curIndex > index) {
    					current = current.prev;
    					curIndex--;
    				}
    				Node<T> newNode = new Node<T>(current.prev, item, current);
    				current.prev.next = newNode;
    				current.prev = newNode;
    				this.size++;
    			}
    		}
    		
    }

    @Override
    public T delete(int index) {
    		if (index < 0 || index >= this.size) {
    			throw new IndexOutOfBoundsException();
    		} else if (this.size < 1) {
    			throw new EmptyContainerException();
    		} else if (index == 0) {
    			// delete from the front of the list
    			Node<T> current = this.front;
    			this.front.next.prev = null;
    			this.front = this.front.next;
    			this.size--;
    			return current.data;
    		} else if (index == this.size - 1) {
    			// delete from the end of the list
    			Node<T> current = this.back;
    			this.back.prev.next = null;
    			this.back = this.back.prev;
    			this.size--;
    			return current.data;
    		} else {
    			int curIndex;
    			if (index <= this.size / 2) {
    			 // Start from the beginning if index is closer to the beginning
    				curIndex = 0;
    				Node<T> current = this.front;
    				while (curIndex < index) {
    					current = current.next;
    					curIndex++;
    				}
    				current.prev.next = current.next;
    				current.next.prev = current.prev;
    				this.size--;
    				return current.data;
    			} else {
    			 // Start from the end if index is closer to the end
    				curIndex = this.size - 1;
    				Node<T> current = this.back;
    				while (curIndex > index) {
    					current = current.prev;
    					curIndex--;
    				}
    				current.prev.next = current.next;
    				current.next.prev = current.prev;
    				this.size--;
    				return current.data;
    			}
    		}
    
    }

    @Override
    public int indexOf(T item) {
    		int index = -1;
    		if (null == item) {
    			for (int i = 0; i < this.size; i++) {
    				if (get(i) == null) {
    					index = i;
    					break;
    				}
    			}
    		} else {
    			for (int i = 0; i < this.size; i++) {
    				if (item.equals(get(i))) {
    					index = i;
    					break;
    				}
    			}
    		}
    		return index;
    }

    @Override
    public int size() {
    		return this.size;
    }

    @Override
    public boolean contains(T other) {
    		return (indexOf(other) != -1);
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
        		return (this.current != null);
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (this.current == null) {
            		throw new NoSuchElementException();
            } else {
        			Node<T> data = this.current;
            		this.current = this.current.next;

            		return data.data;
            }
        }
    }
}
