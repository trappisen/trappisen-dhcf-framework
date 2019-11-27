package com.dHCF.framework.command;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;


public class SimpleCommandManager
        implements CommandManager
{
    public static final String PERMISSION_MESSAGE = ChatColor.RED + "Access was denied.";




    private final Map<String, BaseCommand> commandMap = new HashMap(); public SimpleCommandManager(final BasePlugin plugin) {
    final ConsoleCommandSender console = plugin.getServer().getConsoleSender();
    (new BukkitRunnable()
    {
        public void run()
        {
            Collection<BaseCommand> commands = SimpleCommandManager.this.commandMap.values();
            for (BaseCommand command : commands)
            {
                String commandName = command.getName();
                PluginCommand pluginCommand = plugin.getCommand(commandName);
                if (pluginCommand == null) {

                    Bukkit.broadcastMessage(commandName);
                    console.sendMessage(String.valueOf('[') + plugin.getName() + "] " + BaseConstants.YELLOW + "Failed to register command '" + commandName + "'.");
                    console.sendMessage(String.valueOf('[') + plugin.getName() + "] " + BaseConstants.YELLOW + "Reason: Undefined in plugin.yml.");

                    continue;
                }
                pluginCommand.setAliases(Arrays.asList(command.getAliases()));
                pluginCommand.setDescription(command.getDescription());
                pluginCommand.setExecutor((CommandExecutor) command);
                pluginCommand.setTabCompleter((TabCompleter) command);
                pluginCommand.setUsage(command.getUsage());
                pluginCommand.setPermission("dhcf.command." + command.getName());
                pluginCommand.setPermissionMessage(SimpleCommandManager.PERMISSION_MESSAGE);


            }

        }
    }).runTask(plugin);
}



    public boolean containsCommand(BaseCommand command) { return this.commandMap.containsValue(command); }



    public void registerAll(BaseCommandModule module) {
        if (module.isEnabled()) {

            Set<BaseCommand> commands = module.getCommands();
            for (BaseCommand command : commands) {
                this.commandMap.put(command.getName(), command);
            }
        }
    }



    public void registerCommand(BaseCommand command) { this.commandMap.put(command.getName(), command); }
    public void registerCommands(BaseCommand[] commands) {
        byte b;
        int i;
        int arrayOfBaseCommand;
        for (i = arrayOfBaseCommand = commands.length, b = 0; b < i; ) {
            BaseCommand command = arrayOfBaseCommand[b];
            this.commandMap.put(command.getName(), command);
            b++; }

    }


    public void unregisterCommand(BaseCommand command) { this.commandMap.values().remove(command); }




    public BaseCommand getCommand(String id) { return (BaseCommand)this.commandMap.get(id); }
}

