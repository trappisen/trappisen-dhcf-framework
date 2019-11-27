package com.dHCF.framework.command.module.essential;

import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.util.command.CommandArgument;
import com.dHCF.util.command.CommandWrapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public class AutoRestartCommand
        extends BaseCommand {
    private final CommandWrapper handler;

    public AutoRestartCommand(BasePlugin plugin) {
        super("autore", "Allows management of server restarts.");
        setAliases(new String[] { "autorestart" });
        setUsage("/(command) <cancel|time|schedule>");
        ArrayList<CommandArgument> arguments = new ArrayList<CommandArgument>(3);
        arguments.add(new AutoRestartCancelArgument(plugin));
        arguments.add(new AutoRestartScheduleArgument(plugin));
        arguments.add(new AutoRestartTimeArgument(plugin));
        Collections.sort(arguments, new CommandWrapper.ArgumentComparator());
        this.handler = new CommandWrapper(arguments);
    }



    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { return this.handler.onCommand(sender, command, label, args); }




    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) { return this.handler.onTabComplete(sender, command, label, args); }

    private static class AutoRestartTimeArgument
            extends CommandArgument {
        private final BasePlugin plugin;

        public AutoRestartTimeArgument(BasePlugin plugin) {
            super("time", "Gets the remaining time until next restart.");
            this.plugin = plugin;
            this.aliases = new String[] { "remaining", "time" };
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName(); }












        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { return true; }
    }

    private static class AutoRestartScheduleArgument
            extends CommandArgument {
        private final BasePlugin plugin;

        public AutoRestartScheduleArgument(BasePlugin plugin) {
            super("schedule", "Schedule an automatic restart.");
            this.plugin = plugin;
            this.aliases = new String[] { "reschedule" };
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName() + " <time> [reason]"; }


















        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { return true; }
    }

    private static class AutoRestartCancelArgument
            extends CommandArgument {
        private final BasePlugin plugin;

        public AutoRestartCancelArgument(BasePlugin plugin) {
            super("cancel", "Cancels the current automatic restart.");
            this.plugin = plugin;
        }



        public String getUsage(String label) { return '/' + label + ' ' + getName(); }












        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { return true; }
    }
}

