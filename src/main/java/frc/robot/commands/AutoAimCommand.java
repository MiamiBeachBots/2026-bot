package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TurretSubsystem;

public class AutoAimCommand extends Command {
  private final TurretSubsystem m_turret;

  public AutoAimCommand(TurretSubsystem turret) {
    m_turret = turret;
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
    // TODO: Friend's auto-aim logic here
    m_turret.stop(); // default safe state while waiting for implementation
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
