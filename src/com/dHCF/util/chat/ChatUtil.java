package com.dHCF.util.chat;

import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class ChatUtil {
    public static String getName(ItemStack stack) {
        if (stack.tag != null && stack.tag.hasKeyOfType("display", 10)) {
            NBTTagCompound nbttagcompound = stack.tag.getCompound("display");
            if (nbttagcompound.hasKeyOfType("Name", 8)) {
                return nbttagcompound.getString("Name");
            }
        }
        return stack.getItem().a(stack) + ".name";
    }

    public static Trans localFromItem(ItemStack stack) {
        if (stack.getType() == Material.CAKE && stack.getData().getData() == 0) {
            Potion potion = Potion.fromItemStack(stack);
            if (potion != null) {
                PotionType type = potion.getType();
                if (type != null && type != PotionType.WATER) {
                    String effectName = (potion.isSplash() ? "Splash " : "") + WordUtils.capitalizeFully(type.name().replace('_', ' ')) + " L" + potion.getLevel();
                    return fromItemStack(stack).append(" of " + effectName);
                }
            }
        }
        return fromItemStack(stack);
    }

    public static Trans fromItemStack(ItemStack stack) {
        ItemStack nms = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = new NBTTagCompound();
        nms.save(tag);
        return (new Trans(getName(nms), new Object[0])).setColor(ChatColor.getByChar((nms.w()).e.getChar())).setHover(HoverAction.SHOW_ITEM, new ChatComponentText(tag.toString()));
    }

    public static void reset(IChatBaseComponent text) {
        ChatModifier modifier = text.getChatModifier();
        modifier.a((ChatHoverable)null);
        modifier.setChatClickable((ChatClickable)null);
        modifier.setBold(Boolean.valueOf(false));
        modifier.setColor(EnumChatFormat.RESET);
        modifier.setItalic(Boolean.valueOf(false));
        modifier.setRandom(Boolean.valueOf(false));
        modifier.setStrikethrough(Boolean.valueOf(false));
        modifier.setUnderline(Boolean.valueOf(false));
    }

    public static void send(CommandSender sender, IChatBaseComponent text) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            PacketPlayOutChat packet = new PacketPlayOutChat(text, true);
            EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
            entityPlayer.playerConnection.sendPacket(packet);
        } else {
            sender.sendMessage(text.c());
        }
    }
}

