package it.catchword.entity;

/**
 * HoleWord represents our primitive data. It has reference to a String and relative hole.
 * It's used by the board and the dictionary.
 */
public class HoleWord {


    private String word;
    private int hole;

    public HoleWord(String word, int hole){
        this.word = word;
        this.hole = hole;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getHole() {
        return hole;
    }

    public void setHole(int hole) {
        this.hole = hole;
    }

    @Override
    public String toString() {
        return "HoleWord{" +
                "word='" + word + '\'' +
                ", hole=" + hole +
                '}';
    }
}
