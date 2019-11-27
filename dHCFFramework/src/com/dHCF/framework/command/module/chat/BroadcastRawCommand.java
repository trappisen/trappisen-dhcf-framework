package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.command.BaseCommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BroadcastRawCommand extends BaseCommand {
    public BroadcastRawCommand() {
        super("broadcastraw", "Broadcasts a raw message to the server.");
        setAliases(new String[] { "bcraw", "raw", "rawcast" });
        setUsage("/(command) [-p *perm*] <text..>");
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String requiredNode;
        byte position;
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
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
            return true;
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (requiredNode != null) {
            Bukkit.broadcast(message, requiredNode);
        } else {
            Bukkit.broadcastMessage(message);
        }
        return true;
    }
}

