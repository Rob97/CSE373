package search.analyzers;

import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import search.models.Webpage;


import java.net.URI;

/**
 * This class is responsible for computing the 'page rank' of all available webpages.
 * If a webpage has many different links to it, it should have a higher page rank.
 * See the spec for more details.
 */
public class PageRankAnalyzer {
    private IDictionary<URI, Double> pageRanks;

    /**
     * Computes a graph representing the internet and computes the page rank of all
     * available webpages.
     *
     * @param webpages  A set of all webpages we have parsed.
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    public PageRankAnalyzer(ISet<Webpage> webpages, double decay, double epsilon, int limit) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        // Step 1: Make a graph representing the 'internet'
        IDictionary<URI, ISet<URI>> graph = this.makeGraph(webpages);

        // Step 2: Use this graph to compute the page rank for each webpage
        this.pageRanks = this.makePageRanks(graph, decay, limit, epsilon);

        // Note: we don't store the graph as a field: once we've computed the
        // page ranks, we no longer need it!
    }

    /**
     * This method converts a set of webpages into an unweighted, directed graph,
     * in adjacency list form.
     *
     * You may assume that each webpage can be uniquely identified by its URI.
     *
     * Note that a webpage may contain links to other webpages that are *not*
     * included within set of webpages you were given. You should omit these
     * links from your graph: we want the final graph we build to be
     * entirely "self-contained".
     */
    private IDictionary<URI, ISet<URI>> makeGraph(ISet<Webpage> webpages) {
        IDictionary<URI, ISet<URI>> graph = new ChainedHashDictionary<URI, ISet<URI>>();
        
        // build a set of the URIs of each webpage
        ISet<URI> webpageURIS = new ChainedHashSet<URI>();
        for (Webpage page : webpages) {
            webpageURIS.add(page.getUri());
        }
        
        // crawl again through each page, get its links and add it to the graph
        for (Webpage page : webpages) {
            ISet<URI> pageEdges = new ChainedHashSet<URI>();
            URI pageURI = page.getUri();
            
            // get the links of each page and add to edges
            IList<URI> links = page.getLinks();
            for (URI uriLink : links) {
                // add to edges if a webpage in the set, and not a self-loop
                if (webpageURIS.contains(uriLink) && !uriLink.equals(pageURI)) {
                    pageEdges.add(uriLink);
                }
            }

            // add our built vertex to the graph
            graph.put(pageURI, pageEdges);  
        }
        
        
        return graph;
    }

    /**
     * Computes the page ranks for all webpages in the graph.
     *
     * Precondition: assumes 'this.graphs' has previously been initialized.
     *
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    private IDictionary<URI, Double> makePageRanks(IDictionary<URI, ISet<URI>> graph,
                                                   double decay,
                                                   int limit,
                                                   double epsilon) {
        // Step 1: The initialize step should go here
        IDictionary<URI, Double> oldPageRanks = new ChainedHashDictionary<URI, Double>();
        Double initRank = 1.0/graph.size();
        for (KVPair<URI, ISet<URI>> vertexPair : graph) {
            oldPageRanks.put(vertexPair.getKey(), initRank);
        }
        
        // now go through updating
        for (int i = 0; i < limit; i++) {
            // Step 2: The update step should go here
            
            // make new page ranks and set them all to new rank
            Double newRank = (1-decay) / graph.size();
            IDictionary<URI, Double> newPageRanks = new ChainedHashDictionary<URI, Double>();
            for (KVPair<URI, ISet<URI>> vertexPair : graph) {
                newPageRanks.put(vertexPair.getKey(), newRank);
            }
            
            // for each page, add rank to each of it's edges
            for (KVPair<URI, ISet<URI>> vertexPair : graph) {
                URI vertexURI = vertexPair.getKey();
                ISet<URI> vertexLinks = vertexPair.getValue();
                int numUniqueLinks = vertexLinks.size();
                
                if (numUniqueLinks == 0) {
                    for (KVPair<URI, ISet<URI>> graphPair : graph) {
                        URI pageURI = graphPair.getKey();
                        Double rankStep = decay * oldPageRanks.get(vertexURI) / graph.size();
                        newPageRanks.put(pageURI, newPageRanks.get(pageURI) + rankStep);
                    }
                } else {  
                    Double rankStep = decay * oldPageRanks.get(vertexURI) / numUniqueLinks;
                    for (URI link : vertexLinks) {
                        newPageRanks.put(link, newPageRanks.get(link) + rankStep);
                    }
                }    
            }
                     
            // Step 3: the convergence step should go here.
            // Return early if we've converged.
            Boolean converged = true;
            for (KVPair<URI, ISet<URI>> vertexPair : graph) {
                URI pageURI = vertexPair.getKey();
                double diff = Math.abs(newPageRanks.get(pageURI) - oldPageRanks.get(pageURI));
                
                if (diff >= epsilon) {
                    converged = false;
                    break;
                } 
            }
            
            if (converged) {
                return newPageRanks;
            } else {
                oldPageRanks = newPageRanks;
            }
        }
        return oldPageRanks;
    }

    /**
     * Returns the page rank of the given URI.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public double computePageRank(URI pageUri) {
        // Implementation note: this method should be very simple: just one line!
        return this.pageRanks.get(pageUri);
    }
}
