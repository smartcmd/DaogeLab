package me.daoge.daogelab;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.okaeri.configs.ConfigManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.daoge.daogelab.api.ConnectionManager;
import me.daoge.daogelab.network.WebSocketServer;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.player.PlayerQuitEvent;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.Utils;

/**
 * @author daoge_cmd
 */
@Slf4j
public class DaogeLab extends Plugin {

    public static DaogeLab INSTANCE;
    public static Gson GSON;

    @Getter
    protected DaogeLabConfig config;

    @Override
    public void onEnable() {
        DaogeLab.INSTANCE = this;
        DaogeLab.GSON = new GsonBuilder().create();
        this.config = ConfigManager.create(
                DaogeLabConfig.class,
                Utils.createConfigInitializer(this.getPluginContainer().dataFolder().resolve("config.yml"))
        );
        Registries.COMMANDS.register(new DaogeLabCommand());
        Server.getInstance().getEventBus().registerListener(this);
        WebSocketServer.run();
        log.info("DaogeLab plugin is enabled!");
    }

    @Override
    public void onDisable() {
        WebSocketServer.stop();
        log.info("DaogeLab plugin is disabled!");
    }

    @EventHandler
    protected void onPlayerQuit(PlayerQuitEvent event) {
        var connection = ConnectionManager.getByPlayer(event.getPlayer());
        if (connection != null) {
            connection.disconnect();
        }
    }
}
