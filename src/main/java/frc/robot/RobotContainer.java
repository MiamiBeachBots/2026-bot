package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.SwerveSubsystem;
import java.io.File;
import edu.wpi.first.wpilibj.Filesystem;

/**
 * The RobotContainer class is responsible for instantiating and configuring
 * all robot subsystems, setting up controller bindings, and managing the
 * default and autonomous commands. This class serves as the central hub
 * for organizing the robot's command-based structure.
 */
public class RobotContainer {

  // The Subsystem
  // We use the "swerve" directory we deployed earlier
  private final SwerveSubsystem drivebase = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(), "swerve"));

  // The Controller (Port 0 is usually the first USB controller plugged in)
  private final CommandXboxController driverXbox = new CommandXboxController(0);

  /**
   * Constructs a new RobotContainer.
   * <p>
   * This constructor initializes the robot's subsystems and configures controller bindings
   * by calling {@link #configureBindings()}. This setup ensures that the drivebase subsystem
   * and controller commands are properly initialized before the robot starts operating.
   */
  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {
    drivebase.setDriverJoystickPort(driverXbox.getHID().getPort());

    // DEFAULT COMMAND: The "Idle" state of the robot.
    // If no other button is pressed, do this.
    drivebase.setDefaultCommand(
        // We create a "RunCommand" (runs repeatedly)
        Commands.run(
            () -> {
                boolean hasJoystick = DriverStation.isJoystickConnected(driverXbox.getHID().getPort());

                // 1. Get driver inputs (or demo inputs in sim without a joystick)
                // MathUtil.applyDeadband ignores tiny drift when the stick is centered.
                double yVelocity;
                double xVelocity;
                double rotation;

                if (RobotBase.isSimulation() && !hasJoystick) {
                  double t = Timer.getFPGATimestamp();
                  yVelocity = 0.35;
                  xVelocity = 0.15 * Math.sin(t);
                  rotation = 0.15;
                } else {
                  // Inverted because Y is up-negative in computer graphics
                  yVelocity = -MathUtil.applyDeadband(driverXbox.getLeftY(), 0.1);
                  xVelocity = -MathUtil.applyDeadband(driverXbox.getLeftX(), 0.1);
                  rotation = -MathUtil.applyDeadband(driverXbox.getRightX(), 0.1);
                }

                // 2. Drive
                drivebase.drive(
                    new Translation2d(yVelocity * drivebase.maximumSpeed, xVelocity * drivebase.maximumSpeed),
                    rotation * Math.PI,
                    true // Field Relative (True = Standard, False = Robot Oriented)
                );
            },
            drivebase // REQUIRE the subsystem so no other command can interrupt this one
        )
    );

    // Map "Back" button to zero the gyro (reset field orientation)
    new Trigger(() -> DriverStation.isJoystickConnected(driverXbox.getHID().getPort()) && driverXbox.getHID().getBackButton())
        .onTrue(Commands.runOnce(drivebase::zeroGyro, drivebase));
  }

  /**
   * Returns the command to run during the autonomous period.
   *
   * @return the autonomous command to execute
   */
  public Command getAutonomousCommand() {
    return Commands.print("No Auto Configured Yet!");
  }
}
