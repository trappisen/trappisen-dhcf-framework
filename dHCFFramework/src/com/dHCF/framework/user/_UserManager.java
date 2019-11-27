package com.dHCF.framework.user;

import com.avaje.ebean.Query;
import com.dHCF.framework.BasePlugin;
import com.dHCF.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

@Deprecated
public abstract class _UserManager implements Listener {
    private final ConsoleUser console;
    private final BasePlugin plugin;

    public _UserManager(BasePlugin plugin) {
        this.onlinePlayers = new ConcurrentHashMap();
        this.participators = new ConcurrentHashMap();



        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ServerParticipator participator = (ServerParticipator)this.participators.get(ConsoleUser.CONSOLE_UUID);
        if (participator != null) {
            this.console = (ConsoleUser)participator;
        } else {
            this.participators.put(ConsoleUser.CONSOLE_UUID, this.console = new ConsoleUser());
        }
        if (BasePlugin.isMongo()) {
            Query<ServerParticipator> serverParticipators = (Query<ServerParticipator>) plugin.getDatastore().find(ServerParticipator.class);
            Iterator<ServerParticipator> iterator = serverParticipators.iterator();
            while (iterator.hasNext()) {
                try {
                    ServerParticipator serverParticipator = (ServerParticipator)iterator.next();
                    this.participators.put(serverParticipator.getUniqueId(), serverParticipator);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            this.userConfig = new Config(plugin, "users");
            for (String uuid : this.userConfig.getKeys(false)) {
                UUID uid = UUID.fromString(uuid);
                this.participators.put(uid, (ServerParticipator)this.userConfig.get(uuid));
            }
        }
    }
    private final ConcurrentMap<UUID, ServerParticipator> onlinePlayers; private final ConcurrentMap<UUID, ServerParticipator> participators; private Config userConfig;

    public ConsoleUser getConsole() { return this.console; }



    public Map<UUID, ServerParticipator> getParticipators() { return this.participators; }



    public ServerParticipator getParticipator(CommandSender sender) { return (sender instanceof Player) ? getParticipator(((Player)sender).getUniqueId()) : ((sender instanceof org.bukkit.command.ConsoleCommandSender) ? this.console : null); }


    public void joinPlayer(Player player) {
        BaseUser baseUser = getUser(player.getUniqueId());
        if (baseUser.getUniqueId() == null) {
            baseUser.setUniqueId(player.getUniqueId());
        }
        this.onlinePlayers.put(baseUser.getUniqueId(), baseUser);
    }


    public void quitPlayer(Player player) { this.onlinePlayers.remove(player.getUniqueId()); }


    public ServerParticipator getParticipator(UUID uuid) {
        ServerParticipator serverParticipator = (ServerParticipator)this.onlinePlayers.get(uuid);
        if (serverParticipator == null) {
            serverParticipator = (ServerParticipator)this.participators.get(uuid);
        }
        return (serverParticipator == null) ? insertAndReturn(uuid) : serverParticipator;
    }

    public BaseUser insertAndReturn(UUID uuid) {
        this.plugin.getLogger().log(Level.INFO, "Created new user " + uuid.toString());
        BaseUser serverParticipator;
        this.participators.put(uuid, serverParticipator = new BaseUser());
        serverParticipator.update();
        return serverParticipator;
    }


    public BaseUser getUser(UUID uuid) { return (BaseUser)getParticipator(uuid); }


    public void mongoFetch(UUID uuid) {
        ServerParticipator serverParticipator = (ServerParticipator)((Query)this.plugin.getDatastore().find(ServerParticipator.class).field("uniqueId").equal(uuid)).get();
        if (serverParticipator != null) {
            ServerParticipator old;
            if ((old = (ServerParticipator)this.participators.put(uuid, serverParticipator)) != null) {
                old.merge(serverParticipator);
            }
            if (this.onlinePlayers.remove(uuid) != null && Bukkit.getPlayer(uuid) != null)
                this.onlinePlayers.put(uuid, serverParticipator);
        }
    }
}
