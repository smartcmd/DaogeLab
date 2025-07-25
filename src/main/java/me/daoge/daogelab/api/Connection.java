package me.daoge.daogelab.api;

import com.google.gson.JsonSyntaxException;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.Setter;
import me.daoge.daogelab.DaogeLab;
import me.daoge.daogelab.utils.DgLabUtils;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.scoreboard.Scoreboard;
import org.allaymc.api.scoreboard.data.DisplaySlot;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.TextFormat;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Connection {

    public static final Pattern STRENGTH_PATTERN = Pattern.compile("strength-(\\d+)\\+(\\d+)\\+(\\d+)\\+(\\d+)", Pattern.MULTILINE);

    protected final Channel channel;
    protected final Strength strength;

    @Setter
    protected String clientId;
    @Setter
    protected String targetId;

    protected EntityPlayer player;
    protected Scoreboard scoreboard;

    public Connection(Channel channel) {
        this.channel = channel;
        this.strength = new Strength();
        this.clientId = "";
        this.targetId = "";
    }

    public void handle(String text) {
        DaogeLab.INSTANCE.getPluginLogger().debug("Received: {}", text);
        DgLabMessage message;
        try {
            message = DaogeLab.GSON.fromJson(text, DgLabMessage.class);
            Objects.requireNonNull(message.type());
            Objects.requireNonNull(message.clientId());
            Objects.requireNonNull(message.targetId());
            Objects.requireNonNull(message.message());
        } catch (JsonSyntaxException | NullPointerException exception) {
            sendMessage(DgLabMessage.message("", "", "403"));
            return;
        }

        switch (message.type()) {
            case BIND -> {
                sendMessage(DgLabMessage.bind(message.clientId(), message.targetId(), "200"));
                this.player = Server.getInstance().getPlayerService().getPlayers().get(UUID.fromString(clientId));
                this.scoreboard = new Scoreboard("DgLab Strength of " + player.getOriginName());
                this.scoreboard.addViewer(player, DisplaySlot.SIDEBAR);
            }
            case MSG -> {
                Matcher matcher = STRENGTH_PATTERN.matcher(message.message());
                if (matcher.find()) {
                    this.strength.setACurrentStrength(Integer.parseInt(matcher.group(1)));
                    this.strength.setBCurrentStrength(Integer.parseInt(matcher.group(2)));
                    this.strength.setAMaxStrength(Integer.parseInt(matcher.group(3)));
                    this.strength.setBMaxStrength(Integer.parseInt(matcher.group(4)));
                    this.updateScoreboard();
                }
            }
            // NOTICE: We handle disconnection in DgLabHandlerAdapter
        }
    }

    public void updateScoreboard() {
        this.scoreboard.setLines(Collections.singletonList(
                TextFormat.YELLOW + "A: " + TextFormat.RESET + strength.getACurrentStrength() + "/" + strength.getAMaxStrength() + "\n" +
                TextFormat.YELLOW + "B: " + TextFormat.RESET + strength.getBCurrentStrength() + "/" + strength.getBMaxStrength()
        ));
    }

    public void sendMessage(DgLabMessage message) {
        sendMessage(message.toJson());
    }

    public void sendMessage(String message) {
        DaogeLab.INSTANCE.getPluginLogger().debug("Sending: {}", message);
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    public void disconnect() {
        sendMessage(new DgLabMessage(DgLabMessageType.BREAK, clientId, targetId, "209"));
        channel.close();
    }

    public void reduceStrength(ChannelType type, int value) {
        sendMessage(DgLabMessage.message(clientId, targetId, "strength-%d+0+%d".formatted(type.getTypeNumber(), value)));
    }

    public void addStrength(ChannelType type, int value) {
        sendMessage(DgLabMessage.message(clientId, targetId, "strength-%d+1+%d".formatted(type.getTypeNumber(), value)));
    }

    public void setStrength(ChannelType type, int value) {
        sendMessage(DgLabMessage.message(clientId, targetId, "strength-%d+2+%d".formatted(type.getTypeNumber(), value)));
    }

    public void addPulse(ChannelType type, List<String> pulse) {
        sendMessage(DgLabMessage.message(clientId, targetId, "pulse-%s:%s".formatted(type.name(), DgLabUtils.toStringArray(pulse))));
    }

    public void clearPulse(ChannelType type) {
        sendMessage(DgLabMessage.message(clientId, targetId, "clear-%s".formatted(type.getTypeNumber())));
    }
}
