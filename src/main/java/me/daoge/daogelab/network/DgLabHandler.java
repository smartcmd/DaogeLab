package me.daoge.daogelab.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import me.daoge.daogelab.DaogeLab;
import me.daoge.daogelab.api.Connection;
import me.daoge.daogelab.api.ConnectionManager;
import me.daoge.daogelab.api.DgLabMessage;
import me.daoge.daogelab.utils.QRCodeUtils;
import org.allaymc.api.server.Server;

import java.util.UUID;

public class DgLabHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        Connection connection = ConnectionManager.getByChannel(ctx.channel());
        if (connection != null) {
            connection.handle(msg.text());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (!(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete)) {
            super.userEventTriggered(ctx, evt);
            return;
        }

        Connection connection = ConnectionManager.getByChannel(ctx.channel());
        UUID targetId = UUID.randomUUID();
        if (connection != null) {
            Attribute<String> attr = connection.getChannel().attr(AttributeKey.valueOf("clientId"));
            connection.setClientId(attr.get());
            connection.setTargetId(targetId.toString());
            connection.sendMessage(DgLabMessage.bind(targetId.toString(), "", "targetId"));

            var player = Server.getInstance().getPlayerManager().getPlayers().get(UUID.fromString(connection.getClientId()));
            if (player == null) {
                DaogeLab.INSTANCE.getPluginLogger().error("Player with uuid {} not found", connection.getClientId());
                return;
            }

            QRCodeUtils.clearQRCode(player);
        }
    }
}
