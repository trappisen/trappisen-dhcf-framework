package com.dHCF.framework.command.module.chat;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.framework.user.BaseUser;
import com.dHCF.framework.user.ServerParticipator;
import com.dHCF.util.messgener.GlobalMessager;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StaffChatCommand
        extends BaseCommand {
    private final BasePlugin plugin;

    public StaffChatCommand(BasePlugin plugin) {
        super("staffchat", "Enters staff chat mode.");
        setAliases(new String[] { "sc", "ac", "gsc" });
        setUsage("/(command) [playerName]");
        this.plugin = plugin;
    }

    public static final String FORMAT = ChatColor.BLUE + "(Staff Chat) " + ChatColor.AQUA + "%1$s" + BaseConstants.GRAY + ": %2$s";


    public static String format(String sendername, String message) { return String.format(Locale.ENGLISH, FORMAT, new Object[] { sendername, message }); }



    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        (new BukkitRunnable() { public void run() {
            Object target;
            ServerParticipator participator = StaffChatCommand.this.plugin.getUserManager().getParticipator(sender);
            if (participator == null) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");

                return;
            }
            if (args.length <= 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <message|playerName>");
                    return;
                }
                target = participator;
            } else {
                Player newStaffChat = Bukkit.getPlayerExact(args[0]);
                if (newStaffChat == null || !BaseCommand.canSee(sender, newStaffChat) || !sender.hasPermission(command.getPermission() + ".others")) {
                    String message = StringUtils.join(args, ' ');
                    String format = StaffChatCommand.format(sender.getName(), message);
                    BaseComponent[] global = TextComponent.fromLegacyText(format);
                    StaffChatCommand.this.plugin.getGlobalMessager().broadcastToOtherServers((sender instanceof Player) ? (Player)sender : GlobalMessager.getRandomPlayer(), global, "dhcf.command.staffchat");
                    Bukkit.getConsoleSender().sendMessage(format);
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        BaseUser otherUser = StaffChatCommand.this.plugin.getUserManager().getUser(other.getUniqueId());
                        if (otherUser.isStaffChatVisible() && other.hasPermission("base.command.staffchat")) {
                            other.sendMessage(format);
                        }
                    }
                    return;
                }
                target = StaffChatCommand.this.plugin.getUserManager().getUser(newStaffChat.getUniqueId());
            }
            boolean newStaffChat2 = (!((ServerParticipator)target).isInStaffChat() || (args.length >= 2 && Boolean.parseBoolean(args[1])));
            ((ServerParticipator)target).setInStaffChat(newStaffChat2);
            sender.sendMessage(BaseConstants.YELLOW + "Staff chat mode of " + ((ServerParticipator)target).getName() + " set to " + newStaffChat2 + '.');
        } }
        ).runTaskAsynchronously(this.plugin);
        return true;
    }



    public List onTabComplete(CommandSender sender, Command command, String label, String[] args) { return Collections.emptyList(); }
}

