package it.catchword.network.entity;

import java.io.Serializable;

/**
 * Created by Stefano on 28/03/2016.
 */

/**
 * This class encapsulate information about a network message
 */
public class Message implements Serializable{
    private int type = -1;
    private Object data = null;

    public Message(int type, Object data){
        this.type = type;
        this.data = data;
    }

    /**
     * Create a new network message with the passed type
     * @param type The type of the message
     */
    public Message(int type){
        this.type = type;
    }

    /**
     *
     * @return The data contained in the message
     */
    public Object getData() {
        return data;
    }

    /**
     * Put an Object as data of the message
     * @param data To object to put in the message
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     *
     * @return The type of the message
     */
    public int getType() {
        return type;
    }

    /**
     * Set the type of the message
     * @param type The type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
