package com.dHCF.framework.command.module.essential;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.command.BaseCommand;
import com.dHCF.framework.event.PlayerFreezeEvent;
import com.dHCF.util.BukkitUtils;
import com.dHCF.util.chat.ClickAction;
import com.dHCF.util.chat.Text;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class FreezeCommand extends BaseCommand implements Listener, Runnable, InventoryHolder {
    private static final String FREEZE_BYPASS = "base.freeze.bypass";
    private final TObjectLongMap<UUID> frozenPlayers = new TObjectLongHashMap();
    private final Set<UUID> inventoryLock = new HashSet();
    private long defaultFreezeDuration;
    private Set<UUID> frozen = new HashSet(); private final Inventory inventory; private final ItemStack BOOK;
    private int i;

    public FreezeCommand(BasePlugin plugin) { super("freeze", "Freezes a player from moving");




        this.inventory = Bukkit.createInventory(this, 9, BaseConstants.YELLOW + "You are frozen");
        this.BOOK = new ItemStack(Material.BOOK);


        ItemMeta meta = this.BOOK.getItemMeta();
        meta.setDisplayName(BaseConstants.GOLD + BaseConstants.NAME + "MC" + ChatColor.DARK_GRAY + " � " + BaseConstants.YELLOW + "You have been frozen");
        meta.setLore(Arrays.asList(new String[] { ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH
                .toString() + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 37), BaseConstants.YELLOW + "You have been frozen by a staff member", BaseConstants.YELLOW + "If you disconnect you will be " + ChatColor.DARK_RED + ChatColor.BOLD + "BANNED", BaseConstants.YELLOW + "Please connect to our teamspeak", BaseConstants.GRAY + "(" + BaseConstants.TEAMSPEAK + ")", BaseConstants.YELLOW + "You have 3 minutes to join", ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH





                .toString() + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 37) }));

        this.BOOK.setItemMeta(meta);
        this.inventory.setItem(4, this.BOOK);






        this.i = 0; setUsage("/(command) (<all|playerName>)/(lock <player>)");
        setAliases(new String[] { "ss" });
        this.defaultFreezeDuration = TimeUnit.MINUTES.toMillis(60L);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 1L, 1L); } @EventHandler public void onPlayerClick(InventoryClickEvent event) { Player player = (Player)event.getWhoClicked();
        if (event.getWhoClicked() instanceof Player && (
                getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) && !player.hasPermission("base.freeze.bypass")) {
            event.setCancelled(true);

            return;
        }
        if (!this.inventoryLock.contains(player.getUniqueId()))
            if (event.getView() != null && event.getView().getTopInventory() != null && event.getView().getTopInventory().getHolder() == this)
            { event.setCancelled(true); }
            else if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() == this)
            { event.setCancelled(true); }   }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { if (args.length < 1) { sender.sendMessage(getUsage(label)); } else if (args[0].equalsIgnoreCase("lock") && args.length == 2) { Player target = Bukkit.getServer().getPlayer(args[1]); if (target == null || !BaseCommand.canSee(sender, target)) { sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] })); return true; }  if (target.equals(sender) && target.hasPermission("base.freeze.bypass")) { sender.sendMessage(ChatColor.RED + "You cannot unlock yourself."); return true; }  if (!this.frozen.contains(target.getUniqueId())) { sender.sendMessage(ChatColor.RED + "Target is not frozen"); } else if (this.inventoryLock.add(target.getUniqueId())) { sender.sendMessage(BaseConstants.YELLOW + "Inventory lock toggled off for " + target.getName()); target.closeInventory(); } else if (this.inventoryLock.remove(target.getUniqueId())) { sender.sendMessage(BaseConstants.YELLOW + "Inventory lock toggled on for " + target.getName()); target.openInventory(this.inventory); }  } else { Long freezeTicks = Long.valueOf(this.defaultFreezeDuration); long millis = System.currentTimeMillis(); Player target = Bukkit.getServer().getPlayer(args[0]); if (target == null || !BaseCommand.canSee(sender, target)) { sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, new Object[] { args[0] })); return true; }  if (target.equals(sender) && target.hasPermission("base.freeze.bypass")) { sender.sendMessage(ChatColor.RED + "You cannot freeze yourself."); return true; }
        UUID targetUUID = target.getUniqueId(); boolean shouldFreeze = (getRemainingPlayerFrozenMillis(targetUUID) > 0L); PlayerFreezeEvent playerFreezeEvent = new PlayerFreezeEvent(target, shouldFreeze); Bukkit.getServer().getPluginManager().callEvent(playerFreezeEvent); if (playerFreezeEvent.isCancelled()) { sender.sendMessage(ChatColor.RED + "Unable to freeze " + target.getName() + '.'); return false; }
        if (shouldFreeze) { this.frozen.remove(target.getUniqueId()); this.frozenPlayers.remove(targetUUID); this.inventoryLock.remove(targetUUID); target.sendMessage(ChatColor.GREEN + "You have been un-frozen."); target.updateInventory(); Command.broadcastCommandMessage(sender, BaseConstants.YELLOW + target.getName() + " is no longer frozen"); }
        else { this.frozen.add(target.getUniqueId()); this.frozenPlayers.put(targetUUID, millis + freezeTicks.longValue()); String timeString = DurationFormatUtils.formatDurationWords(freezeTicks.longValue(), true, true); Command.broadcastCommandMessage(sender, BaseConstants.YELLOW + target.getName() + " is now frozen for " + timeString); }
    }
        return true; } public Inventory getInventory() { return this.inventory; } public void run() { for (UUID uuid : this.frozen) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            if (this.i % 200 == 0) {
                player.sendMessage(BaseConstants.GRAY + ChatColor.STRIKETHROUGH.toString() + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 37));
                player.sendMessage(BaseConstants.YELLOW + " You have been frozen by a staff member");
                player.sendMessage(BaseConstants.YELLOW + "   If you disconnect you will be " + ChatColor.DARK_RED + ChatColor.BOLD + "BANNED");
                player.sendMessage(BaseConstants.YELLOW + "     Please connect to our teamspeak");
                player.sendMessage(BaseConstants.GRAY + "          (" + BaseConstants.TEAMSPEAK + ")");
                player.sendMessage(BaseConstants.YELLOW + "           You have 3 minutes to join");
                player.sendMessage(BaseConstants.GRAY + ChatColor.STRIKETHROUGH.toString() + BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 37));
            }
            if ((this.inventoryLock.contains(player.getUniqueId()) && player.getOpenInventory() == null) || player.getOpenInventory().getTopInventory() == null || player.getOpenInventory().getTopInventory().getHolder() != this) {
                player.openInventory(this.inventory);
            }
        }
    }
        this.i++; }




    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) { return (args.length == 1) ? null : Collections.emptyList(); }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player attacker = BukkitUtils.getFinalAttacker(event, false);
            if (attacker == null) {
                return;
            }
            Player player = (Player)entity;
            if ((getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) && !player.hasPermission("base.freeze.bypass")) {
                if (!attacker.hasPermission("base.freeze.bypass")) {
                    attacker.sendMessage(ChatColor.RED + player.getName() + " is currently frozen, you may not attack.");
                    event.setCancelled(true);
                }
                return;
            }
            if ((getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(attacker.getUniqueId()) > 0L) && !attacker.hasPermission("base.freeze.bypass")) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + "You may not attack players whilst frozen.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPreCommandProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if ((getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) && !player.hasPermission("base.freeze.bypass")) {
            String message = event.getMessage().toLowerCase();
            if (message.startsWith("/reply") || message.startsWith("/msg") || message.startsWith("/r") || message.startsWith("/message") || message.startsWith("/helpop") || message.startsWith("/m")) {
                return;
            }
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not use commands whilst frozen.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        if ((getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) && !player.hasPermission("base.freeze.bypass")) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL || event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
                return;
            }
            Player player = event.getPlayer();
            if ((getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) && !player.hasPermission("base.freeze.bypass")) {
                event.setTo(event.getFrom());
            }
        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if ((getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) && !player.hasPermission("base.freeze.bypass")) {
            player.sendMessage(ChatColor.RED + "You may not use blocks whilst frozen.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if ((getRemainingServerFrozenMillis() > 0L || getRemainingPlayerFrozenMillis(player.getUniqueId()) > 0L) && !player.hasPermission("base.freeze.bypass")) {
            player.sendMessage(ChatColor.RED + "You may not use blocks whilst frozen.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (this.frozen.contains(e.getPlayer().getUniqueId())) {
            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                if (!online.hasPermission("base.command.freeze")) {
                    continue;
                }
                (new Text(BaseConstants.YELLOW + e.getPlayer().getName() + " has " + ChatColor.DARK_RED + "QUIT" + BaseConstants.YELLOW + " while frozen. " + BaseConstants.GRAY + ChatColor.ITALIC + "(Click here to ban)")).setHoverText(BaseConstants.YELLOW + "Click here to permanently ban" + BaseConstants.GRAY + e.getPlayer().getName()).setClick(ClickAction.RUN_COMMAND, "/ban " + e.getPlayer().getName() + " Refusal to SS").send(online);
                return;
            }
        }
    }


    public long getRemainingServerFrozenMillis() { return -1L; }


    public long getRemainingPlayerFrozenMillis(UUID uuid) {
        long remaining = this.frozenPlayers.get(uuid);
        if (remaining == this.frozenPlayers.getNoEntryValue()) {
            return 0L;
        }
        return remaining - System.currentTimeMillis();
    }
}

