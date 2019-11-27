package com.dHCF.framework;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;

public enum StaffPriority {
    OWNER(6), HEADADMIN(5), STAFFMANAGER(4), ADMIN(3), MODERATOR(2), TRIAL(1), NONE(0);

    private final int priorityLevel;
    private static final ImmutableMap<Integer, StaffPriority> BY_ID;

    public static StaffPriority of(int level) { return (StaffPriority)BY_ID.get(Integer.valueOf(level)); }


    public static StaffPriority of(Player player) {
        for (StaffPriority staffPriority : values()) {
            if (player.hasPermission("staffpriority." + staffPriority.priorityLevel)) {
                return staffPriority;
            }
        }
        return NONE;
    }

    private static final ImmutableMap.Builder builder;

    static  {
        builder = new ImmutableMap.Builder();
        for (StaffPriority staffPriority : values()) {
            builder.put(Integer.valueOf(staffPriority.priorityLevel), staffPriority);
        }
        BY_ID = builder.build();
    }




    StaffPriority(int priorityLevel) { this.priorityLevel = priorityLevel; }



    public int getPriorityLevel() { return this.priorityLevel; }



    public boolean isMoreThan(StaffPriority other) { return (this.priorityLevel > other.priorityLevel); }
}
