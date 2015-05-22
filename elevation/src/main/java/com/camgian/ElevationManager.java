package com.camgian;

import android.content.Context;


/**
 * Created by npalmer on 5/22/2015.
 */
public class ElevationManager {
    public static final int DEFAULT_ELEVATION_IF_NOT_AVAILABLE = 0;
    TileCache tileCache;

    public ElevationManager(Context appContext) {
        tileCache = new TileCache(appContext);
    }

    public Integer getElevation(Coordinate coord) {
        Tile tile = tileCache.getTileContaining(coord);

        if (tile != null) {
            // Found the correct tile
            return tile.getElevationAtPoint(coord);
        }

        return DEFAULT_ELEVATION_IF_NOT_AVAILABLE;
    }

    public Integer getElevation(Double lat, Double lon) {
        return getElevation(new Coordinate(lat,lon));
    }


}
