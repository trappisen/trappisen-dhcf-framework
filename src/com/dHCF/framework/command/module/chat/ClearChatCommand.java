package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.command.BaseCommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ClearChatCommand
        extends BaseCommand
{
    private static final int CHAT_HEIGHT = 101;
    private static final String[] CLEAR_MESSAGE = new String[101];


    public ClearChatCommand() {
        super("clearchat", "Clears the server chat for players.");
        setAliases(new String[] { "cc" });
        setUsage("/(command) <reason>");
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + getUsage());
            return true;
        }
        String reason = StringUtils.join((Object[])args, ' ');
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(CLEAR_MESSAGE);
            if (player.hasPermission("dhcf.command.clearchat")) {
                player.sendMessage(ChatColor.DARK_AQUA + sender.getName() + BaseConstants.YELLOW + " has cleared chat for: " + reason);
            }
        }
        Bukkit.getConsoleSender().sendMessage(BaseConstants.YELLOW + sender.getName() + " cleared in-game chat.");
        return true;
    }
}
