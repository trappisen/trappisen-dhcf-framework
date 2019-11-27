package com.dHCF.framework;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.dHCF.framework.announcement.AnnouncementManager;
import com.dHCF.framework.announcement.MongoAnnouncementManager;
import com.dHCF.framework.command.CommandManager;
import com.dHCF.framework.command.SimpleCommandManager;
import com.dHCF.framework.command.module.ChatModule;
import com.dHCF.framework.command.module.EssentialModule;
import com.dHCF.framework.command.module.InventoryModule;
import com.dHCF.framework.command.module.TeleportModule;
import com.dHCF.framework.listener.ChatListener;
import com.dHCF.framework.listener.ColouredSignListener;
import com.dHCF.framework.listener.DecreasedLagListener;
import com.dHCF.framework.listener.EventListener;
import com.dHCF.framework.listener.JoinListener;
import com.dHCF.framework.listener.MobstackListener;
import com.dHCF.framework.listener.MoveByBlockEvent;
import com.dHCF.framework.listener.NameVerifyListener;
import com.dHCF.framework.listener.PlayerLimitListener;
import com.dHCF.framework.listener.QuitListener;
import com.dHCF.framework.listener.ServerSecurityListener;
import com.dHCF.framework.listener.VanishListener;
import com.dHCF.framework.server.dHCFServer;
import com.dHCF.framework.server.ServerSettings;
import com.dHCF.framework.user.UserManager;
import com.dHCF.framework.user.mongo.CursorThread;
import com.dHCF.framework.user.mongo.MongoUserManager;
import com.dHCF.framework.user.yaml.YamlUserManager;
import com.dHCF.framework.warp.FlatFileWarpManager;
import com.dHCF.framework.warp.WarpManager;
import com.dHCF.util.RandomUtils;
import com.dHCF.util.SignHandler;
import com.dHCF.util.buycraft.BuycraftAPI;
import com.dHCF.util.chat.Lang;
import com.dHCF.util.itemdb.ItemDb;
import com.dHCF.util.itemdb.SimpleItemDb;
import com.dHCF.util.messgener.GlobalMessager;
import com.dHCF.util.morphia.CustomAllowMapper;
import com.dHCF.util.morphia.CustomObjectFactory;
import com.dHCF.util.nms.NMSProvider;
import com.dHCF.util.nms.NMSVersionProvider;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;








