package ltseng01.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.ArrayList;

public class VisionNetwork {

    private static NetworkTable visionTable;

    private static ArrayList<String[]> visionData;

    enum VisionData {
        COUNT(0), OBJECT(1), NUMBER(2), ELEV_ANGLE(3), AZIMUTH(4), DISTANCE(5);

        public int i;

        VisionData(int i) {
            this.i = i;
        }
    }

    enum VisionType {
        CARGO, PANEL, VISION_TARGET, FLOOR_TAPE;
    }

    public VisionNetwork() {

        visionTable = NetworkTableInstance.getDefault().getTable("Vision");

    }

    public static void refreshVisionData() {

        // A. read every loop
        // B. use NT listeners

        // if count isn't updated

    }

    // Send start to vision
    // Send stop to vision

}
