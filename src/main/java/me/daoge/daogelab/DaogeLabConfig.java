package me.daoge.daogelab;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author daoge_cmd
 */
@Getter
@Accessors(fluent = true)
public class DaogeLabConfig extends OkaeriConfig {
    @Comment("The address for the DgLab-App to connect. Default: 127.0.0.1")
    @Comment("In most cases, this is same with the address you use to connect to the MC server")
    private String address = "127.0.0.1";

    @Comment("The port for the DgLab ws server port number. Default: 8080")
    private int port = 8080;

    @CustomKey("use-https")
    @Comment("If true, connect to WebSocket server using HTTPS protocol. Default: false")
    @Comment("If you connect from the Internet, you may need to turn on this option")
    private boolean useHttps = false;
}
