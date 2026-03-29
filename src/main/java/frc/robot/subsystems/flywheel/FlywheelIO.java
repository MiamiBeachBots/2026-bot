package frc.robot.subsystems.flywheel;

import org.littletonrobotics.junction.AutoLog;

public interface FlywheelIO {
  @AutoLog
  public static class FlywheelIOInputs {
    public double appliedVolts = 0.0;
    public double currentAmps = 0.0;
    public double velocityRPM = 0.0;
  }

  public default void updateInputs(FlywheelIOInputs inputs) {}

  public default void setVelocity(double velocityRPM, double feedforwardVolts) {}

  public default void stop() {}
}
