package com.dHCF.framework.command.module.essential;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.command.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;

public class EntitiesCommand
        extends BaseCommand {
    public EntitiesCommand() {
        super("entities", "Checks the entity count in environments.");
        setUsage("/(command) <playerName>");
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<World> worlds = Bukkit.getWorlds();
        for (World world : worlds) {
            sender.sendMessage(BaseConstants.GRAY + world.getEnvironment().name());

            EntityType[] var7 = EntityType.values(), values = var7;
            for (int var8 = values.length, var9 = 0; var9 < var8; var9++) {
                EntityType entityType = var7[var9];
                if (entityType != EntityType.UNKNOWN) {
                    Class entityClass = entityType.getEntityClass();
                    if (entityClass != null) {
                        int amount = world.getEntitiesByClass(entityClass).size();
                        if (amount >= 20) {
                            sender.sendMessage(BaseConstants.YELLOW + " " + entityType.getName() + " with " + amount);
                        }
                    }
                }
            }
        }
        return true;
    }



    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return Collections.emptyList(); }
}

