package it.catchword.network.entity;

/**
 * Created by Stefano on 28/03/2016.
 */
public interface RemoteUser{

    int getId();

    void setId(int id);

    String getUsername();

    void setUsername(String username);

    String getIp();

    void setIp(String ip);

    int getScore();

    void setScore(int score);
}
