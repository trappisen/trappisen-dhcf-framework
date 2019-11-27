package com.dHCF.framework.command.module.essential;

import com.dHCF.framework.command.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CraftCommand
        extends BaseCommand {
    public CraftCommand() {
        super("craft", "Opens a workbench inventory.");
        setAliases(new String[] { "workbench", "wbench" });
        setUsage("/(command)");
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player)sender;
        player.openWorkbench(player.getLocation(), true);
        return true;
    }



    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return Collections.emptyList(); }
}

