package com.dHCF.framework.listener;

import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.server.dHCFServer;
import com.dHCF.framework.server.ServerSettings;
import com.dHCF.framework.user.BaseUser;
import com.dHCF.framework.user.event.UserLoadEvent;
import com.dHCF.util.messgener.ServerAssignedEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener
        implements Listener {
    private final BasePlugin plugin;

    public JoinListener(BasePlugin plugin) { this.plugin = plugin; }


    @EventHandler
    public void onUserLoad(UserLoadEvent event) {
        BaseUser baseUser = event.getBaseUser();
        Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        boolean newIP = baseUser.tryLoggingAddress(player.getAddress().getAddress().getHostAddress());
        if (player.hasPermission("dhcf.server.staffjoin") && (now - baseUser.getLastSeen() > 900000L || newIP)) {
            String prefix = ChatColor.translateAlternateColorCodes('&', BasePlugin.getChat().getPlayerPrefix(player));











            BaseComponent[] message = (new ComponentBuilder(player.getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(prefix + player.getName() + "\n" + ChatColor.GRAY + "Server: " + ChatColor.WHITE + this.plugin.getGlobalMessager().getId() + "\n" + ChatColor.GRAY + "Opped: " + ChatColor.WHITE + player.isOp() + "\n" + ChatColor.GRAY + "New IP: " + ChatColor.WHITE + newIP + "\n" + ChatColor.GRAY + "Rank: " + ChatColor.WHITE + BasePlugin.getPermission().getPrimaryGroup(player)))).color(newIP ? ChatColor.RED : ChatColor.GREEN).bold(true).append(" has logged in ").bold(false).color(ChatColor.GRAY).append(newIP ? "on a new IP" : "on an existing IP").create();
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.hasPermission("dhcf.server.staffjoin.notify")) {
                    other.sendMessage(message);
                }
            }
            this.plugin.getGlobalMessager().broadcastToOtherServers(player, message, "dhcf.server.staffjoin.notify");
        }
        baseUser.setOnline(true);
        baseUser.setLastSeen(System.currentTimeMillis());
        baseUser.setGroup(BasePlugin.getPermission().getPrimaryGroup(player));
        baseUser.setName(player.getName());
        baseUser.setLastServer(this.plugin.getGlobalMessager().getId());
        if (baseUser.getFirstJoined() == null) {
            baseUser.setFirstJoined(Long.valueOf(baseUser.getLastSeen()));
        }
        baseUser.updateVanishedState(player, baseUser.isVanished());
    }

    @EventHandler
    public void onServerAssigned(ServerAssignedEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BaseUser baseUser = this.plugin.getUserManager().getUser(player.getUniqueId());
            baseUser.setLastServer(event.getId());
        }
        if (!ServerSettings.HASNAME || !ServerSettings.NAME.equals(event.getId())) {
            ServerSettings.setName(event.getId());
            if (BasePlugin.isMongo()) {
                if (this.plugin.getdHCFServer() != null) this.plugin.getdHCFServer().close();
                this.plugin.setFaithfulServer(new dHCFServer(this.plugin));
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinLow(PlayerJoinEvent e) { this.plugin.getUserManager().joinTask(e.getPlayer()); }



    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event) { this.plugin.getUserManager().quitTask(event.getPlayer()); }
}

