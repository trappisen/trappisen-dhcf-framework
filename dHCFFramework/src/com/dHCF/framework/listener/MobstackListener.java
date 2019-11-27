package com.dHCF.framework.listener;

import com.dHCF.framework.BaseConstants;
import com.dHCF.framework.BasePlugin;
import com.dHCF.util.cuboid.CoordinatePair;
import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.primitives.Ints;
import net.minecraft.server.v1_8_R3.MobSpawnerAbstract;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Map;

public class MobstackListener extends BukkitRunnable implements Listener {
    private static final int NATURAL_STACK_RADIUS = 20;
    public static final String STACKED_PREFIX = BaseConstants.GOLD.toString() + "x"; private static final int MAX_STACKED_QUANTITY = 150; private static final int OTHER_STACK_RADIUS = 8; private final Table<CoordinatePair, EntityType, Integer> naturalSpawnStacks; private final BasePlugin plugin;
    private Map<MobSpawnerAbstract, Integer> mobSpawnerAbstractIntegerMap;

    public MobstackListener(BasePlugin plugin) {
        this.mobSpawnerAbstractIntegerMap = Maps.newHashMap();


        this.naturalSpawnStacks = HashBasedTable.create();
        this.plugin = plugin;
        runTaskTimer(plugin, 40L, 1200L);
    }


    private CoordinatePair fromLocation(Location location) { return new CoordinatePair(location.getWorld(), Math.round((location.getBlockX() / 20)), Math.round((location.getBlockZ() / 20))); }


