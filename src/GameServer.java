import it.catchword.config.Constant;
import it.catchword.network.entity.Channel;
import it.catchword.network.entity.Message;
import it.catchword.network.entity.RemoteUser;
import it.catchword.network.entity.Server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefano on 12/03/2016.
 */
public class GameServer implements Server {

    Map<String, Channel> channels = new HashMap<>();

    public GameServer(){
        super();
    }


    @Override
    public Message getChannelsList() throws RemoteException {
        List<String> result = new ArrayList<>();

        for (Map.Entry<String, Channel> entry : channels.entrySet()){
            result.add(entry.getKey());
        }

        return new Message(Constant.MESSAGE_TYPE_CHANNEL_LIST, result);
    }

    @Override
    public Message joinChannel(RemoteUser user, String channelName) throws RemoteException {
        Channel channel = channels.get(channelName);
        if(channel != null){
            channel.addUser(user);
            return new Message(0,channel);
        }else{
            channel = new ChannelImpl();
            channel.addUser(user);
            channels.put(channelName, channel);
            return new Message(Constant.MESSAGE_TYPE_CHANNEL,channel);
        }

    }

    public static void main(String[] args) {

        //System.setProperty("java.security.policy", "server.policy");
        //System.setProperty("java.rmi.server.codebase", "file:/");
        /*
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
*/
        try {
            String name = "Server";
            Server server = new GameServer();
            Server stub = (Server) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(1100);
            registry.rebind(name, stub);
            System.out.println("Server bound");
        } catch (Exception e) {
            System.err.println("Server exception:");
            e.printStackTrace();
        }
    }

    @Override
    public void removePlayer(RemoteUser user, Channel channel) throws RemoteException {
        Channel localChannel = null;

        for(Map.Entry<String, Channel> entry : channels.entrySet()){
            if(entry.getValue().getId() == channel.getId()){
                entry.getValue().getUsers().remove(user);
            }
        }
    }

    @Override
    public String toString() {
        return "GameServer{" +
                "channels=" + channels +
                '}';
    }
}
