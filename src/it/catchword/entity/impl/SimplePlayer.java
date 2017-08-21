package it.catchword.entity.impl;

import it.catchword.config.Constant;
import it.catchword.entity.Player;

/**
 * Created by Stefano on 26/02/2016.
 */
public class SimplePlayer implements Player{

    private int score;
    private int status;
    private int id;
    private boolean owner;


    private String username;

    public SimplePlayer(String username){
        this.score = 0;
        this.status = Constant.USER_STATUS_INIT;
        this.username=username;
        this.owner = false;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int i) {
        id = i;
    }

    @Override
    public boolean isOwner() {
        return owner;
    }

    @Override
    public void setIsOwner(boolean owner) {
        this.owner = owner;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SimplePlayer{" +
                "score=" + score +
                ", status=" + status +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
