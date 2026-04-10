package frc.robot.subsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.DriveConstants;
import frc.robot.constants.Constants;
import frc.robot.constants.Constants.CANConstants;
import frc.robot.utils.SubsystemTuningTab;
import frc.robot.utils.SubsystemTuningTab.TunableBoolean;
import frc.robot.utils.SubsystemTuningTab.TunableDouble;

public class IntakeIOSparkMax implements IntakeIO {
  private final SparkMax m_intakeMotorRun;
  private final SparkMax m_intakeMotorPivotA;
  private final SparkMax m_intakeMotorPivotB;
  public final SparkClosedLoopController m_intakePivotControllerA;
  public final SparkClosedLoopController m_intakePivotControllerB;
  private final TunableBoolean m_runBrakeModeEntry;
  private final TunableDouble m_runRampRateEntry;
  private final TunableDouble m_runCurrentLimitEntry;
  private final TunableDouble m_runPidPEntry;
  private final TunableDouble m_runPidIEntry;
  private final TunableDouble m_runPidDEntry;
  private final TunableDouble m_runMinOutputEntry;
  private final TunableDouble m_runMaxOutputEntry;
  private final TunableBoolean m_pivotBrakeModeEntry;
  private final TunableDouble m_pivotCurrentLimitEntry;
  private final TunableDouble m_pivotPositionConversionEntry;
  private final TunableDouble m_pivotPidPEntry;
  private final TunableDouble m_pivotPidIEntry;
  private final TunableDouble m_pivotPidDEntry;
  private final TunableDouble m_pivotMinOutputEntry;
  private final TunableDouble m_pivotMaxOutputEntry;
  private boolean m_appliedRunBrakeMode;
  private double m_appliedRunRampRate = Double.NaN;
  private int m_appliedRunCurrentLimit = Integer.MIN_VALUE;
  private double m_appliedRunPidP = Double.NaN;
  private double m_appliedRunPidI = Double.NaN;
  private double m_appliedRunPidD = Double.NaN;
  private double m_appliedRunMinOutput = Double.NaN;
  private double m_appliedRunMaxOutput = Double.NaN;
  private boolean m_appliedPivotBrakeMode;
  private int m_appliedPivotCurrentLimit = Integer.MIN_VALUE;
  private double m_appliedPivotPositionConversion = Double.NaN;
  private double m_appliedPivotPidP = Double.NaN;
  private double m_appliedPivotPidI = Double.NaN;
  private double m_appliedPivotPidD = Double.NaN;
  private double m_appliedPivotMinOutput = Double.NaN;
  private double m_appliedPivotMaxOutput = Double.NaN;

  public IntakeIOSparkMax() {
    SubsystemTuningTab tuningTab = new SubsystemTuningTab("Intake");
    m_runBrakeModeEntry = tuningTab.addBoolean("Run IO Brake Mode", false);
    m_runRampRateEntry = tuningTab.addDouble("Run IO Ramp Rate", 0.25);
    m_runCurrentLimitEntry = tuningTab.addDouble("Run IO Current Limit", 40);
    m_runPidPEntry = tuningTab.addDouble("Run IO kP", 0.0);
    m_runPidIEntry = tuningTab.addDouble("Run IO kI", 0.0);
    m_runPidDEntry = tuningTab.addDouble("Run IO kD", 0.0);
    m_runMinOutputEntry = tuningTab.addDouble("Run IO Min Output", -1.0);
    m_runMaxOutputEntry = tuningTab.addDouble("Run IO Max Output", 1.0);
    m_pivotBrakeModeEntry = tuningTab.addBoolean("Pivot IO Brake Mode", true);
    m_pivotCurrentLimitEntry = tuningTab.addDouble("Pivot IO Current Limit", 40);
    m_pivotPositionConversionEntry =
        tuningTab.addDouble(
            "Pivot IO Position Conversion", Constants.kIntakePositionConversionRatio);
    m_pivotPidPEntry = tuningTab.addDouble("Pivot IO kP", 0.0);
    m_pivotPidIEntry = tuningTab.addDouble("Pivot IO kI", 0.0);
    m_pivotPidDEntry = tuningTab.addDouble("Pivot IO kD", 0.0);
    m_pivotMinOutputEntry = tuningTab.addDouble("Pivot IO Min Output", -1.0);
    m_pivotMaxOutputEntry = tuningTab.addDouble("Pivot IO Max Output", 1.0);

    m_intakeMotorRun = new SparkMax(CANConstants.MOTOR_INTAKE_DRIVE_ID, MotorType.kBrushless);
    m_intakeMotorPivotA = new SparkMax(CANConstants.MOTOR_INTAKE_PIVOT_A_ID, MotorType.kBrushless);
    m_intakeMotorPivotB = new SparkMax(CANConstants.MOTOR_INTAKE_PIVOT_B_ID, MotorType.kBrushless);

    m_intakePivotControllerA = m_intakeMotorPivotA.getClosedLoopController();

    m_intakePivotControllerB = m_intakeMotorPivotA.getClosedLoopController();
    applyConfigs(true);
    m_intakeMotorPivotA.getEncoder().setPosition(0.0); // Assume starting position is 0
    m_intakeMotorPivotB.getEncoder().setPosition(0.0); // Assume starting position is 0
  }

