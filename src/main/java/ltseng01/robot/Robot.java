package ltseng01.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Robot extends TimedRobot {

    private static double KP = 0;
    private static double KI = 0;
    private static double KD = 0;
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

        SmartDashboard.putBoolean("Rotation PID Enabled", rotationPIDControllerEnabled);
        SmartDashboard.putNumber("Rotation PID Set Angle", rotationPIDSetAngle);
        SmartDashboard.putNumber("Rotation PID KP", KP);
        SmartDashboard.putNumber("Rotation PID KI", KI);
        SmartDashboard.putNumber("Rotation PID KD", KD);

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
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {

        if (SmartDashboard.getBoolean("Rotation PID Enabled", false)) {

            if (!rotationPIDControllerEnabled) {

                rotationPIDController.setPID(SmartDashboard.getNumber("Rotation PID KP", 0.0),
                        SmartDashboard.getNumber("Rotation PID KI", 0.0),
                        SmartDashboard.getNumber("Rotation PID KD", 0.0));

                turnToAngle(xboxController.getY(GenericHID.Hand.kLeft),
                        SmartDashboard.getNumber("Rotation PID Set Angle", 0.0));

            }

            if (isRotationPIDControllerOnTarget()) {
                disableRotationPIDController();
            }

        } else {

            drive(xboxController.getY(GenericHID.Hand.kLeft),
                    xboxController.getX(GenericHID.Hand.kRight),
                    false);

        }

    }

    // ------------ NAVX METHODS ------------- //

    public static void resetNavXYaw() {
        navX.reset();
    }

    public static double getYawAngle() {
        return navX.getYaw();
    }


    public static void enableRotationPIDController() {
        rotationPIDController.enable();
    }

    public static void disableRotationPIDController() {
        rotationPIDController.disable();
    }

    public static boolean isRotationPIDControllerOnTarget() {
        return rotationPIDController.onTarget();
    }

    public static void turnToAngle(double speed, double angle) {
        rotationPIDController.setSetpoint(angle);
        enableRotationPIDController();
        drive(speed, outputTurn/1.5, false);
    }

    public static void drive(double speed, double rotation, boolean squared) {
        differentialDrive.arcadeDrive(speed, rotation, squared);
    }

}
