package frc.robot.subsystems;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotTelemetry;
import frc.robot.constants.Constants;
import frc.robot.constants.SpeedConstants;
import frc.robot.utils.SubsystemTuningTab;
import frc.robot.utils.SubsystemTuningTab.TunableDouble;
import org.littletonrobotics.junction.Logger;

public class TurretSubsystem extends SubsystemBase {
  private final TurretIO m_io;
  private final TurretIOInputsAutoLogged m_inputs = new TurretIOInputsAutoLogged();
  private SlewRateLimiter m_speedLimiter;
  private double m_manualSlewRateLimit = 2.0;
  private final TunableDouble m_manualSlewRateLimitEntry;
  private final TunableDouble m_manualDeadbandEntry;
  private final TunableDouble m_unwindTriggerAngleEntry;
  private final TunableDouble m_unwindResetToleranceEntry;

  private boolean m_isUnwinding = false;

  public TurretSubsystem(TurretIO io) {
    m_io = io;
    SubsystemTuningTab tuningTab = new SubsystemTuningTab("Turret");
    m_manualSlewRateLimitEntry = tuningTab.addDouble("Manual Slew Rate", 2.0);
    m_manualDeadbandEntry = tuningTab.addDouble("Manual Deadband", 0.1);
    m_unwindTriggerAngleEntry = tuningTab.addDouble("Unwind Trigger Angle Deg", 360.0);
    m_unwindResetToleranceEntry = tuningTab.addDouble("Unwind Reset Tolerance Deg", 5.0);
    // Software Slew Rate Limiter for manual inputs (acceleration cap: full speed in 0.5s)
    m_speedLimiter = new SlewRateLimiter(m_manualSlewRateLimit);
  }

  /**
   * Sets the speed of the turret motor.
   *
   * @param speed The target speed (-1 to 1) (bool).
   */
  public void setTurretSpeed(double speed) {
    syncTuning();
    if (m_isUnwinding) return;
    // Add simple range just in case controller has drift
    if (Math.abs(speed) < m_manualDeadbandEntry.get()) {
      speed = 0;
    }
    double adjustedSpeed =
        m_speedLimiter.calculate(
            SpeedConstants.adjustSpeed(
                speed, SpeedConstants.TURRET_MAX_SPEED, SpeedConstants.TURRET_SENSITIVITY));
    m_io.setVoltage(adjustedSpeed * 12.0);
  }

  /**
   * Directly sets the voltage of the turret motor, useful for ProfiledPID + Feedforward outputs.
   *
   * @param volts Output voltage.
   */
  public void setTurretVoltage(double volts) {
    if (m_isUnwinding) return;
    m_io.setVoltage(volts);
  }

  /** Gets the current robot-relative position of the turret in radians. */
  public double getTurretAngleRadians() {
    double currentRotations = m_inputs.positionRotations;
    return (currentRotations / Constants.TURRET_GEAR_RATIO) * 2.0 * Math.PI;
  }

  /** Gets the current robot-relative position of the turret in degrees. */
  public double getTurretAngleDegrees() {
    double currentRotations = m_inputs.positionRotations;
    return (currentRotations / Constants.TURRET_GEAR_RATIO) * 360.0;
  }

  /**
   * Sets the target angle of the turret using closed-loop control.
   *
   * @param targetAngleDegrees Target angle in degrees.
   */
  public void setTargetAngle(double targetAngleDegrees) {
    if (m_isUnwinding) return;
    double targetRotations = (targetAngleDegrees / 360.0) * Constants.TURRET_GEAR_RATIO;
    m_io.setPosition(targetRotations);
  }

  /**
   * Sets the target radians of the turret using closed-loop control.
   *
   * @param targetAngle Target angle in radians.
   */
  public void setTargetRadians(double targetAngle) {
    if (m_isUnwinding) return;
    double targetRotations = (targetAngle / (Math.PI * 2)) * Constants.TURRET_GEAR_RATIO;
    m_io.setPosition(targetRotations);
  }

  /**
   * Checks if the turret is at the specified target angle.
   *
   * @param targetAngleDegrees Target angle in degrees.
   * @param toleranceDegrees Tolerance in degrees.
   * @return True if within tolerance, false otherwise.
   */
  public boolean isAtAngle(double targetAngleDegrees, double toleranceDegrees) {
    return Math.abs(getTurretAngleDegrees() - targetAngleDegrees) <= toleranceDegrees;
  }

  /** Stops the turret motor. */
  public void stop() {
    m_isUnwinding = false;
    m_io.stop();
    m_speedLimiter.reset(0); // Reset limiter so next move doesn't jump
  }

  /** Returns whether the turret is currently auto-unwinding. */
  public boolean isUnwinding() {
    return m_isUnwinding;
  }

  private void syncTuning() {
    double desiredSlewRate = m_manualSlewRateLimitEntry.get();
    if (Math.abs(desiredSlewRate - m_manualSlewRateLimit) > 1e-9) {
      m_manualSlewRateLimit = desiredSlewRate;
      m_speedLimiter = new SlewRateLimiter(m_manualSlewRateLimit);
      m_speedLimiter.reset(m_inputs.appliedVolts / 12.0);
    }
  }

  @Override
  public void periodic() {
    syncTuning();
    m_io.updateInputs(m_inputs);
    Logger.processInputs("Turret", m_inputs);

    double currentAngle = getTurretAngleDegrees();

    // Check if we exceeded bounds and enter unwinding state
    if (Math.abs(currentAngle) >= m_unwindTriggerAngleEntry.get() && !m_isUnwinding) {
      m_isUnwinding = true;
    }

    // Handle unwinding logic
    if (m_isUnwinding) {
      double targetRotations = 0.0;
      m_io.setPosition(targetRotations);

      // Check if we're back near 0 center
      // Narrowed tolerance to 5.0 degrees for better precision before returning control
      if (Math.abs(currentAngle) <= m_unwindResetToleranceEntry.get()) {
        m_isUnwinding = false;
        // Reset our rate limiter so the driver can cleanly regain control
        m_speedLimiter.reset(0);
      }
    }

    // Output current state of turret motor for debugging
    RobotTelemetry.putNumber("Turret Motor Speed Output", m_inputs.appliedVolts / 12.0);
    RobotTelemetry.putNumber("Turret Position", m_inputs.positionRotations);
    RobotTelemetry.putBoolean("Turret Is Unwinding", m_isUnwinding);
  }

  @Override
  public void simulationPeriodic() {}
}
