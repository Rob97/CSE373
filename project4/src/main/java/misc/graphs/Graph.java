package misc.graphs;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ArrayDictionary;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Searcher;
import misc.exceptions.NoPathExistsException;
import misc.exceptions.NotYetImplementedException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated then usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've contrained Graph
    //   so that E *must* always be an instance of Edge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the Edge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException  if any of the edges have a negative weight
     * @throws IllegalArgumentException  if one of the edges connects to a vertex not
     *                                   present in the 'vertices' list
     */
    
    private IDictionary<V,IList<E>> ajList;
    private int numVertices;
    private int numEdges;
    private IList<E> sortedEdges;
    
    public Graph(IList<V> vertices, IList<E> edges) {
       
        this.numVertices = vertices.size();
        this.numEdges = edges.size();
        this.ajList = new ArrayDictionary<V,IList<E>>();
        for (V vertex : vertices) {
            ajList.put(vertex, new DoubleLinkedList<E>());
        }
        
        for (E edge : edges) {
            if (edge.getWeight() >= 0 
                    && this.ajList.containsKey(edge.getVertex1()) 
                    && this.ajList.containsKey(edge.getVertex2())) {
                this.ajList.get(edge.getVertex1()).add(edge);
            } else {
                throw new IllegalArgumentException();
            }
        }
        
        // Store a sorted version of the edges for later use
        this.sortedEdges = Searcher.topKSort(edges.size(), edges); 
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    
    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.numVertices;
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.numEdges;
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> result = new ChainedHashSet<E>();
        IDisjointSet<V> msts = new ArrayDisjointSet<V>();
        
        // Add all vertices in the graph to an array disjoint set
        // As their own minimum spanning tree
        for (KVPair<V, IList<E>> vertex : this.ajList) {
        		msts.makeSet(vertex.getKey());
        }
        
        // Iterate through sorted ascending edge weights
        // and union the MSTs if they do not currently belong
        // to an MST
        for (E edge : this.sortedEdges) {
        		V v1 = edge.getVertex1();
        		V v2 = edge.getVertex2();
        		if (msts.findSet(v1) != msts.findSet(v2)) {
        			msts.union(v1, v2);
        			result.add(edge);
        		}
        }
        
        return result;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        throw new NotYetImplementedException();
        IList<E> returnPath = new DoubleLinkedList<E>();
        IList<V> visited = new DoubleLinkedList<V>();
        IDictionary<V,VertexInfo> vInfos = new ArrayDictionary<V,VertexInfo>(); 
        IPriorityQueue<VertexInfo> vQueue = new ArrayHeap<VertexInfo>();
        
        if(start == end) {
            return returnPath;
        }
        
        for (KVPair<V, IList<E>> pair : ajList) {
            V vertex = pair.getKey();
            VertexInfo info = new VertexInfo(null, Double.POSITIVE_INFINITY);
            vInfos.put(vertex, info);
            vQueue.insert(info);
        }
        //Keep Track of Vertices with their costs and return path
        //Keep Track of known Vertices
        
        V current = start;
        while(visited.size() < numVertices()) {
            //update costs of current's children
            //add updated children to vQueue (remove and re-add)
            //Get next vertex to visit from vQueue, break if next lowest is infinity, or already visited
        }
        
        //build the list for the return path, if it was found
        
        
        return returnPath;
    }
    
    // stores the cost and path for a vertex
    private class VertexInfo implements Comparable<VertexInfo> {
        private E path;
        private Double cost;
        
        
        VertexInfo(E path,Double cost) {
            this.path = path;
            this.cost = cost;
        }
        
        public E getPath() {
            return this.path;
        }
        public void setPath(E path) {
            this.path = path;
        }
        
        public Double getCost() {
            return this.cost;
        }
        
        public void setCost(Double cost) {
            this.cost = cost;
        }
        
        @Override
        public int compareTo(VertexInfo o) {
            return Double.compare(this.cost, o.cost);
        }
    }
    
}
