package frc.robot.subsystems.turret;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.constants.Constants.CANConstants;
import frc.robot.constants.DriveConstants;

public class TurretIOSparkMax implements TurretIO {
  private final SparkMax m_turretMotor;
  private SparkClosedLoopController m_pidController;
  private final SparkMaxConfig m_config;

  public TurretIOSparkMax() {
    m_turretMotor = new SparkMax(CANConstants.MOTOR_TURRET_ID, MotorType.kBrushless);
    m_config = new SparkMaxConfig();
    // Configure motor settings
    m_config.smartCurrentLimit(TurretConstants.kCurrentLimit);
    m_config.idleMode(SparkMaxConfig.IdleMode.kBrake);

    m_config.encoder.positionConversionFactor(TurretConstants.kPositionConversionRatio);
    m_config.encoder.velocityConversionFactor(TurretConstants.kVelocityConversionRatio);

    m_config.closedLoop.pid(
        TurretConstants.kpP,
        TurretConstants.kpI,
        TurretConstants.kpD,
        DriveConstants.kDrivetrainPositionPIDSlot);
    m_config.closedLoop.pid(
        TurretConstants.kvP,
        TurretConstants.kvI,
        TurretConstants.kvD,
        DriveConstants.kDrivetrainVelocityPIDSlot);

    m_config.closedLoop.outputRange(TurretConstants.kMin, TurretConstants.kMax);

    m_config.closedLoop.maxMotion.cruiseVelocity(1000);
    m_config.closedLoop.maxMotion.maxAcceleration(500);
    m_config.closedLoop.maxMotion.allowedProfileError(0.5);
    m_turretMotor.configure(
        m_config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    m_pidController = m_turretMotor.getClosedLoopController();
  }

  @Override
  public void updateInputs(TurretIOInputs inputs) {
    inputs.appliedVolts =
        Volts.of(m_turretMotor.getAppliedOutput() * m_turretMotor.getBusVoltage());
    inputs.currentAmps = Amps.of(m_turretMotor.getOutputCurrent());
    inputs.positionRadians = Radians.of(m_turretMotor.getEncoder().getPosition());
    inputs.velocityRPM = RPM.of(m_turretMotor.getEncoder().getVelocity());
  }

  @Override
  public void setPosition(double position, double feedforward) {
    m_pidController.setSetpoint(
        position,
        SparkBase.ControlType.kPosition,
        DriveConstants.kDrivetrainPositionPIDSlot,
        feedforward);
  }

  @Override
  public void setVelocity(double velocity, double feedforward) {
    m_pidController.setSetpoint(
        velocity,
        SparkBase.ControlType.kVelocity,
        DriveConstants.kDrivetrainVelocityPIDSlot,
        feedforward);
  }

  @Override
  public void setVoltage(double volts) {
    m_turretMotor.setVoltage(volts);
  }

  @Override
  public void updatePIDValues(double kP, double kI, double kD, ClosedLoopSlot slot) {
    m_config.closedLoop.pid(kP, kI, kD, slot);
    m_turretMotor.configure(
        m_config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_pidController = m_turretMotor.getClosedLoopController();
  }

  @Override
  public void stop() {
    m_turretMotor.stopMotor();
  }
}
