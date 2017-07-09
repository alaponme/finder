package me.alapon.reaz.friendfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by prince on 3/11/2017.
 */

public class getCircles {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("result")
    @Expose
    private List<circleResult> result = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<circleResult> getResult() {
        return result;
    }

    public void setResult(List<circleResult> result) {
        this.result = result;
    }

}