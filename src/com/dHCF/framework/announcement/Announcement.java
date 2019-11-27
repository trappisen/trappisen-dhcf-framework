package com.dHCF.framework.announcement;

import com.dHCF.framework.BasePlugin;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Announcement
        implements ConfigurationSerializable {
    private final String name;
    private String[] lines;
    private int delay;

    public Announcement(String name, String[] lines, int delay) {
        this.name = name;
        this.lines = lines;
        this.delay = delay;
    }

    public Announcement(Map map) {
        this.name = (String)map.get("name");
        List<String> lines = (List)map.get("lines");
        this.lines = (String[])lines.toArray(new String[lines.size()]);
        this.delay = ((Integer)map.get("delay")).intValue();
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", this.name);
        map.put("lines", BasePlugin.isMongo() ? Arrays.asList(this.lines) : this.lines);
        map.put("delay", Integer.valueOf(this.delay));
        return map;
    }


    public String getName() { return this.name; }



    public String[] getLines() { return this.lines; }



    public int getDelay() { return this.delay; }



    public void setLines(String[] lines) { this.lines = lines; }



    public void setDelay(int delay) { this.delay = delay; }



    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Announcement that = (Announcement)o;

        return this.name.equals(that.name);
    }



    public int hashCode() { return this.name.hashCode(); }
}

