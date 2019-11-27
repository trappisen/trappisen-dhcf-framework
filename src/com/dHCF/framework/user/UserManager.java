package com.dHCF.framework.user;

import com.dHCF.framework.BasePlugin;
import com.faithfulmc.framework.user.event.UserLoadEvent;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class UserManager
{
    protected final ConsoleUser console;

    public UserManager(BasePlugin plugin) {
        this.onlinePlayers = new ConcurrentHashMap();


        this.plugin = plugin;
        this.onlinePlayers.put(ConsoleUser.CONSOLE_UUID, this.console = new ConsoleUser());
    }
    protected final BasePlugin plugin; protected final ConcurrentMap<UUID, ServerParticipator> onlinePlayers;

    public ConsoleUser getConsole() { return this.console; }


    public ServerParticipator getParticipator(UUID uuid) {
        ServerParticipator serverParticipator = (ServerParticipator)this.onlinePlayers.get(uuid);
        if (serverParticipator == null) {
            if (Thread.currentThread() == BasePlugin.getMainThread()) {
                throw new NullPointerException("Cannot establish connection from main thread");
            }
            serverParticipator = load(uuid);
        }
        return serverParticipator;
    }


    public ServerParticipator getParticipator(CommandSender sender) { return (sender instanceof org.bukkit.command.ConsoleCommandSender) ? this.console : getParticipator(((Player)sender).getUniqueId()); }


    public BaseUser getUser(UUID uuid) {
        ServerParticipator serverParticipator;
        return ((serverParticipator = getParticipator(uuid)) == null || !(serverParticipator instanceof BaseUser)) ? null : (BaseUser)serverParticipator;
    }

    public void joinTask(Player player) {
        UUID uuid = player.getUniqueId();
        this.onlinePlayers.put(uuid, new BaseUser(player.getUniqueId(), false));
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            BaseUser baseUser = load(paramUUID);
            if (baseUser == null) {
                baseUser = new BaseUser(paramUUID, paramPlayer.getName());
                save(baseUser);
            }
            if (paramPlayer.isOnline() && this.onlinePlayers.containsKey(paramUUID)) {
                BaseUser old = (BaseUser)this.onlinePlayers.put(paramUUID, baseUser);
                if (old != null) {
                    old.merge(baseUser);
                    if (baseUser.isOnline()) {
                        baseUser.setLastSeen(System.currentTimeMillis());
                    }
                }
                Bukkit.getPluginManager().callEvent(new UserLoadEvent(baseUser, paramPlayer));
            }
        }5L);
    }

    public void quitTask(Player player) {
        UUID uuid = player.getUniqueId();
        ServerParticipator serverParticipator = (ServerParticipator)this.onlinePlayers.remove(uuid);
        if (serverParticipator != null && serverParticipator instanceof BaseUser)
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () ->
                    save((BaseUser)paramServerParticipator));
    }

    public abstract void save(BaseUser paramBaseUser);

    public abstract BaseUser load(UUID paramUUID);
}
