package com.dHCF.framework;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;


public class ServerHandler
{
    private final List<String> announcements;
    private final List<String> serverRules;
    private final BasePlugin plugin;
    public boolean useProtocolLib;
    private int clearlagdelay;
    private int announcementDelay;
    private long chatSlowedMillis;
    private long chatDisabledMillis;
    private int chatSlowedDelay;
    private String broadcastFormat;
    private FileConfiguration config;
    private boolean decreasedLagMode;

    public ServerHandler(BasePlugin plugin) {
        this.announcements = new ArrayList();
        this.serverRules = new ArrayList();
        this.plugin = plugin;
        this.config = plugin.getConfig();
        reloadServerData();
    }



    public int getAnnouncementDelay() { return this.announcementDelay; }




    public void setAnnouncementDelay(int delay) { this.announcementDelay = delay; }




    public void setClearlagdelay(Integer integer) { this.clearlagdelay = integer.intValue(); }




    public int getClaggDelay() { return this.clearlagdelay; }




    public List<String> getAnnouncements() { return this.announcements; }




    public boolean isChatSlowed() { return (getRemainingChatSlowedMillis() > 0L); }




    public long getChatSlowedMillis() { return this.chatSlowedMillis; }




    public void setChatSlowedMillis(long ticks) { this.chatSlowedMillis = System.currentTimeMillis() + ticks; }




    public long getRemainingChatSlowedMillis() { return this.chatSlowedMillis - System.currentTimeMillis(); }




    public boolean isChatDisabled() { return (getRemainingChatDisabledMillis() > 0L); }




    public long getChatDisabledMillis() { return this.chatDisabledMillis; }



    public void setChatDisabledMillis(long ticks) {
        long millis = System.currentTimeMillis();
        this.chatDisabledMillis = millis + ticks;
    }



    public long getRemainingChatDisabledMillis() { return this.chatDisabledMillis - System.currentTimeMillis(); }




    public int getChatSlowedDelay() { return this.chatSlowedDelay; }




    public void setChatSlowedDelay(int delay) { this.chatSlowedDelay = delay; }




    public String getBroadcastFormat() { return this.broadcastFormat; }




    public void setBroadcastFormat(String broadcastFormat) { this.broadcastFormat = broadcastFormat; }




    public boolean isDecreasedLagMode() { return this.decreasedLagMode; }




    public void setDecreasedLagMode(boolean decreasedLagMode) { this.decreasedLagMode = decreasedLagMode; }



    public void reloadServerData() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        this.serverRules.clear();
        this.clearlagdelay = this.config.getInt("clearlag.delay", 200);
        this.announcementDelay = this.config.getInt("announcements.delay", 15);
        this.announcements.clear();
        for (String each : this.config.getStringList("announcements.list")) {
            this.announcements.add(ChatColor.translateAlternateColorCodes('&', each));
        }
        this.chatDisabledMillis = this.config.getLong("chat.disabled.millis", 0L);
        this.chatSlowedMillis = this.config.getLong("chat.slowed.millis", 0L);
        this.chatSlowedDelay = this.config.getInt("chat.slowed.delay", 15);
        this.useProtocolLib = this.config.getBoolean("use-protocol-lib", true);
        this.decreasedLagMode = this.config.getBoolean("decreased-lag-mode");
        this.broadcastFormat = ChatColor.translateAlternateColorCodes('&', this.config.getString("broadcast.format", ChatColor.AQUA + "[" + ChatColor.GRAY + "Velt" + ChatColor.AQUA + "]" + ChatColor.GRAY + " &7%1$s"));
    }


    public void saveServerData() {
        this.config.set("clearlag.delay", Integer.valueOf(this.clearlagdelay));
        this.config.set("server-rules", this.serverRules);
        this.config.set("use-protocol-lib", Boolean.valueOf(this.useProtocolLib));
        this.config.set("chat.disabled.millis", Long.valueOf(this.chatDisabledMillis));
        this.config.set("chat.slowed.millis", Long.valueOf(this.chatSlowedMillis));
        this.config.set("chat.slowed-delay", Integer.valueOf(this.chatSlowedDelay));
        this.config.set("announcements.delay", Integer.valueOf(this.announcementDelay));
        this.config.set("announcements.list", this.announcements);
        this.config.set("decreased-lag-mode", Boolean.valueOf(this.decreasedLagMode));
        this.plugin.saveConfig();
    }
}

