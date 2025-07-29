package me.daoge.daogelab;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.daoge.daogelab.mode.DefaultMode;

@Getter
@Accessors(fluent = true)
public class DaogeLabConfig extends OkaeriConfig {
    @Comment("The address for the DgLab app to connect In most cases, this")
    @Comment("is same with the address you use to connect to the MC server")
    private String address = "127.0.0.1";

    @Comment("The port for the DgLab app to connect. In most cases, this is same")
    @Comment("with the port that websocket server use, unless you are using frp,")
    @Comment("causing the app to connect to a different port than the one open to")
    @Comment("the websocket server")
    @CustomKey("public-port")
    private int publicPort = 8080;

    @Comment("The port that will open to the DgLab websocket server")
    private int port = 8080;

    @CustomKey("use-https")
    @Comment("If true, connect to WebSocket server using HTTPS protocol. If you connect")
    @Comment("from the Internet, you may need to turn on this option. Note that if you")
    @Comment("have this enabled, the address must be a domain name instead of an IP address")
    private boolean useHttps = false;

    @Comment("Mode determines how the current will change")
    private String mode = DefaultMode.NAME;
}
