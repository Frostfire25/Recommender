package words;

import driver.Option;
import datastructures.dictionary.LinkedDictionary;
import datastructures.list.LinkedList;
import datastructures.tree.BinarySearchTree;
import java.io.FileNotFoundException;

/**
 * A Corpus of Documents
 * @author Alexander Elguezabal
 */
public class Corpus {
    
    private LinkedDictionary<String, WordBag> collection;
    private LinkedDictionary<String, Integer> doqFrequency;
    
    public Corpus() {
        this.collection = new LinkedDictionary<>();
        this.doqFrequency = new LinkedDictionary<>();
    }
    
    /**
     * Adds a Document to collection
     * 
     * @param label String label of the document
     * @param fileName String location of the document to be loaded
     */
    public void addDocument(String label, String fileName) {
        //New WordBag
        WordBag wordBag = new WordBag();
        
        //Loads the doccument and catches FileNotFound exception
        try {
            wordBag.loadDoccument(fileName);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        
        //Adds wordBag to collection
        collection.add(label.toLowerCase(), wordBag);
        
        //Updates doqFrequency
        wordBag.getWords().stream().forEach(word -> doqFrequency.add(word, doqFrequency.contains(word) ? doqFrequency.getValue(word)+1 : 1));
    }
    
    /**
     * Returns the top amount of Options from WordBag
     * 
     * @param title String title of the WordBag
     * @param k Integer amount of Options to be returned
     * @return LinkedList<Option> amount of Options to be returned
     */
    public LinkedList<Option> getTopKRecommendations(String title, int k) {
        //Word bag to compare to for each bag in collection         
        final WordBag comparitiveWordBag = collection.getValue(title);
        
        //The searched WordBag must exist in the list
        assert comparitiveWordBag != null;
        
        //BinarySearchTree<Option> to store Options
        BinarySearchTree<Option> bst = new BinarySearchTree<>();
        
        int i = 0;
                
        for(Object n : collection.getKeys()) {
            
            //Key and title of a WordBag
            @SuppressWarnings("ignored")
            String key = (String) n;
            
            //If iterates over the same WordBag, skipped.
            if(key.equalsIgnoreCase(title))
                continue;
            
            //WordBag from n
            WordBag bag = collection.getValue(key);
                        
            double similarity = comparitiveWordBag.getSimilarity(bag);
                        
            //Option from the similarities of the two bags
            Option option = new Option(key, (100-similarity));
            
            bst.add(option);
        }
                               
        //LinkedList of options that are sorted from bst and truncated by-k
        LinkedList<Option> truncated = bst.sort();
        truncated.truncate(k);
        
        //returns truncated
        return truncated;
    }
    
    /**
     * Applies IDF onto each WordBag
     */
    public void applyTFIDF() {
        LinkedDictionary<String, Double> idf = new LinkedDictionary<>();
        
        //Iterates through each Word in doqFrequency and adds it to idf
        for(Object n : doqFrequency.getKeys()) {
            
            @SuppressWarnings("ignored")
            String word = (String) n;
            
            //Need to check math here
            idf.add(word, 1 + Math.log( collection.getSize()/ (1+doqFrequency.getValue(word)) ));
        }
        
        //Applies the IDF on each WordBag
        for(Object n : collection.getKeys()) {
            
            @SuppressWarnings("ignored")
            String title = (String) n;
            
            collection.getValue(title).applyTFIDF(idf);
        }
    }
    
}
