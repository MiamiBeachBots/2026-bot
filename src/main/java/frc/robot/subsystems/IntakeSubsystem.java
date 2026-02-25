package frc.robot.subsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CANConstants;

/** Subsystem handling the intake/loading system. */
public class IntakeSubsystem extends SubsystemBase {

  private final SparkMax m_intakeMotor;
  private final SparkMaxConfig m_config;

  public IntakeSubsystem() {
    m_intakeMotor = new SparkMax(CANConstants.MOTOR_INTAKE_ID, MotorType.kBrushless);
    m_config = new SparkMaxConfig();

    // Default to Coast mode or Brake mode depending on team preference.
    // Usually intakes run in Coast so balls/notes aren't crushed on stop.
    m_config.idleMode(SparkMaxConfig.IdleMode.kCoast);

    m_intakeMotor.configure(
        m_config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  /**
   * Sets the speed of the intake motor.
   *
   * @param speed Speed from -1.0 to 1.0. positive spins intake inward.
   */
  public void setIntakeSpeed(double speed) {
    m_intakeMotor.set(speed);
  }

  /** Stops the intake. */
  public void stop() {
    m_intakeMotor.stopMotor();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
