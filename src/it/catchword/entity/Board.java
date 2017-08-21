package it.catchword.entity;

import it.catchword.config.Constant;

/**
 * This class represents the current state of the board of the game. So it keeps track of the words in the different holes and permits to
 * generate new words or to remove older ones.
 */
public class Board {
    private Dictionary dictionary;
    private String[] board;

    /**
     * The constructor initializes the board and call the constructor of Dictionary.
     * @param seed The seed utilized for the random initialization of Dictionary
     */
    public Board(long seed){
        board= new String[Constant.HOLES_NUMBER];
        for (int i = 0; i<board.length; i++)
            board[i] = "";
        dictionary = new Dictionary(seed);
    }



    /**
     * This method will return the word at the hole in index passed.
     * @param index The index of the wanted word.
     * @return The word indexed by @param index.
     */
    public String getWord(int index){
        return board[index];
    }

    /**
     * This method will return the length of the board. The meaning of 'length' is the number of words currently present on it.
     * @return The number of the words currently present on the board.
     */
    public int getLength(){
        int len=0;
        for(int i=0; i<6; i++){
            if(!board[i].equals(""))len++;
        }
        return len;
    }

    /**
     * This method will set the word at index passed to empty string.
     * @param index The index of the word to remove
     */
    public void removeWord(int index){
        board[index]="";
    }

    public void removeWord(String word){
        for(int i = 0; i<Constant.HOLES_NUMBER; i++){
            if(word.equals(board[i])){
                board[i] = "";
            }
        }
    }
    /**
     * This method will trigger the spawn of the next word on board.
     * @return The index of the new spawned word.
     */
    public int spawnWord(){
        if(getLength() <= Constant.HOLES_NUMBER + 1) {
            HoleWord tmp = dictionary.next();
            while (!getWord(tmp.getHole()).equals(""))
                tmp = dictionary.next();
            board[tmp.getHole()] = tmp.getWord();
            dictionary.confirm();
            return tmp.getHole();
        }
        return -1;
    }


}
