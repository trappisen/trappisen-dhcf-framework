package com.dHCF.framework.command.module.essential;


import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.util.BukkitUtils;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AmivisCommand
        extends BaseCommand {
    private final BasePlugin plugin;

    public AmivisCommand(BasePlugin plugin) {
        super("amivis", "Check if a player is visible.");
        setUsage("/(command) <playerName> [targetName]");
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length > 0) {
            target = BukkitUtils.playerWithNameOrUUID(args[0]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }
            target = (Player)sender;
        }
        if (target != null && BaseCommand.canSee(sender, target)) {
            boolean vanished = this.plugin.getUserManager().getUser(target.getUniqueId()).isVanished();
            sender.sendMessage(BaseConstants.YELLOW + target.getName() + " is " + (vanished ? "in vanish" : "not in vanish") + '.');
            return true;
        }
        sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
        return true;
    }



    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return (args.length == 1) ? null : Collections.emptyList(); }
}

