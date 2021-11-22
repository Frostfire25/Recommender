package words;

import datastructures.dictionary.LinkedDictionary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Stores a Bag of Words
 * @author Alexander Elguezabal
 */
public class WordBag {
    
    private LinkedDictionary<String, Double> wordVec;
    private int uniqueWords;
    private int totalWords;
    private int totalLines;
    
    /**
     * Default Constructor
     */
    public WordBag() {
        init();
    }
    
    /**
     * Takes in a doccument and loads it to this WordBag
     * Parses each line of the file and loads the words
     * 
     * @param fileName String name of the file
     * @throws FileNotFoundException if file can not be found, or an issue arrises while parsing
     */
    public void loadDoccument(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        
        if(!file.exists())
            throw new FileNotFoundException("File name " +fileName+" did not exist.");
        
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
        
            //Gets all the lines
            br.lines()
                    //Maps each line to a String[] of words split by the space
                    .map(n -> n.split(" "))
                    //For every line we add each of the words to wordVec and increase the counters
                    .forEach(n -> {
                
                //Increase the total number of lines
                this.totalLines++;
                
                //For every line we get all the strings
                for(String m : n) {
                    
                    //Sets the word to lowercase
                    String word = m.toLowerCase();
                    
                    //Removes all white space and punctuation
                    word = word.replaceAll("\\W+",""); //QUESTION! ask if \\W removes all whitespace and punctuation
                    
                    //If wordVec already contains Key(m) then we increase the count by one
                    if(wordVec.contains(word)) {
                       wordVec.add(word, (wordVec.getValue(word) + 1.0));
                    } else {
                        wordVec.add(word, 1.0);
                        this.uniqueWords++;
                    }
                    
                    this.totalWords++;
                }
            });
        
        } catch(IOException e) {
            throw new FileNotFoundException(String.format("Error parsing file %s.", fileName));
        }
        
    }
    
    //QUESTION! Do I have to sort the words in the WordBag when finding the Dot Product? And what happens if I have more words? Do I use the larger bag
    
    /**
     * Returns the similiarty of this WordBag compared to another
     * 
     * θ = cos−1 ( v1 (dot_product) v2 / ||v1|| × ||v2|| ) 
     * 
     * @param wordBag WordBag to compare to
     * 
     * @return Theta (degree) of similarty between two word bags
    */
    public double getSimilarity(WordBag wordBag) {
        LinkedDictionary<String, Double> largerBag = (wordVec.getSize() > wordBag.wordVec.getSize()) ? wordVec : wordBag.wordVec;
        LinkedDictionary<String, Double> smallerBag = (wordVec.getSize() < wordBag.wordVec.getSize()) ? wordVec : wordBag.wordVec;

        return Math.acos((computeDotProduct(largerBag, smallerBag) / computeEuclideanNorm(largerBag, smallerBag)));
    }
    
   
    /**
     * Clears this WordBag
     */
    public void clear() {
        init();
    }
    
    /**
     * Determines if this WordBag is empty
     * @return boolean if this WordBag does not contain any words
     */
    public boolean isEmpty() {
        return wordVec.isEmpty();
    }
    
    /**
     * To-String Method
     * @return "this.totalLines Lines, this.totalWords words, this.uniqueWords unique words."
     */
    @Override
    public String toString() {
        return String.format("%s Lines, %s words, %s unique words.", this.totalLines, this.totalWords, this.uniqueWords);
    }
    
    /*
    Add a method applyTFIDF that takes a LinkedDictionary with keys that are
    String and values that are Doubles called idf. For each word w in the bag’s
    wordVec update the value to be f
    D
    idf (w) where D is the number of words in the

    document and idf (w) is the IDF value associated with word w.
    */
    
    /**
     * Gets a List of Words fro the WordBag
     * @return ArrayList<String> all keys from wordVec
     */
    public ArrayList<String> getWords() {
        ArrayList<String> words = new ArrayList<>();
        
        //Adds all Keys from wordVen to words
        for(Object n : wordVec.getKeys()) {
            
            @SuppressWarnings("ignored")
            String word = (String) n;
            
            words.add(word);
        }
        
        return words;
    }
    
    /**
     * Applies each word in wordVec with it's TFIDF
     * 
     * @param tFIDF LinkedDictionary<String, Double> where String is a Word and the Double is it's IDF value
     */
    public void applyTFIDF(LinkedDictionary<String, Double> tFIDF) {
        //Applies each word in wordVec with the appropriet TFIDF
        getWords().stream().forEach(key -> {
            if(tFIDF.contains(key))
              wordVec.add(key, (wordVec.getValue(key)/totalWords)*tFIDF.getValue(key));
        });
    }
    
    /*
    Private Methods
    */
    
    /**
     * Initializes this class
     * Instantiates all global variables for the constructor and clear method(s)
     */
    private void init() {
        this.wordVec = new LinkedDictionary<>();
        this.uniqueWords = 0;
        this.totalWords = 0;
        this.totalLines = 0;
    }
    
    /**
     * Computes the DotProduct for getSimilarity
     * Parameters are fixed, largerBag must be the largerBag and thus wise with smallerBag
     * 
     * DotProduct = (<lB:1> * <sB:1>) + (<lB:2> * <sB:2>)...
     * 
     * @param largerBag LinkedDictionary<String, Double> larger (of the two) bag to be used
     * @param smallerBag LinkedDictionary<String, Double> smaller (of the two) bag to be used
     * @return Dot product of both LinkedDictionary (doubles)
     */
    private double computeDotProduct(LinkedDictionary<String, Double> largerBag, LinkedDictionary<String, Double> smallerBag) { // Make sure passed through bags are the larger and smaller accordingly
        double dotProduct = 0.0; // Returns the dotProduct
        
        //Loops through all the smaller bags keys (every key possibly used)
        for(Object n : smallerBag.getKeys()) {
            
            @SuppressWarnings("ignored")
            String key = (String) n;
            
            //Adds to dot product
            if(largerBag.contains(key)) {
                dotProduct += (largerBag.getValue(key) * smallerBag.getValue(key));
            }
        }

        return dotProduct;
    }
    
   /**
    * Computes the Euclidean Norm for WordBag and WordBag2
    * Euclidean Norm = |sqrt(b1:1^2 + b1:2^2...)| * |sqrt(b2:1^2 + b2^2...)|
    * 
    * @param bag1 LinkedDictionary<String, Double> bag one to be used 
    * @param bag2 LinkedDictionary<String, Double> bag two to be used
    * @return Euclidean Norm of bag1 and bag2
    */ 
   private double computeEuclideanNorm(LinkedDictionary<String, Double> bag1, LinkedDictionary<String, Double> bag2) { 
       double v1 = 0.0;
       double v2 = 0.0;
       
        //Loops through all bag(s) keys (every key possibly used)
        for(Object n : bag1.getKeys()) {
            
            @SuppressWarnings("ignored")
            String key = (String) n;
            
            v1 += (Math.pow(bag1.getValue(key), 2));
        }
        
        //Loops through all of bag2(s) keys (every key possibly used)
        for(Object n : bag2.getKeys()) {
            
            @SuppressWarnings("ignored")
            String key = (String) n;
            
            v2 += (Math.pow(bag2.getValue(key), 2));
        }
       
       return Math.abs(Math.sqrt(v1)) * Math.abs(Math.sqrt(v2));
   }
}
