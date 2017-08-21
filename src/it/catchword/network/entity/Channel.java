package it.catchword.network.entity;

import java.util.List;

/**
 * Created by Stefano on 28/03/2016.
 */
public interface Channel{
    /**
     *
     * @return The list of remote user in the channel
     */
    List<RemoteUser> getUsers();

    /**
     * This method permits to add a User in the channel
     * @param var1 The user to add
     */
    void addUser(RemoteUser var1);

    /**
     * This method permits to remove a User from the channel
     * @param var1 The user to remove
     */
    void removeUser(RemoteUser var1);

    /**
     *
     * @return The numeric ID of the channel
     */
    long getId();

    /**
     * Set a numerical ID to the channel
     * @param var1 The ID to set
     */
    void setId(long var1);
}
