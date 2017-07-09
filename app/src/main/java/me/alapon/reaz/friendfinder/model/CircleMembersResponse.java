package me.alapon.reaz.friendfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by prince on 3/4/2017.
 */

public class CircleMembersResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("result")
    @Expose
    private List<CircleMembersResponseResult> result = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<CircleMembersResponseResult> getResult() {
        return result;
    }

    public void setResult(List<CircleMembersResponseResult> result) {
        this.result = result;
    }

}