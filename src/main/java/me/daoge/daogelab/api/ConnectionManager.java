package me.daoge.daogelab.api;

import io.netty.channel.Channel;
import org.allaymc.api.entity.interfaces.EntityPlayer;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class ConnectionManager {
    public static final Set<Connection> CONNECTIONS = new CopyOnWriteArraySet<>();

    public static Connection getByPlayer(EntityPlayer player) {
        return getByUUID(player.getLoginData().getUuid());
    }

    public static Connection getByUUID(UUID uuid) {
        Optional<Connection> connection = CONNECTIONS.stream()
                .filter(c -> c.getClientId().equals(uuid.toString()))
                .findFirst();
        return connection.orElse(null);
    }

    public static Connection getByChannel(Channel channel) {
        Optional<Connection> connection = ConnectionManager.CONNECTIONS.stream()
                .filter(c -> c.getChannel().equals(channel))
                .findFirst();
        return connection.orElse(null);
    }

    public static void sendToAll(DgLabMessage message) {
        CONNECTIONS.forEach(connection -> connection.sendMessage(message));
    }
}
