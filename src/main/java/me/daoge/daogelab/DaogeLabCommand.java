package me.daoge.daogelab;

import me.daoge.daogelab.api.ConnectionManager;
import me.daoge.daogelab.utils.QRCodeUtils;
import org.allaymc.api.command.Command;
import org.allaymc.api.command.SenderType;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.permission.PermissionGroups;
import org.allaymc.api.utils.TextFormat;

public class DaogeLabCommand extends Command {

    public DaogeLabCommand() {
        super("dglab", "DgLab main command");
        this.permissions.forEach(PermissionGroups.MEMBER::addPermission);
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                .key("connect")
                .exec((context, player) -> {
                    var qrText = "https://www.dungeon-lab.com/app-download.php#DGLAB-SOCKET#%s://%s:%d/%s"
                            .formatted(
                                    DaogeLab.INSTANCE.getConfig().useHttps() ? "wss" : "ws",
                                    DaogeLab.INSTANCE.getConfig().address(),
                                    DaogeLab.INSTANCE.getConfig().publicPort(),
                                    player.getLoginData().getUuid().toString());
                    if (QRCodeUtils.showQRCode(player, qrText)) {
                        player.sendTranslatable(TextFormat.YELLOW + "%daogelab:scan_qr_code");
                        return context.success();
                    }

                    return context.fail();
                }, SenderType.PLAYER)
                .root()
                .key("disconnect")
                .exec((context, player) -> {
                    QRCodeUtils.clearQRCode(player);
                    var connection = ConnectionManager.getByUUID(player.getLoginData().getUuid());
                    if (connection != null) {
                        connection.disconnect();
                        player.sendTranslatable(TextFormat.YELLOW + "%daogelab:disconnecting");
                        return context.success();
                    } else {
                        player.sendTranslatable(TextFormat.RED + "%daogelab:not_connected");
                        return context.fail();
                    }
                }, SenderType.PLAYER)
                .root()
                .key("switchmode")
                .str("modename")
                .exec(context -> {
                    String modeName = context.getResult(1);
                    if (!DaogeLab.INSTANCE.hasMode(modeName)) {
                        context.getSender().sendTranslatable(TextFormat.RED + "%daogelab:unknown_mode", modeName);
                        return context.fail();
                    }

                    DaogeLab.INSTANCE.switchMode(modeName);
                    context.getSender().sendTranslatable(TextFormat.GREEN + "%daogelab:mode_switched", modeName);
                    return context.success();
                });
    }
}
