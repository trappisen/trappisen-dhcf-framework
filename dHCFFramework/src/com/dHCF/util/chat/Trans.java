package com.dHCF.util.chat;

import net.minecraft.server.v1_8_R3.ChatHoverable;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Trans extends ChatMessage {
    public static Trans fromItemStack(ItemStack stack) { return ChatUtil.fromItemStack(stack); }



    public Trans() { super("", new Object[0]); }



    public Trans(String string, Object... objects) { super(string, objects); }



    public IChatBaseComponent f() { return h(); }



    public Trans append(Object object) { return append(String.valueOf(object)); }



    public Trans append(String text) { return (Trans)a(text); }



    public Trans append(IChatBaseComponent node) { return (Trans)addSibling(node); }


    public Trans append(IChatBaseComponent... nodes) {
        for (IChatBaseComponent node : nodes) {
            addSibling(node);
        }
        return this;
    }


    public Trans appendItem(ItemStack stack) { return append(ChatUtil.fromItemStack(stack)); }



    public Trans localText(ItemStack stack) { return append(ChatUtil.localFromItem(stack)); }


    public Trans setBold(boolean bold) {
        getChatModifier().setBold(Boolean.valueOf(bold));
        return this;
    }

    public Trans setItalic(boolean italic) {
        getChatModifier().setItalic(Boolean.valueOf(italic));
        return this;
    }

    public Trans setUnderline(boolean underline) {
        getChatModifier().setUnderline(Boolean.valueOf(underline));
        return this;
    }

    public Trans setRandom(boolean random) {
        getChatModifier().setRandom(Boolean.valueOf(random));
        return this;
    }

    public Trans setStrikethrough(boolean strikethrough) {
        getChatModifier().setStrikethrough(Boolean.valueOf(strikethrough));
        return this;
    }

    public Trans setColor(ChatColor color) {
        getChatModifier().setColor(EnumChatFormat.valueOf(color.name()));
        return this;
    }

    public Trans setClick(ClickAction action, String value) {
        getChatModifier().setChatClickable(new ChatClickable(action.getNMS(), value));
        return this;
    }

    public Trans setHover(HoverAction action, IChatBaseComponent value) {
        getChatModifier().a(new ChatHoverable(action.getNMS(), value));
        return this;
    }


    public Trans setHoverText(String text) { return setHover(HoverAction.SHOW_TEXT, new Text(text)); }


    public Trans reset() {
        ChatUtil.reset(this);
        return this;
    }


    public String toRawText() { return c(); }



    public void send(CommandSender sender) { ChatUtil.send(sender, this); }
}

