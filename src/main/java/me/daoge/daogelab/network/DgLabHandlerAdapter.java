package me.daoge.daogelab.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import me.daoge.daogelab.DaogeLab;
import me.daoge.daogelab.api.Connection;
import me.daoge.daogelab.api.ConnectionManager;
import me.daoge.daogelab.utils.QRCodeUtils;
import org.allaymc.api.utils.TextFormat;

/**
 * @author daoge_cmd
 */
public class DgLabHandlerAdapter extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest request) {

            String clientId = request.uri();
            if (clientId.startsWith("/")) {
                clientId = clientId.substring(1);
            }
            ctx.channel().attr(AttributeKey.valueOf("clientId")).set(clientId);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Connection connection = new Connection(ctx.channel());
        ConnectionManager.CONNECTIONS.add(connection);
        DaogeLab.INSTANCE.getPluginLogger().info("DgLab client connected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Connection connection = ConnectionManager.getByChannel(ctx.channel());
        if (connection != null) {
            ConnectionManager.CONNECTIONS.remove(connection);
            DaogeLab.INSTANCE.getPluginLogger().info("DgLab client disconnected: {}", connection.getChannel().remoteAddress());

            var player = connection.getPlayer();
            if (player != null) {
                player.sendTr(TextFormat.YELLOW + "%daogelab:disconnected");
                QRCodeUtils.clearQRCode(player);
                connection.getStrength().clear();
                connection.updateScoreboard();
            }
        }
    }
}
