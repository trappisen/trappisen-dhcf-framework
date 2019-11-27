package com.dHCF.framework.command.module.essential;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.command.BaseCommand;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CreateWorldCommand
        extends BaseCommand {
    HashMap flags;

    public CreateWorldCommand() {
        super("createworld", "Creates a world");
        this.flags = new HashMap();
        setUsage("/(command) [worldname]");
        setAliases(new String[] { "cw", "createw", "worldgen", "cworld" });
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (Bukkit.getWorld(args[false]) != null) {
                sender.sendMessage(ChatColor.RED + "That world already exists.");
                return true;
            }
            Bukkit.createWorld((new WorldCreator(args[0])).environment(World.Environment.NORMAL).type(WorldType.FLAT));
            sender.sendMessage(BaseConstants.GOLD + "The world with the name '" + ChatColor.WHITE + args[0] + BaseConstants.GOLD + "' is being created.");
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + getUsage());
            return false;
        }
        return true;
    }
}

