package it.catchword.engine;

/**
 * Interface for the Object that can be updated by the GameEngine
 */
public interface GameObject {
    /**
     * The method that will be called by GameEngine.
     * @return FALSE, if the GameObject wants to stop the game.
     * @return TRUE, otherwise.
     */
    boolean update();
}
