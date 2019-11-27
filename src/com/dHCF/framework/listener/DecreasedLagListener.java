package com.dHCF.framework.listener;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DecreasedLagListener implements Listener {
    private static final String COMMAND = "stoplag";


    private final BasePlugin plugin;


    public DecreasedLagListener(BasePlugin plugin) { this.plugin = plugin; }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            Player player = event.getPlayer();
            BaseCommand baseCommand = this.plugin.getCommandManager().getCommand(COMMAND);
            if (player.hasPermission(baseCommand.getPermission())) {
                event.getPlayer().sendMessage(BaseConstants.YELLOW + "Intensive server activity is currently prevented. Use /" + baseCommand.getName() + " to toggle.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            event.getEntity().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            event.getEntity().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (this.plugin.getServerHandler().isDecreasedLagMode()) {
            switch (event.getSpawnReason()) {
                case SPAWNER:
                case SPAWNER_EGG:
                case BUILD_SNOWMAN:
                case BUILD_IRONGOLEM:
                case BUILD_WITHER:
                case DISPENSE_EGG:
                    return;
            }

            event.setCancelled(true);
        }
    }
}

