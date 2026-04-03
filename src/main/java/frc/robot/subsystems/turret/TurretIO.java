package frc.robot.subsystems.turret;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.Volts;

import com.revrobotics.spark.ClosedLoopSlot;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import org.littletonrobotics.junction.AutoLog;

public interface TurretIO {
  @AutoLog
  public static class TurretIOInputs {
    public Voltage appliedVolts = Volts.of(0.0);
    public Current currentAmps = Amps.of(0.0);
    public Angle positionRadians = Angle.ofBaseUnits(0.0, Radians);
    public AngularVelocity velocityRPM = RPM.of(0.0);
  }

  /** Updates input */
  public default void updateInputs(TurretIOInputs inputs) {}

  public default void updateFeedforward(TrapezoidProfile.State setpoint) {}

  /**
   * Moves turret to a specific position
   *
   * @param position The target position for the turret to move to.
   * @param feedforward The feedforward voltage to apply.
   */
  public default void setPosition(double Position, double feedforward) {}

  /**
   * Sets the speed of the turret motor.
   *
   * @param velocity The target speed in Rotations per second.
   * @param feedforward The feedforward voltage to apply.
   */
  public default void setVelocity(double velocity, double feedforward) {}

  public default void updatePIDValues(double kP, double kI, double kD, ClosedLoopSlot slot) {}

  public default void setVoltage(double volts) {}

  public default void stop() {}
}
