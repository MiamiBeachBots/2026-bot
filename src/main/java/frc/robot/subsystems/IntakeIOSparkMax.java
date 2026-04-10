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

public class IntakeIOSparkMax implements IntakeIO {
  private final SparkMax m_intakeMotorRun;
  private final SparkMax m_intakeMotorPivotA;
  private final SparkMax m_intakeMotorPivotB;
  public final SparkClosedLoopController m_intakePivotControllerA;
  public final SparkClosedLoopController m_intakePivotControllerB;

  public IntakeIOSparkMax() {
    m_intakeMotorRun = new SparkMax(CANConstants.MOTOR_INTAKE_DRIVE_ID, MotorType.kBrushless);
    m_intakeMotorPivotA = new SparkMax(CANConstants.MOTOR_INTAKE_PIVOT_A_ID, MotorType.kBrushless);
    m_intakeMotorPivotB = new SparkMax(CANConstants.MOTOR_INTAKE_PIVOT_B_ID, MotorType.kBrushless);

    SparkMaxConfig config = new SparkMaxConfig();
    config.idleMode(SparkMaxConfig.IdleMode.kCoast);
    config.openLoopRampRate(0.25);
    config.smartCurrentLimit(40);

    config.closedLoop.pid(0.0, 0.0, 0.0);
    config.closedLoop.outputRange(-1.0, 1.0);

    m_intakeMotorRun.configure(
        config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    SparkMaxConfig pivotConfig = new SparkMaxConfig();
    pivotConfig.idleMode(SparkMaxConfig.IdleMode.kBrake);
    pivotConfig.smartCurrentLimit(40);
    // Configure encoder to output in degrees based on the gear ratio
    pivotConfig.encoder.positionConversionFactor(Constants.kIntakePositionConversionRatio);

    m_intakeMotorPivotA.configure(
        pivotConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    m_intakeMotorPivotA.getEncoder().setPosition(0.0); // Assume starting position is 0

    m_intakePivotControllerA = m_intakeMotorPivotA.getClosedLoopController();

    config.follow(m_intakeMotorPivotA);
    m_intakeMotorPivotB.configure(
        pivotConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    m_intakeMotorPivotB.getEncoder().setPosition(0.0); // Assume starting position is 0

    m_intakePivotControllerB = m_intakeMotorPivotA.getClosedLoopController();
  }

  @Override
  public void updateInputs(IntakeIOInputs inputs) {
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
    m_intakeMotorRun.setVoltage(volts);
  }

  @Override
  public void setPivotVoltage(double volts) {
    m_intakeMotorPivotA.setVoltage(volts);
    m_intakeMotorPivotB.setVoltage(volts);
  }

  @Override
  public void setPivotTargetPos(double theta) {
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
}
