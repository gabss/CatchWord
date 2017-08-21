package it.catchword.network.entity;

import it.catchword.entity.ChatMessage;
import it.catchword.entity.Player;

import java.util.List;

/**
 * Created by Stefano on 14/03/2016.
 */
public interface NetworkAdapter {

    /**
     * Set the current channel of the match
     * @param channel The channel to set
     */
    void setChannel(Channel channel);

    /**
     * Add user to the current match
     * @param user The user which has to be added
     */
    void addUser(RemoteUser user);

    /**
     * This method notify to the Game that the score of the passed RemoteUser has to be updated
     * @param user The user to update in the local list
     */
    void receiveUpdateScore(RemoteUser user);

    /**
     * This method try to let the local player join a specified channel.
     * @param player The Local player
     * @param channelName The channel name of the channel where the local user wants to join
     * @param networkAddress The hostname/ip of the local user
     * @return The ID of the joined channel
     */
    long joinChannel(String channelName, Player player, String networkAddress);

    /**
     *
     * @return The list of playing user
     */
    List<Player> getPlayersList();

    /**
     * Notify to the Adapter that the local player has caught a word.
     * @param word The caught word
     * @param score The score relative the the caught word
     */
    void wordCaught(String word, int score);

    /**
     * This method is called by the Game for notifying to the Adapter that the actual manche is end
     */
    void notifyMancheEnd();

    /**
     *
     * @return The list of channels
     */
    List<String> getChannels();

    /**
     * Local player is owner of the channel and he wants to start the game.
     */
    void startGame();

    /**
     * We have received a message of start game. It means that the owner of the room chose to start the game.
     * @param time The time to wait in ms since now.
     */
    void startGame(long time);

    /**
     * The owner of the channel notify his presence
     */
    void ownerUpdate();

    /**
     * A new owner has been declared
     * @param id The id of the new owner
     */
    void ownerDeclaration(int id);

    /**
     * Other networks notify to us that they finish their actual manche
     */
    void mancheEnd();

    /**
     * Another user has caught a word
     * @param wordscore A string formatted as: word;score
     */
    void remoteWordCaught(String wordscore);

    /**
     * The other user confirm us that we can take our last word caught
     */
    void wordAllowed();

    /**
     * The local user wants to know who is owner
     * @param data
     */
    void whoIsOwner(RemoteUser data);

    /**
     * A user has been disconnected.
     * @param user The disconnected user
     */
    void disconnected(RemoteUser user);

    /**
     *
     * @return The list of remote users
     */
    List<RemoteUser> getRemoteUsers();

    /**
     * Our network has received a chat message
     * @param message
     */
    void chatMessage(ChatMessage message);

    /**
     * The local user wants to send a chat message
     * @param message The chat message to send
     */
    void sendChatMessage(ChatMessage message);
}
