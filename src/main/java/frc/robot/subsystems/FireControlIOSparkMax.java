package frc.robot.subsystems;

import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.constants.Constants.CANConstants;
import frc.robot.utils.SubsystemTuningTab;
import frc.robot.utils.SubsystemTuningTab.TunableBoolean;
import frc.robot.utils.SubsystemTuningTab.TunableDouble;

public class FireControlIOSparkMax implements FireControlIO {
  private final SparkMax m_fireMotor;
  private final SparkClosedLoopController m_pidController;
  private final SparkMaxConfig m_config = new SparkMaxConfig();
  private final TunableDouble m_currentLimitEntry;
  private final TunableDouble m_pidPEntry;
  private final TunableDouble m_pidIEntry;
  private final TunableDouble m_pidDEntry;
  private final TunableDouble m_minOutputEntry;
  private final TunableDouble m_maxOutputEntry;
  private final TunableBoolean m_invertedEntry;
  private int m_appliedCurrentLimit = Integer.MIN_VALUE;
  private double m_appliedPidP = Double.NaN;
  private double m_appliedPidI = Double.NaN;
  private double m_appliedPidD = Double.NaN;
  private double m_appliedMinOutput = Double.NaN;
  private double m_appliedMaxOutput = Double.NaN;
  private boolean m_appliedInverted;

  @SuppressWarnings("removal")
  public FireControlIOSparkMax() {
    SubsystemTuningTab tuningTab = new SubsystemTuningTab("Fire Control");
    m_currentLimitEntry = tuningTab.addDouble("IO Current Limit", 40);
    m_pidPEntry = tuningTab.addDouble("IO Velocity kP", 0.005);
    m_pidIEntry = tuningTab.addDouble("IO Velocity kI", 0.0);
    m_pidDEntry = tuningTab.addDouble("IO Velocity kD", 0.0);
    m_minOutputEntry = tuningTab.addDouble("IO Min Output", -1.0);
    m_maxOutputEntry = tuningTab.addDouble("IO Max Output", 1.0);
    m_invertedEntry = tuningTab.addBoolean("IO Inverted", true);

    m_fireMotor = new SparkMax(CANConstants.MOTOR_FIRE_ID, MotorType.kBrushless);
    m_pidController = m_fireMotor.getClosedLoopController();
    applyConfig(true);
  }

  @Override
  public void updateInputs(FireControlIOInputs inputs) {
    syncTuning();
    inputs.appliedVolts = m_fireMotor.getAppliedOutput() * m_fireMotor.getBusVoltage();
    inputs.currentAmps = m_fireMotor.getOutputCurrent();
    inputs.velocityRPM = m_fireMotor.getEncoder().getVelocity();
  }

  @SuppressWarnings("removal")
  @Override
  public void setVelocity(double velocityRPM, double feedforwardVolts) {
    syncTuning();
    m_pidController.setReference(
        velocityRPM,
        ControlType.kVelocity,
        com.revrobotics.spark.ClosedLoopSlot.kSlot0,
        feedforwardVolts,
        SparkClosedLoopController.ArbFFUnits.kVoltage);
  }

  @Override
  public void stop() {
    m_fireMotor.stopMotor();
  }

  private void syncTuning() {
    int desiredCurrentLimit = (int) Math.round(m_currentLimitEntry.get());
    double desiredPidP = m_pidPEntry.get();
    double desiredPidI = m_pidIEntry.get();
    double desiredPidD = m_pidDEntry.get();
    double desiredMinOutput = m_minOutputEntry.get();
    double desiredMaxOutput = m_maxOutputEntry.get();
    boolean desiredInverted = m_invertedEntry.get();

    if (desiredCurrentLimit != m_appliedCurrentLimit
        || Math.abs(desiredPidP - m_appliedPidP) > 1e-9
        || Math.abs(desiredPidI - m_appliedPidI) > 1e-9
        || Math.abs(desiredPidD - m_appliedPidD) > 1e-9
        || Math.abs(desiredMinOutput - m_appliedMinOutput) > 1e-9
        || Math.abs(desiredMaxOutput - m_appliedMaxOutput) > 1e-9
        || desiredInverted != m_appliedInverted) {
      applyConfig(false);
    }
  }

  @SuppressWarnings("removal")
  private void applyConfig(boolean persist) {
    m_appliedCurrentLimit = (int) Math.round(m_currentLimitEntry.get());
    m_appliedPidP = m_pidPEntry.get();
    m_appliedPidI = m_pidIEntry.get();
    m_appliedPidD = m_pidDEntry.get();
    m_appliedMinOutput = m_minOutputEntry.get();
    m_appliedMaxOutput = m_maxOutputEntry.get();
    m_appliedInverted = m_invertedEntry.get();

    m_config.smartCurrentLimit(m_appliedCurrentLimit);
    m_config.closedLoop.pid(m_appliedPidP, m_appliedPidI, m_appliedPidD);
    m_config.closedLoop.outputRange(m_appliedMinOutput, m_appliedMaxOutput);
    m_config.inverted(m_appliedInverted);
    m_fireMotor.configure(
        m_config,
        persist ? ResetMode.kResetSafeParameters : ResetMode.kNoResetSafeParameters,
        persist ? PersistMode.kPersistParameters : PersistMode.kNoPersistParameters);
  }
}
