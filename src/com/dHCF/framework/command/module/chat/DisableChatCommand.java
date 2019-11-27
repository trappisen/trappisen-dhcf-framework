package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.util.JavaUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;



public class DisableChatCommand
        extends BaseCommand
{
    private static final long DEFAULT_DELAY = TimeUnit.MINUTES.toMillis(5L);

    private final BasePlugin plugin;


    public DisableChatCommand(BasePlugin plugin) {
        super("disablechat", "Disables the chat for non-staff.");
        setAliases(new String[] { "mutechat", "restrictchat", "mc", "rc" });
        setUsage("/(command)");
        this.plugin = plugin;
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long newTicks, oldTicks = this.plugin.getServerHandler().getRemainingChatDisabledMillis();

        if (oldTicks > 0L) {
            newTicks = 0L;
        } else if (args.length < 1) {
            newTicks = DEFAULT_DELAY;
        } else {
            newTicks = JavaUtils.parse(StringUtils.join((Object[])args, ' ', 0, args.length));
            if (newTicks == -1L) {
                sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m1s");
                return true;
            }
        }
        this.plugin.getServerHandler().setChatDisabledMillis(newTicks);
        Bukkit.broadcastMessage(BaseConstants.YELLOW + "Global chat is now " + ((newTicks > 0L) ? (ChatColor.RED + "disabled" + BaseConstants.YELLOW + " for " + ChatColor.GOLD + DurationFormatUtils.formatDurationWords(newTicks, true, true)) : (ChatColor.GREEN + "enabled")));
        return true;
    }
}
