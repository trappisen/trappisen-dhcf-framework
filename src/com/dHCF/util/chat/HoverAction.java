package com.dHCF.util.chat;

import net.minecraft.server.v1_8_R3.EnumHoverAction;

public static enum HoverAction {
    SHOW_TEXT(EnumHoverAction.SHOW_TEXT), SHOW_ITEM(EnumHoverAction.SHOW_ITEM), SHOW_ACHIEVEMENT(EnumHoverAction.SHOW_ACHIEVEMENT);

    private final EnumHoverAction hoverAction;


    HoverAction(EnumHoverAction hoverAction) { this.hoverAction = hoverAction; }



    public EnumHoverAction getNMS() { return this.hoverAction; }
}

