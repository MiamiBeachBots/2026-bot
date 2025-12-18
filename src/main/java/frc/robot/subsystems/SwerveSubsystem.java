package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.io.File;
import swervelib.SwerveDrive;
import swervelib.parser.SwerveParser;

public class SwerveSubsystem extends SubsystemBase {

  // The heavy lifting object from YAGSL
  // Note: This is null during simulation/testing when RobotBase.isReal() returns false
  private SwerveDrive swerveDrive;

  // Field2d object for visualization in simulation and SmartDashboard
  private final Field2d m_field = new Field2d();

  private int driverJoystickPort = 0;

  // Start in the middle of an FRC field (54ft x 27ft).
  private Pose2d simPose = new Pose2d(8.2296, 4.1148, new Rotation2d());
  private Translation2d simTranslation = new Translation2d();
  private double simRotationRadPerSec = 0.0;
  private boolean simFieldRelative = true;

  private final SwerveDriveKinematics simKinematics =
      new SwerveDriveKinematics(
          new Translation2d(0.3, 0.3),
          new Translation2d(0.3, -0.3),
          new Translation2d(-0.3, 0.3),
          new Translation2d(-0.3, -0.3));

  private final StructPublisher<Pose2d> pose2dPublisher =
      NetworkTableInstance.getDefault()
          .getStructTopic("/Swerve/Pose2d", Pose2d.struct)
          .publish();
  private final StructPublisher<Pose3d> pose3dPublisher =
      NetworkTableInstance.getDefault()
          .getStructTopic("/Swerve/Pose3d", Pose3d.struct)
          .publish();
  private final StructArrayPublisher<Pose2d> pose2dArrayPublisher =
      NetworkTableInstance.getDefault()
          .getStructArrayTopic("/Swerve/Pose2dArray", Pose2d.struct)
          .publish();
  private final StructArrayPublisher<Pose3d> pose3dArrayPublisher =
      NetworkTableInstance.getDefault()
          .getStructArrayTopic("/Swerve/Pose3dArray", Pose3d.struct)
          .publish();
  private final Pose2d[] pose2dArray = new Pose2d[1];
  private final Pose3d[] pose3dArray = new Pose3d[1];
  private final StructPublisher<ChassisSpeeds> robotSpeedsPublisher =
      NetworkTableInstance.getDefault()
          .getStructTopic("/Swerve/RobotSpeeds", ChassisSpeeds.struct)
          .publish();
  private final StructArrayPublisher<SwerveModuleState> moduleStatesPublisher =
      NetworkTableInstance.getDefault()
          .getStructArrayTopic("/Swerve/ModuleStates", SwerveModuleState.struct)
          .publish();
  private final StructPublisher<Rotation2d> headingPublisher =
      NetworkTableInstance.getDefault()
          .getStructTopic("/Swerve/Heading", Rotation2d.struct)
          .publish();
  private final StructPublisher<Rotation3d> heading3dPublisher =
      NetworkTableInstance.getDefault()
          .getStructTopic("/Swerve/Heading3d", Rotation3d.struct)
          .publish();
  private final DoublePublisher poseXPublisher =
      NetworkTableInstance.getDefault().getDoubleTopic("/Swerve/Pose/X").publish();
  private final DoublePublisher poseYPublisher =
      NetworkTableInstance.getDefault().getDoubleTopic("/Swerve/Pose/Y").publish();
  private final DoublePublisher poseYawRadPublisher =
      NetworkTableInstance.getDefault().getDoubleTopic("/Swerve/Pose/YawRad").publish();
  private final BooleanPublisher joystickConnectedPublisher =
      NetworkTableInstance.getDefault().getBooleanTopic("/Swerve/JoystickConnected").publish();
  private final BooleanPublisher demoDriveActivePublisher =
      NetworkTableInstance.getDefault().getBooleanTopic("/Swerve/DemoDriveActive").publish();
  private final BooleanPublisher isSimulationPublisher =
      NetworkTableInstance.getDefault().getBooleanTopic("/Swerve/IsSimulation").publish();
  private final BooleanPublisher isRealPublisher =
      NetworkTableInstance.getDefault().getBooleanTopic("/Swerve/IsReal").publish();
  private final BooleanPublisher simOdometryActivePublisher =
      NetworkTableInstance.getDefault().getBooleanTopic("/Swerve/SimOdometryActive").publish();

  // Maximum speed in Meters/Second. Adjust this to your specific robot gearing/safety needs.
  // 4.5 m/s is a standard fast speed for L2 gearing.
  public double maximumSpeed = 4.5;

  public SwerveSubsystem(File directory) {
    if (RobotBase.isReal()) {
      try {
        // Configure the Telemetry to be less spammy (optional)
        // SwerveTelemetry.verbosity = SwerveTelemetry.TelemetryVerbosity.LOW;
        
        // Load the JSON configuration
        this.swerveDrive = new SwerveParser(directory).createSwerveDrive(maximumSpeed);

      } catch (Exception e) {
        throw new RuntimeException("CRITICAL: YAGSL Failed to load. Check JSON paths. \n" + e.getMessage());
      }
    }
    SmartDashboard.putData("Field", m_field);
  }