public class BasePlugin
        extends JavaPlugin
{
    public static BasePlugin plugin;
    public static boolean MONGO;
    public static boolean PRACTICE;
    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null; private static Thread MAIN_THREAD; public BukkitRunnable clearEntityHandler;
    private dHCFServer dHCFServer;
    private AnnouncementManager announcementManager;
    private ItemDb itemDb;

    public static BasePlugin getPlugin() { return plugin; }



    public static boolean isMongo() { return MONGO; }



    public static Chat getChat() { return chat; }



    public static Economy getEconomy() { return economy; }



    public static Thread getMainThread() { return MAIN_THREAD; }



    public static Permission getPermission() { return permission; }
























    private Random random = new Random(); private RandomUtils randomUtils; private CommandManager commandManager; private ServerHandler serverHandler; private SignHandler signHandler; private WarpManager warpManager; private CursorThread cursorThread; private ServerAddress serverAddress;
    private GlobalMessager globalMessager;

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = (Permission)permissionProvider.getProvider();
        }
        return (permission != null);
    }
    private MongoCredential mongoCredential; private String database; private MongoClient mongoClient; private Morphia morphia; private Datastore datastore; private NMSProvider nmsProvider; private BuycraftAPI buycraftAPI; public UserManager userManager;
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = (Chat)chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = (Economy)economyProvider.getProvider();
        }

        return (economy != null);
    }

    public void onEnable() {
        BasePlugin.plugin = this;
        MAIN_THREAD = Thread.currentThread();
        ConfigurationSerialization.registerClass(com.dHCF.framework.warp.Warp.class);
        ConfigurationSerialization.registerClass(com.dHCF.framework.user.ServerParticipator.class);
        ConfigurationSerialization.registerClass(com.dHCF.framework.user.BaseUser.class);
        ConfigurationSerialization.registerClass(com.dHCF.framework.user.ConsoleUser.class);
        ConfigurationSerialization.registerClass(com.dHCF.framework.user.util.NameHistory.class);
        ConfigurationSerialization.registerClass(com.dHCF.util.PersistableLocation.class);
        ConfigurationSerialization.registerClass(com.dHCF.util.cuboid.Cuboid.class);
        ConfigurationSerialization.registerClass(com.dHCF.util.cuboid.NamedCuboid.class);
        ConfigurationSerialization.registerClass(com.dHCF.framework.announcement.Announcement.class);
        setupMongo();
        if (isMongo() && ServerSettings.HASNAME) {
            this.dHCFServer = new dHCFServer(this);
        }
        registerManagers();
        registerCommands();
        registerListeners();
        Plugin plugin = getServer().getPluginManager().getPlugin("ProtocolLib");
        if (plugin != null && plugin.isEnabled()) {
            try {
                ProtocolHook.hook(this);
            } catch (Exception var3) {
                getLogger().severe("Error hooking into ProtocolLib from Base.");
                var3.printStackTrace();
            }
        }
        (new BukkitRunnable() {
            public void run() {
                BasePlugin.this.setupPermissions();
                BasePlugin.this.setupChat();
                BasePlugin.this.setupEconomy();
            }
        }).runTask(plugin);
        (new BukkitRunnable() {
            public void run() {
                BasePlugin.this.saveData();
            }
        }).runTaskTimerAsynchronously(plugin, 400L, 6000L);
    }





















    public void onLoad() {
        File extraConfig = new File(getDataFolder(), "server.yml");
        if (extraConfig.exists()) {
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            try {
                yamlConfiguration.load(extraConfig);
            } catch (IOException|org.bukkit.configuration.InvalidConfigurationException e) {
                e.printStackTrace();
            }
            BaseConstants.load(yamlConfiguration);
        }
        MONGO = getConfig().getBoolean("mongo.enabled", false);
        if (MONGO) {
            String host = getConfig().getString("mongo.host");
            int port = getConfig().getInt("mongo.port");
            this.database = getConfig().getString("mongo.database");
            boolean auth = getConfig().getBoolean("mongo.auth", false);
            if (auth) {
                String user = getConfig().getString("mongo.user");
                String passwd = getConfig().getString("mongo.passwd");
                this.mongoCredential = MongoCredential.createCredential(user, this.database, passwd.toCharArray());
            }
            this.serverAddress = new ServerAddress(host, port);
            getLogger().info("Using MongoDB");
        }
        PRACTICE = getConfig().getBoolean("practice", false);
        if (PRACTICE) {
            getLogger().info("Using Practice Core");
        }
    }

    public void setupMongo() {
        if (isMongo()) {
            try {
                this.mongoClient = (this.mongoCredential == null) ? new MongoClient(this.serverAddress) : new MongoClient(this.serverAddress, Collections.singletonList(this.mongoCredential));
                this.morphia = new Morphia();
                this.morphia.getMapper().getOptions().setObjectFactory(new CustomObjectFactory(getClassLoader()));
                this.morphia.getMapper().getOptions().setReferenceMapper(new CustomAllowMapper(this.morphia.getMapper().getOptions().getReferenceMapper(), getLogger()));
                this.morphia.getMapper().getOptions().setEmbeddedMapper(new CustomAllowMapper(this.morphia.getMapper().getOptions().getEmbeddedMapper(), getLogger()));
                this.morphia.getMapper().getOptions().setDefaultMapper(new CustomAllowMapper(this.morphia.getMapper().getOptions().getDefaultMapper(), getLogger()));
                this.morphia.getMapper().getOptions().setStoreEmpties(true);
                this.morphia.map(new Class[] { com.dHCF.framework.user.ServerParticipator.class });
                this.morphia.map(new Class[] { com.dHCF.framework.user.BaseUser.class });
                this.morphia.map(new Class[] { com.dHCF.framework.user.ConsoleUser.class });
                this.morphia.map(new Class[] { com.dHCF.framework.user.util.NameHistory.class });
                this.datastore = this.morphia.createDatastore(this.mongoClient, this.database);
                this.datastore.ensureIndexes();
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Failed to connect to mongoDB ", e);
                Bukkit.shutdown();
                throw new RuntimeException("Failed to connect to mongoDB ", e);
            }
        }
    }

    public void onDisable() {
        super.onDisable();
        this.serverHandler.saveServerData();
        this.signHandler.cancelTasks(null);
        this.warpManager.saveWarpData();
        if (isMongo()) {
            if (this.dHCFServer != null) {
                this.dHCFServer.close();
            }
            this.mongoClient.close();
        }
        plugin = null;
    }

    public void saveData() {
        this.serverHandler.saveServerData();
        this.warpManager.saveWarpData();
    }

    private void registerManagers() {
        this.nmsProvider = NMSVersionProvider.getProvider();
        this.buycraftAPI = new BuycraftAPI(getConfig().getString("buycraft-secret", ""));
        this.globalMessager = new GlobalMessager(this);

        this.randomUtils = new RandomUtils();
        this.serverHandler = new ServerHandler(this);
        this.signHandler = new SignHandler(this);
        this.userManager = isMongo() ? new MongoUserManager(this) : new YamlUserManager(this);
        if (isMongo()) {
            this.cursorThread = new CursorThread(this);
            this.announcementManager = new MongoAnnouncementManager(this);
        }
        this.warpManager = new FlatFileWarpManager(this);
        this.itemDb = new SimpleItemDb(this);
        try {
            Lang.initialize("en_US");
        } catch (Throwable var2) {
            var2.printStackTrace();
        }
    }


    public ServerAddress getServerAddress() { return this.serverAddress; }



    public MongoCredential getMongoCredential() { return this.mongoCredential; }



    public MongoClient getMongoClient() { return this.mongoClient; }



    public Morphia getMorphia() { return this.morphia; }


    private void registerCommands() {
        (this.commandManager = new SimpleCommandManager(this)).registerAll(new ChatModule(this));
        this.commandManager.registerAll(new EssentialModule(this));
        this.commandManager.registerAll(new InventoryModule(this));
        this.commandManager.registerAll(new TeleportModule(this));
        (new BukkitRunnable() {
            public void run() {
                FieldAccessor fieldAccessor = Accessors.getFieldAccessor(org.bukkit.plugin.SimplePluginManager.class, SimpleCommandMap.class, true);
                SimpleCommandMap commandMap = (SimpleCommandMap)fieldAccessor.get(Bukkit.getPluginManager());
                for (Command command : commandMap.getCommands()) {
                    command.setPermissionMessage(SimpleCommandManager.PERMISSION_MESSAGE);
                }
            }
        }).runTaskLater(this, 10L);
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        if (!PRACTICE) {
            manager.registerEvents(new VanishListener(this), this);
            manager.registerEvents(new MobstackListener(this), this);
        }
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new ColouredSignListener(), this);
        manager.registerEvents(new DecreasedLagListener(this), this);
        manager.registerEvents(new JoinListener(this), this);
        manager.registerEvents(new MoveByBlockEvent(), this);
        manager.registerEvents(new NameVerifyListener(this), this);
        manager.registerEvents(new PlayerLimitListener(), this);
        manager.registerEvents(new ServerSecurityListener(), this);
        manager.registerEvents(new QuitListener(this), this);
        manager.registerEvents(new EventListener(this), this);
    }


    public RandomUtils getRandomUtils() { return this.randomUtils; }



    public Random getRandom() { return this.random; }



    public CommandManager getCommandManager() { return this.commandManager; }



    public ItemDb getItemDb() { return this.itemDb; }



    public ServerHandler getServerHandler() { return this.serverHandler; }



    public SignHandler getSignHandler() { return this.signHandler; }



    public UserManager getUserManager() { return this.userManager; }



    public WarpManager getWarpManager() { return this.warpManager; }



    public Datastore getDatastore() { return this.datastore; }



    public CursorThread getCursorThread() { return this.cursorThread; }



    public GlobalMessager getGlobalMessager() { return this.globalMessager; }



    public NMSProvider getNmsProvider() { return this.nmsProvider; }



    public String getDatabaseName() { return this.database; }



    public dHCFServer getdHCFServer() { return this.dHCFServer; }



    public void setFaithfulServer(dHCFServer faithfulServer) { this.dHCFServer = dHCFServer; }



    public AnnouncementManager getAnnouncementManager() { return this.announcementManager; }



    public BuycraftAPI getBuycraftAPI() { return this.buycraftAPI; }
}
