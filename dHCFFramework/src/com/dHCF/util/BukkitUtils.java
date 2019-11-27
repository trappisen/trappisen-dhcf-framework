package com.dHCF.util;


import com.dHCF.framework.BasePlugin;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public final class BukkitUtils {
    public static final String STRAIGHT_LINE_DEFAULT;
    private static final ImmutableMap<Object, Object> CHAT_DYE_COLOUR_MAP;

    public static int countColoursUsed(String id, boolean ignoreDuplicates) {
        ChatColor[] values = ChatColor.values();
        ArrayList<Character> charList = new ArrayList<Character>(values.length);
        ChatColor[] count = values;
        for (int found = values.length, i = 0; i < found; i++) {
            ChatColor colour = count[i];
            charList.add(Character.valueOf(colour.getChar()));
        }
        int var8 = 0;
        HashSet<ChatColor> var9 = new HashSet<ChatColor>();
        for (int i = 1; i < id.length(); i++) {
            if (charList.contains(Character.valueOf(id.charAt(i))) && id.charAt(i - 1) == '&') {
                ChatColor colour = ChatColor.getByChar(id.charAt(i));
                if (var9.add(colour) || ignoreDuplicates) {
                    var8++;
                }
            }
        }
        return var8;
    }
    private static final ImmutableSet<Object> DEBUFF_TYPES; private static final int DEFAULT_COMPLETION_LIMIT = 80;

    public static List<String> getCompletions(String[] args, List<String> input) { return getCompletions(args, input, 80); }


    public static List<String> getCompletions(String[] args, List<String> input, int limit) {
        Preconditions.checkNotNull(args);
        Preconditions.checkArgument((args.length != 0));
        String argument = args[args.length - 1];

        String paramString1 = null;
        return (List)input.stream().filter(string -> string.regionMatches(true, 0, paramString1, 0, paramString1.length())).limit(limit).collect(Collectors.toList());
    }

    public static String getDisplayName(CommandSender sender) {
        Preconditions.checkNotNull(sender);
        return (sender instanceof Player) ? ((Player)sender).getDisplayName() : sender.getName();
    }

    public static long getIdleTime(Player player) {
        Preconditions.checkNotNull(player);
        long idleTime = BasePlugin.getPlugin().getNmsProvider().getIdleTime(player);
        return (idleTime > 0L) ? (System.currentTimeMillis() - idleTime) : 0L;
    }


    public static DyeColor toDyeColor(ChatColor colour) { return (DyeColor)CHAT_DYE_COLOUR_MAP.get(colour); }



    public static boolean hasMetaData(Metadatable metadatable, String input, Plugin plugin) { return (getMetaData(metadatable, input, plugin) != null); }


    public static MetadataValue getMetaData(Metadatable metadatable, String input, Plugin plugin) {
        List<MetadataValue> values = metadatable.getMetadata(input);
        for (MetadataValue value : values) {
            if (value.getOwningPlugin() == plugin) {
                return value;
            }
        }
        return null;
    }

    public static Player getFinalAttacker(EntityDamageEvent ede, boolean ignoreSelf) {
        Player attacker = null;
        if (ede instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)ede;
            Entity damager = event.getDamager();
            if (event.getDamager() instanceof Player) {
                attacker = (Player)damager;
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile)damager;
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player) {
                    attacker = (Player)shooter;
                }
            }
            if (attacker != null && ignoreSelf && event.getEntity().equals(attacker)) {
                attacker = null;
            }
        }
        return attacker;
    }


    public static Player playerWithNameOrUUID(String string) { return (string == null) ? null : (JavaUtils.isUUID(string) ? Bukkit.getPlayer(UUID.fromString(string)) : Bukkit.getPlayer(string)); }



    @Deprecated
    public static OfflinePlayer offlinePlayerWithNameOrUUID(String string) { return (string == null) ? null : (JavaUtils.isUUID(string) ? Bukkit.getOfflinePlayer(UUID.fromString(string)) : Bukkit.getOfflinePlayer(string)); }



    public static boolean isWithinX(Location location, Location other, double distance) { return (location.getWorld().equals(other.getWorld()) && Math.abs(other.getX() - location.getX()) <= distance && Math.abs(other.getZ() - location.getZ()) <= distance); }



    public static Location getHighestLocation(Location origin) { return getHighestLocation(origin, null); }


    public static Location getHighestLocation(Location origin, Location def) {
        Preconditions.checkNotNull(origin, "The location cannot be null");
        Location cloned = origin.clone();
        World world = cloned.getWorld();
        int x = cloned.getBlockX();
        int y = world.getMaxHeight();
        int z = cloned.getBlockZ();
        while (y > origin.getBlockY()) {
            y--;
            Block block = world.getBlockAt(x, y, z);
            if (!block.isEmpty()) {
                Location next = block.getLocation();
                next.setPitch(origin.getPitch());
                next.setYaw(origin.getYaw());
                return next;
            }
        }
        return def;
    }


    public static boolean isDebuff(PotionEffectType type) { return DEBUFF_TYPES.contains(type); }



    public static boolean isDebuff(PotionEffect potionEffect) { return isDebuff(potionEffect.getType()); }


    public static boolean isDebuff(ThrownPotion thrownPotion) {
        for (PotionEffect effect : thrownPotion.getEffects()) {
            if (isDebuff(effect)) {
                return true;
            }
        }
        return false;
    }


    public static String toString(Location location) { return location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ(); }



    private static final String STRAIGHT_LINE_TEMPLATE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256); static  {
        STRAIGHT_LINE_DEFAULT = STRAIGHT_LINE_TEMPLATE.substring(0, 55);
        CHAT_DYE_COLOUR_MAP = ImmutableMap.builder().put(ChatColor.AQUA, DyeColor.LIGHT_BLUE).put(ChatColor.BLACK, DyeColor.BLACK).put(ChatColor.BLUE, DyeColor.LIGHT_BLUE).put(ChatColor.DARK_AQUA, DyeColor.CYAN).put(ChatColor.DARK_BLUE, DyeColor.BLUE).put(ChatColor.DARK_GRAY, DyeColor.GRAY).put(ChatColor.DARK_GREEN, DyeColor.GREEN).put(ChatColor.DARK_PURPLE, DyeColor.PURPLE).put(ChatColor.DARK_RED, DyeColor.RED).put(ChatColor.GOLD, DyeColor.ORANGE).put(ChatColor.GRAY, DyeColor.SILVER).put(ChatColor.GREEN, DyeColor.LIME).put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA).put(ChatColor.RED, DyeColor.RED).put(ChatColor.WHITE, DyeColor.WHITE).put(ChatColor.YELLOW, DyeColor.YELLOW).build();
        DEBUFF_TYPES = ImmutableSet.builder().add(PotionEffectType.BLINDNESS).add(PotionEffectType.CONFUSION).add(PotionEffectType.HARM).add(PotionEffectType.HUNGER).add(PotionEffectType.POISON).add(PotionEffectType.SATURATION).add(PotionEffectType.SLOW).add(PotionEffectType.SLOW_DIGGING).add(PotionEffectType.WEAKNESS).add(PotionEffectType.WITHER).build();
    }
}

