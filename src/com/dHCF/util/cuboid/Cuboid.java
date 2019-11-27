package com.dHCF.util.cuboid;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Cuboid extends Object implements Iterable<Block>, Cloneable, ConfigurationSerializable {
    private static final int CHUNK_SIZE = 16;
    protected String worldName;
    protected int x1;
    protected int y1;

    public Cuboid(Map<String, Object> map) {
        this.worldName = (String)map.get("worldName");
        this.x1 = ((Integer)map.get("x1")).intValue();
        this.y1 = ((Integer)map.get("y1")).intValue();
        this.z1 = ((Integer)map.get("z1")).intValue();
        this.x2 = ((Integer)map.get("x2")).intValue();
        this.y2 = ((Integer)map.get("y2")).intValue();
        this.z2 = ((Integer)map.get("z2")).intValue();
    }
    protected int z1; protected int x2; protected int y2; protected int z2;
    public Cuboid() {}
    public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) { this(((World)Preconditions.checkNotNull(world)).getName(), x1, y1, z1, x2, y2, z2); }


    private Cuboid(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        Preconditions.checkNotNull(worldName, "World name cannot be null");
        this.worldName = worldName;
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    public Cuboid(Location first, Location second) {
        Preconditions.checkNotNull(first, "Location 1 cannot be null");
        Preconditions.checkNotNull(second, "Location 2 cannot be null");
        Preconditions.checkArgument(first.getWorld().equals(second.getWorld()), "Locations must be on the same world");
        this.worldName = first.getWorld().getName();
        this.x1 = Math.min(first.getBlockX(), second.getBlockX());
        this.y1 = Math.min(first.getBlockY(), second.getBlockY());
        this.z1 = Math.min(first.getBlockZ(), second.getBlockZ());
        this.x2 = Math.max(first.getBlockX(), second.getBlockX());
        this.y2 = Math.max(first.getBlockY(), second.getBlockY());
        this.z2 = Math.max(first.getBlockZ(), second.getBlockZ());
    }


    public Cuboid(Location location) { this(location, location); }



    public Cuboid(Cuboid other) { this(other.getWorld().getName(), other.x1, other.y1, other.z1, other.x2, other.y2, other.z2); }


    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("worldName", this.worldName);
        map.put("x1", Integer.valueOf(this.x1));
        map.put("y1", Integer.valueOf(this.y1));
        map.put("z1", Integer.valueOf(this.z1));
        map.put("x2", Integer.valueOf(this.x2));
        map.put("y2", Integer.valueOf(this.y2));
        map.put("z2", Integer.valueOf(this.z2));
        return map;
    }


    public boolean hasBothPositionsSet() { return (getMinimumPoint() != null && getMaximumPoint() != null); }



    public int getMinimumX() { return Math.min(this.x1, this.x2); }



    public int getMinimumZ() { return Math.min(this.z1, this.z2); }



    public int getMaximumX() { return Math.max(this.x1, this.x2); }



    public int getMaximumZ() { return Math.max(this.z1, this.z2); }


    public List<Vector> edges() {
        Location v1 = getMinimumPoint();
        Location v2 = getMaximumPoint();
        int minX = v1.getBlockX();
        int maxX = v2.getBlockX();
        int minZ = v1.getBlockZ();
        int startX = minZ + 1;
        int maxZ = v2.getBlockZ();
        int capacity = (maxX - minX) * 4 + (maxZ - minZ) * 4;
        capacity += 4;
        List<Vector> result = new ArrayList<Vector>(capacity);
        if (capacity <= 0) {
            return result;
        }
        int minY = v1.getBlockY();
        int maxY = v1.getBlockY();
        for (int z = minX; z <= maxX; z++) {
            result.add(new Vector(z, minY, minZ));
            result.add(new Vector(z, minY, maxZ));
            result.add(new Vector(z, maxY, minZ));
            result.add(new Vector(z, maxY, maxZ));
        }
        for (int z = startX; z < maxZ; z++) {
            result.add(new Vector(minX, minY, z));
            result.add(new Vector(minX, maxY, z));
            result.add(new Vector(maxX, minY, z));
            result.add(new Vector(maxX, maxY, z));
        }
        return result;
    }

    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<Player>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (contains(player)) {
                players.add(player);
            }
        }
        return players;
    }


    public Location getLowerNE() { return new Location(getWorld(), this.x1, this.y1, this.z1); }



    public Location getUpperSW() { return new Location(getWorld(), this.x2, this.y2, this.z2); }


    public Location getCenter() {
        int x1 = this.x2 + 1;
        int y1 = this.y2 + 1;
        int z1 = this.z2 + 1;
        return new Location(getWorld(), this.x1 + (x1 - this.x1) / 2.0D, this.y1 + (y1 - this.y1) / 2.0D, this.z1 + (z1 - this.z1) / 2.0D);
    }


    public String getWorldName() { return this.worldName; }



    public World getWorld() { return Bukkit.getWorld(this.worldName); }



    public int getSizeX() { return this.x2 - this.x1 + 1; }



    public int getSizeY() { return this.y2 - this.y1 + 1; }



    public int getSizeZ() { return this.z2 - this.z1 + 1; }



    public int getX1() { return this.x1; }



    public void setX1(int x1) { this.x1 = x1; }



    public int getY1() { return this.y1; }



    public void setY1(int y1) { this.y1 = y1; }



    public int getZ1() { return this.z1; }



    public void setZ1(int z1) { this.z1 = z1; }



    public int getX2() { return this.x2; }



    public int getY2() { return this.y2; }



    public void setY2(int y2) { this.y2 = y2; }



    public int getZ2() { return this.z2; }


    public Location[] getCornerLocations() {
        Location[] result = new Location[8];
        Block[] cornerBlocks = getCornerBlocks();
        for (int i = 0; i < cornerBlocks.length; i++) {
            result[i] = cornerBlocks[i].getLocation();
        }
        return result;
    }

    public Block[] getCornerBlocks() {
        Block[] result = new Block[8];
        World world = getWorld();
        result[0] = world.getBlockAt(this.x1, this.y1, this.z1);
        result[1] = world.getBlockAt(this.x1, this.y1, this.z2);
        result[2] = world.getBlockAt(this.x1, this.y2, this.z1);
        result[3] = world.getBlockAt(this.x1, this.y2, this.z2);
        result[4] = world.getBlockAt(this.x2, this.y1, this.z1);
        result[5] = world.getBlockAt(this.x2, this.y1, this.z2);
        result[6] = world.getBlockAt(this.x2, this.y2, this.z1);
        result[7] = world.getBlockAt(this.x2, this.y2, this.z2);
        return result;
    }

    public Cuboid expand(CuboidDirection dir, int amount) {
        switch (dir.ordinal()) {
            case 1:
                return new Cuboid(this.worldName, this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2);

            case 2:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2);

            case 3:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2);

            case 4:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount);

            case 5:
                return new Cuboid(this.worldName, this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2);

            case 6:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2);
        }

        throw new IllegalArgumentException("invalid direction " + dir);
    }




    public Cuboid shift(CuboidDirection dir, int amount) { return expand(dir, amount).expand(dir.opposite(), -amount); }


    public Cuboid outset(CuboidDirection dir, int amount) {
        switch (dir.ordinal()) {
            case 7:
                return expand(CuboidDirection.NORTH, amount).expand(CuboidDirection.SOUTH, amount).expand(CuboidDirection.EAST, amount).expand(CuboidDirection.WEST, amount);

            case 8:
                return expand(CuboidDirection.DOWN, amount).expand(CuboidDirection.UP, amount);

            case 9:
                return outset(CuboidDirection.HORIZONTAL, amount).outset(CuboidDirection.VERTICAL, amount);
        }

        throw new IllegalArgumentException("invalid direction " + dir);
    }




    public Cuboid inset(CuboidDirection direction, int amount) { return outset(direction, -amount); }



    public boolean contains(Cuboid cuboid) { return (contains(cuboid.getMinimumPoint()) || contains(cuboid.getMaximumPoint())); }



    public boolean contains(Player player) { return contains(player.getLocation()); }



    public boolean contains(World world, int x, int z) { return ((world == null || getWorld().equals(world)) && x >= this.x1 && x <= this.x2 && z >= this.z1 && z <= this.z2); }



    public boolean contains(int x, int y, int z) { return (x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2); }



    public boolean contains(Block block) { return contains(block.getLocation()); }


    public boolean contains(Location location) {
        if (location != null && this.worldName != null) {
            World world = location.getWorld();
            return (world != null && this.worldName.equals(location.getWorld().getName()) && contains(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
        return false;
    }


    public int getVolume() { return getSizeX() * getSizeY() * getSizeZ(); }


    public int getArea() {
        Location min = getMinimumPoint();
        Location max = getMaximumPoint();
        return (max.getBlockX() - min.getBlockX() + 1) * (max.getBlockZ() - min.getBlockZ() + 1);
    }

    public byte getAverageLightLevel() {
        long total = 0L;
        int count = 0;
        for (Block block : this) {
            if (block.isEmpty()) {
                total += block.getLightLevel();
                count++;
            }
        }
        return (count > 0) ? (byte)(int)(total / count) : 0;
    }


    public Location getMinimumPoint() { return new Location(getWorld(), Math.min(this.x1, this.x2), Math.min(this.y1, this.y2), Math.min(this.z1, this.z2)); }



    public Location getMaximumPoint() { return new Location(getWorld(), Math.max(this.x1, this.x2), Math.max(this.y1, this.y2), Math.max(this.z1, this.z2)); }



    public int getWidth() { return getMaximumPoint().getBlockX() - getMinimumPoint().getBlockX(); }



    public int getHeight() { return getMaximumPoint().getBlockY() - getMinimumPoint().getBlockY(); }



    public int getLength() { return getMaximumPoint().getBlockZ() - getMinimumPoint().getBlockZ(); }



    public Cuboid contract() { return contract(CuboidDirection.DOWN).contract(CuboidDirection.SOUTH).contract(CuboidDirection.EAST).contract(CuboidDirection.UP).contract(CuboidDirection.NORTH).contract(CuboidDirection.WEST); }


    public Cuboid contract(CuboidDirection direction) {
        Cuboid face = getFace(direction.opposite());
        switch (direction.ordinal()) {
            case 1:
                while (face.containsOnly(Material.AIR) && face.x1 > this.x1) {
                    face = face.shift(CuboidDirection.NORTH, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, face.x2, this.y2, this.z2);

            case 2:
                while (face.containsOnly(Material.AIR) && face.x2 < this.x2) {
                    face = face.shift(CuboidDirection.SOUTH, 1);
                }
                return new Cuboid(this.worldName, face.x1, this.y1, this.z1, this.x2, this.y2, this.z2);

            case 3:
                while (face.containsOnly(Material.AIR) && face.z1 > this.z1) {
                    face = face.shift(CuboidDirection.EAST, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, face.z2);

            case 4:
                while (face.containsOnly(Material.AIR) && face.z2 < this.z2) {
                    face = face.shift(CuboidDirection.WEST, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, face.z1, this.x2, this.y2, this.z2);

            case 5:
                while (face.containsOnly(Material.AIR) && face.y1 > this.y1) {
                    face = face.shift(CuboidDirection.DOWN, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, face.y2, this.z2);

            case 6:
                while (face.containsOnly(Material.AIR) && face.y2 < this.y2) {
                    face = face.shift(CuboidDirection.UP, 1);
                }
                return new Cuboid(this.worldName, this.x1, face.y1, this.z1, this.x2, this.y2, this.z2);
        }

        throw new IllegalArgumentException("Invalid direction " + direction);
    }



    public Cuboid getFace(CuboidDirection direction) {
        switch (direction.ordinal()) {
            case 1:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);

            case 2:
                return new Cuboid(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);

            case 3:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);

            case 4:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);

            case 5:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);

            case 6:
                return new Cuboid(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
        }

        throw new IllegalArgumentException("Invalid direction " + direction);
    }



    public boolean containsOnly(Material material) {
        for (Block block : this) {
            if (block.getType() != material) {
                return false;
            }
        }
        return true;
    }

    public Cuboid getBoundingCuboid(Cuboid other) {
        if (other == null) {
            return this;
        }
        int xMin = Math.min(this.x1, other.x1);
        int yMin = Math.min(this.y1, other.y1);
        int zMin = Math.min(this.z1, other.z1);
        int xMax = Math.max(this.x2, other.x2);
        int yMax = Math.max(this.y2, other.y2);
        int zMax = Math.max(this.z2, other.z2);
        return new Cuboid(this.worldName, xMin, yMin, zMin, xMax, yMax, zMax);
    }


    public Block getRelativeBlock(int x, int y, int z) { return getWorld().getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z); }



    public Block getRelativeBlock(World world, int x, int y, int z) { return world.getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z); }


    public List<Chunk> getChunks() {
        World world = getWorld();
        int x1 = this.x1 & 0xFFFFFFF0;
        int x2 = this.x2 & 0xFFFFFFF0;
        int z1 = this.z1 & 0xFFFFFFF0;
        int z2 = this.z2 & 0xFFFFFFF0;
        ArrayList<Chunk> result = new ArrayList<Chunk>(x2 - x1 + 16 + (z2 - z1) * 16);
        for (int x3 = x1; x3 <= x2; x3 += 16) {
            for (int z3 = z1; z3 <= z2; z3 += 16) {
                result.add(world.getChunkAt(x3 >> 4, z3 >> 4));
            }
        }
        return result;
    }



    public Iterator<Block> iterator() { return new CuboidBlockIterator(getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2); }



    public Iterator<?> locationIterator() { return new CuboidLocationIterator(getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2); }


    public Cuboid clone() {
        try {
            return (Cuboid)super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new RuntimeException("this could never happen", var2);
        }
    }



    public String toString() { return "Cuboid: " + this.worldName + ',' + this.x1 + ',' + this.y1 + ',' + this.z1 + "=>" + this.x2 + ',' + this.y2 + ',' + this.z2; }
}

