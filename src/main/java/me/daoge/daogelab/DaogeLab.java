package me.daoge.daogelab;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.Getter;
import me.daoge.daogelab.api.ConnectionManager;
import me.daoge.daogelab.mode.DefaultMode;
import me.daoge.daogelab.mode.Mode;
import me.daoge.daogelab.network.WebSocketServer;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.server.PlayerQuitEvent;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;

import java.util.HashMap;
import java.util.Map;

public class DaogeLab extends Plugin {

    public static DaogeLab INSTANCE;
    public static Gson GSON;

    @Getter
    protected DaogeLabConfig config;
    protected Map<String, Mode> modes;
    protected Mode currentMode;

    public void switchMode(String modeName) {
        var mode = modes.get(modeName);
        if (mode == null) {
            pluginLogger.error("Mode {} not found! Fallback to default mode", modeName);
            mode = modes.get(DefaultMode.NAME);
        }

        if (currentMode != null) {
            currentMode.onDisable();
        }
        currentMode = mode;
        currentMode.onEnable();
        pluginLogger.info("Mode switched to {}", modeName);
    }

    public boolean hasMode(String modeName) {
        return modes.containsKey(modeName);
    }

    @Override
    public void onEnable() {
        DaogeLab.INSTANCE = this;
        DaogeLab.GSON = new GsonBuilder().create();
        this.config = ConfigManager.create(
                DaogeLabConfig.class,
                it -> {
                    it.withConfigurer(new YamlSnakeYamlConfigurer());
                    it.withBindFile(this.getPluginContainer().dataFolder().resolve("config.yml"));
                    it.withRemoveOrphans(true);
                    it.saveDefaults();
                    it.load(true);
                }
        );
        Registries.COMMANDS.register(new DaogeLabCommand());
        Server.getInstance().getEventBus().registerListener(this);
        registerModes();
        switchMode(this.config.mode());
        WebSocketServer.run();
        pluginLogger.info("DaogeLab plugin is enabled!");
    }

    @Override
    public void onDisable() {
        WebSocketServer.stop();
        pluginLogger.info("DaogeLab plugin is disabled!");
    }

    @EventHandler
    protected void onPlayerQuit(PlayerQuitEvent event) {
        var connection = ConnectionManager.getByPlayer(event.getPlayer().getControlledEntity());
        if (connection != null) {
            connection.disconnect();
        }
    }

    protected void registerModes() {
        this.modes = new HashMap<>();
        registerMode(new DefaultMode());
    }

    protected void registerMode(Mode mode) {
        this.modes.put(mode.getModeName(), mode);
    }
}
