package com.dHCF.framework.command;

import com.dHCF.framework.BasePlugin;
import com.dHCF.util.command.ArgumentExecutor;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public abstract class BaseCommand
        extends ArgumentExecutor
{
    public static boolean canSee(CommandSender sender, Player[][] target) {
        if (BasePlugin.PRACTICE) {
            return true;
        }
        return (target != null && (!(sender instanceof Player) || ((Player)sender).canSee(target)));
    }


    private static final Pattern USAGE_REPLACER_PATTERN = Pattern.compile("(command)", 16);

    private final String name;

    private final String description;
    private String[] aliases;
    private String[] flags;
    private String usage;

    public BaseCommand(String name, String description) {
        super(name);
        this.name = name;
        this.description = description;
    }


    public final String getPermission() { return "dhcf.command." + this.name; }



    public boolean isPlayerOnlyCommand() { return false; }



    public String getName() { return this.name; }



    public String getDescription() { return this.description; }



    public String[] getFlags() { return this.flags; }



    protected void setFlags(String[] flags) { this.flags = flags; }


    public String getUsage() {
        if (this.usage == null) {
            this.usage = "";
        }
        return USAGE_REPLACER_PATTERN.matcher(this.usage).replaceAll(this.name);
    }


    public void setUsage(String usage) { this.usage = usage; }



    public String getUsage(String label) { return ChatColor.RED + "Usage: " + USAGE_REPLACER_PATTERN.matcher(this.usage).replaceAll(label); }


    public String[] getAliases() {
        if (this.aliases == null) {
            this.aliases = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return (String[])Arrays.copyOf(this.aliases, this.aliases.length);
    }


    protected void setAliases(String[] aliases) { this.aliases = aliases; }
}
