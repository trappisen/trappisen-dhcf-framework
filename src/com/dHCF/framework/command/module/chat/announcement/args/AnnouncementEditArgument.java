package com.dHCF.framework.command.module.chat.announcement.args;

import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.announcement.Announcement;
import com.dHCF.util.command.CommandArgument;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class AnnouncementEditArgument
        extends CommandArgument
{
    private final BasePlugin plugin;

    public AnnouncementEditArgument(BasePlugin plugin) {
        super("e", "Edit an announcement line");
        this.plugin = plugin;
    }



    public String getUsage(String label) { return " (Utility Command) "; }


    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length >= 3) {
            String announcementName = args[1].toLowerCase();
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                Announcement announcement = this.plugin.getAnnouncementManager().getAnnouncement(paramString);
                if (paramArrayOfString[2].startsWith("l")) {
                    if (announcement == null) {
                        paramCommandSender.sendMessage(ChatColor.RED + "An announcement with that name does not exist");
                    } else {

                        Bukkit.dispatchCommand(paramCommandSender, "a lines " + paramString);
                        String message = (paramArrayOfString.length >= 4) ? ChatColor.translateAlternateColorCodes('&', paramArrayOfString[3]) : "";
                        for (int i = 4; i < paramArrayOfString.length; i++) {
                            message = message + " " + ChatColor.translateAlternateColorCodes('&', paramArrayOfString[i]);
                        }
                        int line = Ints.tryParse(paramArrayOfString[2].substring(1, 2)).intValue();
                        if (message.isEmpty()) {
                            List<String> lines = new ArrayList<String>(Arrays.asList(announcement.getLines()));
                            String previous = (String)lines.remove(line);
                            announcement.setLines((String[])lines.toArray(new String[lines.size()]));
                            paramCommandSender.sendMessage(ChatColor.RED + "Removed " + ChatColor.GRAY + "[" + line + "] " + ChatColor.RESET + previous);
                        } else {

                            paramCommandSender.sendMessage(ChatColor.YELLOW + "Set line " + ChatColor.GRAY + "[" + line + "] ");
                            paramCommandSender.sendMessage(ChatColor.RED + "- " + ChatColor.RESET + announcement.getLines()[line]);
                            announcement.getLines()[line] = message.isEmpty() ? null : message;
                            paramCommandSender.sendMessage(ChatColor.GREEN + "+ " + ChatColor.RESET + message);
                        }
                        this.plugin.getAnnouncementManager().saveAnnouncement(announcement);
                    }

                } else if (paramArrayOfString[2].startsWith("a")) {
                    if (announcement == null) {
                        paramCommandSender.sendMessage(ChatColor.RED + "An announcement with that name does not exist");
                    } else {

                        Bukkit.dispatchCommand(paramCommandSender, "a lines " + paramString);
                        String message = (paramArrayOfString.length >= 4) ? ChatColor.translateAlternateColorCodes('&', paramArrayOfString[3]) : "";
                        for (int i = 4; i < paramArrayOfString.length; i++) {
                            message = message + " " + ChatColor.translateAlternateColorCodes('&', paramArrayOfString[i]);
                        }
                        int line = paramArrayOfString[2].endsWith("-") ? -1 : Ints.tryParse(paramArrayOfString[2].substring(1, 2)).intValue();
                        List<String> lines = new ArrayList<String>(Arrays.asList(announcement.getLines()));
                        lines.add(line + 1, message);
                        announcement.setLines((String[])lines.toArray(new String[lines.size()]));
                        this.plugin.getAnnouncementManager().saveAnnouncement(announcement);
                    }

                } else if (paramArrayOfString[2].equals("c")) {
                    if (announcement == null) {
                        Bukkit.dispatchCommand(paramCommandSender, "a");
                        announcement = new Announcement(paramString, new String[0], -1);
                        this.plugin.getAnnouncementManager().saveAnnouncement(announcement);
                        paramCommandSender.sendMessage(ChatColor.YELLOW + "Created the announcement " + ChatColor.GOLD + paramString);
                    } else {

                        paramCommandSender.sendMessage(ChatColor.RED + "An announcement with that name already exists");
                    }

                } else if (paramArrayOfString[2].equals("r")) {
                    if (announcement != null) {
                        this.plugin.getAnnouncementManager().removeAnnouncement(announcement);
                        paramCommandSender.sendMessage(ChatColor.YELLOW + "Removed the announcement " + ChatColor.GOLD + paramString);
                    } else {

                        paramCommandSender.sendMessage(ChatColor.RED + "An announcement with that name does not exist");
                    }
                    Bukkit.dispatchCommand(paramCommandSender, "a");
                }
                else if (paramArrayOfString[2].startsWith("d")) {
                    if (announcement == null) {
                        paramCommandSender.sendMessage(ChatColor.RED + "An announcement with that name does not exist");
                    } else {
                        Bukkit.dispatchCommand(paramCommandSender, "a lines " + paramString);
                        int line = Ints.tryParse(paramArrayOfString[2].substring(1, 2)).intValue();
                        if (line < announcement.getLines().length) {
                            List<String> lines = new ArrayList<String>(Arrays.asList(announcement.getLines()));
                            String replace = (String)lines.remove(line);
                            paramCommandSender.sendMessage(ChatColor.YELLOW + "Removed line " + ChatColor.GRAY + "[" + line + "] ");
                            paramCommandSender.sendMessage(ChatColor.RED + "- " + ChatColor.RESET + replace);
                            announcement.setLines((String[])lines.toArray(new String[lines.size()]));
                            this.plugin.getAnnouncementManager().saveAnnouncement(announcement);
                        } else {

                            paramCommandSender.sendMessage(ChatColor.RED + "Line not found");
                        }

                    }
                } else if (paramArrayOfString[2].equals("b")) {
                    this.plugin.getAnnouncementManager().sendBroadcastMessage(announcement.getLines());
                }
            });
        } else {

            commandSender.sendMessage(ChatColor.RED + "Usage: " + getUsage("a"));
        }
        return false;
    }
}

