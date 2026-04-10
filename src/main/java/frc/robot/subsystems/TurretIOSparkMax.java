package frc.robot.subsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.constants.Constants.CANConstants;
import frc.robot.utils.SubsystemTuningTab;
import frc.robot.utils.SubsystemTuningTab.TunableBoolean;
import frc.robot.utils.SubsystemTuningTab.TunableDouble;

public class TurretIOSparkMax implements TurretIO {
  private final SparkMax m_turretMotor;
  private final SparkClosedLoopController m_pidController;
  private final SparkMaxConfig m_config = new SparkMaxConfig();
  private final TunableDouble m_currentLimitEntry;
  private final TunableBoolean m_brakeModeEntry;
  private final TunableDouble m_pidPEntry;
  private final TunableDouble m_pidIEntry;
  private final TunableDouble m_pidDEntry;
  private final TunableDouble m_minOutputEntry;
  private final TunableDouble m_maxOutputEntry;
  private final TunableDouble m_maxVelocityEntry;
  private final TunableDouble m_maxAccelerationEntry;
  private final TunableDouble m_allowedErrorEntry;
  private int m_appliedCurrentLimit = Integer.MIN_VALUE;
  private boolean m_appliedBrakeMode;
  private double m_appliedPidP = Double.NaN;
  private double m_appliedPidI = Double.NaN;
  private double m_appliedPidD = Double.NaN;
  private double m_appliedMinOutput = Double.NaN;
  private double m_appliedMaxOutput = Double.NaN;
  private double m_appliedMaxVelocity = Double.NaN;
  private double m_appliedMaxAcceleration = Double.NaN;
  private double m_appliedAllowedError = Double.NaN;

  @SuppressWarnings("removal")
  public TurretIOSparkMax() {
    SubsystemTuningTab tuningTab = new SubsystemTuningTab("Turret");
    m_currentLimitEntry = tuningTab.addDouble("IO Current Limit", 25);
    m_brakeModeEntry = tuningTab.addBoolean("IO Brake Mode", true);
    m_pidPEntry = tuningTab.addDouble("IO Position kP", 0.05);
    m_pidIEntry = tuningTab.addDouble("IO Position kI", 0.0);
    m_pidDEntry = tuningTab.addDouble("IO Position kD", 0.0);
    m_minOutputEntry = tuningTab.addDouble("IO Min Output", -1.0);
    m_maxOutputEntry = tuningTab.addDouble("IO Max Output", 1.0);
    m_maxVelocityEntry = tuningTab.addDouble("IO Max Motion Velocity", 1000.0);
    m_maxAccelerationEntry = tuningTab.addDouble("IO Max Motion Acceleration", 500.0);
    m_allowedErrorEntry = tuningTab.addDouble("IO Allowed Error", 0.5);

    m_turretMotor = new SparkMax(CANConstants.MOTOR_TURRET_ID, MotorType.kBrushless);
    m_pidController = m_turretMotor.getClosedLoopController();
    applyConfig(true);
  }

  @Override
  public void updateInputs(TurretIOInputs inputs) {
    syncTuning();
    inputs.appliedVolts = m_turretMotor.getAppliedOutput() * m_turretMotor.getBusVoltage();
    inputs.currentAmps = m_turretMotor.getOutputCurrent();
    inputs.positionRotations = m_turretMotor.getEncoder().getPosition();
    inputs.velocityRPM = m_turretMotor.getEncoder().getVelocity();
  }

  @SuppressWarnings("removal")
  @Override
  public void setPosition(double positionRotations) {
    syncTuning();
    m_pidController.setReference(positionRotations, ControlType.kPosition);
    //        com.revrobotics.spark.ClosedLoopSlot.kSlot0);
  }

  @Override
  public void setVoltage(double volts) {
    syncTuning();
    m_turretMotor.setVoltage(volts);
  }

  @Override
  public void stop() {
    m_turretMotor.stopMotor();
  }

  private void syncTuning() {
    int desiredCurrentLimit = (int) Math.round(m_currentLimitEntry.get());
    boolean desiredBrakeMode = m_brakeModeEntry.get();
    double desiredPidP = m_pidPEntry.get();
    double desiredPidI = m_pidIEntry.get();
    double desiredPidD = m_pidDEntry.get();
    double desiredMinOutput = m_minOutputEntry.get();
    double desiredMaxOutput = m_maxOutputEntry.get();
    double desiredMaxVelocity = m_maxVelocityEntry.get();
    double desiredMaxAcceleration = m_maxAccelerationEntry.get();
    double desiredAllowedError = m_allowedErrorEntry.get();

    if (desiredCurrentLimit != m_appliedCurrentLimit
        || desiredBrakeMode != m_appliedBrakeMode
        || Math.abs(desiredPidP - m_appliedPidP) > 1e-9
        || Math.abs(desiredPidI - m_appliedPidI) > 1e-9
        || Math.abs(desiredPidD - m_appliedPidD) > 1e-9
        || Math.abs(desiredMinOutput - m_appliedMinOutput) > 1e-9
        || Math.abs(desiredMaxOutput - m_appliedMaxOutput) > 1e-9
        || Math.abs(desiredMaxVelocity - m_appliedMaxVelocity) > 1e-9
        || Math.abs(desiredMaxAcceleration - m_appliedMaxAcceleration) > 1e-9
        || Math.abs(desiredAllowedError - m_appliedAllowedError) > 1e-9) {
      applyConfig(false);
    }
  }

  @SuppressWarnings("removal")
  private void applyConfig(boolean persist) {
    m_appliedCurrentLimit = (int) Math.round(m_currentLimitEntry.get());
    m_appliedBrakeMode = m_brakeModeEntry.get();
    m_appliedPidP = m_pidPEntry.get();
    m_appliedPidI = m_pidIEntry.get();
    m_appliedPidD = m_pidDEntry.get();
    m_appliedMinOutput = m_minOutputEntry.get();
    m_appliedMaxOutput = m_maxOutputEntry.get();
    m_appliedMaxVelocity = m_maxVelocityEntry.get();
    m_appliedMaxAcceleration = m_maxAccelerationEntry.get();
    m_appliedAllowedError = m_allowedErrorEntry.get();

    m_config.smartCurrentLimit(m_appliedCurrentLimit);
    m_config.idleMode(
        m_appliedBrakeMode ? SparkMaxConfig.IdleMode.kBrake : SparkMaxConfig.IdleMode.kCoast);
    m_config.closedLoop.pid(m_appliedPidP, m_appliedPidI, m_appliedPidD);
    m_config.closedLoop.outputRange(m_appliedMinOutput, m_appliedMaxOutput);
    m_config.closedLoop.maxMotion.maxVelocity(m_appliedMaxVelocity);
    m_config.closedLoop.maxMotion.maxAcceleration(m_appliedMaxAcceleration);
    m_config.closedLoop.maxMotion.allowedClosedLoopError(m_appliedAllowedError);

    m_turretMotor.configure(
        m_config,
        persist ? ResetMode.kResetSafeParameters : ResetMode.kNoResetSafeParameters,
        persist ? PersistMode.kPersistParameters : PersistMode.kNoPersistParameters);
  }
}
