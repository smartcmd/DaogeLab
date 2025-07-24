package me.daoge.daogelab.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import me.daoge.daogelab.api.Connection;
import me.daoge.daogelab.api.ConnectionManager;
import me.daoge.daogelab.utils.QRCodeUtils;
import org.allaymc.api.utils.TextFormat;

/**
 * @author daoge_cmd
 */
@Slf4j
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
        log.info("new DgLab connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Connection connection = ConnectionManager.getByChannel(ctx.channel());
        if (connection != null) {
            ConnectionManager.CONNECTIONS.remove(connection);
            log.info("DgLab disconnected, clientId: {}", connection.getClientId());

            QRCodeUtils.clearQRCode(connection.getPlayer());
            connection.getStrength().clear();
            connection.updateScoreboard();
        }
    }
}
