package com.camgian;

import java.io.*;

/**
 * Created by npalmer on 5/22/2015.
 */
public class Tile {

    private static final int MAX_SUPPORTED_WIDTH = 2048;
    private static final int MAX_SUPPORTED_HEIGHT = 2048;

    private Coordinate southWest;
    private Coordinate northEast;

    private int dataWidth;
    private int dataHeight;

    private int uniqueId;

    byte[] fileData;

    /**
     *
     * The data format is a stream of dataWidth x dataHeight 16bit integers. The data starts with the Upper Left
     * (NW) most point and proceeds in a raster to the right (East) for a dataWidth number of values.  Then the
     * next ROW of data follows in the same fashion until finally the last value of the Lower Right (SE) most point
     * is read.
     *
     * The data format for each value is Least Significant Byte first. 16-bit Signed integers.
     *
     * @param source    Assumes a sequence of little endian 16bit integers
     * @param southWest
     * @param northEast
     * @param dataWidth
     * @param dataHeight
     */
    Tile (File source, String format, Coordinate southWest, Coordinate northEast, int dataWidth, int dataHeight ) {
        // Sanity check values
        if (southWest == null || northEast == null ) {
            // exception
        }
        if (source == null || source.canRead() == false ) {
            // exception
        }
        if (dataWidth > MAX_SUPPORTED_WIDTH || dataHeight > MAX_SUPPORTED_HEIGHT) {
            // exception
        }

        this.southWest = southWest;
        this.northEast = northEast;
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;

        if (format != null && format.equals("application/bil16")) {
            if (source.length() != dataWidth*2 * dataHeight*2) {
                // exception
            }
            try {
                fileData = new byte[(int) source.length()];

                DataInputStream dis = new DataInputStream(new FileInputStream(source));
                dis.readFully(fileData);
                dis.close();

                /*
                elevationData = new short[dataWidth][dataHeight];
                int index = 0;
                // Convert the raw data to ints,
                for (int i = 0; i < dataHeight; i++) {
                    for (int j = 0; j < dataWidth; j++) {
                        int nextInt = (fileData[index] & 0xFF) | (fileData[index + 1] & 0xFF) << 8;
                        elevationData[i][j] = (short)nextInt;
                        //elevationData[j][dataWidth - i - 1] = nextInt;
                        index = index + 2;
                    }
                }
                */

            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
        } else {
            // exception
        }
    }

    /**
     *
     * @param coord
     * @return
     */
    private int getElevationFromRawData (Coordinate coord) {
        // Interpolate the Lat/Lon pair to an X/Y coordinate
        //coord.getLatitude() - northEast.getLatitude();
        int x = 100;
        int y = 100;

        int index = 2 * (x + dataWidth * y);
        int elevation = (fileData[index] & 0xFF) | (fileData[index + 1] & 0xFF) << 8;

        return (short)elevation;
    }

    public boolean containsCoordinate(Coordinate coord ) {
        return  (coord.getLatitude() >= southWest.getLatitude()) &&
                (coord.getLatitude() <= northEast.getLatitude()) &&
                (coord.getLongitude() >= southWest.getLongitude()) &&
                (coord.getLongitude() <= northEast.getLongitude());
    }

    public Integer getElevationAtPoint(Coordinate coordinate) {
        if (!containsCoordinate(coordinate)) {
            return null;
        }

        return getElevationFromRawData(coordinate);
    }
}
