package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.framework.event.PlayerMessageEvent;
import com.dHCF.framework.user.BaseUser;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Bukkit.*;


public class ReplyCommand
        extends BaseCommand
{
    private static final long VANISH_REPLY_TIMEOUT = TimeUnit.SECONDS.toMillis(45L);

    private final BasePlugin plugin;


    public ReplyCommand(BasePlugin plugin) {
        super("reply", "Replies to the last conversing player.");
        setAliases(new String[] { "r", "respond" });
        setUsage("/(command) <message>");
        this.plugin = plugin;
    }


    public boolean onCommand(final CommandSender sender, Command command, final String label, final String[] args) {
        new BukkitRunnable() {
            public void run() {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
                    return;
                }
                Player player = (Player)sender;
                UUID uuid = player.getUniqueId();
                BaseUser baseUser = ReplyCommand.this.plugin.getUserManager().getUser(uuid);
                UUID lastReplied = baseUser.getLastRepliedTo();
                Player[][] target = (lastReplied == null) ? null : Bukkit.getPlayer(lastReplied);
                if (args.length < 1) {
                    sender.sendMessage(ChatColor.RED + "Usage: " + ReplyCommand.this.getUsage(label));
                    if (lastReplied != null && BaseCommand.canSee(sender, target)) {
                        sender.sendMessage(ChatColor.RED + "You are in a conversation with " + player.getName() + '.');
                    }
                    return;
                }
                long millis = System.currentTimeMillis();
                if (target == null || (!BaseCommand.canSee(sender, target) && millis - baseUser.getLastReceivedMessageMillis() > VANISH_REPLY_TIMEOUT)) {
                    sender.sendMessage(BaseConstants.GOLD + "There is no player to reply to.");
                    return;
                }
                String message = StringUtils.join((Object[])args, ' ', 0, args.length);
                new Player[1][0] = target; HashSet recipients = Sets.newHashSet((Object[])new Player[1]);
                PlayerMessageEvent playerMessageEvent = new PlayerMessageEvent(player, recipients, message, false);
                getPluginManager().callEvent(playerMessageEvent);
                if (!playerMessageEvent.isCancelled()) {
                    playerMessageEvent.send();
                }
            }
        }.runTaskAsynchronously(this.plugin);
        return true;
    }



    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return null; }
}

