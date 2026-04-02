package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FireControlSubsystem;
import frc.robot.subsystems.TurretSubsystem;

public class SetFireRPMCommand extends Command {
    FireControlSubsystem m_fireSubsystem;
    double m_targetRPM;
    double m_startTime;

    public SetFireRPMCommand(FireControlSubsystem fireSubsystem, double targetRPM) {
        m_fireSubsystem = fireSubsystem;
        m_targetRPM = targetRPM;
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
        m_fireSubsystem.setRPM(speed);
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
