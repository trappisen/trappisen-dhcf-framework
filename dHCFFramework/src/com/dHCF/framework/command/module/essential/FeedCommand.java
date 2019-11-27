package com.dHCF.framework.command.module.essential;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.util.BukkitUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class FeedCommand extends BaseCommand {
    private static final int MAX_HUNGER = 20;

    public FeedCommand() {
        super("feed", "Feeds a player.");
        setUsage("/(command) <playerName>");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ImmutableSet<Player> targets;
        Player onlyTarget = null;

        if (args.length > 0 && sender.hasPermission(command.getPermission() + ".others")) {
            if (args[0].equalsIgnoreCase("all") && sender.hasPermission(command.getPermission() + ".all")) {
                targets = ImmutableSet.copyOf(Bukkit.getOnlinePlayers());
            } else {
                if ((onlyTarget = BukkitUtils.playerWithNameOrUUID(args[false])) == null || !BaseCommand.canSee(sender, onlyTarget)) {
                    sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] }));
                    return true;
                }
                targets = ImmutableSet.of(onlyTarget);
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }
            targets = ImmutableSet.of(onlyTarget = (Player)sender);
        }
        if (onlyTarget != null && onlyTarget.getFoodLevel() == 20) {
            sender.sendMessage(ChatColor.RED + onlyTarget.getName() + " already has full hunger.");
            return true;
        }
        for (UnmodifiableIterator unmodifiableIterator = targets.iterator(); unmodifiableIterator.hasNext(); ) { Player target = (Player)unmodifiableIterator.next();
            target.removePotionEffect(PotionEffectType.HUNGER);
            target.setFoodLevel(20); }

        sender.sendMessage(BaseConstants.YELLOW + "Fed " + ((onlyTarget == null) ? "all online players" : ("player " + onlyTarget.getName())) + '.');
        return true;
    }



    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return (args.length == 1) ? null : Collections.emptyList(); }
}

