package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.framework.user.BaseUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleChatCommand extends BaseCommand {
    private final BasePlugin plugin;

    public ToggleChatCommand(BasePlugin plugin) {
        super("togglechat", "Toggles global chat visibility.");
        setAliases(new String[] { "tgc", "toggleglobalchat" });
        setUsage("/(command)");
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable for players.");
            return true;
        }
        Player player = (Player)sender;
        BaseUser baseUser = this.plugin.getUserManager().getUser(player.getUniqueId());
        boolean newChatToggled = !baseUser.isGlobalChatVisible();
        baseUser.setGlobalChatVisible(newChatToggled);
        sender.sendMessage(BaseConstants.YELLOW + "You have toggled global chat visibility " + (newChatToggled ? (ChatColor.GREEN + "on") : (ChatColor.RED + "off")) + BaseConstants.YELLOW + '.');
        return true;
    }
}

