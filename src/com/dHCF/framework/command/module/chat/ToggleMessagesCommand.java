package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.framework.user.BaseUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleMessagesCommand extends BaseCommand {
    private final BasePlugin plugin;

    public ToggleMessagesCommand(BasePlugin plugin) {
        super("togglemessages", "Toggles private messages.");
        setAliases(new String[] { "togglepm", "toggleprivatemessages" });
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
        boolean newToggled = !baseUser.isMessagesVisible();
        baseUser.setMessagesVisible(newToggled);
        sender.sendMessage(BaseConstants.YELLOW + "You have turned private messages " + (newToggled ? (ChatColor.GREEN + "on") : (ChatColor.RED + "off")) + BaseConstants.YELLOW + '.');
        return true;
    }
}

