package frc.robot.subsystems.turret;

import edu.wpi.first.math.util.Units;
import org.littletonrobotics.junction.networktables.LoggedNetworkBoolean;
import org.littletonrobotics.junction.networktables.LoggedNetworkNumber;

public class TurretConstants {
  // Is tuning through advantageKit
  public static final boolean IS_ADVANTAGE_TUNING = false;
  // Is finding tuning through sysid
  public static final boolean IS_SYSID_TUNING = false;
  // PID values
  // TODO: Tune
  // Position PID values
  public static final double kpP = 0.05;
  public static final double kpI = 0.001;
  public static final double kpD = 0.01;
  // Velocity PID values
  public static final double kvP = 0.05;
  public static final double kvI = 0.001;
  public static final double kvD = 0.01;

  // TODO Implement live tuning for PID values using advantagekit LoggedNetworkNumbers
  // FeedForward Values
  // TODO: Tune
  public static final double kS = 0.01; // Static volts
  public static final double kG = 0.01; // V/rad/s
  public static final double kV = 0.01; // V/rad/s^2
  public static final double kA = 0.02; // Period in seonds
  // Max motion values
  public static final double MAX_VELOCITY =
      Units.degreesToRadians(360); // 1 FUll rotation per second
  public static final double MAX_ACCELERATION = Units.degreesToRadians(360); // TODO:
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

  // Setup LoggedNetworkNumbers for live PID and Feedforward tuning through advantagekit
  // PID
  public static final LoggedNetworkBoolean tuningSlot =
      new LoggedNetworkBoolean("Turret/slot", true); // true for position, false for velocity
  public static final LoggedNetworkNumber kPLogged = new LoggedNetworkNumber("Turret/kP", kpP); // P
  public static final LoggedNetworkNumber kILogged = new LoggedNetworkNumber("Turret/kI", kpI);
  public static final LoggedNetworkNumber kDLogged = new LoggedNetworkNumber("Turret/kD", kpD);

  // Feedforward
  public static final LoggedNetworkNumber kSLogged = new LoggedNetworkNumber("Turret/kS", kS);
  public static final LoggedNetworkNumber kGLogged = new LoggedNetworkNumber("Turret/kG", kG);
  public static final LoggedNetworkNumber kVLogged = new LoggedNetworkNumber("Turret/kV", kV);
  public static final LoggedNetworkNumber kALogged = new LoggedNetworkNumber("Turret/kA", kA);
}
