package it.catchword.network.entity;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Stefano on 14/03/2016.
 */
public interface Network extends Remote {

    /**
     * The unique remote function. It permits to the local network to receive a message from the others.
     * @param message A message sent from another network to local user
     * @throws RemoteException
     * @throws NotBoundException
     */
    void receiveMessage(Message message) throws RemoteException, NotBoundException;
}
