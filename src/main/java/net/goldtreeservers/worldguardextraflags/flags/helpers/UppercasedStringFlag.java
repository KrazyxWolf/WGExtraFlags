package net.goldtreeservers.worldguardextraflags.flags.helpers;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;

public class UppercasedStringFlag extends Flag<String> {

    public UppercasedStringFlag(String name, RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    public UppercasedStringFlag(String name) {
        super(name);
    }

    public String parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        input = input.trim();
        return input.toUpperCase();
    }

    public String unmarshal(Object o) {
        return String.valueOf(o);
    }

    public Object marshal(String o) {
        return o;
    }
}