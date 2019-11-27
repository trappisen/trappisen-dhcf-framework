package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.framework.user.ServerParticipator;
import com.dHCF.util.BukkitUtils;
import com.dHCF.util.JavaUtils;
import com.dHCF.util.command.CommandArgument;
import com.dHCF.util.command.CommandWrapper;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public class MessageSpyCommand extends BaseCommand {
    public MessageSpyCommand(BasePlugin plugin) {
        super("messagespy", "Spies on the PM's of a player.");
        setAliases(new String[] { "ms", "msgspy", "pmspy", "whisperspy", "privatemessagespy", "tellspy" });
        setUsage("/(command) <list|add|del|clear> [playerName]");
        ArrayList arguments = new ArrayList(4);
        arguments.add(new MessageSpyListArgument(plugin));
        arguments.add(new IgnoreClearArgument(plugin));
        arguments.add(new MessageSpyAddArgument(plugin));
        arguments.add(new MessageSpyDeleteArgument(plugin));
        Collections.sort(arguments, new CommandWrapper.ArgumentComparator());
        this.handler = new CommandWrapper(arguments);
    }

    private final CommandWrapper handler;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { return this.handler.onCommand(sender, command, label, args); }




    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return this.handler.onTabComplete(sender, command, label, args); }

    private static class MessageSpyListArgument
            extends CommandArgument {
        private final BasePlugin plugin;

        public MessageSpyListArgument(BasePlugin plugin) {
            super("list", "Lists all players you're spying on.");
            this.plugin = plugin;
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName(); }



        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            ServerParticipator participator = this.plugin.getUserManager().getParticipator(sender);
            if (participator == null) {
                sender.sendMessage(ChatColor.RED + "Access was denied.");
                return true;
            }
            LinkedHashSet spyingNames = new LinkedHashSet();
            Set<String> messageSpying = participator.getMessageSpying();
            if (messageSpying.size() == 1 && Iterables.getOnlyElement(messageSpying).equals("all")) {
                sender.sendMessage(BaseConstants.GRAY + "You are currently spying on the messages of all players.");
                return true;
            }
            for (String spyingId : messageSpying) {
                String name = Bukkit.getOfflinePlayer(UUID.fromString(spyingId)).getName();
                if (name != null) {
                    spyingNames.add(name);
                }
            }
            if (spyingNames.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "You are not spying on the messages of any players.");
                return true;
            }
            sender.sendMessage(BaseConstants.GRAY + "You are currently spying on the messages of (" + spyingNames.size() + " players): " + ChatColor.RED + StringUtils.join(spyingNames, BaseConstants.GRAY.toString() + ", " + ChatColor.RED) + BaseConstants.GRAY + '.');
            return true;
        }
    }

    private static class IgnoreClearArgument extends CommandArgument {
        private final BasePlugin plugin;

        public IgnoreClearArgument(BasePlugin plugin) {
            super("clear", "Clears your current spy list.");
            this.plugin = plugin;
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName(); }



        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            ServerParticipator participator = this.plugin.getUserManager().getParticipator(sender);
            if (participator == null) {
                sender.sendMessage(ChatColor.RED + "You are not able to message spy.");
                return true;
            }
            participator.getMessageSpying().clear();
            participator.update();
            sender.sendMessage(BaseConstants.YELLOW + "You are no longer spying the messages of anyone.");
            return true;
        }
    }

    private static class MessageSpyAddArgument extends CommandArgument {
        private final BasePlugin plugin;

        public MessageSpyAddArgument(BasePlugin plugin) {
            super("add", "Adds a player to your message spy list.");
            this.plugin = plugin;
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName() + " <all|playerName>"; }



        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            ServerParticipator participator = this.plugin.getUserManager().getParticipator(sender);
            if (participator == null) {
                sender.sendMessage(ChatColor.RED + "You are not able to message spy.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }
            Set messageSpying = participator.getMessageSpying();
            boolean all;
            if ((all = messageSpying.contains("all")) || JavaUtils.containsIgnoreCase(messageSpying, args[1])) {
                sender.sendMessage(ChatColor.RED + "You are already spying on the messages of " + (all ? "all players" : args[1]) + '.');
                return true;
            }
            if (args[1].equalsIgnoreCase("all")) {
                messageSpying.clear();
                messageSpying.add("all");
                participator.update();
                sender.sendMessage(ChatColor.GREEN + "You are now spying on the messages of all players.");
                return true;
            }
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[1]);
            if (!offlineTarget.hasPlayedBefore() && offlineTarget.getPlayer() == null) {
                sender.sendMessage(BaseConstants.GOLD + "Player '" + ChatColor.WHITE + args[1] + BaseConstants.GOLD + "' not found.");
                return true;
            }
            if (offlineTarget.equals(sender)) {
                sender.sendMessage(ChatColor.RED + "You cannot spy on the messages of yourself.");
                return true;
            }
            sender.sendMessage(BaseConstants.YELLOW + "You are " + (messageSpying.add(offlineTarget.getUniqueId().toString()) ? (ChatColor.GREEN + "now") : (ChatColor.RED + "already")) + BaseConstants.YELLOW + " spying on the messages of " + offlineTarget.getName() + '.');
            participator.update();
            return true;
        }



        public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return (args.length == 2) ? null : Collections.emptyList(); }
    }

    private static class MessageSpyDeleteArgument
            extends CommandArgument {
        private final BasePlugin plugin;

        public MessageSpyDeleteArgument(BasePlugin plugin) {
            super("delete", "Deletes a player from your message spy list.");
            this.plugin = plugin;
            this.aliases = new String[] { "del", "remove" };
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName() + " <playerName>"; }



        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            ServerParticipator participator = this.plugin.getUserManager().getParticipator(sender);
            if (participator == null) {
                sender.sendMessage(ChatColor.RED + "You are not able to message spy.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }
            Set messageSpying = participator.getMessageSpying();
            if (args[1].equalsIgnoreCase("all")) {
                messageSpying.remove("all");
                sender.sendMessage(ChatColor.RED + "You are no longer spying on the messages of all players.");
                return true;
            }
            OfflinePlayer offlineTarget = BukkitUtils.offlinePlayerWithNameOrUUID(args[1]);
            if (!offlineTarget.hasPlayedBefore() && !offlineTarget.isOnline()) {
                sender.sendMessage(BaseConstants.GOLD + "Player named or with UUID '" + ChatColor.WHITE + args[1] + BaseConstants.GOLD + "' not found.");
                return true;
            }
            sender.sendMessage("You are " + (messageSpying.remove(offlineTarget.getUniqueId().toString()) ? (ChatColor.GREEN + "no longer") : (ChatColor.RED + "still not")) + BaseConstants.YELLOW + " spying on the messages of " + offlineTarget.getName() + '.');
            return true;
        }



        public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return (args.length == 2) ? null : Collections.emptyList(); }
    }
}

