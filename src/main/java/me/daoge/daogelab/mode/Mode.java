package me.daoge.daogelab.mode;

import org.allaymc.api.server.Server;

public abstract class Mode {
    public abstract String getModeName();

    public void onEnable() {
        Server.getInstance().getEventBus().registerListener(this);
    }

    public void onDisable() {
        Server.getInstance().getEventBus().unregisterListener(this);
    }
}
