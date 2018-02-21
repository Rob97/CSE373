package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;
import java.lang.Math;

/**
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;

    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;
    
    private double numPages;

    // Feel free to add extra fields and helper methods.

    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.
    	    
    		this.numPages = (double) webpages.size();
        this.idfScores = this.computeIdfScores(webpages);
        this.documentTfIdfVectors = this.computeAllDocumentTfIdfVectors(webpages);
    }

    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    // Note: these private methods are suggestions or hints on how to structure your
    // code. However, since they're private, you're not obligated to implement exactly
    // these methods: feel free to change or modify these methods however you want. The
    // important thing is that your 'computeRelevance' method ultimately returns the
    // correct answer in an efficient manner.

    /**
     * Return a dictionary mapping every single unique word found
     * in every single document to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
    		IDictionary<String, Double> result = new ChainedHashDictionary<>();
    		
    		// Iterate through all the pages to find how many 
    		// pages a certain word occurs in
    		for (Webpage page : pages) {
    			
    			// Get all the words from a specific page and 
    			// put them in a hash set -- disregards duplicates
    			ISet<String> words = new ChainedHashSet<>();
    			for (String word : page.getWords()) {
    				words.add(word);
    			}
    			
    			// Add all words from the set into the dictionary
    			// If the words already appear in the dictionary, 
    			// Update their occurrence value
    			for (String word : words) {
    				if (!result.containsKey(word)) {
    					result.put(word, 1.0);
    				} else {
    					result.put(word, result.get(word) + 1.0);
    				}
    			}
    		}
    		
    		// Update all occurrence values
    		// to IDF values
    		for (KVPair<String, Double> word : result) {
    			result.put(word.getKey(), calculateIDF(word.getValue()));
    		}
    		
    		return result;
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * The input list represents the words contained within a single document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
    		IDictionary<String, Double> result = new ChainedHashDictionary<>();
    		int numWords = words.size();
    		
    		// Place the words into a hashed ArrayDictionary
    		// If they do not currently exist there, assign their
    		// Occurrence level to one.
    		//
    		// If the item already exists, update its occurrence by adding 1.0
    		for (String word : words) {
    			if (!result.containsKey(word)) {
    				result.put(word, 1.0);
    			} else {
    				result.put(word, result.get(word) + 1.0);
    			}
    		}
    		
    		// Go through Dictionary and update values for all the keys
    		// To their TF score rather than their occurrences
    		for (KVPair<String, Double> word : result) {
    			double occurrence = word.getValue();
    			double tf = occurrence / (double) numWords;
    			result.put(word.getKey(), tf);
    		}
    		
    		return result;

    }

    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        // Hint: this method should use the idfScores field and
        // call the computeTfScores(...) method.
    		
    		IDictionary<URI, IDictionary<String, Double>> result = new ChainedHashDictionary<>();
    		
    		
    		for (Webpage page : pages) {
    			// Get the TF scores for the page
    			IDictionary<String, Double> TFscores = computeTfScores(page.getWords());
    			IDictionary<String, Double> vectors = new ChainedHashDictionary<>();
    			
    			// Iterate through the TF scores and add all words to 
    			// the TFIDF vectors set with their TF * IDF
    			for (KVPair<String, Double> word : TFscores) {
    				vectors.put(word.getKey(), TFscores.get(word.getKey()) * this.idfScores.get(word.getKey()));
    			}
    			
    			
    			result.put(page.getUri(), vectors);
    		}
    
    		return result;
    }

    /**
     * 
     * @param numOccurrences the number of occurrences that an item has across all pages
     * 
     * @return the IDF value given a certain number of occurrences
     */
    private double calculateIDF(double numOccurrences) {
    		return Math.log(((double) this.numPages) / numOccurrences);
    }
    
    
    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        // Note: The pseudocode we gave you is not very efficient. When implementing,
        // this method, you should:
        //
        // 1. Figure out what information can be precomputed in your constructor.
        //    Add a third field containing that information.
        //
        // 2. See if you can combine or merge one or more loops.
    	
    		// Find Tf-idf for the document
    		IDictionary<String, Double> IDFvalDoc = this.documentTfIdfVectors.get(pageUri);
    		IDictionary<String, Double> TFvalQuery = this.computeTfScores(query);
    		
    		double numerator = 0.0;
        IDictionary<String, Double> TFIDFvalQuery = new ChainedHashDictionary<>();
        for (String word : query) {
        		double docScore = (IDFvalDoc.containsKey(word)) ? IDFvalDoc.get(word) : 0;
        		if(this.idfScores.containsKey(word)) {
        			TFIDFvalQuery.put(word, this.idfScores.get(word) * TFvalQuery.get(word));
        			numerator += (TFIDFvalQuery.get(word) * docScore);
        		} else {
        			TFIDFvalQuery.put(word, 0.0);
        		}
        }
    		
        double denominator = norm( (ChainedHashDictionary<String, Double>) IDFvalDoc) * 
        		norm( (ChainedHashDictionary<String, Double>) TFIDFvalQuery);
        
        if(denominator != 0.0) {
        		return numerator / denominator;
        }
        return 0.0;
    }
    
    private double norm(ChainedHashDictionary<String, Double> vector) {
    		double result = 0.0;
    		for (KVPair<String, Double> pair : vector) {
    			double score = pair.getValue();
    			result += Math.pow(score, 2);
    		}
    		
    		return Math.sqrt(result);
    	
    }
}
