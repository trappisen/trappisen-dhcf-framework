package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.framework.user.ServerParticipator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ToggleStaffChatCommand extends BaseCommand {
    private final BasePlugin plugin;

    public ToggleStaffChatCommand(BasePlugin plugin) {
        super("togglestaffchat", "Toggles staff chat visibility.");
        setAliases(new String[] { "tsc", "togglesc" });
        setUsage("/(command)");
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ServerParticipator participator = this.plugin.getUserManager().getParticipator(sender);
        if (participator == null) {
            sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");
            return true;
        }
        boolean newChatToggled = !participator.isStaffChatVisible();
        participator.setStaffChatVisible(newChatToggled);
        sender.sendMessage(BaseConstants.YELLOW + "You have toggled staff chat visibility " + (newChatToggled ? (ChatColor.GREEN + "on") : (ChatColor.RED + "off")) + BaseConstants.YELLOW + '.');
        return true;
    }
}

