package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.TankSubsystem;

public class RobotContainer {

  // FUCK swerve
  private final TankSubsystem drivebase = new TankSubsystem();

  private final CommandXboxController driverXbox = new CommandXboxController(0);

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {
    drivebase.setDefaultCommand(
        Commands.run(
            () -> {
                double speed = -MathUtil.applyDeadband(driverXbox.getLeftY(), 0.1); 
                double rotation = -MathUtil.applyDeadband(driverXbox.getRightX(), 0.1);

                drivebase.drive(speed, rotation);
            },
            drivebase
        )
    );
  }

  public Command getAutonomousCommand() {
    return Commands.print("No Auto Configured Yet!");
  }
}
