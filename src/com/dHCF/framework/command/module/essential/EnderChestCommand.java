package com.dHCF.framework.command.module.essential;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderChestCommand extends BaseCommand {
    public EnderChestCommand() {
        super("enderchest", "Open an enderchest.");
        setAliases(new String[] { "echest" });
        setUsage("/(command)");
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target != null && BaseCommand.canSee(sender, target)) {
            ((Player)sender).openInventory(target.getEnderChest());
            return true;
        }
        sender.sendMessage(BaseConstants.GOLD + "Player '" + ChatColor.WHITE + args[0] + BaseConstants.GOLD + "' not found.");
        return true;
    }
}

