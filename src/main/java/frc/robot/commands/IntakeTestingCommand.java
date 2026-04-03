package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeSubsystem;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class IntakeTestingCommand extends Command {
    private IntakeSubsystem m_intakeSubsystem;
    private final BooleanSupplier m_runCondition;
    private final Supplier<Float> m_deltaSupplier;

    public IntakeTestingCommand(IntakeSubsystem i_subsystem, BooleanSupplier runCondition, Supplier<Float> deltaSupplier) {
        m_intakeSubsystem = i_subsystem;
        m_runCondition = runCondition;
        m_deltaSupplier = deltaSupplier;

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {}

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {

        m_intakeSubsystem.pivotIntake();
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {}

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return m_runCondition.getAsBoolean();
    }
}
