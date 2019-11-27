package com.dHCF.framework.event;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class AnvilRepairEvent
        extends Event implements Cancellable {
    private static HandlerList handlerList = new HandlerList();
    private final HumanEntity humanEntity;
    private final AnvilInventory anvilInventory;

    public AnvilRepairEvent(HumanEntity humanEntity, AnvilInventory anvilInventory, ItemStack result) {
        this.cancelled = false;


        this.humanEntity = humanEntity;
        this.anvilInventory = anvilInventory;
        this.result = result;
    }
    private ItemStack result; private boolean cancelled;

    public ItemStack getResult() { return this.result; }



    public void setResult(ItemStack result) { this.result = result; }



    public HumanEntity getHumanEntity() { return this.humanEntity; }



    public AnvilInventory getAnvilInventory() { return this.anvilInventory; }



    public boolean isCancelled() { return this.cancelled; }



    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }



    public HandlerList getHandlers() { return handlerList; }



    public static HandlerList getHandlerList() { return handlerList; }
}