  // Overloaded constructor for default path
  public SwerveSubsystem() {
    this(new File(Filesystem.getDeployDirectory(), "swerve"));
  }

  /**
   * The primary drive method.
   * @param translation  The X/Y translation vector (forward/strafe)
   * @param rotation     The Z rotation value
   * @param fieldRelative True for field-oriented control (standard), False for robot-oriented
   */
  public void drive(Translation2d translation, double rotation, boolean fieldRelative) {
    if (swerveDrive != null) {
      swerveDrive.drive(translation, rotation, fieldRelative, false);
    } else if (RobotBase.isSimulation()) {
      simTranslation = translation;
      simRotationRadPerSec = rotation;
      simFieldRelative = fieldRelative;
    }
  }

  private void publishTelemetry(Pose2d pose, ChassisSpeeds robotRelativeSpeeds, SwerveModuleState[] states) {
    boolean joystickConnected = DriverStation.isJoystickConnected(driverJoystickPort);
    boolean isSimulation = RobotBase.isSimulation();
    joystickConnectedPublisher.set(joystickConnected);
    demoDriveActivePublisher.set(isSimulation && DriverStation.isEnabled() && !joystickConnected);
    isSimulationPublisher.set(isSimulation);
    isRealPublisher.set(RobotBase.isReal());
    simOdometryActivePublisher.set(isSimulation && swerveDrive == null);

    pose2dPublisher.set(pose);
    pose2dArray[0] = pose;
    pose2dArrayPublisher.set(pose2dArray);

    Pose3d pose3d =
        new Pose3d(
            pose.getX(),
            pose.getY(),
            0.0,
            new Rotation3d(0.0, 0.0, pose.getRotation().getRadians()));
    pose3dPublisher.set(pose3d);
    pose3dArray[0] = pose3d;
    pose3dArrayPublisher.set(pose3dArray);

    headingPublisher.set(pose.getRotation());
    heading3dPublisher.set(pose3d.getRotation());

    poseXPublisher.set(pose.getX());
    poseYPublisher.set(pose.getY());
    poseYawRadPublisher.set(pose.getRotation().getRadians());

    if (robotRelativeSpeeds != null) {
      robotSpeedsPublisher.set(robotRelativeSpeeds);
    }
    if (states != null) {
      moduleStatesPublisher.set(states);
    }
  }

  @Override
  public void periodic() {
    // This updates the odometry (robot position on field). 
    // ABSOLUTELY REQUIRED for the robot to know where it is.
    if (swerveDrive != null) {
      swerveDrive.updateOdometry();
      // Update the Field2d visualization with current robot pose
      Pose2d pose = swerveDrive.getPose();
      m_field.setRobotPose(pose);
      publishTelemetry(pose, swerveDrive.getRobotVelocity(), swerveDrive.getStates());
    }
  }

  @Override
  public void simulationPeriodic() {
    if (!RobotBase.isSimulation()) {
      return;
    }
    if (swerveDrive != null) {
      return;
    }

    if (!DriverStation.isEnabled()) {
      simTranslation = new Translation2d();
      simRotationRadPerSec = 0.0;
    }

    ChassisSpeeds robotRelativeSpeeds =
        simFieldRelative
            ? ChassisSpeeds.fromFieldRelativeSpeeds(
                simTranslation.getX(),
                simTranslation.getY(),
                simRotationRadPerSec,
                simPose.getRotation())
            : new ChassisSpeeds(
                simTranslation.getX(), simTranslation.getY(), simRotationRadPerSec);

    // Default TimedRobot period is 20ms.
    double dtSeconds = 0.02;
    simPose =
        simPose.exp(
            new Twist2d(
                robotRelativeSpeeds.vxMetersPerSecond * dtSeconds,
                robotRelativeSpeeds.vyMetersPerSecond * dtSeconds,
                robotRelativeSpeeds.omegaRadiansPerSecond * dtSeconds));
    m_field.setRobotPose(simPose);
    SwerveModuleState[] states = simKinematics.toSwerveModuleStates(robotRelativeSpeeds);
    SwerveDriveKinematics.desaturateWheelSpeeds(states, maximumSpeed);
    publishTelemetry(simPose, robotRelativeSpeeds, states);
  }
  
  // Helper to get the drive object if needed for advanced features (PathPlanner)
  public SwerveDrive getSwerveDrive() {
    return swerveDrive;
  }

  public void setDriverJoystickPort(int port) {
    driverJoystickPort = Math.max(0, Math.min(port, 5));
  }

  public Pose2d getPose() {
    return swerveDrive != null ? swerveDrive.getPose() : simPose;
  }

  public void zeroGyro() {
    if (swerveDrive != null) {
      swerveDrive.zeroGyro();
      return;
    }
    simPose = new Pose2d(simPose.getTranslation(), new Rotation2d());
  }
}
