package com.dHCF.framework.user;

import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.user.util.NameHistory;
import com.faithfulmc.framework.event.PlayerVanishEvent;
import com.faithfulmc.util.GenericUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import java.util.*;

@Entity("globaluser")
public class BaseUser extends ServerParticipator {
    @Embedded
    private List<String> addressHistories = new ArrayList();
    @Embedded
    private List<NameHistory> nameHistories = new ArrayList();
    @Embedded
    private List<String> notes = new ArrayList();

    private Long firstJoined = null;
    private boolean messagingSounds = true;
    @Transient
    private boolean vanished = false;
    private long lastSeen;
    private boolean online = false;
    private String lastServer = null;
    private String group = null;
    @Transient
    private boolean loaded = true;

    @PostLoad
    public void postLoadMethod() {
        if (this.firstJoined == null) {
            this.firstJoined = Long.valueOf(this.lastSeen);
        }
    }




    public BaseUser(UUID uniqueID, String name) {
        this(uniqueID, true);
        setName(name);
    }

    public BaseUser(UUID uniqueID, boolean loaded) {
        super(uniqueID);
        this.lastSeen = System.currentTimeMillis();
        this.firstJoined = Long.valueOf(this.lastSeen);
        this.loaded = loaded;
    }

    public BaseUser(Map<String, Object> map) {
        super(map);
        this.notes.addAll(GenericUtils.createList(map.get("notes"), String.class));
        this.addressHistories.addAll(GenericUtils.createList(map.get("addressHistories"), String.class));
        this.lastSeen = ((Number)map.getOrDefault("lastSeen", Long.valueOf(-1L))).longValue();
        Object object = map.get("nameHistories");
        if (object != null) {
            this.nameHistories.addAll(GenericUtils.createList(object, NameHistory.class));
        }
        if (object = map.get("messagingSounds") instanceof Boolean) {
            this.messagingSounds = ((Boolean)object).booleanValue();
        }
        if (object = map.get("vanished") instanceof Boolean) {
            this.vanished = ((Boolean)object).booleanValue();
        }
        this.group = (String)map.getOrDefault("group", "default");
    }


    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("lastSeen", Long.valueOf(this.lastSeen));
        map.put("addressHistories", this.addressHistories);
        map.put("notes", this.notes);
        map.put("nameHistories", this.nameHistories);
        map.put("messagingSounds", Boolean.valueOf(this.messagingSounds));
        map.put("vanished", Boolean.valueOf(this.vanished));
        map.put("group", this.group);
        return map;
    }


    public List<NameHistory> getNameHistories() { return this.nameHistories; }



    public List<String> getNotes() { return this.notes; }


    public void setNote(String note) {
        this.notes.add(note);
        update();
    }

    public boolean tryRemoveNote() {
        this.notes.clear();
        update();
        return true;
    }


    public List<String> getAddressHistories() { return this.addressHistories; }



    public boolean tryLoggingAddress(String address) {
        Preconditions.checkNotNull(address, "Cannot log null address");
        if (!this.addressHistories.contains(address)) {
            Preconditions.checkArgument(InetAddresses.isInetAddress(address), "Not an Inet address");
            this.addressHistories.add(address);
            update();
            return true;
        }
        return false;
    }


    public boolean isMessagingSounds() { return this.messagingSounds; }


    public void setMessagingSounds(boolean messagingSounds) {
        this.messagingSounds = messagingSounds;
        update();
    }


    public boolean isVanished() { return this.vanished; }



    public void setVanished(boolean vanished) { setVanished(vanished, true); }



    public void setVanished() { setVanished(!isVanished(), true); }



    public void setVanished(boolean vanished, boolean update) { setVanished(Bukkit.getPlayer(getUniqueId()), vanished, update); }


    public boolean setVanished(Player player, boolean vanished, boolean notifyPlayerList) {
        if (BasePlugin.PRACTICE) {
            return false;
        }
        if (this.vanished != vanished) {
            if (player != null) {
                PlayerVanishEvent event = new PlayerVanishEvent(player, notifyPlayerList ? new HashSet(Bukkit.getOnlinePlayers()) : Collections.emptySet(), vanished);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return false;
                }
                if (notifyPlayerList) {
                    updateVanishedState(player, event.getViewers(), vanished);
                }
            }
            this.vanished = vanished;
            return true;
        }
        return false;
    }


    public void updateVanishedState(Player player, boolean vanished) { updateVanishedState(player, new HashSet(Bukkit.getOnlinePlayers()), vanished); }


    public void updateVanishedState(Player player, Collection<Player> viewers, boolean vanished) {
        if (BasePlugin.PRACTICE) {
            return;
        }
        player.spigot().setCollidesWithEntities(!vanished);


        for (Player target : viewers) {
            if (player.equals(target)) {
                continue;
            }
            if (BasePlugin.getPlugin().getUserManager().getUser(target.getUniqueId()).isVanished()) {
                if (!vanished) {
                    player.hidePlayer(target);
                    continue;
                }
                player.showPlayer(target);

                continue;
            }
            if (vanished) {
                target.hidePlayer(player);
                continue;
            }
            target.showPlayer(player);
        }
    }



    public String getLastKnownName() { return ((NameHistory) Iterables.getLast(this.nameHistories)).getName(); }



    public Player toPlayer() { return Bukkit.getPlayer(getUniqueId()); }



    public long getLastSeen() { return this.lastSeen; }


    public void setLastSeen(long lastSeen) {
        if (!Objects.equals(Long.valueOf(this.lastSeen), Long.valueOf(lastSeen))) {
            this.lastSeen = lastSeen;
            update();
        }
    }


    public String getGroup() { return this.group; }


    public void setGroup(String group) {
        if (!Objects.equals(this.group, group)) {
            this.group = group;
            update();
        }
    }

    public void setName(String name) {
        if (!Objects.equals(getName(), name)) {
            boolean log = true;
            for (NameHistory nameHistory : this.nameHistories) {
                if (nameHistory.getName().equals(name)) {
                    log = false;
                    break;
                }
            }
            if (log) {
                this.nameHistories.add(new NameHistory(name, System.currentTimeMillis()));
            }
            update();
        }
        super.setName(name);
    }


    public boolean isOnline() { return this.online; }


    public void setOnline(boolean online) {
        if (!Objects.equals(Boolean.valueOf(this.online), Boolean.valueOf(online))) {
            this.online = online;
            update();
        }
    }


    public String getLastServer() { return this.lastServer; }


    public void setLastServer(String lastServer) {
        if (!Objects.equals(this.lastServer, lastServer)) {
            this.lastServer = lastServer;
            update();
        }
    }


    public Long getFirstJoined() { return this.firstJoined; }


    public void setFirstJoined(Long firstJoined) {
        if (!Objects.equals(this.firstJoined, firstJoined)) {
            this.firstJoined = firstJoined;
            update();
        }
    }

    public void update() {
        BasePlugin basePlugin = BasePlugin.getPlugin();
        if (basePlugin == null || basePlugin.getDatastore() == null || basePlugin.getCursorThread() == null) {
            return;
        }
        if (getUniqueId() != null) {
            Bukkit.getScheduler().runTaskAsynchronously(basePlugin, () -> {
                paramBasePlugin.getUserManager().save(this);
                paramBasePlugin.getCursorThread().createUpdate(getUniqueId());
            });
        }
    }


    public void merge(ServerParticipator self) {
        super.merge(self);
        if (self instanceof BaseUser) {
            BaseUser selfBase = (BaseUser)self;
            this.loaded = selfBase.loaded;
            this.addressHistories = selfBase.addressHistories;
            this.nameHistories = selfBase.nameHistories;
            this.notes = selfBase.notes;
            this.firstJoined = selfBase.firstJoined;
            this.messagingSounds = selfBase.messagingSounds;
            selfBase.vanished = this.vanished;
            this.lastSeen = selfBase.lastSeen;
            this.online = selfBase.online;
            this.lastServer = selfBase.lastServer;
            this.group = selfBase.group;
        }
    }

    public BaseUser() {}
}
