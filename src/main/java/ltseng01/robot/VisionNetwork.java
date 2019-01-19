package ltseng01.robot;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the communication between a vision co-processor over NetworkTables and the robot.
 *
 * @author Larry Tseng
 *
 */
public class VisionNetwork {

    private static NetworkTable visionTable;    // reference to the Vision NetworkTables table
    private static NetworkTable visionSettingsTable;    // for settings and commands to the Jetson

    private static volatile ConcurrentHashMap<VisionType, VisionObjectDetails> visionData;  // dictionary for local storage of vision values

    /**
     * Items that can be chosen for vision tracking
     */
    enum VisionType {

        /**
         * CARGO game piece
         */
        CARGO,

        /**
        * HATCH PANEL game piece
        */
        PANEL,

        /**
        * Reflective tape vision targets
        */
        TARGET,

        /**
        * Floor gaffer tape alignment lines
        */
        TAPE

    }

    /**
     * Nested inner class that structures the vision item's data.
     */
    static class VisionObjectDetails {

        private int count;
        private double elevationAngle;
        private double azimuth;
        private double distance;

        VisionObjectDetails(int count, double elevationAngle, double azimuth, double distance) {
            this.count = count;
            this.elevationAngle = elevationAngle;
            this.azimuth = azimuth;
            this.distance = distance;
        }

        int getCount() {
            return count;
        }

        double getElevationAngle() {
            return elevationAngle;
        }

        double getAzimuth() {
            return azimuth;
        }

        double getDistance() {
            return distance;
        }

        public String toString() {
            return "[count: " + getCount() +
                    ", elevAngle: " + getElevationAngle() +
                    ", azimuth: " + getAzimuth() +
                    ", distance: " + getDistance() + "]";
        }

    }

    static {

        visionTable = NetworkTableInstance.getDefault().getTable("Vision");
        visionSettingsTable = visionTable.getSubTable("settings");

        visionData = new ConcurrentHashMap<>(4);


        // Register NetworkTables Listeners

        for (VisionType type : VisionType.values()) {

            visionTable.addEntryListener(type.toString(), (table, key, entry, value, flags) -> {
                double[] objectValues = value.getDoubleArray();
                VisionObjectDetails details = new VisionObjectDetails(
                        (int) objectValues[0],
                        objectValues[1],
                        objectValues[2],
                        objectValues[3]);

                visionData.put(type, details);

                System.out.println("DEBUG: Vision Data updated: " + flags + " details: " + details.toString());

            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        }

    }

    /**
     * @param visionType
     * @return
     */
    public static VisionObjectDetails readVisionObjectDetails(VisionType visionType) {
        return visionData.get(visionType);
    }

    /**
     * @param visionTypes
     */
    public static void setVisionTypes(VisionType... visionTypes) {

        String[] vt = Arrays.stream(visionTypes)
                .map(Enum::toString)
                .toArray(String[]::new);

        visionSettingsTable.getEntry("vision_types").setStringArray(vt);

    }


    // TODO
    // Send start to vision
    // Send stop to vision

    // check if data is stale --> delete? (can use a global latest_count variable and delete when accessing or periodically)

}
