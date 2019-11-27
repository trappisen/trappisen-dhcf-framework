package com.dHCF.framework.command.module.chat.announcement.args;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.announcement.Announcement;
import com.dHCF.util.BukkitUtils;
import com.dHCF.util.command.CommandArgument;
import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AnnouncementListArgument extends CommandArgument {
    public AnnouncementListArgument(BasePlugin plugin) {
        super("list", "List all announcements");
        this.plugin = plugin;
    }


    public String getUsage(String label) { return '/' + label + ' ' + getName(); }
    private final BasePlugin plugin;

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            paramCommandSender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 45));
            paramCommandSender.sendMessage(BaseConstants.GOLD + ChatColor.BOLD.toString() + "Announcements");
            paramCommandSender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 45));
            List<Announcement> announcements = this.plugin.getAnnouncementManager().getAnnouncements();
            if (announcements.isEmpty()) {
                paramCommandSender.sendMessage(ChatColor.RED + "There are currently no announcements");
            } else {

                for (Announcement announcement : announcements) {

                    HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + "Name: " + ChatColor.WHITE +
                            WordUtils.capitalize(announcement.getName()) + "\n\n" + ChatColor.GRAY + "Delay: " + ChatColor.WHITE + (
                            (announcement.getDelay() <= 0) ? (ChatColor.RED + "disabled") : DurationFormatUtils.formatDurationWords(TimeUnit.SECONDS.toMillis(announcement.getDelay()), true, true)) + "\n\n" + ChatColor.GRAY + "Announcement: " + ChatColor.WHITE + "\n" +

                            Joiner.on("\n").join(announcement.getLines())));





























                    BaseComponent[] baseComponents = (new ComponentBuilder(" * ")).color(ChatColor.DARK_GRAY).bold(true).event(hoverEvent).append(WordUtils.capitalize(announcement.getName())).color(ChatColor.YELLOW).event(hoverEvent).bold(false).append(" (Hover for information) ").color(ChatColor.GRAY).event(hoverEvent).append(" [Remove]").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.RED + "Click to remove this announcement"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a e " + announcement.getName() + " r ")).append(" [Lines]").color(ChatColor.GREEN).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Click to set the lines of this announcement"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a lines " + announcement.getName())).append(" [Delay]").color(ChatColor.AQUA).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.AQUA + "Click to set the delay of this announcement"))).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/a delay " + announcement.getName() + " ")).append(" [Broadcast]").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.YELLOW + "Click to broadcast this announcement"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a e " + announcement.getName() + " b")).create();
                    if (paramCommandSender instanceof Player) {
                        ((Player)paramCommandSender).spigot().sendMessage(baseComponents);
                        continue;
                    }
                    paramCommandSender.sendMessage(TextComponent.toLegacyText(baseComponents));
                }
            }

            paramCommandSender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 45));








            BaseComponent[] components = (new ComponentBuilder("[Create Announcement]")).color(ChatColor.GREEN).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Click to create announcement"))).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/a e <name> c")).create();
            if (paramCommandSender instanceof Player) {
                ((Player)paramCommandSender).spigot().sendMessage(components);
            } else {

                paramCommandSender.sendMessage(TextComponent.toLegacyText(components));
            }
            paramCommandSender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 45));
        });
        return false;
    }
}

