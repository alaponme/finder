package me.alapon.reaz.friendfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by prince on 3/4/2017.
 */

public class CircleMembersResponseResult {


    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("lati")
    @Expose
    private String lati;
    @SerializedName("longi")
    @Expose
    private String longi;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}

