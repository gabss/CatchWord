
package it.catchword.network.entity.impl;

import it.catchword.config.Constant;
import it.catchword.engine.GameEngine;
import it.catchword.engine.GameObject;
import it.catchword.entity.ChatMessage;
import it.catchword.entity.Game;
import it.catchword.entity.Player;
import it.catchword.entity.impl.SimplePlayer;
import it.catchword.network.entity.Channel;
import it.catchword.network.entity.Message;
import it.catchword.network.entity.NetworkAdapter;
import it.catchword.network.entity.RemoteUser;
import it.catchword.util.Debug;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefano on 14/03/2016.
*/
public class NetworkAdapterImpl implements NetworkAdapter, GameObject {

    private NetworkImpl network;
    private Game game;
    private List<RemoteUser> remoteUsers;
    private List<Player> players;
    private Channel channel;
    private RemoteUser localUser;
    private long lastOwnerUpdate;
    private int ownerId;
    private int mancheEndCounter = 0;
    private int wordAllowedCounter = 0;
    private String lastWord = "";


    public NetworkAdapterImpl(Game game){
        this.game = game;
        try {
            this.network = new NetworkImpl(this);
        } catch (RemoteException e) {
            Debug.print(e.getMessage());
        } catch (NotBoundException e) {
            Debug.print(e.getMessage());
        }
        remoteUsers = new ArrayList<>();
        players = new ArrayList<>();
    }

    @Override
    public List<RemoteUser> getRemoteUsers(){
        return remoteUsers;
    }

    private RemoteUser playerToRemoteUser(Player player){
        RemoteUser result = null;

        for (RemoteUser rUser : remoteUsers) {
            if (rUser.getId() == player.getId()) {
                result = rUser;
                break;
            }
        }

        return result;
    }

    private Player remoteUserToPlayer(RemoteUser remoteUser){
        Player result = null;
        for (Player pUser : players) {
            if (pUser.getId() == remoteUser.getId()) {
                result = pUser;
                break;
            }
        }

        return result;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
        remoteUsers = channel.getUsers();
        //Setta l'id al remote user e player (tenuto dal game)
        localUser.setId(remoteUsers.get(remoteUsers.indexOf(localUser)).getId());
        game.getLocalUser().setId(localUser.getId());
        //
        for (RemoteUser rUser : remoteUsers) {
            Player player = new SimplePlayer(rUser.getUsername());
            player.setId(rUser.getId());
            players.add(player);
        }
        Debug.print("Adapter: SetChannel: Numero di remote user: "+remoteUsers.size() + " numero di player: "+players.size());
        //Se c'è un solo giocatore, allora è l'owner della stanza (ed è il giocatore locale)
        if(players.size() == 1) {
            game.getLocalUser().setIsOwner(true);
            lastOwnerUpdate = System.currentTimeMillis();
        }
        else if(players.size()>0){
            Message message = new Message(Constant.MESSAGE_TYPE_WHOIS_OWNER, localUser);
            network.sendMessage(message, remoteUsers);
        }
    }

    @Override
    public void addUser(RemoteUser user) {
        Debug.print("Adapter: aggiungo l'utente "+user.getId());
        remoteUsers.add(user);
        SimplePlayer cPlayer = new SimplePlayer(user.getUsername());
        cPlayer.setId(user.getId());
        players.add(cPlayer);
    }

    @Override
    public void chatMessage(ChatMessage message) {
        game.addChatMessage(message);
    }

    @Override
    public void sendChatMessage(ChatMessage message) {
        network.sendMessage(new Message(Constant.MESSAGE_TYPE_CHAT, message), remoteUsers);
    }

    @Override
    public void receiveUpdateScore(RemoteUser user) {
        if(remoteUsers.contains(user)){
            RemoteUser local = remoteUsers.get(remoteUsers.indexOf(user));
            local.setScore(user.getScore());
            remoteUserToPlayer(local).setScore(local.getScore());
        }
    }

