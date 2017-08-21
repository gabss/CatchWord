package it.catchword.entity;

/**
 * Interface for player
 */
public interface Player {
    /**
     * Return the ID of the player
     * @return The numeric ID of the player
     */
    int getId();

    /**
     * Set the ID of the player
     * @param i The numeric ID to set
     */
    void setId(int i);

    /**
     * Return current score of the player
     * @return The player's current score
     */
    int getScore();

    /**
     * Set the passed valued as player's score
     * @param score The score to set
     */
    void setScore(int score);

    /**
     * Return the current status of the player
     * @return The status of the player
     */
    int getStatus();

    /**
     * Set the status to the player
     * @param status The status to set
     */
    void setStatus(int status);

    /**
     * Return the username of the player
     * @return The player's username
     */
    String getUsername();

    /**
     * Set the username to the player
     * @param username The username to set
     */
    void setUsername(String username);

    /**
     * Check if the player is the owner of the channel
     * @return true, if local player is the owner of the room
     *         false, otherwise
     */
    boolean isOwner();

    /**
     * Set the player as owner (or not wrt parameter owner) of channel
     * @param owner true, if you want to elect the local player as owner
     *              false, otherwise
     */
    void setIsOwner(boolean owner);
}
