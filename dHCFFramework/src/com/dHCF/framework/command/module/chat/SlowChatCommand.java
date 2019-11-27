package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.util.JavaUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;



public class SlowChatCommand
        extends BaseCommand
{
    private static final long DEFAULT_DELAY = TimeUnit.MINUTES.toMillis(5L);

    private final BasePlugin plugin;


    public SlowChatCommand(BasePlugin plugin) {
        super("slowchat", "Slows the chat down for non-staff.");
        setAliases(new String[] { "slow" });
        setUsage("/(command)");
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Long newTicks;
        long oldTicks = this.plugin.getServerHandler().getRemainingChatSlowedMillis();

        if (oldTicks > 0L) {
            newTicks = Long.valueOf(0L);
        } else if (args.length < 1) {
            newTicks = Long.valueOf(DEFAULT_DELAY);
        } else {
            newTicks = Long.valueOf(JavaUtils.parse(args[0]));
            if (newTicks.longValue() == -1L) {
                sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m1s");
                return true;
            }
        }
        this.plugin.getServerHandler().setChatSlowedMillis(newTicks.longValue());
        Bukkit.broadcastMessage(BaseConstants.YELLOW + "Global chat " + ((newTicks.longValue() > 0L) ? ("has now been slowed for " + ChatColor.GOLD + DurationFormatUtils.formatDurationWords(newTicks.longValue(), true, true)) : ("is no longer " + BaseConstants.GOLD + "slowed")));
        return true;
    }
}

