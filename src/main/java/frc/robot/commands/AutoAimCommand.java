package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CameraSubsystem;
import frc.robot.subsystems.TurretSubsystem;

public class AutoAimCommand extends Command {
  private final TurretSubsystem m_turret;
  private final CameraSubsystem m_camera;

  // A simple Proportional controller to turn the turret based on AprilTag Yaw
  private final PIDController m_yawController = new PIDController(0.015, 0, 0);

  public AutoAimCommand(TurretSubsystem turret, CameraSubsystem camera) {
    m_turret = turret;
    m_camera = camera;

    // Set tolerance (in degrees) for when we consider the turret "aimed"
    m_yawController.setTolerance(1.0);

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(turret);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("AutoAimCommand Scheduled - Handing over to AutoAim");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // Check if the camera sees a target
    if (m_camera.targetingCamera1Result.isPresent()
        && m_camera.targetingCamera1Result.get().hasTargets()) {
      // Get the best target's yaw (horizontal offset from center of camera in degrees)
      double yaw = m_camera.targetingCamera1Result.get().getBestTarget().getYaw();

      // Calculate motor output to turn the turret towards the target (0 degrees yaw)
      double output = m_yawController.calculate(yaw, 0.0);

      // Invert output if the camera is mounted such that positive yaw requires negative motor power
      m_turret.setTurretSpeed(-output);
    } else {
      // If we lose sight of the target, stop moving
      m_turret.stop();
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false; // Run until interrupted/toggled off
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_turret.stop();
    System.out.println("AutoAimCommand Ended - Returning to Manual Control");
  }
}
