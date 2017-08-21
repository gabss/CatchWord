package it.catchword.entity;

import java.util.List;

/**
 * Core interface of the game. Who implements it has to manage all the game and merge the others component (network, core, gui)
 */
public interface Game {

    /**
     * This method will return all the users playing the game.
     * @return The list of all users
     */
    List<Player> getAllUser();

    /**
     * This method will return only the local user.
     * @return The local user playing the game.
     */
    Player getLocalUser();

    /**
     * This method will return the status of the game.
     * The differents status are stored in the Constant class.
     * <ul>
     *     <li>GAME_STATUS_INIT</li>
     *     <li>GAME_STATUS_WAIT</li>
     *     <li>GAME_STATUS_ACTIVE</li>
     *     <li>GAME_STAtUS_END</li>
     * </ul>
     * @return The status of the game.
     */
    int getStatus();

    /**
     * This method will ask to board for the word in the hole with index @param index
     * @param index The index of the hole which we want to get word in.
     * @return The string in the indexed hole
     */
    String getWord(int index);

    /**
     * This method will ask to board to remove the word at the passed index.
     * @param word The index of the hole where we want remove the word.
     */
    void removeWord(String word);

    /**
     * This method try to let the local player join a specified channel.
     * @param playerName Username of the local player
     * @param channelName The channel name of the channel where the local user wants to join
     * @param networkAddress The hostname/ip of the local user
     * @return 1, if everything goes fine
     *         0, otherwise
     */
    int joinChannel(String playerName, String channelName, String networkAddress);

    /**
     * This method is called by the local player. He is owner of the channel and he wants to start the game
     * @return Constant.OK, if everything goes fine
     *         Constant.NOT_ENOUGH_PLAYERS, if there isn't enough player to start game
     */
    int startGame();

    /**
     * This method is called by the network adapter. It means that the owner of the room chose to start the game.
     * @param time The time to wait in ms since now.
     */
    void startGame(long time);

    /**
     * The local player has caught the word which is in position 'index' on the board.
     * @param index The board's index of the word
     */
    void wordCaught(int index);

    /**
     * This method removes the last player of the rank
     */
    void removeLastPlayer();

    /**
     * This method returns the current server's channel list
     * @return The list of the channels name
     */
    List<String> getChannels();

    /**
     * This method try to find if the local player has caught passed word. If so, returns the obtained score.
     * @param word The word to search for in the history
     * @return Score value, if the word has been caught
     *         0, otherwise
     */
    int getWordInfo(String word);

    /**
     *
     * @return The number of the current manche
     */
    int getManches();

    /**
     *
     * @return The remaining seconds before the current manche ends
     */
    int getMancheTime();

    /**
     * This method is called by NetworkAdapter, whenever another user send a chat message
     * @param message The new message received
     */
    void addChatMessage(ChatMessage message);

    /**
     *
     * @return The list of all chat messages
     */
    List<ChatMessage> getChatMessages();

    /**
     * It permits to the local player to send a chat message
     * @param message The message to sent
     */
    void sendChatMessage(String message);

    long getStartTime();

}
