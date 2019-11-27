package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

public class BroadcastCommand
        extends BaseCommand {
    private final BasePlugin plugin;

    public BroadcastCommand(BasePlugin plugin) {
        super("broadcast", "Broadcasts a message to the server.");
        setAliases(new String[] { "bc" });
        setUsage("/(command) [-p *perm*] <text..>");
        this.plugin = plugin;
    }


    public boolean onCommand(final CommandSender sender, Command command, final String label, final String[] args) {
        (new BukkitRunnable() { public void run() { String requiredNode;
            byte position;
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Usage: " + BroadcastCommand.this.getUsage(label));

                return;
            }

            String arg;
            if (args.length > 2 && (arg = args[0]).startsWith("-p")) {
                position = 1;
                requiredNode = arg.substring(2, arg.length());
            } else {
                position = 0;
                requiredNode = null;
            }
            String message = StringUtils.join((Object[])args, ' ', position, args.length);
            if (message.length() < 3) {
                sender.sendMessage(ChatColor.RED + "Broadcasts must be at least 3 characters.");
                return;
            }
            message = ChatColor.translateAlternateColorCodes('&', String.format(Locale.ENGLISH, BroadcastCommand.this.plugin.getServerHandler().getBroadcastFormat(), new Object[] { message }));
            if (requiredNode != null) {
                Bukkit.broadcast(message, requiredNode);
            } else {
                Bukkit.broadcastMessage(message);
            }  }
        }
        ).runTaskAsynchronously(this.plugin);
        return true;
    }
}

