package com.dHCF.util.cuboid;

import org.bukkit.block.BlockFace;

public enum CuboidDirection {
    NORTH, EAST, SOUTH, WEST, UP, DOWN, HORIZONTAL, VERTICAL, BOTH, UNKNOWN;

    public CuboidDirection opposite() {
        switch (ordinal()) {
            case 1:
                return SOUTH;

            case 2:
                return WEST;

            case 3:
                return NORTH;

            case 4:
                return EAST;

            case 5:
                return VERTICAL;

            case 6:
                return HORIZONTAL;

            case 7:
                return DOWN;

            case 8:
                return UP;

            case 9:
                return BOTH;
        }

        return UNKNOWN;
    }



    public BlockFace toBukkitDirection() {
        switch (ordinal()) {
            case 1:
                return BlockFace.NORTH;

            case 2:
                return BlockFace.EAST;

            case 3:
                return BlockFace.SOUTH;

            case 4:
                return BlockFace.WEST;

            case 5:
                return null;

            case 6:
                return null;

            case 7:
                return BlockFace.UP;

            case 8:
                return BlockFace.DOWN;

            case 9:
                return null;
        }

        return null;
    }
}

