package frc.robot.subsystems;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.SpeedConstants;
import frc.robot.utils.SubsystemTuningTab;
import frc.robot.utils.SubsystemTuningTab.TunableDouble;
import org.littletonrobotics.junction.Logger;

/** Subsystem handling the 3-motor loader. */
public class LoaderSubsystem extends SubsystemBase {

  private final LoaderIO m_io;
  private final LoaderIOInputsAutoLogged m_inputs = new LoaderIOInputsAutoLogged();
  private SlewRateLimiter m_speedLimiter;
  private double m_manualSlewRateLimit = 2.0;
  private final TunableDouble m_manualSlewRateLimitEntry;

  public LoaderSubsystem(LoaderIO io) {
    m_io = io;
    SubsystemTuningTab tuningTab = new SubsystemTuningTab("Loader");
    m_manualSlewRateLimitEntry = tuningTab.addDouble("Manual Slew Rate", 2.0);
    // Software Slew Rate Limiter for manual inputs (acceleration cap: full speed in 0.5s)
    m_speedLimiter = new SlewRateLimiter(m_manualSlewRateLimit);
  }

  /**
   * Sets the speed of the loader motor.
   *
   * @param speed Speed from -1.0 to 1.0. positive spins inward.
   */
  public void setLoaderSpeed(double speed) {
    syncTuning();
    double rawSpeed =
        SpeedConstants.adjustSpeed(
            speed, SpeedConstants.LOADER_1_MAX_SPEED, SpeedConstants.LOADER_1_SENSITIVITY);
    double adjustedSpeed = m_speedLimiter.calculate(rawSpeed);

    m_io.setVoltage(adjustedSpeed * 12.0);
  }

  /** Stops the loader. */
  public void stop() {
    m_io.stop();
    m_speedLimiter.reset(0);
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
    Logger.processInputs("Loader", m_inputs);
  }
}
