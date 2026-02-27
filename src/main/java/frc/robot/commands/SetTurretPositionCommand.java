package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TurretSubsystem;

public class SetTurretPositionCommand extends Command {
  private final TurretSubsystem m_turret;
  private final double m_targetRotations;
  private final double m_tolerance = 0.05; // 1/20th of a rotation

  public SetTurretPositionCommand(TurretSubsystem turret, double targetRotations) {
    m_turret = turret;
    m_targetRotations = targetRotations;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(turret);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_turret.setTargetPosition(m_targetRotations);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // The SparkMax built in PID will handle the movement automatically.
    // We optionally can continuously set the reference here just in case.
    m_turret.setTargetPosition(m_targetRotations);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_turret.isAtPosition(m_targetRotations, m_tolerance);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // We intentionally don't call stop() here so the PID can hold the position
  }
}
