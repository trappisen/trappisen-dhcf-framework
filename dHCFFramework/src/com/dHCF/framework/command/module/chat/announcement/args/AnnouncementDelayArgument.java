package com.dHCF.framework.command.module.chat.announcement.args;

import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.announcement.Announcement;
import com.dHCF.util.JavaUtils;
import com.dHCF.util.command.CommandArgument;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;







public class AnnouncementDelayArgument
        extends CommandArgument
{
    private final BasePlugin plugin;

    public AnnouncementDelayArgument(BasePlugin plugin) {
        super("delay", "Sets the delay of an announcements");
        this.plugin = plugin;
    }


    public String getUsage(String label) { return "/" + label + " " + getName() + " <announcement> <delay>"; }


    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length >= 3) {
            String announcementName = args[1].toLowerCase();
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                Announcement announcement = this.plugin.getAnnouncementManager().getAnnouncement(paramString);
                if (announcement == null) {
                    paramCommandSender.sendMessage(ChatColor.RED + "Announcement not found");
                } else {

                    int delay = -1;
                    try {
                        delay = (int)TimeUnit.MILLISECONDS.toSeconds(JavaUtils.parse(paramArrayOfString[2]));
                    }
                    catch (NumberFormatException exception) {
                        paramCommandSender.sendMessage(ChatColor.RED + "Invalid number, disabling announcement");
                    }
                    announcement.setDelay(delay);
                    this.plugin.getAnnouncementManager().saveAnnouncement(announcement);

                    Bukkit.dispatchCommand(paramCommandSender, "a");

                    paramCommandSender.sendMessage(ChatColor.YELLOW + "Set the delay of " + ChatColor.GOLD + WordUtils.capitalize(paramString) + ChatColor.YELLOW + " to " + ChatColor.GRAY + ((delay <= 0) ? (ChatColor.RED + "disabled") : DurationFormatUtils.formatDurationWords(TimeUnit.SECONDS.toMillis(delay), true, true)));
                }
            });
        } else {

            commandSender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
        }

        return false;
    }
}

