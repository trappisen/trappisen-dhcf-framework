package com.dHCF.util;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config
        extends YamlConfiguration
{
    private final String fileName;
    private final JavaPlugin plugin;

    public Config(JavaPlugin plugin, String fileName) { this(plugin, fileName, ".yml"); }


    public Config(JavaPlugin plugin, String fileName, String fileExtension) {
        this.plugin = plugin;
        this.fileName = fileName + (fileName.endsWith(fileExtension) ? "" : fileExtension);
        createFile();
    }


    public String getFileName() { return this.fileName; }



    public JavaPlugin getPlugin() { return this.plugin; }


    private void createFile() {
        File folder = this.plugin.getDataFolder();
        try {
            File ex = new File(folder, this.fileName);
            if (!ex.exists()) {
                if (this.plugin.getResource(this.fileName) != null) {
                    this.plugin.saveResource(this.fileName, false);
                } else {
                    save(ex);
                }
            } else {
                load(ex);
                save(ex);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    public void save() {
        File folder = this.plugin.getDataFolder();
        try {
            save(new File(folder, this.fileName));
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Config)) {
            return false;
        }
        Config config = (Config)o;
        if (this.fileName != null) {
            if (this.fileName.equals(config.fileName)) {
                return (this.plugin != null) ? this.plugin.equals(config.plugin) : ((config.plugin == null) ? 1 : 0);
            }
        } else if (config.fileName == null) {
            return (this.plugin != null) ? this.plugin.equals(config.plugin) : ((config.plugin == null) ? 1 : 0);
        }
        return false;
    }

    public int hashCode() {
        result = (this.fileName != null) ? this.fileName.hashCode() : 0;
        return 31 * result + ((this.plugin != null) ? this.plugin.hashCode() : 0);
    }
}
