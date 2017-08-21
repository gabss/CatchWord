package it.catchword.entity;


import it.catchword.config.Constant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * This class generate the sequence of the HoleWord needed by board.
 */
public class Dictionary {

    private List<HoleWord> dictionary = new ArrayList<>();
    private int index = 0;
    private HoleWord currentWord;

    /**
     * The constructor initialize the sequence of HoleWord in a random way guided by seed.
     * This will guarantee that all the players with the same seed (eg. the id of the channel) will have the same words in the same hole.
     * @param seed
     */
    public Dictionary(long seed){
        try {
            BufferedReader br = new BufferedReader(new FileReader("dizionario"));
            List<String> words = new LinkedList<String>(Arrays.asList(br.readLine().replace("[","").replace("]","").split("[\\s,]+")));
            filter(words);
            generateWords(words, seed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filter(List<String> words) {
        ListIterator<String> it = words.listIterator();

        while(it.hasNext()){
            String word = it.next();
            if(word.length() <= 3 || word.length() >= 10)
                it.remove();
        }
    }

    /**
     * This method will offer the next word on the dictionary. We keep an index because it's possible that the word isn't suitable ( eg. its hole
     * is currently fill). So the idea is that the board call next() until this generates a suitable word, then will call confirm().
     * @return The word at index <i>index</i> at dictionary.
     */
    HoleWord next(){
        currentWord = dictionary.get(index++);
        return currentWord;
    }

    /**
     * This method is needed to confirm to dictionary that the last word generated is suitable so that it can remove this word.
     * After the removal of the word, the index will set to 0.
     */
    void confirm(){
        dictionary.remove(currentWord);
        index = 0;
    }

    /**
     * Util function to generate the random list of dictionary
     * @param words A list of words.
     * @param seed Random seed
     */
    private void generateWords(List<String> words, long seed){
        Random generator = new Random(seed);
        int max_words = 1000;
        for (int i=0; i<max_words; i++){
            int n_hole = (int)(generator.nextFloat() * Constant.HOLES_NUMBER);
            int word_index = (int)(generator.nextFloat() * words.size());

            dictionary.add(new HoleWord(words.get(word_index), n_hole));


        }

    }


}
