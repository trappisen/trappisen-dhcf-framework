package com.dHCF.framework.user;

import com.dHCF.framework.BasePlugin;
import com.dHCF.util.GenericUtils;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.mongodb.morphia.annotations.Id;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.*;

@Entity ("globaluser")
public abstract class ServerParticipator implements ConfigurationSerializable {
    @Embedded
    private Set<String> ignoring = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER); @Id private UUID uniqueId; @Embedded
    private Set<String> messageSpying = new HashSet();

    private UUID lastRepliedTo = null; private boolean inStaffChat = false;
    private boolean globalChatVisible = true;
    private boolean staffChatVisible = true;
    private boolean messagesVisible = true;
    @Transient
    private long lastSpeakTimeMillis = 0L;
    @Transient
    private long lastReceivedMessageMillis = 0L;
    @Transient
    private long lastSentMessageMillis = 0L;


    private String name;


    public ServerParticipator() {}


    public ServerParticipator(UUID uniqueId) { this.uniqueId = uniqueId; }


    public ServerParticipator(Map map) {
        this.uniqueId = UUID.fromString((String)map.get("uniqueID"));
        this.ignoring.addAll(GenericUtils.createList(map.get("ignoring"), String.class));
        this.messageSpying.addAll(GenericUtils.createList(map.get("messageSpying"), String.class));
        Object object = map.get("lastRepliedTo");
        if (object instanceof String) {
            this.lastRepliedTo = UUID.fromString((String)object);
        }
        if (object = map.get("inStaffChat") instanceof Boolean) {
            this.inStaffChat = ((Boolean)object).booleanValue();
        }
        if (object = map.get("globalChatVisible") instanceof Boolean) {
            this.globalChatVisible = ((Boolean)object).booleanValue();
        }
        if (object = map.get("staffChatVisible") instanceof Boolean) {
            this.staffChatVisible = ((Boolean)object).booleanValue();
        }
        if (object = map.get("messagesVisible") instanceof Boolean) {
            this.messagesVisible = ((Boolean)object).booleanValue();
        }
        if (object = map.get("lastSpeakTimeMillis") instanceof String) {
            this.lastSpeakTimeMillis = Long.parseLong((String)object);
        }
        if (object = map.get("lastReceivedMessageMillis") instanceof String) {
            this.lastReceivedMessageMillis = Long.parseLong((String)object);
        }
        if (object = map.get("lastSentMessageMillis") instanceof String) {
            this.lastSentMessageMillis = Long.parseLong((String)object);
        }
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("uniqueID", this.uniqueId.toString());
        map.put("ignoring", new ArrayList(this.ignoring));
        map.put("messageSpying", new ArrayList(this.messageSpying));
        if (this.lastRepliedTo != null) {
            map.put("lastRepliedTo", this.lastRepliedTo.toString());
        }
        map.put("inStaffChat", Boolean.valueOf(this.inStaffChat));
        map.put("globalChatVisible", Boolean.valueOf(this.globalChatVisible));
        map.put("staffChatVisible", Boolean.valueOf(this.staffChatVisible));
        map.put("messagesVisible", Boolean.valueOf(this.messagesVisible));
        map.put("lastSpeakTimeMillis", Long.toString(this.lastSpeakTimeMillis));
        map.put("lastReceivedMessageMillis", Long.toString(this.lastReceivedMessageMillis));
        map.put("lastSentMessageMillis", Long.toString(this.lastSentMessageMillis));
        return map;
    }


    public String getName() { return this.name; }


    public void setName(String name) {
        if (!Objects.equals(this.name, name)) {
            this.name = name;
            update();
        }
    }


    public UUID getUniqueId() { return this.uniqueId; }



    public Set<String> getIgnoring() { return this.ignoring; }



    public Set<String> getMessageSpying() { return this.messageSpying; }



    public UUID getLastRepliedTo() { return this.lastRepliedTo; }


    public void setLastRepliedTo(UUID lastRepliedTo) {
        if (!Objects.equals(this.lastRepliedTo, lastRepliedTo)) {
            this.lastRepliedTo = lastRepliedTo;
            update();
        }
    }


    public Player getLastRepliedToPlayer() { return Bukkit.getPlayer(this.lastRepliedTo); }



    public boolean isInStaffChat() { return this.inStaffChat; }


    public void setInStaffChat(boolean inStaffChat) {
        if (!Objects.equals(Boolean.valueOf(this.inStaffChat), Boolean.valueOf(inStaffChat))) {
            this.inStaffChat = inStaffChat;
            update();
        }
    }


    public boolean isGlobalChatVisible() { return this.globalChatVisible; }


    public void setGlobalChatVisible(boolean globalChatVisible) {
        if (!Objects.equals(Boolean.valueOf(this.globalChatVisible), Boolean.valueOf(globalChatVisible))) {
            this.globalChatVisible = globalChatVisible;
            update();
        }
    }


    public boolean isStaffChatVisible() { return this.staffChatVisible; }


    public void setStaffChatVisible(boolean staffChatVisible) {
        if (!Objects.equals(Boolean.valueOf(this.staffChatVisible), Boolean.valueOf(staffChatVisible))) {
            this.staffChatVisible = staffChatVisible;
            update();
        }
    }


    public boolean isMessagesVisible() { return this.messagesVisible; }


    public void setMessagesVisible(boolean messagesVisible) {
        if (!Objects.equals(Boolean.valueOf(this.messagesVisible), Boolean.valueOf(messagesVisible))) {
            this.messagesVisible = messagesVisible;
            update();
        }
    }


    public long getLastSpeakTimeRemaining() { return (this.lastSpeakTimeMillis > 0L) ? (this.lastSpeakTimeMillis - System.currentTimeMillis()) : 0L; }



    public long getLastSpeakTimeMillis() { return this.lastSpeakTimeMillis; }



    public void setLastSpeakTimeMillis(long lastSpeakTimeMillis) { this.lastSpeakTimeMillis = lastSpeakTimeMillis; }


    public void updateLastSpeakTime() {
        long slowChatDelay = BasePlugin.getPlugin().getServerHandler().getChatSlowedDelay() * 1000L;
        this.lastSpeakTimeMillis = System.currentTimeMillis() + slowChatDelay;
    }


    public long getLastReceivedMessageMillis() { return this.lastReceivedMessageMillis; }



    public void setLastReceivedMessageMillis(long lastReceivedMessageMillis) { this.lastReceivedMessageMillis = lastReceivedMessageMillis; }



    public long getLastSentMessageMillis() { return this.lastSentMessageMillis; }



    public void setLastSentMessageMillis(long lastSentMessageMillis) { this.lastSentMessageMillis = lastSentMessageMillis; }



    public void update() {}


    public void setUniqueId(UUID uniqueId) { this.uniqueId = uniqueId; }


    public void merge(ServerParticipator self) {
        this.ignoring = self.ignoring;
        this.messageSpying = self.messageSpying;
        this.lastRepliedTo = self.lastRepliedTo;
        this.inStaffChat = self.inStaffChat;
        this.globalChatVisible = self.globalChatVisible;
        this.messagesVisible = self.messagesVisible;
        this.messageSpying = self.messageSpying;
        this.name = self.name;
        self.lastReceivedMessageMillis = this.lastReceivedMessageMillis;
        self.lastSentMessageMillis = this.lastSentMessageMillis;
        self.lastSpeakTimeMillis = this.lastSpeakTimeMillis;
    }
}