  @Override
  public void updateInputs(IntakeIOInputs inputs) {
    syncTuning();
    inputs.runMotorAppliedVolts =
        m_intakeMotorRun.getAppliedOutput() * m_intakeMotorRun.getBusVoltage();
    inputs.runMotorCurrentAmps = m_intakeMotorRun.getOutputCurrent();
    inputs.runMotorVelocityRPM = m_intakeMotorRun.getEncoder().getVelocity();

    inputs.pivotMotorAppliedVolts =
        m_intakeMotorPivotA.getAppliedOutput() * m_intakeMotorPivotA.getBusVoltage();
    inputs.pivotMotorCurrentAmps = m_intakeMotorPivotA.getOutputCurrent();
    inputs.pivotPositionDeg = m_intakeMotorPivotA.getEncoder().getPosition();
  }

  @Override
  public void setRunVoltage(double volts) {
    syncTuning();
    m_intakeMotorRun.setVoltage(volts);
  }

  @Override
  public void setPivotVoltage(double volts) {
    syncTuning();
    m_intakeMotorPivotA.setVoltage(volts);
    m_intakeMotorPivotB.setVoltage(volts);
  }

  @Override
  public void setPivotTargetPos(double theta) {
    syncTuning();
    m_intakePivotControllerA.setSetpoint(
        theta, SparkBase.ControlType.kPosition, DriveConstants.kDrivetrainPositionPIDSlot);
  }

  @Override
  public void stop() {
    m_intakeMotorRun.stopMotor();
  }

  @Override
  public void stopPivot() {
    m_intakeMotorPivotA.stopMotor();
    m_intakeMotorPivotB.stopMotor();
  }

  private void syncTuning() {
    boolean desiredRunBrakeMode = m_runBrakeModeEntry.get();
    double desiredRunRampRate = m_runRampRateEntry.get();
    int desiredRunCurrentLimit = (int) Math.round(m_runCurrentLimitEntry.get());
    double desiredRunPidP = m_runPidPEntry.get();
    double desiredRunPidI = m_runPidIEntry.get();
    double desiredRunPidD = m_runPidDEntry.get();
    double desiredRunMinOutput = m_runMinOutputEntry.get();
    double desiredRunMaxOutput = m_runMaxOutputEntry.get();
    boolean desiredPivotBrakeMode = m_pivotBrakeModeEntry.get();
    int desiredPivotCurrentLimit = (int) Math.round(m_pivotCurrentLimitEntry.get());
    double desiredPivotPositionConversion = m_pivotPositionConversionEntry.get();
    double desiredPivotPidP = m_pivotPidPEntry.get();
    double desiredPivotPidI = m_pivotPidIEntry.get();
    double desiredPivotPidD = m_pivotPidDEntry.get();
    double desiredPivotMinOutput = m_pivotMinOutputEntry.get();
    double desiredPivotMaxOutput = m_pivotMaxOutputEntry.get();

    if (desiredRunBrakeMode != m_appliedRunBrakeMode
        || Math.abs(desiredRunRampRate - m_appliedRunRampRate) > 1e-9
        || desiredRunCurrentLimit != m_appliedRunCurrentLimit
        || Math.abs(desiredRunPidP - m_appliedRunPidP) > 1e-9
        || Math.abs(desiredRunPidI - m_appliedRunPidI) > 1e-9
        || Math.abs(desiredRunPidD - m_appliedRunPidD) > 1e-9
        || Math.abs(desiredRunMinOutput - m_appliedRunMinOutput) > 1e-9
        || Math.abs(desiredRunMaxOutput - m_appliedRunMaxOutput) > 1e-9
        || desiredPivotBrakeMode != m_appliedPivotBrakeMode
        || desiredPivotCurrentLimit != m_appliedPivotCurrentLimit
        || Math.abs(desiredPivotPositionConversion - m_appliedPivotPositionConversion) > 1e-9
        || Math.abs(desiredPivotPidP - m_appliedPivotPidP) > 1e-9
        || Math.abs(desiredPivotPidI - m_appliedPivotPidI) > 1e-9
        || Math.abs(desiredPivotPidD - m_appliedPivotPidD) > 1e-9
        || Math.abs(desiredPivotMinOutput - m_appliedPivotMinOutput) > 1e-9
        || Math.abs(desiredPivotMaxOutput - m_appliedPivotMaxOutput) > 1e-9) {
      applyConfigs(false);
    }
  }

