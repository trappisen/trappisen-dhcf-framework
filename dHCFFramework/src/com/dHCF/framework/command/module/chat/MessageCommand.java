package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.framework.event.PlayerMessageEvent;
import com.dHCF.util.BukkitUtils;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MessageCommand
        extends BaseCommand
{
    private final BasePlugin plugin;

    public MessageCommand(BasePlugin plugin) {
        super("message", "Sends a message to a recipient(s).");
        this.plugin = plugin;
        setAliases(new String[] { "msg", "m", "whisper", "w", "tell" });
        setUsage("/(command) <playerName> [text...]");
    }


    public boolean onCommand(final CommandSender sender, Command command, final String label, final String[] args) {
        (new BukkitRunnable() {
            public void run() {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: " + MessageCommand.this.getUsage(label));
                    return;
                }
                Player player = (Player)sender;
                Player target = BukkitUtils.playerWithNameOrUUID(args[0]);
                if (target != null && BaseCommand.canSee(sender, target)) {
                    String message = StringUtils.join((Object[])args, ' ', 1, args.length);
                    Set recipients = Collections.singleton(target);
                    PlayerMessageEvent playerMessageEvent = new PlayerMessageEvent(player, recipients, message, false);
                    Bukkit.getPluginManager().callEvent(playerMessageEvent);
                    if (!playerMessageEvent.isCancelled()) {
                        playerMessageEvent.send();
                    }
                    return;
                }
                sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
            }
        }).runTaskAsynchronously(this.plugin);
        return true;
    }



    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return null; }
}

