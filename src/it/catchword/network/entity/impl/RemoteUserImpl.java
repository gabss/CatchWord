package it.catchword.network.entity.impl;


import it.catchword.network.entity.RemoteUser;

import java.io.Serializable;

/**
 * Created by Stefano on 12/03/2016.
 */
public class RemoteUserImpl implements Serializable, RemoteUser {
    private int id;
    private String username = "";
    private String ip;
    private int score;

    public RemoteUserImpl(){}

    public RemoteUserImpl(String username, String ip){
        this.username = username;
        this.ip = ip;
        score = 0;
    }

    @Override
    public String toString() {
        return "RemoteUserImpl{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RemoteUserImpl that = (RemoteUserImpl) o;

        return id == that.id || username.equals(that.getUsername());

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {

    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public void setIp(String ip) {

    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }
}
