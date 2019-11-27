package com.dHCF.util.command;

import com.dHCF.framework.BaseConstants;
import com.dHCF.util.BukkitUtils;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.*;

public class CommandWrapper implements CommandExecutor, TabCompleter {
    public static void printUsage(CommandSender sender, String label, Collection<CommandArgument> arguments) {
        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 45));
        sender.sendMessage(BaseConstants.GOLD + ChatColor.BOLD.toString() + WordUtils.capitalizeFully(label) + " Help");
        for (CommandArgument argument : arguments) {
            String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, argument.getUsage(label));
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(BaseConstants.YELLOW + "Click to run " + BaseConstants.GRAY + argument.getUsage(label)));
                BaseComponent[] components = (new ComponentBuilder(argument.getUsage(label))).color(BaseConstants.fromBukkit(BaseConstants.YELLOW)).event(clickEvent).event(hoverEvent).append(" - " + argument.getDescription()).event(clickEvent).event(hoverEvent).create();
                if (sender instanceof Player) {
                    ((Player)sender).spigot().sendMessage(components);
                    continue;
                }
                sender.sendMessage(BaseComponent.toLegacyText(components));
            }
        }

        sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 45));
    }
    private final Collection<CommandArgument> arguments;
    public static CommandArgument matchArgument(String id, CommandSender sender, Collection<CommandArgument> arguments) {
        for (CommandArgument argument : arguments) {
            String permission = argument.getPermission();
            if ((permission == null || sender.hasPermission(permission)) && (argument.getName().equalsIgnoreCase(id) || Arrays.asList(argument.getAliases()).contains(id))) {
                return argument;
            }
        }
        return null;
    }

    public static List<String> getAccessibleArgumentNames(CommandSender sender, Collection<CommandArgument> arguments) {
        List<String> results = new ArrayList<String>();
        for (CommandArgument argument : arguments) {
            String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                results.add(argument.getName());
            }
        }
        return results;
    }



    public CommandWrapper(Collection<CommandArgument> arguments) { this.arguments = arguments; }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 45));
            sender.sendMessage(BaseConstants.GOLD + ChatColor.BOLD.toString() + WordUtils.capitalizeFully(command.getName()) + " Help");
            for (CommandArgument argument : this.arguments) {
                String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, argument.getUsage(label));
                    HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(BaseConstants.YELLOW + "Click to run " + BaseConstants.GRAY + argument.getUsage(label)));
                    BaseComponent[] components = (new ComponentBuilder(argument.getUsage(command.getName()))).color(BaseConstants.fromBukkit(BaseConstants.YELLOW)).event(clickEvent).event(hoverEvent).append(" - " + argument.getDescription()).event(clickEvent).event(hoverEvent).create();
                    if (sender instanceof Player) {
                        ((Player)sender).spigot().sendMessage(components);
                        continue;
                    }
                    sender.sendMessage(BaseComponent.toLegacyText(components));
                }
            }

            sender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 45));
            return true;
        }
        CommandArgument argument2 = matchArgument(args[0], sender, this.arguments);
        String permission2 = (argument2 == null) ? null : argument2.getPermission();
        if (argument2 == null || (permission2 != null && !sender.hasPermission(permission2))) {
            sender.sendMessage(ChatColor.RED + WordUtils.capitalizeFully(command.getName()) + " sub-command " + args[0] + " not found.");
            return true;
        }
        argument2.onCommand(sender, command, label, args);
        return true;
    }
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results;
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            results = getAccessibleArgumentNames(sender, this.arguments);
        } else {
            CommandArgument argument = matchArgument(args[0], sender, this.arguments);
            if (argument == null) {
                return Collections.emptyList();
            }
            results = argument.onTabComplete(sender, command, label, args);
            if (results == null) {
                return null;
            }
        }
        return BukkitUtils.getCompletions(args, results);
    }

    public static class ArgumentComparator
            extends Object
            implements Comparator<CommandArgument>, Serializable {
        public int compare(CommandArgument primaryArgument, CommandArgument secondaryArgument) { return secondaryArgument.getName().compareTo(primaryArgument.getName()); }
    }
}

