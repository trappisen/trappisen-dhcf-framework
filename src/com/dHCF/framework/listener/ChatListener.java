package com.dHCF.framework.listener;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.module.chat.StaffChatCommand;
import com.dHCF.framework.event.PlayerMessageEvent;
import com.dHCF.framework.user.BaseUser;
import com.dHCF.framework.user.ServerParticipator;
import com.dHCF.util.BukkitUtils;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permissible;


public class ChatListener
        implements Listener
{
    private static final String MESSAGE_SPY_FORMAT = BaseConstants.GOLD + "[" + ChatColor.DARK_RED + "SS: " + BaseConstants.YELLOW + "%1$s" + ChatColor.WHITE + " -> " + BaseConstants.YELLOW + "%2$s" + BaseConstants.GOLD + "] %3$s";
    private static final long AUTO_IDLE_TIME = TimeUnit.MINUTES.toMillis(5L);

    private static final String STAFF_CHAT_NOTIFY = "dhcf.command.staffchat";

    private final BasePlugin plugin;

    public ChatListener(BasePlugin plugin) { this.plugin = plugin; }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        BaseUser baseUser = this.plugin.getUserManager().getUser(uuid);
        if (baseUser.isInStaffChat()) {
            HashSet<CommandSender> remainingChatDisabled2 = Sets.newHashSet();
            for (Permissible remainingChatSlowed : Bukkit.getOnlinePlayers()) {
                if (remainingChatSlowed.hasPermission("dhcf.command.staffchat") && remainingChatSlowed instanceof CommandSender) {
                    remainingChatDisabled2.add((CommandSender)remainingChatSlowed);
                }
            }
            if (remainingChatDisabled2.contains(player) && baseUser.isInStaffChat()) {
                String format3 = StaffChatCommand.format(player.getName(), event.getMessage());
                BaseComponent[] global = TextComponent.fromLegacyText(format3);
                this.plugin.getGlobalMessager().broadcastToOtherServers(player, global, "dhcf.command.staffchat");
                for (CommandSender target2 : remainingChatDisabled2) {
                    if (target2 instanceof Player) {
                        Player speakTimeRemaining1 = (Player)target2;
                        BaseUser targetUser2 = this.plugin.getUserManager().getUser(speakTimeRemaining1.getUniqueId());
                        if (targetUser2.isStaffChatVisible()) {
                            target2.sendMessage(format3); continue;
                        }
                        if (!target2.equals(player)) {
                            continue;
                        }
                        target2.sendMessage(ChatColor.RED + "Your message was sent, but you cannot see staff chat messages as your notifications are disabled: Use /togglesc.");
                    }
                }

                event.setCancelled(true);
                return;
            }
        }
        Iterator<?> iterator = event.getRecipients().iterator();
        while (iterator.hasNext()) {
            Player remainingChatDisabled = (Player)iterator.next();
            BaseUser format = this.plugin.getUserManager().getUser(remainingChatDisabled.getUniqueId());
            if (format == null) {
                iterator.remove();
                continue;
            }
            if (baseUser.isInStaffChat() && !format.isStaffChatVisible()) {
                iterator.remove(); continue;
            }  if (format.getIgnoring().contains(player.getName())) {
                iterator.remove(); continue;
            }
            if (format.isGlobalChatVisible()) {
                continue;
            }
            iterator.remove();
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPreMessage(PlayerMessageEvent event) {
        Player sender = event.getSender();
        Player recipient = event.getRecipient();
        UUID recipientUUID = recipient.getUniqueId();
        if (sender.hasPermission("dhcf.messaging.bypass")) {
            ServerParticipator senderParticipator1 = this.plugin.getUserManager().getParticipator(sender);
            if (!senderParticipator1.isMessagesVisible()) {
                event.setCancelled(true);
                sender.sendMessage(ChatColor.RED + "You have private messages toggled.");
            }
        } else {
            BaseUser senderParticipator2 = this.plugin.getUserManager().getUser(recipientUUID);
            if (!senderParticipator2.isMessagesVisible() || senderParticipator2.getIgnoring().contains(sender.getName())) {
                event.setCancelled(true);
                sender.sendMessage(ChatColor.RED + recipient.getName() + " has private messaging toggled.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMessage(PlayerMessageEvent event) {
        Player sender = event.getSender();
        Player recipient = event.getRecipient();
        String message = event.getMessage();
        if (BukkitUtils.getIdleTime(recipient) > AUTO_IDLE_TIME) {
            sender.sendMessage(ChatColor.RED + recipient.getName() + " may not respond as their idle time is over " + DurationFormatUtils.formatDurationWords(AUTO_IDLE_TIME, true, true) + '.');
        }
        UUID senderUUID = sender.getUniqueId();
        String senderId = senderUUID.toString();
        String recipientId = recipient.getUniqueId().toString();
        HashSet<CommandSender> recipients = new HashSet<CommandSender>();
        recipients.addAll(Bukkit.getOnlinePlayers());
        recipients.remove(sender);
        recipients.remove(recipient);
        recipients.add(Bukkit.getConsoleSender());
        for (CommandSender target : recipients) {
            ServerParticipator participator = this.plugin.getUserManager().getParticipator(target);
            Set<?> messageSpying = participator.getMessageSpying();
            if (messageSpying.contains("all") || messageSpying.contains(recipientId) || messageSpying.contains(senderId))
                target.sendMessage(String.format(Locale.ENGLISH, MESSAGE_SPY_FORMAT, new Object[] { sender.getName(), recipient.getName(), message }));
        }
    }
}

