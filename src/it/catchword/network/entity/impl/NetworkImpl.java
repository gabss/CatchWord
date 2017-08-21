package it.catchword.network.entity.impl;

import it.catchword.config.Constant;
import it.catchword.entity.ChatMessage;
import it.catchword.network.entity.*;
import it.catchword.util.Debug;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefano on 14/03/2016.
 */
public class NetworkImpl implements Network{

    private RemoteUser localUser;
    private Server gameServer;
    private Map<Integer,Network> networks = new HashMap<>();
    private NetworkAdapter adapter;
    private Registry serverRegistry;
    private Registry clientRegistry;
    private Channel channel;

    public NetworkImpl(NetworkAdapter adapter) throws java.rmi.RemoteException, NotBoundException {
        this.adapter = adapter;
        clientRegistry = LocateRegistry.createRegistry(Constant.CLIENT_PORT);
        serverRegistry = LocateRegistry.getRegistry(Constant.SERVER_ADDRESS, Constant.SERVER_PORT);
        gameServer = (Server) serverRegistry.lookup(Constant.SERVER_NAME);
    }

    /**
     * This method ask for the channel list to the server
     * @return The current channels list
     * @throws RemoteException
     */
    public List<String> getChannelList() throws RemoteException {
        Message message = gameServer.getChannelsList();
        return (List<String>) message.getData();
    }

    /**
     * This method sends information about the local user, and the channel where who wants join, to the server
     * @param channelName The channel name where the local user wants enter
     * @param user The local user
     * @return The reference to the Channel
     * @throws RemoteException
     * @throws NotBoundException
     */
    public Channel joinChannel(String channelName, RemoteUser user) throws RemoteException, NotBoundException {
        Debug.print("Network: Cerco di entrare nel canale: "+channelName);
        Message message = gameServer.joinChannel(user, channelName);

        channel = (Channel) message.getData();
        localUser = user;
        localUser.setId(channel.getUsers().get(channel.getUsers().indexOf(localUser)).getId());
        Debug.print("Id utente locale: "+localUser.getId());
        Network stub = (Network) UnicastRemoteObject.exportObject(this, 0);
        clientRegistry.rebind(Integer.toString(localUser.getId()), stub);

        //Recuperiamo la lista dei network attuali
        for (RemoteUser cUser : channel.getUsers()){
            if(cUser.getId() != localUser.getId()) {
                Debug.print("Analizzo l'utente: "+cUser.getUsername()+ " con id: "+cUser.getId());
                Registry localRegistry = LocateRegistry.getRegistry(cUser.getIp(), Constant.CLIENT_PORT);
                Network cNetwork = (Network) localRegistry.lookup(Integer.toString(cUser.getId()));
                networks.put(cUser.getId(), cNetwork);
                cNetwork.receiveMessage(new Message(Constant.MESSAGE_TYPE_JOIN, localUser));
            }
        }

        return channel;
    }

    private void addUser(RemoteUser user) throws RemoteException, NotBoundException {
        //Dobbiamo aggiungere un utente alla nostra lista (è entrato dopo di noi)
        Debug.print("Aggiungo l'utente: "+user.getUsername()+" id: "+user.getId());
        Registry localRegistry = LocateRegistry.getRegistry(user.getIp(), Constant.CLIENT_PORT);
        networks.put(user.getId(), (Network) localRegistry.lookup(Integer.toString(user.getId())));
        adapter.addUser(user);

    }

    private void startGame(long time){
        adapter.startGame(time);
    }

    private void pingPong (){
        adapter.ownerUpdate();
    }

    @Override
    public void receiveMessage(Message message) throws RemoteException, NotBoundException {
        switch(message.getType()){
            case Constant.MESSAGE_TYPE_JOIN:
                addUser((RemoteUser)message.getData());
                break;
            case Constant.MESSAGE_TYPE_START_GAME:
                startGame((long)message.getData());
                break;
            case Constant.MESSAGE_TYPE_PINGPONG: //owner
                pingPong();
                break;
            case Constant.MESSAGE_TYPE_OWNER:
                ownerDeclaration((int)message.getData());
                break;
            case Constant.MESSAGE_TYPE_MANCHE_END:
                mancheEnd();
                break;
            case Constant.MESSAGE_TYPE_WORD_CAUGHT:
                wordCaught((String)message.getData());
                break;
            case Constant.MESSAGE_TYPE_OK:
                ok();
                break;
            case Constant.MESSAGE_TYPE_CONFIRM:
                confirm((RemoteUser)message.getData());
                break;
            case Constant.MESSAGE_TYPE_WHOIS_OWNER:
                whoIsOwner((RemoteUser)message.getData());
                break;
            case Constant.MESSAGE_TYPE_USER_DISCONNECTED:
                disconnected((RemoteUser)message.getData());
                break;
            case Constant.MESSAGE_TYPE_CHAT:
                chatMessage((ChatMessage) message.getData());
        }
    }

    private void chatMessage(ChatMessage data) {
        adapter.chatMessage(data);
    }

    private void whoIsOwner(RemoteUser data) {
        adapter.whoIsOwner(data);
    }

    private void wordCaught(String idwordscore) {
        Debug.print("Network: Qualche utente ha catturato una parola: "+idwordscore);
        adapter.remoteWordCaught(idwordscore);
    }

    private void ok(){
        adapter.wordAllowed();
    }

    private void confirm(RemoteUser user){
        adapter.receiveUpdateScore(user);
    }

    private void mancheEnd() {
        adapter.mancheEnd();
    }

    private void ownerDeclaration(int id) {
        adapter.ownerDeclaration(id);
    }

    /**
     * This method represent the primitive for sending message to another User
     * @param message The message to send
     * @param user The user which has to receive the message
     * @throws RemoteException
     * @throws NotBoundException
     */
    public boolean sendMessage(Message message, RemoteUser user) {
        if(!user.equals(localUser)) {
            try {
                networks.get(user.getId())
                        .receiveMessage(message);
                return true;
            } catch (RemoteException e) {
                Debug.print("User disconnesso: " + user.getUsername());
                return false;
            } catch (NotBoundException e) {
                Debug.print(e.getMessage());
                return false;
            }
        }
        return true;
    }

    private void disconnected(RemoteUser user) {
        networks.remove(user.getId());
        try {
            gameServer.removePlayer(user, channel);
        } catch (RemoteException e) {
            Debug.print("Tentativo di eliminare il player: "+user.getUsername()+" dalla lista del server. Il server è down.");
        }
        adapter.disconnected(user);
    }

    /**
     * This method permits to send a message to a list of user
     * @param message The message to send
     * @param users The users which have to receive the message
     * @throws RemoteException
     * @throws NotBoundException
     */
    public synchronized void sendMessage(Message message, List<RemoteUser> users){
        List<RemoteUser> disconnected = new ArrayList<>();
        List<RemoteUser> tmpUsers = new ArrayList<>();
        tmpUsers.addAll(users);
        for (RemoteUser user : users) {
            if(!sendMessage(message, user))
                disconnected.add(user);
        }

        for(RemoteUser user : disconnected){
            disconnected(user);
            sendMessage(new Message(Constant.MESSAGE_TYPE_USER_DISCONNECTED, user), adapter.getRemoteUsers());
        }

    }


}
