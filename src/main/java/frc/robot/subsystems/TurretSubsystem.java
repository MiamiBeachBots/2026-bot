package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.CANConstants;

public class TurretSubsystem extends SubsystemBase {
  private final SparkMax m_turretMotor;
  private final SparkMaxConfig m_config;

  public TurretSubsystem() {
    m_turretMotor = new SparkMax(CANConstants.MOTOR_TURRET_ID, MotorType.kBrushless);
    m_config = new SparkMaxConfig();
    
    // Safety Limits (adjust if needed)
    m_config.smartCurrentLimit(40);

    m_turretMotor.configure(m_config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  /**
   * Sets the speed of the turret motor.
   *
   * @param speed The target speed (-1 to 1).
   */
  public void setTurretSpeed(double speed) {
    // Add simple deadband just in case controller has drift
    if (Math.abs(speed) < 0.1) {
        speed = 0;
    }
    m_turretMotor.set(speed);
  }

  /** Stops the turret motor. */
  public void stop() {
    m_turretMotor.set(0);
  }

  @Override
  public void periodic() {
    // Output current state of turret motor for debugging
    SmartDashboard.putNumber("Turret Motor Speed Output", m_turretMotor.get());
  }

  @Override
  public void simulationPeriodic() {
    // Basic simulation logic if needed. 
  }
}
