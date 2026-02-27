package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FireControlSubsystem;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class FireCommand extends Command {
  private final FireControlSubsystem m_fireSubsystem;
  private final DoubleSupplier m_speedSupplier;
  private final BooleanSupplier m_triggerHeldSupplier;

  private double m_startTime;
  private final double m_minimumFireTimeSeconds = 2.0;

  /**
   * Creates a new FireCommand that runs for at least 2 seconds, and continues to run as long as the
   * trigger is held. The speed is determined by the absolute value of the provided double supplier.
   */
  public FireCommand(
      FireControlSubsystem fireSubsystem,
      DoubleSupplier speedSupplier,
      BooleanSupplier triggerHeldSupplier) {
    m_fireSubsystem = fireSubsystem;
    m_speedSupplier = speedSupplier;
    m_triggerHeldSupplier = triggerHeldSupplier;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(fireSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_startTime = Timer.getFPGATimestamp();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // Math.abs to ensure positive velocity based on Y-axis
    double rawSpeed = Math.abs(m_speedSupplier.getAsDouble());
    // Apply a deadband to ignore slightly noisy inputs, then clamp between 0 and 1
    double speed = MathUtil.clamp(MathUtil.applyDeadband(rawSpeed, 0.1), 0.0, 1.0);
    m_fireSubsystem.fire(speed);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    double currentTime = Timer.getFPGATimestamp();
    boolean minimumTimeMet = (currentTime - m_startTime) >= m_minimumFireTimeSeconds;
    boolean triggerStillHeld = m_triggerHeldSupplier.getAsBoolean();

    // Finish when the 2 seconds are up AND the trigger is no longer being held.
    return minimumTimeMet && !triggerStillHeld;
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // Stop the motor when the command finishes
    m_fireSubsystem.stop();
  }
}
