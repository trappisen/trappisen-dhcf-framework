package com.dHCF.framework.announcement;


import com.dHCF.framework.BasePlugin;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class AnnouncementManager
{
    public static final String NO_BROADCAST_META = "NO_BROADCAST";

    public AnnouncementManager(BasePlugin plugin) {
        this.announcementConcurrentMap = new ConcurrentHashMap();


        this.plugin = plugin;
        Map<String, Long> lastUpdate = new HashMap<String, Long>();
        long start = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            for (Announcement announcement : this.announcementConcurrentMap.values()) {
                if (announcement.getDelay() > 0) {
                    long last = ((Long)paramMap.getOrDefault(announcement.getName(), Long.valueOf(paramLong))).longValue();
                    long diff = now - last;
                    long time = TimeUnit.SECONDS.toMillis(announcement.getDelay());
                    if (diff > time) {
                        sendBroadcastMessage(announcement.getLines());
                        paramMap.put(announcement.getName(), Long.valueOf(now));
                    }
                }
            }
        }20L, 20L);
    }
    protected final BasePlugin plugin; protected ConcurrentMap<String, Announcement> announcementConcurrentMap;
    public void sendBroadcastMessage(String[] message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasMetadata("NO_BROADCAST")) {
                player.sendMessage(message);
            }
        }
    }




    public List<Announcement> getAnnouncements() { return ImmutableList.copyOf(this.announcementConcurrentMap.values()); }





    public void removeAnnouncement(Announcement announcement) { this.announcementConcurrentMap.remove(announcement.getName()); }



    public void saveAnnouncement(Announcement announcement) { this.announcementConcurrentMap.put(announcement.getName(), announcement); }

    public abstract List<Announcement> getAllAnnouncemens();

    public abstract Announcement getAnnouncement(String paramString);
}

