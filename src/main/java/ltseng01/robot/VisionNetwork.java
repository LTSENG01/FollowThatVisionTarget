package ltseng01.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.HashMap;

public class VisionNetwork {

    private static NetworkTable visionTable;

    private static HashMap<String, VisionEntity> visionData;

    enum VisionType {
        CARGO, PANEL, VISION_TARGET, FLOOR_TAPE;
    }

    class VisionEntity {

        private int count;
        private VisionType object_type;
        private int number;
        private double elevation_angle;
        private double azimuth;
        private double distance;

        public VisionEntity(int count, VisionType object_type, int number, double elevation_angle, double azimuth, double distance) {
            this.count = count;
            this.object_type = object_type;
            this.number = number;
            this.elevation_angle = elevation_angle;
            this.azimuth = azimuth;
            this.distance = distance;
        }

    }

    static {

        visionTable = NetworkTableInstance.getDefault().getTable("Vision");
        visionData = new HashMap<>();

    }

    public static void refreshVisionData() {

        // A. read every loop
        // B. use NT listeners

        // if count isn't updated

    }

    // Send start to vision
    // Send stop to vision

    // check if data is stale --> delete

}
