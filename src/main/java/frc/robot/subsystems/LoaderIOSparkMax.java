package frc.robot.subsystems;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.constants.Constants.CANConstants;
import frc.robot.utils.SubsystemTuningTab;
import frc.robot.utils.SubsystemTuningTab.TunableBoolean;
import frc.robot.utils.SubsystemTuningTab.TunableDouble;

public class LoaderIOSparkMax implements LoaderIO {
  private final SparkMax m_loaderMotor1;
  private final SparkMax m_loaderMotor2;
  private final SparkMax m_loaderMotor3;
  private final TunableDouble m_currentLimitEntry;
  private final TunableBoolean m_brakeModeEntry;
  private int m_appliedCurrentLimit = Integer.MIN_VALUE;
  private boolean m_appliedBrakeMode;

  @SuppressWarnings("removal")
  public LoaderIOSparkMax() {
    SubsystemTuningTab tuningTab = new SubsystemTuningTab("Loader");
    m_currentLimitEntry = tuningTab.addDouble("IO Current Limit", 30);
    m_brakeModeEntry = tuningTab.addBoolean("IO Brake Mode", true);

    m_loaderMotor1 = new SparkMax(CANConstants.MOTOR_TURRET_CHANNEL_ID, MotorType.kBrushless);
    m_loaderMotor2 = new SparkMax(CANConstants.MOTOR_SPINDEXER_ID, MotorType.kBrushless);
    m_loaderMotor3 = new SparkMax(CANConstants.MOTOR_LOADER_ID, MotorType.kBrushless);
    applyConfig(true);
  }

  @Override
  public void updateInputs(LoaderIOInputs inputs) {
    syncTuning();
    inputs.appliedVolts = m_loaderMotor1.getAppliedOutput() * m_loaderMotor1.getBusVoltage();
    inputs.currentAmps = m_loaderMotor1.getOutputCurrent();
    inputs.velocityRPM = m_loaderMotor1.getEncoder().getVelocity();
  }

  @Override
  public void setVoltage(double volts) {
    syncTuning();
    m_loaderMotor1.setVoltage(volts);
    m_loaderMotor2.setVoltage(volts);
    m_loaderMotor3.setVoltage(volts);
  }

  @Override
  public void stop() {
    m_loaderMotor1.stopMotor();
    m_loaderMotor2.stopMotor();
    m_loaderMotor3.stopMotor();
  }

  private void syncTuning() {
    int desiredCurrentLimit = (int) Math.round(m_currentLimitEntry.get());
    boolean desiredBrakeMode = m_brakeModeEntry.get();
    if (desiredCurrentLimit != m_appliedCurrentLimit || desiredBrakeMode != m_appliedBrakeMode) {
      applyConfig(false);
    }
  }

  @SuppressWarnings("removal")
  private void applyConfig(boolean persist) {
    m_appliedCurrentLimit = (int) Math.round(m_currentLimitEntry.get());
    m_appliedBrakeMode = m_brakeModeEntry.get();

    SparkMaxConfig leaderConfig = new SparkMaxConfig();
    leaderConfig.idleMode(
        m_appliedBrakeMode ? SparkMaxConfig.IdleMode.kBrake : SparkMaxConfig.IdleMode.kCoast);
    leaderConfig.smartCurrentLimit(m_appliedCurrentLimit);

    SparkMaxConfig followerConfig = new SparkMaxConfig();
    followerConfig.idleMode(
        m_appliedBrakeMode ? SparkMaxConfig.IdleMode.kBrake : SparkMaxConfig.IdleMode.kCoast);
    followerConfig.smartCurrentLimit(m_appliedCurrentLimit);
    followerConfig.follow(m_loaderMotor1);

    m_loaderMotor1.configure(
        leaderConfig,
        persist ? ResetMode.kResetSafeParameters : ResetMode.kNoResetSafeParameters,
        persist ? PersistMode.kPersistParameters : PersistMode.kNoPersistParameters);
    m_loaderMotor2.configure(
        followerConfig,
        persist ? ResetMode.kResetSafeParameters : ResetMode.kNoResetSafeParameters,
        persist ? PersistMode.kPersistParameters : PersistMode.kNoPersistParameters);
    m_loaderMotor3.configure(
        followerConfig,
        persist ? ResetMode.kResetSafeParameters : ResetMode.kNoResetSafeParameters,
        persist ? PersistMode.kPersistParameters : PersistMode.kNoPersistParameters);
  }
}
