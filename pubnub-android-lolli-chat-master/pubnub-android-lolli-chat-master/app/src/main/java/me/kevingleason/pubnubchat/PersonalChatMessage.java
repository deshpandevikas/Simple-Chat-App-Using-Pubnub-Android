package me.kevingleason.pubnubchat;

import java.io.Serializable;

/**
 * Created by Vikas Deshpande on 1/22/2017.
 */

public class PersonalChatMessage implements Serializable
{
    private String message, sender, reciever;
    private long timeStamp;

    public PersonalChatMessage(String message, String sender, String reciever, long timeStamp) {
        this.message = message;
        this.sender = sender;
        this.reciever = reciever;
        this.timeStamp = timeStamp;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
