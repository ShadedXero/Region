package com.mortisdevelopment.regionplugin.region;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Location;

@Getter @Setter
public class WandSelection {

    private Location originLocation;
    private Location endLocation;

    public WandSelection() {
    }

    public WandSelection(Location originLocation, Location endLocation) {
        this.originLocation = originLocation;
        this.endLocation = endLocation;
    }

    public boolean isValid() {
        return originLocation != null && endLocation != null;
    }

    public void apply(Region region) {
        region.setOriginLocation(originLocation);
        region.setEndLocation(endLocation);
    }
}
