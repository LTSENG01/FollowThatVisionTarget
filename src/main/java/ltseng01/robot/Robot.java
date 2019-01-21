package ltseng01.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static ltseng01.robot.VisionNetwork.VisionType.*;

/**
 *
 */
public class Robot extends TimedRobot {

    private static double KP = 0.28;
    private static double KI = 0;
    private static double KD = 0.018;
    private static final double ROT_TOLERANCE_DEG = 0.5f;

    private static WPI_TalonSRX leftFront;
    private static WPI_TalonSRX leftBack;
    private static WPI_TalonSRX rightFront;
    private static WPI_TalonSRX rightBack;

    private static SpeedControllerGroup leftDrive;
    private static SpeedControllerGroup rightDrive;

    private static DifferentialDrive differentialDrive;

    private static XboxController xboxController;

    private static AHRS navX;
    private static PIDController rotationPIDController;
    private static double outputTurn;
    private static boolean rotationPIDControllerEnabled = false;
    private static double rotationPIDSetAngle = 0.0;


    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {

        xboxController = new XboxController(0);

        leftFront = new WPI_TalonSRX(1);
        leftBack = new WPI_TalonSRX(2);
        rightFront = new WPI_TalonSRX(3);
        rightBack = new WPI_TalonSRX(4);

        leftDrive = new SpeedControllerGroup(leftFront, leftBack);
        rightDrive = new SpeedControllerGroup(rightFront, rightBack);

        differentialDrive = new DifferentialDrive(leftDrive, rightDrive);

        rightFront.setInverted(true);
        rightBack.setInverted(true);

        rightDrive.setInverted(true);

        navX = new AHRS(SPI.Port.kMXP);
        resetNavXYaw();

        rotationPIDController = new PIDController(KP, KI, KD, navX, output -> outputTurn = output);
        rotationPIDController.setAbsoluteTolerance(ROT_TOLERANCE_DEG);
        rotationPIDController.setInputRange(-180.0, 180.0);
        rotationPIDController.setOutputRange(-1.0, 1.0);
        rotationPIDController.setContinuous();
        rotationPIDController.disable();

        SmartDashboard.putNumber("Rotation PID Set Angle", rotationPIDSetAngle);
        SmartDashboard.putNumber("Rotation PID KP", KP);
        SmartDashboard.putNumber("Rotation PID KI", KI);
        SmartDashboard.putNumber("Rotation PID KD", KD);

        VisionNetwork.setVisionTypes(CARGO, PANEL, TARGET, TAPE);   // set to receive all items

    }

    /**
     * This function is called every robot packet, no matter the mode. Use
     * this for items like diagnostics that you want ran during disabled,
     * autonomous, teleoperated and test.
     *
     * <p>This runs after the mode specific periodic functions, but before
     * LiveWindow and SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
        SmartDashboard.putNumber("NavX Yaw", getYawAngle());
        SmartDashboard.putBoolean("Rotation PID Enabled", rotationPIDControllerEnabled);
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {

//        // X Button --> reset gyro
//        if (xboxController.getXButtonPressed()) {
//            resetNavXYaw();
//        }
//
//        // A Button --> Set PID
//        if (xboxController.getAButtonPressed()) {
//
//
//            KP = SmartDashboard.getNumber("Rotation PID KP", KP);
//            KI = SmartDashboard.getNumber("Rotation PID KI", KI);
//            KD = SmartDashboard.getNumber("Rotation PID KD", KD);
//
//            rotationPIDController.setPID(KP, KI, KD);
//
//            System.out.println("Rotation PID Set to " + KP + ", " + KI + ", " + KD);
//
//            rotationPIDControllerEnabled = true;
//
//        }
//
//        // B Button --> Cancel Rotation PID
//        if (xboxController.getBButtonPressed()) {
//
//            rotationPIDControllerEnabled = false;
//            disableRotationPIDController();
//
//        }
//
//        if (rotationPIDControllerEnabled) {
//
//            double turnAngle = SmartDashboard.getNumber("Rotation PID Set Angle", 0.0);
////            String[] visionInfo = NetworkTableInstance.getDefault().getTable("Vision").getEntry("ball_0").getStringArray(new String[]{""});
////            double turnAngle = Double.parseDouble(visionInfo[4]);
//
//            turnToAngle(xboxController.getY(GenericHID.Hand.kLeft), turnAngle);
//
//            if (isRotationPIDControllerOnTarget()) {
//                disableRotationPIDController();
//            }
//
//            enableRotationPIDController();
//
//        } else {
//
//            drive(xboxController.getY(GenericHID.Hand.kLeft),
//                    xboxController.getX(GenericHID.Hand.kRight),
//                    false);
//
//        }

        drive(0, 0, false);

    }

    public static void drive(double speed, double rotation, boolean squared) {
        differentialDrive.arcadeDrive(speed, rotation, squared);
    }

    public static void tank_drive(double speed_L, double speed_R, boolean squared) {
        differentialDrive.tankDrive(speed_L, speed_R);
    }

    // ------------ NAVX METHODS ------------- //

    public static void resetNavXYaw() {
        System.out.println("Reset gyro!");
        navX.reset();
    }

    public static double getYawAngle() {
        return navX.getYaw();
    }


    public static void enableRotationPIDController() {
        rotationPIDController.enable();
    }

    public static void disableRotationPIDController() {
        rotationPIDControllerEnabled = false;
        rotationPIDController.disable();
    }

    public static boolean isRotationPIDControllerOnTarget() {
        System.out.println("Rotation PID On Target");
        return rotationPIDController.onTarget();
    }

    public static void turnToAngle(double speed, double angle) {
        System.out.println("Turning to angle: " + angle);
        rotationPIDController.setSetpoint(angle);
        enableRotationPIDController();
        drive(speed, outputTurn/1.5, false);
    }


    // ------------ VISION METHODS ------------- //

    public static void applyVisionAngle(VisionNetwork.VisionType visionType, double speed) {
        VisionNetwork.getAzimuth(visionType)
                .ifPresentOrElse(angle -> turnToAngle(speed, angle),
                        () -> System.out.println("ERROR: No azimuth found for: " + visionType.toString()));     // Error handling
    }

    public static void fullTargetedDriving(VisionNetwork.VisionType visionType) {
        VisionNetwork.getAzimuth(visionType)
                .ifPresentOrElse(angle -> turnToAngle(
                        distanceToSpeed(VisionNetwork.getDistance(visionType)
                                .orElse((double) 0)), angle),
                        () -> System.out.println("ERROR: Cannot complete FTD for: " + visionType.toString()));     // Error handling
    }

    public static double distanceToSpeed(double distance) {
        return distance;
    }


}
