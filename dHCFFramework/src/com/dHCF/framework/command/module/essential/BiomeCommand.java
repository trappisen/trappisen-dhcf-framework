package com.dHCF.framework.command.module.essential;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.util.BukkitUtils;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BiomeCommand
        extends BaseCommand {
    public BiomeCommand() {
        super("biome", "Checks a players biome.");
        setUsage("/(command) [playerName]");
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
            Location location = target.getLocation();
            Biome biome = location.getWorld().getBiome(location.getBlockX(), location.getBlockZ());
            sender.sendMessage(BaseConstants.YELLOW + target.getName() + " is in the " + biome.name() + " biome.");
            return true;
        }
        sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
        return true;
    }



    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return (args.length == 1) ? null : Collections.emptyList(); }
}