  private void applyConfigs(boolean persist) {
    m_appliedRunBrakeMode = m_runBrakeModeEntry.get();
    m_appliedRunRampRate = m_runRampRateEntry.get();
    m_appliedRunCurrentLimit = (int) Math.round(m_runCurrentLimitEntry.get());
    m_appliedRunPidP = m_runPidPEntry.get();
    m_appliedRunPidI = m_runPidIEntry.get();
    m_appliedRunPidD = m_runPidDEntry.get();
    m_appliedRunMinOutput = m_runMinOutputEntry.get();
    m_appliedRunMaxOutput = m_runMaxOutputEntry.get();
    m_appliedPivotBrakeMode = m_pivotBrakeModeEntry.get();
    m_appliedPivotCurrentLimit = (int) Math.round(m_pivotCurrentLimitEntry.get());
    m_appliedPivotPositionConversion = m_pivotPositionConversionEntry.get();
    m_appliedPivotPidP = m_pivotPidPEntry.get();
    m_appliedPivotPidI = m_pivotPidIEntry.get();
    m_appliedPivotPidD = m_pivotPidDEntry.get();
    m_appliedPivotMinOutput = m_pivotMinOutputEntry.get();
    m_appliedPivotMaxOutput = m_pivotMaxOutputEntry.get();

    SparkMaxConfig runConfig = new SparkMaxConfig();
    runConfig.idleMode(
        m_appliedRunBrakeMode ? SparkMaxConfig.IdleMode.kBrake : SparkMaxConfig.IdleMode.kCoast);
    runConfig.openLoopRampRate(m_appliedRunRampRate);
    runConfig.smartCurrentLimit(m_appliedRunCurrentLimit);
    runConfig.closedLoop.pid(m_appliedRunPidP, m_appliedRunPidI, m_appliedRunPidD);
    runConfig.closedLoop.outputRange(m_appliedRunMinOutput, m_appliedRunMaxOutput);

    SparkMaxConfig pivotConfig = new SparkMaxConfig();
    pivotConfig.idleMode(
        m_appliedPivotBrakeMode ? SparkMaxConfig.IdleMode.kBrake : SparkMaxConfig.IdleMode.kCoast);
    pivotConfig.smartCurrentLimit(m_appliedPivotCurrentLimit);
    pivotConfig.encoder.positionConversionFactor(m_appliedPivotPositionConversion);
    pivotConfig.closedLoop.pid(
        m_appliedPivotPidP,
        m_appliedPivotPidI,
        m_appliedPivotPidD,
        DriveConstants.kDrivetrainPositionPIDSlot);
    pivotConfig.closedLoop.outputRange(
        m_appliedPivotMinOutput,
        m_appliedPivotMaxOutput,
        DriveConstants.kDrivetrainPositionPIDSlot);

    m_intakeMotorRun.configure(
        runConfig,
        persist ? ResetMode.kResetSafeParameters : ResetMode.kNoResetSafeParameters,
        persist ? PersistMode.kPersistParameters : PersistMode.kNoPersistParameters);
    m_intakeMotorPivotA.configure(
        pivotConfig,
        persist ? ResetMode.kResetSafeParameters : ResetMode.kNoResetSafeParameters,
        persist ? PersistMode.kPersistParameters : PersistMode.kNoPersistParameters);
    m_intakeMotorPivotB.configure(
        pivotConfig,
        persist ? ResetMode.kResetSafeParameters : ResetMode.kNoResetSafeParameters,
        persist ? PersistMode.kPersistParameters : PersistMode.kNoPersistParameters);
  }
}
