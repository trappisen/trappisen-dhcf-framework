package com.dHCF.framework.listener;

import com.dHCF.framework.BasePlugin;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class LoginListener
        implements Listener {
    BasePlugin plugin;
    ArrayList players;

    public LoginListener(BasePlugin plugin) {
        this.plugin = plugin;
        this.players = new ArrayList();
    }
}
