package com.dHCF.framework.command.module.chat.announcement.args;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.announcement.Announcement;
import com.dHCF.util.BukkitUtils;
import com.dHCF.util.command.CommandArgument;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementLinesArgument extends CommandArgument {
    public AnnouncementLinesArgument(BasePlugin plugin) {
        super("lines", "Displays lines from an announcement");
        this.plugin = plugin;
    }


    public String getUsage(String label) { return "/" + label + " " + getName() + " <announcement>"; }
    private final BasePlugin plugin;

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            String announcementName = args[1].toLowerCase();
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                Announcement announcement = this.plugin.getAnnouncementManager().getAnnouncement(paramString);
                if (announcement == null) {
                    paramCommandSender.sendMessage(ChatColor.RED + "Announcement not found");
                } else {

                    paramCommandSender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 45));
                    paramCommandSender.sendMessage(BaseConstants.GOLD + ChatColor.BOLD.toString() + "Announcement - " + WordUtils.capitalize(paramString));
                    paramCommandSender.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 45));
                    paramCommandSender.sendMessage(BaseConstants.YELLOW + ChatColor.BOLD.toString() + "Lines: ");
                    List<BaseComponent[]> messages = new ArrayList<BaseComponent[]>();
                    messages.add((new ComponentBuilder("[Add]"))
                            .color(ChatColor.GREEN)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    TextComponent.fromLegacyText(ChatColor.GREEN + "Click to add at line " + ChatColor.GRAY + "[" + Character.MIN_VALUE + "]")))

                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/a e " + paramString + " a- "))


                            .create());
                    int i = 0;
                    for (String line : announcement.getLines()) {
                        messages.add((new ComponentBuilder("[" + i + "]"))
                                .color(ChatColor.GRAY)
                                .append(" ")
                                .append(line)
                                .append(" ")
                                .append("[Delete] ")
                                .color(ChatColor.RED)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        TextComponent.fromLegacyText(ChatColor.RED + "Click to delete line " + ChatColor.GRAY + i)))

                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/a e " + paramString + " d" + i))


                                .append("[Edit] ")
                                .color(ChatColor.AQUA)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        TextComponent.fromLegacyText(ChatColor.AQUA + "Click to edit line " + ChatColor.GRAY + i)))

                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/a e " + paramString + " l" + i + " " + announcement
                                        .getLines()[i].replace(String.valueOf('�'), "&")))

                                .append("[Add] ")
                                .color(ChatColor.GREEN)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        TextComponent.fromLegacyText(ChatColor.GREEN + "Click to add a line after " + ChatColor.GRAY + "[" + i + "]")))

                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/a e " + paramString + " a" + i + " "))


                                .create());
                        i++;
                    }
                    for (BaseComponent[] baseComponents : messages) {
                        if (paramCommandSender instanceof Player) {
                            ((Player)paramCommandSender).spigot().sendMessage(baseComponents);
                            continue;
                        }
                        paramCommandSender.sendMessage(TextComponent.toLegacyText(baseComponents));
                    }

                }
            });
        } else {

            commandSender.sendMessage(ChatColor.RED + "Usage: " + getUsage("a"));
        }

        return false;
    }
}

