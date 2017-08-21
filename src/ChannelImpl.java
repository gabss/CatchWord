import it.catchword.network.entity.Channel;
import it.catchword.network.entity.RemoteUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Stefano on 12/03/2016.
 */
public class ChannelImpl implements Serializable, Channel {
    private long id;
    private List<RemoteUser> users;

    public ChannelImpl(){
        this.id = (long) (new Random().nextFloat() * 1000000000);
        users = new ArrayList<>();
    }

    @Override
    public List<RemoteUser> getUsers() {
        return users;
    }

    @Override
    public void addUser(RemoteUser user) {
        this.users.add(user);
    }

    @Override
    public void removeUser(RemoteUser user){
        this.users.remove(user);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ChannelImpl{" +
                "id=" + id +
                ", users=" + users +
                '}';
    }
}
