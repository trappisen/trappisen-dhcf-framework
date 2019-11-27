package com.dHCF.framework;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class ProtocolHook
{
    public static void hook(BasePlugin basePlugin) {}

    private static ItemStack convert(ItemStack origin) {
        if (origin != null && origin.getType() != Material.AIR) {
            switch (origin.getType().ordinal())
            { case 1:
                case 2:
                    if (origin.getDurability() > 0) {
                        origin.setDurability((short)0);
                    }












                    return origin;case 3: origin.setType(Material.BOOK); return origin; }  origin.getEnchantments().keySet().forEach(origin::removeEnchantment); return origin;
        }
        return origin;
    }


    private static final ItemStack AIR = new ItemStack(Material.AIR, 1);
}
