package com.sda.dialadoc.Models;

/**
 * Created by Touseef Rao on 8/15/2018.
 */

public class Message {
    private String message,type;
    private Boolean seen;
    private long timestamp;
    private String from;
    private String url;

    public Message(){}


    public Message(String message, String type, Boolean seen, long timestamp, String from, String url) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.timestamp = timestamp;
        this.from = from;
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
