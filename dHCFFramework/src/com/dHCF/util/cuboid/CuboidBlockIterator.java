package com.dHCF.util.cuboid;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Iterator;

public class CuboidBlockIterator
        extends Object implements Iterator<Block> {
    private final World world;
    private final int baseX;
    private final int baseY;
    private final int baseZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private int x;
    private int y;
    private int z;

    public CuboidBlockIterator(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;
        this.baseX = x1;
        this.baseY = y1;
        this.baseZ = z1;
        this.sizeX = Math.abs(x2 - x1) + 1;
        this.sizeY = Math.abs(y2 - y1) + 1;
        this.sizeZ = Math.abs(z2 - z1) + 1;
        boolean x3 = false;
        this.z = 0;
        this.y = 0;
        this.x = 0;
    }



    public boolean hasNext() { return (this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ); }



    public Block next() {
        Block block = this.world.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);

        this.x = 0;
        if (++this.x >= this.sizeX && ++this.y >= this.sizeY) {
            this.y = 0;
            this.z++;
        }

        return block;
    }

    public void remove() {}
}

