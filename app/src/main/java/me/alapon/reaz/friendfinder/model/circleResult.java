package me.alapon.reaz.friendfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by prince on 3/11/2017.
 */

public class circleResult {

    @SerializedName("circle_id")
    @Expose
    private String circleId;

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

}
