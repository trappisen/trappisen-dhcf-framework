package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.framework.user.BaseUser;
import com.dHCF.framework.user.ServerParticipator;
import com.dHCF.util.BukkitUtils;
import com.dHCF.util.command.CommandArgument;
import com.dHCF.util.command.CommandWrapper;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class IgnoreCommand extends BaseCommand {
    public IgnoreCommand(BasePlugin plugin) {
        super("ignore", "Ignores a player from messages.");
        setUsage("/(command) <list|add|del|clear> [playerName]");
        ArrayList arguments = new ArrayList(4);
        arguments.add(new IgnoreClearArgument(plugin));
        arguments.add(new IgnoreListArgument(plugin));
        arguments.add(new IgnoreAddArgument(plugin));
        arguments.add(new IgnoreDeleteArgument(plugin));
        Collections.sort(arguments, new CommandWrapper.ArgumentComparator());
        this.handler = new CommandWrapper(arguments);
    }

    private final CommandWrapper handler;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { return this.handler.onCommand(sender, command, label, args); }




    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return this.handler.onTabComplete(sender, command, label, args); }

    private static class IgnoreDeleteArgument
            extends CommandArgument {
        private final BasePlugin plugin;

        public IgnoreDeleteArgument(BasePlugin plugin) {
            super("delete", "Un-ignores a player.");
            this.plugin = plugin;
            this.aliases = new String[] { "del", "remove", "unset" };
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName() + " <playerName>"; }



        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }
            sender.sendMessage(BaseConstants.YELLOW + "You are " + (this.plugin.getUserManager().getUser(((Player)sender).getUniqueId()).getIgnoring().remove(args[1]) ? (ChatColor.RED + "not") : (ChatColor.GREEN + "no longer")) + BaseConstants.YELLOW + " ignoring " + args[1] + '.');
            return true;
        }



        public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return null; }
    }

    private static class IgnoreListArgument
            extends CommandArgument {
        private final BasePlugin plugin;

        public IgnoreListArgument(BasePlugin plugin) {
            super("list", "Lists all ignored players.");
            this.plugin = plugin;
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName(); }



        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                return true;
            }
            Set ignoring = this.plugin.getUserManager().getUser(((Player)sender).getUniqueId()).getIgnoring();
            if (ignoring.isEmpty()) {
                sender.sendMessage(BaseConstants.YELLOW + "You are not ignoring anyone.");
                return true;
            }
            sender.sendMessage(BaseConstants.YELLOW + "You are ignoring (" + ignoring.size() + ") members: " + '[' + ChatColor.WHITE + StringUtils.join(ignoring, ", ") + BaseConstants.YELLOW + ']');
            return true;
        }



        public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return Collections.emptyList(); }
    }

    private static class IgnoreClearArgument
            extends CommandArgument {
        private final BasePlugin plugin;

        public IgnoreClearArgument(BasePlugin plugin) {
            super("clear", "Clears all ignored players.");
            this.plugin = plugin;
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName(); }



        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                return true;
            }
            ServerParticipator serverParticipator = this.plugin.getUserManager().getParticipator(sender);
            Set ignoring = serverParticipator.getIgnoring();
            if (ignoring.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Your ignore list is already empty.");
                return true;
            }
            ignoring.clear();
            serverParticipator.update();
            sender.sendMessage(BaseConstants.YELLOW + "Your ignore list has been cleared.");
            return true;
        }



        public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return Collections.emptyList(); }
    }

    private static class IgnoreAddArgument
            extends CommandArgument {
        private final BasePlugin plugin;

        public IgnoreAddArgument(BasePlugin plugin) {
            super("add", "Starts ignoring a player.");
            this.plugin = plugin;
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName() + " <playerName>"; }



        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
                return true;
            }
            Player player = (Player)sender;
            UUID uuid = player.getUniqueId();
            BaseUser baseUser = this.plugin.getUserManager().getUser(uuid);
            Set ignoring = baseUser.getIgnoring();
            Player target = BukkitUtils.playerWithNameOrUUID(args[1]);
            if (target == null || !BaseCommand.canSee(sender, target)) {
                sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[1] }));
                return true;
            }
            if (sender.equals(target)) {
                sender.sendMessage(ChatColor.RED + "You may not ignore yourself.");
                return true;
            }
            if (target.hasPermission("base.command.ignore.exempt")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to ignore this player.");
                return true;
            }
            String targetName = target.getName();
            if (ignoring.add(target.getName())) {
                sender.sendMessage(BaseConstants.GOLD + "You are now ignoring " + targetName + '.');
                baseUser.update();
            } else {
                sender.sendMessage(ChatColor.RED + "You are already ignoring someone named " + targetName + '.');
            }
            return true;
        }



        public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return (args.length == 2) ? null : Collections.emptyList(); }
    }
}
