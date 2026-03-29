package frc.robot.subsystems.turret;

import edu.wpi.first.math.util.Units;

public class TurretConstants {
  // PID values
  // TODO: Tune

  public static final double kP = 0.05;
  public static final double kI = 0.001;
  public static final double kD = 0.01;
  //TODO Implement live tuning for PID values using advantagekit LoggedNetworkNumbers
  // FeedForward Values
  // TODO: Tune
  public static final double kS = 0.01; // Static volts
  public static final double kG = 0.01; // V/rad/s
  public static final double kV = 0.01; // V/rad/s^2
  public static final double kA = 0.02; // Period in seonds
  // Max motion values
  public static final double MAX_VELOCITY =
      Units.degreesToRadians(360); // 1 FUll rotation per second
  public static final double MAX_ACCELERATION = Units.degreesToRadians(180); // TODO:
  public static final double kMin = -1.0;
  public static final double kMax = 1.0;
  // general drive constants
  // https://www.chiefdelphi.com/t/encoders-velocity-to-m-s/390332/2
  // https://sciencing.com/convert-rpm-linear-speed-8232280.html
  public static final double kGearRatio =
      200.0 / 18.0; // 18 Tooth driving gear to 200 tooth driven gear, 11.1:1
  // basically converted from rotations to to radians to then meters using the wheel diameter.
  // the diameter is already *2 so we don't need to multiply by 2 again.
  public static final double kPositionConversionRatio =
      (Math.PI * 2) / kGearRatio; // Motor rotation -> Turret rotation (radians) Per minute)
  public static final double kVelocityConversionRatio =
      kPositionConversionRatio / 60; // Radians/min -> Radians/s
  public static final double kPositionConversionRatioAbsolute = (Math.PI * 2); // Radians per minute
  public static final double kVelocityConversionRatioAbsolute =
      kPositionConversionRatioAbsolute / 60; // Radians Per second  
    public static final int kCurrentLimit = 25;
}
