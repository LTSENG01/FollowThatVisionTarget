package ltseng01.robot;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class VisionNetwork {

    private static NetworkTable visionTable;
    private static NetworkTable visionDataTable;

    private static volatile ConcurrentHashMap<VisionType, VisionObjectDetails> visionData;

    enum VisionType {
        CARGO, PANEL, VISION_TARGET, FLOOR_TAPE
    }

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

        public int getCount() {
            return count;
        }

        public double getElevationAngle() {
            return elevationAngle;
        }

        public double getAzimuth() {
            return azimuth;
        }

        public double getDistance() {
            return distance;
        }


    }

    static {

        visionTable = NetworkTableInstance.getDefault().getTable("Vision");
        visionDataTable = visionTable.getSubTable("data");

        visionData = new ConcurrentHashMap<>(4);


        // Register NetworkTables Listeners

        for (VisionType type : VisionType.values()) {

            visionTable.addEntryListener(type.toString(), (table, key, entry, value, flags) -> {
                double[] objectValues = value.getDoubleArray();
                visionData.put(type,
                        new VisionObjectDetails(
                                (int) objectValues[0],
                                objectValues[1],
                                objectValues[2],
                                objectValues[3]));
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        }

    }

    public static VisionObjectDetails getVisionObjectDetails(VisionType visionType) {
        return visionData.get(visionType);
    }


    // Send start to vision
    // Send stop to vision

    // check if data is stale --> delete? (can use a global latest_count variable and delete when accessing)

}
