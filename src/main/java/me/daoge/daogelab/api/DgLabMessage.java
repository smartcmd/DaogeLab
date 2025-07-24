package me.daoge.daogelab.api;

import me.daoge.daogelab.DaogeLab;

/**
 * @author daoge_cmd
 */
public record DgLabMessage(DgLabMessageType type, String clientId, String targetId, String message) {

    public String toJson() {
        return DaogeLab.GSON.toJson(this);
    }

    public static DgLabMessage message(String clientId, String targetId, String message) {
        return new DgLabMessage(DgLabMessageType.MSG, clientId, targetId, message);
    }

    public static DgLabMessage bind(String clientId, String targetId, String message) {
        return new DgLabMessage(DgLabMessageType.BIND, clientId, targetId, message);
    }
}
