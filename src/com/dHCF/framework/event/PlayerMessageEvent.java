package com.dHCF.framework.event;


import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.user.BaseUser;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;


public class PlayerMessageEvent
        extends Event
        implements Cancellable
{
    public static HandlerList getHandlerList() { return handlers; }



    private static final HandlerList handlers = new HandlerList();

    private final Player sender;

    private final Player recipient;
    private final String message;
    private final boolean isReply;
    private boolean cancelled;

    public PlayerMessageEvent(Player sender, Set recipients, String message, boolean isReply) {
        super(true);
        this.cancelled = false;
        this.sender = sender;
        this.recipient = (Player)Iterables.getFirst(recipients, null);
        this.message = message;
        this.isReply = isReply;
    }


    public Player getSender() { return this.sender; }



    public Player getRecipient() { return this.recipient; }



    public String getMessage() { return this.message; }



    public boolean isReply() { return this.isReply; }


    public void send() {
        Preconditions.checkNotNull(this.sender, "The sender cannot be null");
        Preconditions.checkNotNull(this.recipient, "The recipient cannot be null");
        BasePlugin plugin = BasePlugin.getPlugin();
        BaseUser sendingUser = plugin.getUserManager().getUser(this.sender.getUniqueId());
        BaseUser recipientUser = plugin.getUserManager().getUser(this.recipient.getUniqueId());
        sendingUser.setLastRepliedTo(recipientUser.getUniqueId());
        recipientUser.setLastRepliedTo(sendingUser.getUniqueId());
        long millis = System.currentTimeMillis();
        recipientUser.setLastReceivedMessageMillis(millis);
        String rank = ChatColor.translateAlternateColorCodes('&', "&f" + BasePlugin.getChat().getPlayerPrefix(this.sender)).replace("_", " ");
        String displayName = rank + this.sender.getDisplayName();
        String rank2 = ChatColor.translateAlternateColorCodes('&', "&f" + BasePlugin.getChat().getPlayerPrefix(this.recipient)).replace("_", " ");
        String displayName2 = rank2 + this.recipient.getDisplayName();
        if (recipientUser.isMessagingSounds()) {
            this.recipient.playSound(this.recipient.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
        }
        this.sender.sendMessage(ChatColor.GRAY + "(To " + displayName2 + ChatColor.GRAY + ") " + ChatColor.GRAY + this.message);
        this.recipient.sendMessage(ChatColor.GRAY + "(From " + displayName + ChatColor.GRAY + ") " + ChatColor.GRAY + this.message);
    }


    public boolean isCancelled() { return this.cancelled; }



    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }



    public HandlerList getHandlers() { return handlers; }
}

