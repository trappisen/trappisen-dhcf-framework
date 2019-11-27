package com.dHCF.util.cuboid;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class CoordinatePair
{
    private final String worldName;
    private final int x;
    private final int z;

    public CoordinatePair(Block block) { this(block.getWorld(), block.getX(), block.getZ()); }


    public CoordinatePair(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }

    public CoordinatePair(World world, int x, int z) {
        this.worldName = world.getName();
        this.x = x;
        this.z = z;
    }


    public String getWorldName() { return this.worldName; }



    public World getWorld() { return Bukkit.getWorld(this.worldName); }



    public int getX() { return this.x; }



    public int getZ() { return this.z; }



    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoordinatePair)) {
            return false;
        }
        CoordinatePair that = (CoordinatePair)o;
        return (this.x == that.x && this.z == that.z && ((this.worldName != null) ? this.worldName.equals(that.worldName) : (that.worldName == null)));
    }


    public int hashCode() {
        int result = (this.worldName != null) ? this.worldName.hashCode() : 0;
        result = 31 * result + this.x;
        return 31 * result + this.z;
    }
}