    public void run() {
        long now = System.currentTimeMillis();
        for (World world : Bukkit.getServer().getWorlds()) {
            if (world.getEnvironment() != World.Environment.THE_END) {
                for (LivingEntity entity : world.getLivingEntities()) {
                    if (!entity.isValid() || entity.isDead() || (
                            !(entity instanceof org.bukkit.entity.Animals) && !(entity instanceof org.bukkit.entity.Monster))) {
                        continue;
                    }
                    for (Entity nearby : entity.getNearbyEntities(8.0D, 8.0D, 8.0D)) {
                        if (nearby == null || !(nearby instanceof LivingEntity) || nearby.isDead() || !nearby.isValid() || (
                                !(nearby instanceof org.bukkit.entity.Animals) && !(nearby instanceof org.bukkit.entity.Monster))) {
                            continue;
                        }
                        if (stack((LivingEntity)nearby, entity)) {
                            if (this.naturalSpawnStacks.containsValue(Integer.valueOf(entity.getEntityId()))) {
                                for (Map.Entry<CoordinatePair, Integer> entry : this.naturalSpawnStacks.column(entity.getType()).entrySet()) {
                                    if (((Integer)entry.getValue()).intValue() == entity.getEntityId())
                                        this.naturalSpawnStacks.put(entry.getKey(), entity.getType(), Integer.valueOf(nearby.getEntityId()));
                                }
                                continue;
                            }
                            if (this.mobSpawnerAbstractIntegerMap.containsValue(Integer.valueOf(entity.getEntityId()))) {
                                for (Map.Entry<MobSpawnerAbstract, Integer> entry : this.mobSpawnerAbstractIntegerMap.entrySet()) {
                                    if (((Integer)entry.getValue()).intValue() == entity.getEntityId()) {
                                        this.mobSpawnerAbstractIntegerMap.put(entry.getKey(), Integer.valueOf(nearby.getEntityId()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }




        long finish = System.currentTimeMillis();
        long time = finish - now;
        double seconds = time / 50.0D;
        if (seconds > 0.4D) {
            ChatColor color; String formatted = (new DecimalFormat("#.#")).format(seconds * 100.0D);

            if (seconds < 0.6D) {
                color = ChatColor.GREEN;
            } else if (seconds < 0.8D) {
                color = BaseConstants.GOLD;
            } else if (seconds < 1.0D) {
                color = ChatColor.RED;
            } else {
                color = ChatColor.DARK_RED;
            }
            String message = BaseConstants.GOLD + ChatColor.BOLD.toString() + "> " + BaseConstants.YELLOW + "Mob stacking took " + color + formatted + "% " + BaseConstants.YELLOW + " of a tick";
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp()) {
                    player.sendMessage(message);
                }
            }
        }
    }































    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSpawnerSpawn(SpawnerSpawnEvent event)
    {
        if (!canRemove(event.getEntity() {
            return;
        }
        if (event.getSpawner().getWorld().getEnvironment() != World.Environment.THE_END) {
            CreatureSpawner creatureSpawner = event.getSpawner();
            TileEntityMobSpawner tile = (TileEntityMobSpawner)((CraftWorld)creatureSpawner.getWorld()).getTileEntityAt(creatureSpawner.getX(), creatureSpawner.getY(), creatureSpawner.getZ());
            Integer integer = (Integer)this.mobSpawnerAbstractIntegerMap.get(tile.getSpawner());
            if (integer != null) {
                Entity nmsTarget = ((CraftWorld)creatureSpawner.getWorld()).getHandle().getEntity(integer.intValue());
                if (nmsTarget != null) {
                    CraftEntity craftEntity = ((net.minecraft.server.v1_8_R3.Entity) nmsTarget).getBukkitEntity();
                    if (craftEntity != null && craftEntity instanceof LivingEntity && craftEntity.isValid() && !craftEntity.isDead() && craftEntity.getLocation().distance(creatureSpawner.getBlock().getLocation()) < 10.0D) {
                        event.setCanceled(true);
                        LivingEntity targetLiving = (LivingEntity)craftEntity;
                        int stackedQuantity = getStackedQuantity(targetLiving);
                        if (stackedQuantity == -1) {
                            stackedQuantity = 1;
                        }
                        setStackedQuantity(targetLiving, Math.min(150, stackedQuantity + 1));
                        return;
                    }
                }
            }
            this.mobSpawnerAbstractIntegerMap.put(tile.getSpawner(), Integer.valueOf(event.getEntity().getEntityId()));
        }  } @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Optional<Integer> entityIdOptional;
        CoordinatePair coordinatePair;
        Location location;
        EntityType entityType = event.getEntityType();
        switch (event.getSpawnReason()) {
            case CHUNK_GEN:
            case NATURAL:
            case DEFAULT:
                location = event.getLocation();
                coordinatePair = fromLocation(location);
                entityIdOptional = Optional.fromNullable(this.naturalSpawnStacks.get(coordinatePair, entityType));
                if (entityIdOptional.isPresent()) {
                    int entityId = ((Integer)entityIdOptional.get()).intValue();
                    Entity target = BasePlugin.getPlugin().getNmsProvider().getEntityFromID(location.getWorld(), entityId);
                    if (target != null && target instanceof LivingEntity) {
                        boolean canSpawn; LivingEntity targetLiving = (LivingEntity)target;

                        if (targetLiving instanceof Ageable) {
                            canSpawn = ((Ageable)targetLiving).isAdult();
                        } else {
                            canSpawn = (!(targetLiving instanceof Zombie) || !((Zombie)targetLiving).isBaby());
                        }
                        if (canSpawn) {
                            int stackedQuantity = getStackedQuantity(targetLiving);
                            if (stackedQuantity == -1) {
                                stackedQuantity = 1;
                            }
                            int stacked = Math.min(150, stackedQuantity + 1);
                            setStackedQuantity(targetLiving, stacked);
                            event.setCancelled(true);
                        }
                    }
                }
                this.naturalSpawnStacks.put(coordinatePair, entityType, Integer.valueOf(event.getEntity().getEntityId()));
                break;
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity == null) {
            return;
        }
        int stackedQuantity = getStackedQuantity(livingEntity);
        if (stackedQuantity > 1) {
            LivingEntity respawned = (LivingEntity)livingEntity.getWorld().spawnEntity(livingEntity.getLocation(), event.getEntityType());
            (((CraftLivingEntity)respawned).getHandle()).fromMobSpawner = true;
            setStackedQuantity(respawned, Math.min(150, stackedQuantity - 1));
            if (respawned instanceof Ageable) {
                ((Ageable)respawned).setAdult();
            }
            if (respawned instanceof Slime) {
                ((Slime)respawned).setSize(3);
            }
            if (respawned instanceof Zombie) {
                ((Zombie)respawned).setBaby(false);
            }
            if (this.mobSpawnerAbstractIntegerMap.containsValue(Integer.valueOf(livingEntity.getEntityId()))) {
                for (Map.Entry<MobSpawnerAbstract, Integer> entry : this.mobSpawnerAbstractIntegerMap.entrySet()) {
                    if (((Integer)entry.getValue()).intValue() == livingEntity.getEntityId()) {
                        this.mobSpawnerAbstractIntegerMap.put(entry.getKey(), Integer.valueOf(respawned.getEntityId()));
                        return;
                    }
                }
            } else if (this.naturalSpawnStacks.containsValue(Integer.valueOf(livingEntity.getEntityId()))) {
                for (Map.Entry<CoordinatePair, Integer> entry : this.naturalSpawnStacks.column(livingEntity.getType()).entrySet()) {
                    if (((Integer)entry.getValue()).intValue() == livingEntity.getEntityId()) {
                        this.naturalSpawnStacks.put(entry.getKey(), respawned.getType(), Integer.valueOf(respawned.getEntityId()));
                        return;
                    }
                }
            }
        } else {
            this.naturalSpawnStacks.values().remove(Integer.valueOf(livingEntity.getEntityId()));
            this.mobSpawnerAbstractIntegerMap.values().remove(Integer.valueOf(livingEntity.getEntityId()));
        }
    }

    private int getStackedQuantity(LivingEntity livingEntity) {
        if (livingEntity == null) {
            return -1;
        }
        String customName = livingEntity.getCustomName();
        if (customName == null || !customName.contains(STACKED_PREFIX)) {
            return -1;
        }
        customName = customName.replace(STACKED_PREFIX, "");
        if (customName == null) {
            return -1;
        }
        customName = ChatColor.stripColor(customName);
        return Ints.tryParse(customName).intValue();
    }

    private boolean stack(LivingEntity tostack, LivingEntity toremove) {
        if (tostack == null || toremove == null || !tostack.isValid() || !toremove.isValid() || toremove.getType() != tostack.getType() || tostack instanceof org.bukkit.entity.MagmaCube || tostack instanceof Slime || tostack instanceof org.bukkit.entity.Villager) {
            return false;
        }
        Integer newStack = Integer.valueOf(1);
        Integer removeStack = Integer.valueOf(1);
        if (hasStack(tostack)) {
            newStack = Integer.valueOf(getStackedQuantity(tostack));
        }
        if (hasStack(toremove)) {
            removeStack = Integer.valueOf(getStackedQuantity(toremove));
        } else if (getStackedQuantity(toremove) == -1 && toremove.getCustomName() != null && toremove.getCustomName().contains(ChatColor.WHITE.toString())) {
            return false;
        }
        toremove.remove();
        tostack.eject();
        toremove.eject();
        setStackedQuantity(tostack, Math.min(150, newStack.intValue() + removeStack.intValue()));
        return true;
    }


    public boolean canRemove(Entity toremove) { return (!(toremove instanceof org.bukkit.entity.MagmaCube) && !(toremove instanceof Slime) && !(toremove instanceof org.bukkit.entity.Villager)); }



    private boolean hasStack(LivingEntity livingEntity) { return (getStackedQuantity(livingEntity) != -1); }


    private void setStackedQuantity(LivingEntity livingEntity, int quantity) {
        livingEntity.eject();
        livingEntity.setPassenger(null);
        if (quantity <= 1) {
            livingEntity.setCustomName(null);
        } else {
            livingEntity.setCustomName(STACKED_PREFIX + quantity);
            livingEntity.setCustomNameVisible(false);
        }
    }
}

