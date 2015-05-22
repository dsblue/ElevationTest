package com.camgian;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.Toast;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by npalmer on 5/22/2015.
 */
public class TileCache {
    private Context appContext;

    private LinkedList<Tile> tiles = new LinkedList<Tile>();

    public TileCache(Context appContext) {
        this.appContext = appContext;
    }

    /**
     *
     * @param coord
     * @return Tile or null
     */
    Tile getTileContaining(Coordinate coord) {
        Iterator<Tile> i = tiles.iterator();

        Tile tile = null;
        while (i.hasNext()) {
            tile = i.next();

            if (tile.containsCoordinate(coord)) {
                return tile;
            }
        }

        // Could not find the tile, so load from disk


        // Still could not find tile, so download
        downloadTile (
                new Coordinate(coord.getLatitude()-0.001,coord.getLongitude()-0.001),
                new Coordinate(coord.getLatitude()+0.001,coord.getLongitude()+0.001));

        return tile;
    }

    private void downloadTile (Coordinate sw, Coordinate ne) {
        DownloadReceiver receiver = new DownloadReceiver(new Handler());

        String boundingBox =
                sw.getLongitude() + "," + sw.getLatitude() + "," + ne.getLongitude() + "," + ne.getLatitude();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("data.worldwind.arc.nasa.gov")
                .appendPath("elev")
                .appendQueryParameter("service", "wms")
                .appendQueryParameter("request", "GetMap")
                .appendQueryParameter("version", "1.3.0")
                .appendQueryParameter("crs", "CRS:84")
                .appendQueryParameter("layers", "mergedSrtm")
                .appendQueryParameter("styles", "")
                .appendQueryParameter("width", "512")
                .appendQueryParameter("height", "512")
                .appendQueryParameter("format", "application/bil16")
                .appendQueryParameter("bbox", boundingBox);

        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/elevation_" + sw.getLatitude() + ".bin";

        DownloadService.startDownload(appContext, receiver, builder.build().toString(), filename);
    }

    /**
     *
     */
    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            String filename = resultData.getString("filename");

            if (resultCode == DownloadService.DOWNLOAD_COMPLETE_OK) {
                Toast toast = Toast.makeText(appContext, filename, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

}
