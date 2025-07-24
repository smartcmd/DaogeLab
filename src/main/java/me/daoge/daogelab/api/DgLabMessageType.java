package me.daoge.daogelab.api;

import com.google.gson.annotations.SerializedName;

/**
 * @author daoge_cmd
 */
public enum DgLabMessageType {
    @SerializedName("heartbeat")
    HEARTBEAT,
    @SerializedName("bind")
    BIND,
    @SerializedName("msg")
    MSG,
    @SerializedName("break")
    BREAK,
    @SerializedName("error")
    ERROR
}
