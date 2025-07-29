package me.daoge.daogelab.mode;

import me.daoge.daogelab.api.ChannelType;
import me.daoge.daogelab.api.ConnectionManager;
import me.daoge.daogelab.utils.DgLabUtils;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.entity.interfaces.EntitySnowball;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.entity.EntityDamageEvent;
import org.allaymc.api.eventbus.event.entity.EntityDieEvent;
import org.allaymc.api.eventbus.event.entity.ProjectileHitEvent;
import org.allaymc.api.world.Sound;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.packet.PlaySoundPacket;

import java.util.concurrent.ThreadLocalRandom;

public class DefaultMode extends Mode {

    public static final String NAME = "default_mode";

    @Override
    public String getModeName() {
        return NAME;
    }

    @EventHandler
    protected void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer player)) {
            return;
        }

        var connection = ConnectionManager.getByPlayer(player);
        if (connection == null) {
            return;
        }

        var damage = event.getDamageContainer().getFinalDamage();
        if (player.getHealth() - damage < 1) {
            return;
        }

        var maxHealth = player.getMaxHealth();
        var percentage = damage / maxHealth;
        var aStrength = (int) (percentage * connection.getStrength().getMaxStrength(ChannelType.A));
        var bStrength = (int) (percentage * connection.getStrength().getMaxStrength(ChannelType.B));

        connection.setStrength(ChannelType.A, aStrength);
        connection.setStrength(ChannelType.B, bStrength);

        var freq = ThreadLocalRandom.current().nextInt(0, 30);
        var pulse = DgLabUtils.sinPulse(freq, 0, 100, 40);
        connection.clearPulse(ChannelType.A);
        connection.clearPulse(ChannelType.B);
        connection.addPulse(ChannelType.A, pulse);
        connection.addPulse(ChannelType.B, pulse);
    }

    @EventHandler
    protected void onPlayerDeath(EntityDieEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer player)) {
            return;
        }

        var connection = ConnectionManager.getByPlayer(player);
        if (connection == null) {
            return;
        }

        var strength = connection.getStrength();
        connection.addStrength(ChannelType.A, strength.getMaxStrength(ChannelType.A));
        connection.addStrength(ChannelType.B, strength.getMaxStrength(ChannelType.B));

        var freq = ThreadLocalRandom.current().nextInt(0, 30);
        var pulse = DgLabUtils.smoothPulse(freq, 100, 120);
        connection.clearPulse(ChannelType.A);
        connection.clearPulse(ChannelType.B);
        connection.addPulse(ChannelType.A, pulse);
        connection.addPulse(ChannelType.B, pulse);
    }

    @EventHandler
    protected void onSnowballHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof EntitySnowball snowball &&
            event.getVictim() instanceof EntityPlayer zako) {
            var connection = ConnectionManager.getByPlayer(zako);
            if (connection == null) {
                return;
            }

            var strength = connection.getStrength();
            connection.addStrength(ChannelType.A, strength.getMaxStrength(ChannelType.A));
            connection.addStrength(ChannelType.B, strength.getMaxStrength(ChannelType.B));

            var freq = ThreadLocalRandom.current().nextInt(0, 30);
            var stre = ThreadLocalRandom.current().nextInt(50, 100);
            var pulse = DgLabUtils.smoothPulse(freq, stre, 20);
            connection.clearPulse(ChannelType.A);
            connection.clearPulse(ChannelType.B);
            connection.addPulse(ChannelType.A, pulse);
            connection.addPulse(ChannelType.B, pulse);

            if (snowball.getShootingEntity() instanceof EntityPlayer player) {
                var packet = new PlaySoundPacket();
                packet.setSound(Sound.RANDOM_ORB);
                var pos = player.getLocation();
                packet.setPosition(Vector3f.from(pos.x(), pos.y() + player.getEyeHeight(), pos.z()));
                packet.setVolume(0.4f);
                packet.setPitch(0.5f);
                player.sendPacket(packet);
            }
        }
    }
}
