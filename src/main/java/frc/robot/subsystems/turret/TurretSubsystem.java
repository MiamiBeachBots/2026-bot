package frc.robot.subsystems.turret;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotTelemetry;
import frc.robot.constants.SpeedConstants;
import org.littletonrobotics.junction.Logger;

public class TurretSubsystem extends SubsystemBase {
  private final TurretIO m_io;
  private final TurretIOInputsAutoLogged m_inputs = new TurretIOInputsAutoLogged();
  private final SlewRateLimiter m_speedLimiter;

  // Setup trapezoidal profile for smoothing movement
  private final TrapezoidProfile m_profile =
      new TrapezoidProfile(
          new TrapezoidProfile.Constraints(
              TurretConstants.MAX_VELOCITY, TurretConstants.MAX_ACCELERATION));
  private TrapezoidProfile.State m_goal = new TrapezoidProfile.State();
  private TrapezoidProfile.State m_setpoint = new TrapezoidProfile.State();

  // Setup Feedforward
  private SimpleMotorFeedforward m_feedForward =
      new SimpleMotorFeedforward(
          TurretConstants.kS, TurretConstants.kG, TurretConstants.kV, TurretConstants.kA);
  private double m_previousVelocity = 0;
  public boolean m_pidEnabled = true;

  ;

  private boolean m_isUnwinding = false;

  public TurretSubsystem(TurretIO io) {

    m_io = io;

    // Software Slew Rate Limiter for manual inputs (acceleration cap: full speed in 0.5s)
    m_speedLimiter = new SlewRateLimiter(2.0);
  }

  /** Updates the setpoint */
  private void updateSetpoint(TrapezoidProfile.State goal) {
    m_previousVelocity = m_setpoint.velocity;
    m_setpoint = m_profile.calculate(0.02, m_setpoint, goal);
  }

  /**
   * Sets the speed of the turret motor.
   *
   * @param speed The target speed (-1 to 1) (bool).
   */
  public void setTurretSpeed(double speed) {
    if (m_isUnwinding) return;
    // Add simple range just in case controller has drift
    if (Math.abs(speed) < 0.1) {
      speed = 0;
    }

    double adjustedSpeed =
        m_speedLimiter.calculate(
            SpeedConstants.adjustSpeed(
                speed, SpeedConstants.TURRET_MAX_SPEED, SpeedConstants.TURRET_SENSITIVITY));

    m_io.setVelocity(
       adjustedSpeed * TurretConstants.MAX_VELOCITY, 0);
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
    double currentRotations = m_inputs.positionRadians.magnitude();
    return currentRotations;
  }

  /** Gets the current robot-relative position of the turret in degrees. */
  public double getTurretAngleDegrees() {
    double currentRotations = m_inputs.positionRadians.magnitude();
    return Units.radiansToDegrees(currentRotations);
  }

  /**
   * Sets the target angle of the turret using closed-loop control.
   *
   * @param targetAngleDegrees Target angle in degrees.
   */
  public void setTargetAngle(double targetAngleDegrees) {
    if (m_isUnwinding) return;

    double targetAngleRadians = Units.degreesToRadians(targetAngleDegrees);

    m_goal = new TrapezoidProfile.State(targetAngleRadians, 0);
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
    if (m_isUnwinding) return;
    m_io.stop();
    m_speedLimiter.reset(0); // Reset limiter so next move doesn't jump
  }

  /** Returns whether the turret is currently auto-unwinding. */
  public boolean isUnwinding() {
    return m_isUnwinding;
  }

  public void disablePID() {
    m_pidEnabled = false;
  }

  @Override
  public void periodic() {
    double currentAngle = getTurretAngleDegrees();

    // Check if we exceeded bounds and enter unwinding state
    if (Math.abs(currentAngle) >= 360.0 && !m_isUnwinding && m_pidEnabled) {
      m_isUnwinding = true;
    }

    // Handle unwinding logic
    if (m_isUnwinding) {
      updateSetpoint(new TrapezoidProfile.State(0, 0));

      // Check if we're back near 0 center
      // Stiction and SparkMax deadband with an undertuned PID (kP=0.1) can cause
      // the motor to stall ~18 degrees away from 0.0, so we use a wider 25.0 deg tolerance.
      if (Math.abs(currentAngle) <= 25.0) {
        m_isUnwinding = false;
        // Reset our rate limiter so the driver can cleanly regain control
        m_speedLimiter.reset(0);
      }
    } else if (m_pidEnabled) {
      updateSetpoint(m_goal);
    }
    m_io.setPosition(
        m_setpoint.position, m_feedForward.calculateWithVelocities(m_previousVelocity, m_setpoint.velocity));

    m_io.updateInputs(m_inputs);
    Logger.processInputs("Turret", m_inputs);

    // Output current state of turret motor for debugging
    RobotTelemetry.putNumber("Turret Motor Speed Output", m_inputs.velocityRPM.magnitude() / 12.0);
    RobotTelemetry.putNumber("Turret Position", m_inputs.positionRadians.magnitude());
    RobotTelemetry.putBoolean("Turret Is Unwinding", m_isUnwinding);
  }

  @Override
  public void simulationPeriodic() {}
}
