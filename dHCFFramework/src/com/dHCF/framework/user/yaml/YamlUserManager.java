package com.dHCF.framework.user.yaml;

import com.dHCF.framework.BasePlugin;
import com.dHCF.framework.user.BaseUser;
import com.dHCF.framework.user.UserManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class YamlUserManager
        extends UserManager
{
    private final File userDir;

    public YamlUserManager(BasePlugin plugin) {
        super(plugin);
        this.userDir = new File(plugin.getDataFolder(), "users");
        if (!this.userDir.exists()) {
            this.userDir.mkdir();
        }
    }


    private File getFile(UUID uuid) { return new File(this.userDir, uuid.toString() + ".yml"); }


    public void save(BaseUser baseUser) {
        if (baseUser.getUniqueId() == null) {
            return;
        }
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("user", baseUser);
        try {
            yamlConfiguration.save(getFile(baseUser.getUniqueId()));
        } catch (IOException exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save user ", exception);
        }
    }

    public BaseUser load(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        File file = getFile(uuid);
        if (file.exists()) {
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            try {
                yamlConfiguration.load(file);
            } catch (IOException|org.bukkit.configuration.InvalidConfigurationException exception) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to load user ", exception);
            }
            if (yamlConfiguration.contains("user")) {
                return (BaseUser)yamlConfiguration.get("user");
            }
        }
        return null;
    }
}
