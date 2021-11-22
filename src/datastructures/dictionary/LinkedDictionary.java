package datastructures.dictionary;

import java.util.Arrays;

/**
 * A linked dictionary is a dictionary that uses a hash table with separate
 * chaining.
 * @param K the type of the keys.
 * @param V the type of the values.
 *
 * @author Zach Kissel
 */
 public class LinkedDictionary<K extends Comparable<? super K>, V> implements DictionaryInterface<K, V>
 {
   private Entry<K, V>[] table;
   private int numEntries;    // Number of entries in the dictionary.

   /**
    * Creates a new dictionary with default numEntries of 11.
    * Note: for optimal use, the numEntries of the dictionary should be a prime
    * number.
    */
    public LinkedDictionary()
    {
      this(2);
    }

   /**
    * Creates a new dictionary with numEntries sz.
    *
    * @param sz the size to set the dictionary to.
    */
  @SuppressWarnings("unchecked")
   public LinkedDictionary(int sz) throws IllegalArgumentException
   {
     if (sz <= 0)
      throw new IllegalArgumentException();

     this.table = new Entry[sz];
     this.numEntries = 0;
   }

   /**
    * Adds a new entry with key {@code key} and value {@code val}.
    *
    * @param key the key associated with the entry.
    * @param val the value associated with the entry.
    * @return null if a new entry was added or the entry that was replaced.
    */
   public V add(K key, V val)
   {
     Entry<K, V> entry;
     V tmp;
     
     // If the key is already found in the table, replace the value
     // with the new value.
     entry = findInChain(table[getHashIndex(key)], key);
     if (entry != null)
     {
      tmp = entry.getValue();
      entry.setValue(val);
      return tmp;
     }

     // At this point we did not find the key so we must add our
     // new entry to the head of the appropriate chain.
     entry = new Entry<K, V>(key, val, table[getHashIndex(key)]);
     table[getHashIndex(key)] = entry;
     this.numEntries++;

     //Checks and Performs table doubling and table halfing
     rehashAndResisze(getLoadFactor());
     
     return null;
   }

   /**
    * Removes the entry with key {@code key} from the dictionary.
    *
    * @param key the key associated with the entry to remove.
    * @return the value associated with the key or null.
    */
   public V remove(K key)
   {
     Entry<K, V> entry;
     Entry<K, V> tmp;
     
     System.out.println(Arrays.toString(table) + "   " +  getHashIndex(key) + " " + table[getHashIndex(key)]);
     
     //If the table[i] value is the key 
     if(table[getHashIndex(key)].getKey().equals(key)) {
         //Assigns key
         entry = table[getHashIndex(key)];
         
         //Assign table[i] to Entrys next, mininize numEntries, and return the value
         table[getHashIndex(key)] = entry.getNext();
         this.numEntries--;
         
         //Checks and Performs table doubling and table halfing
         rehashAndResisze(getLoadFactor());
         
         return entry.getValue();
     }
     
     //If the value is in a chain removed
     for (entry = table[getHashIndex(key)]; entry != null; entry = entry.getNext())
     { 
       // Remove the entry if we have found it.
       if (entry.getNext() != null && entry.getNext().getKey().equals(key))
       {
         tmp = entry.getNext();
         entry.setNext(tmp.getNext());
         tmp.setNext(null);
         this.numEntries--;

         //Checks and Performs table doubling and table halfing
          rehashAndResisze(getLoadFactor());
         
         return tmp.getValue();
       } 
     }
     
        //Checks and Performs table doubling and table halfing
     rehashAndResisze(getLoadFactor());
     
     return null;
   }

   /**
    * Gets the entry with key {@code key} from the dictionary.
    *
    * @param key the key associated with the entry to retrieve
    * @return the value associated with the key or null.
    */
   public V getValue(K key)
   {
     Entry<K, V> entry =  findInChain(table[getHashIndex(key)], key);

     if (entry != null)
        return entry.getValue();
     return null;
   }

   /**
    * Determines if the dictionary contains an entry with key {@code key}.
    *
    * @param key the key associated with the entry to find.
    * @return true if the key was found in the dictionary; otherwise, false.
    */
   public boolean contains(K key)
   {

     return findInChain(table[getHashIndex(key)], key) != null;
   }

   /**
    * Determines if the dictionary is empty.
    *
    * @return true if the dictionary is empty; otherwise, false.
    */
   public boolean isEmpty()
   {
     return this.numEntries == 0;
   }

   /**
    * Gets the number of entries of the dictionary.
    *
    * @return the number of entries currently in the dictionary.
    */
   public int getSize()
   {
     return this.numEntries;
   }

   /**
    * Removes all entries from the dictionary.
    */
   public void clear()
   {
     for (int i = 0; i < table.length; i++)
      table[i] = null;
     numEntries = 0;
   }
   
    /**
    * Gets a list of all keys in the dictionary.
    *
    * @return a list of keys in the table.
    */
    public Object[] getKeys()
    {
      Object[] keys = new Object[numEntries];

      int currEntry = 0;

      Entry<K, V> walker;

      for (int i = 0; i < table.length; i++)
      {
          for (walker = table[i]; walker != null; walker = walker.getNext())
              keys[currEntry++] = walker.getKey();
      }
      return keys;
    }

    /**
     * Get the load factor of the table.
     *
     * @return the load factor of the table.
     */
     public double getLoadFactor()
     {
       return ((double) numEntries / (double) table.length);
     }

   /********
    * Private Methods
    ********/

    /**
     * Finds a location for the key {@code key}.
     *
     * @param key the key to insert.
     * @return an index in the table, a number between 0 and numEntries - 1 inclusive
     */
    private int getHashIndex(K key)
    {
      int idx = key.hashCode() % table.length;

      if (idx < 0)
        idx += table.length;

      return idx;
    }

    /**
     * Finds an entry in the chain and returns it to the caller.
     *
     * @param head the head node of the chain.
     * @param key the key to search the chain for.
     * @return the node that mathces the key {@code key} or null.
     */
    private Entry<K, V> findInChain(Entry<K, V> head, K key)
    {
      Entry<K, V> walker;

      for(walker = head; walker != null; walker = walker.getNext())
        if (walker.getKey().equals(key))
          return walker;
      return null;
    }
    
    /**
     * Resizes the table and performs table double-ing and table half-ing.
     * Only works when the loadFactor > 0.5 or loadFactor < 0.25
     * 
     * @param loadFactor the load factor of the current table
     */
    private void rehashAndResisze(double loadFactor) {
        // If the loadFactor is greater than 0.5 we table double.
        // If the loadFactor is less than 0.25 we table half.    
        if(loadFactor > 0.5 || loadFactor < 0.25) {            
            //New table that we will hash entries to.
            Entry<K,V>[] tempTable = (loadFactor > 0.5) ? new Entry[table.length*2] : new Entry[table.length/2];
            
            System.out.println(""+ table.length*2 +" here " + numEntries);
            
            // Walker Entry
            Entry<K, V> walker;
            // Loops through every Entry in the table
            for (Entry<K, V> table1 : table) {
                //Loops through the chain
                for (walker = table1; walker != null; walker = walker.getNext()) {
                    // Appends each Entry from table to tempTable at a re-hashed position
                                        
                    //Creates a new entry
                    Entry<K, V> newEntry = new Entry(walker.getKey(), walker.getValue(), tempTable[getHashIndexSizeable(walker.getKey(), tempTable.length)]);
                    
                    //Assigns the new entry to the start of tempTable
                    tempTable[getHashIndexSizeable(walker.getKey(), tempTable.length)] = newEntry;
                }
            }
            
            // Assign tempTable to table
            this.table = tempTable;
        }
    }
    
    /**
     * Finds a location for the key {@code key}.
     * Used for find the index of tables that are not the global table
     *
     * @param key the key to insert.
     * @param length length of the current table
     * @return an index in the table, a number between 0 and numEntries - 1 inclusive
     */
    private int getHashIndexSizeable(K key, int length)
    {
      int idx = key.hashCode() % length;

      if (idx < 0)
        idx += length;

      return idx;
    }
    
 }
