package frc.robot.subsystems.loader;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import org.littletonrobotics.junction.AutoLog;

public interface LoaderIO {
  @AutoLog
  public static class LoaderIOInputs {
    public Voltage appliedVolts = Volts.of(0.0);
    public Current currentAmps = Amps.of(0.0);
    public AngularVelocity velocityRPM = RPM.of(0.0);
  }

  public default void updateInputs(LoaderIOInputs inputs) {}

  public default void setVoltage(double volts) {}

  public default void stop() {}
}