    @Override
    public long joinChannel(String channelName, Player player, String networkAddress) {
        Debug.print("Adapter: Cerco di entrare nel canale: "+channelName);
        localUser = new RemoteUserImpl(player.getUsername(), networkAddress);
        try {
            Channel cChannel = network.joinChannel(channelName, localUser);
            setChannel(cChannel);
            GameEngine.addObject(this);
            return channel.getId();
        } catch (RemoteException e) {
            Debug.print(e.getMessage());
            e.printStackTrace();
            return -1;
        } catch (NotBoundException e) {
            Debug.print(e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void whoIsOwner(RemoteUser data) {
        Debug.print("Adapter: Sono owner: "+game.getLocalUser().isOwner());
        if(game.getLocalUser().isOwner()){
            Message message = new Message(Constant.MESSAGE_TYPE_OWNER, game.getLocalUser().getId());
            network.sendMessage(message, data);
        }
    }

    @Override
    public List<Player> getPlayersList() {
        return players;
    }

    @Override
    public void wordCaught(String word, int score) {
        Debug.print("L'utente locale ha preso la parola: "+word + " con score: "+score);
        wordAllowedCounter = 1;
        lastWord = word;
        Message message = new Message(Constant.MESSAGE_TYPE_WORD_CAUGHT, localUser.getId()+";"+word+";"+score);
        network.sendMessage(message, remoteUsers);
    }

    @Override
    public void notifyMancheEnd() {
        mancheEndCounter++;
        Message message = new Message(Constant.MESSAGE_TYPE_MANCHE_END);
        network.sendMessage(message, remoteUsers);
    }

    @Override
    public List<String> getChannels() {
        try {
            return network.getChannelList();
        } catch (RemoteException e) {
            Debug.print(e.getMessage());
            return null;
        }
    }

    @Override
    public void startGame() {
        long startTime = System.currentTimeMillis()+8000; //Tra 8 sec si parte
        Message message = new Message(Constant.MESSAGE_TYPE_START_GAME, startTime);
        network.sendMessage(message, remoteUsers);
        game.startGame(startTime);
    }

    @Override
    public void startGame(long time) {
        game.startGame(time);
    }

    //E' la funzione per la gestione dell'owner. Ricevo un update costante dall'owner
    @Override
    public void ownerUpdate() {
        lastOwnerUpdate = System.currentTimeMillis();
    }

    @Override
    public void ownerDeclaration(int id) {
        Debug.print("Nuovo owner dichiarato: "+id);
        ownerId = id;
        lastOwnerUpdate = System.currentTimeMillis();
    }

    @Override
    public synchronized void mancheEnd() {
        mancheEndCounter++;
    }

    @Override
    public void remoteWordCaught(String idwordscore) {
        String[] idWordScore = idwordscore.split(";");
        int score = game.getWordInfo(idWordScore[1]);


        if(score < Integer.parseInt(idWordScore[2])) {
            if(score > 0){
                lastWord = "";
            }
            RemoteUser user = new RemoteUserImpl();
            user.setId(Integer.parseInt(idWordScore[0]));
            user = remoteUsers.get(remoteUsers.indexOf(user));
            Debug.print("L'utente: "+ user.getUsername() + " ha preso la parola");
            Message message = new Message(Constant.MESSAGE_TYPE_OK);
            game.removeWord(idWordScore[1]);
            network.sendMessage(message, user);
        }


    }

    @Override
    public synchronized void wordAllowed() {
        wordAllowedCounter++;
    }

    @Override
    public boolean update() {
        long now = System.currentTimeMillis();

        if(!game.getLocalUser().isOwner()) {
            if (now - lastOwnerUpdate > Constant.MAX_OWNER_DELAY) {
                //Vuol dire che l'owner non ci sta mandando più messaggi, probabilmente è morto
                //Diamo la possibilità all'id successivo

                ownerId = nextOwnerId(ownerId);
                lastOwnerUpdate = now;
                Debug.print("OwnwerId: "+ownerId);
                if(ownerId == game.getLocalUser().getId()) {
                    Debug.print("Sono diventato owner");
                    game.getLocalUser().setIsOwner(true);
                }
            }
        }else{
            //Noi siamo owner

            if(now - lastOwnerUpdate > Constant.OWNER_REFRESH_TIME){
                Message message = new Message(Constant.MESSAGE_TYPE_PINGPONG);
                network.sendMessage(message, remoteUsers);
                lastOwnerUpdate = now;
            }

        }

        if(game.getStatus() == Constant.GAME_STATUS_ACTIVE) {

            if (mancheEndCounter == remoteUsers.size()) {
                game.removeLastPlayer();
                mancheEndCounter = 0;
            }

            if(lastWord.equals("")){
                wordAllowedCounter = 0;
            }

            if (wordAllowedCounter == remoteUsers.size()) {
                localUser.setScore(localUser.getScore() + game.getWordInfo(lastWord));
                Message message = new Message(Constant.MESSAGE_TYPE_CONFIRM, localUser);
                network.sendMessage(message, remoteUsers);
                remoteUserToPlayer(localUser).setScore(localUser.getScore());
                lastWord = "";
                wordAllowedCounter = 0;
            }
        }
        return true;
    }

    private int nextOwnerId(int ownerId) {
        int i = (ownerId+1)%Constant.MAX_PLAYER_NUMBER;
        while (true){
            if(i == 0) i = 1;
            for(Player player : players){
                if(player.getId() == i)
                    return i;
            }
            i = (i+1)%Constant.MAX_PLAYER_NUMBER;
        }
    }

    @Override
    public void disconnected(RemoteUser user) {
        Player player = remoteUserToPlayer(user);
        if(player != null)
            players.remove(player);

        remoteUsers.remove(user);
    }
}