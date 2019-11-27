package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.framework.user.BaseUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class ToggleSoundsCommand
        extends BaseCommand implements Listener {
    private final BasePlugin plugin;

    public ToggleSoundsCommand(BasePlugin plugin) {
        super("sounds", "Toggles messaging sounds.");
        setAliases(new String[] { "pmsounds", "togglepmsounds", "messagingsounds" });
        setUsage("/(command) [playerName]");
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player)sender;
        BaseUser baseUser = this.plugin.getUserManager().getUser(player.getUniqueId());
        boolean newMessagingSounds = (!baseUser.isMessagingSounds() || (args.length >= 2 && Boolean.parseBoolean(args[1])));
        baseUser.setMessagingSounds(newMessagingSounds);
        sender.sendMessage(BaseConstants.YELLOW + "Messaging sounds are now " + (newMessagingSounds ? (ChatColor.GREEN + "on") : (ChatColor.RED + "off")) + BaseConstants.YELLOW + '.');
        return true;
    }
}

