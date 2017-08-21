package it.catchword.network.entity;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Stefano on 28/03/2016.
 */
public interface Server extends Remote{

    Message getChannelsList() throws RemoteException;

    Message joinChannel(RemoteUser var1, String var2) throws RemoteException;

    void removePlayer(RemoteUser user, Channel channel) throws RemoteException;
}
