package frc.robot.subsystems;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotTelemetry;
import frc.robot.utils.SubsystemTuningTab;
import frc.robot.utils.SubsystemTuningTab.TunableDouble;
import org.littletonrobotics.junction.Logger;

public class FireControlSubsystem extends SubsystemBase {
  private final FireControlIO m_io;
  private final FireControlIOInputsAutoLogged m_inputs = new FireControlIOInputsAutoLogged();
  private edu.wpi.first.math.filter.SlewRateLimiter m_spinDownLimiter;
  private double m_spinDownRateRpmPerSecond = 5000.0;
  private final TunableDouble m_feedforwardKs;
  private final TunableDouble m_feedforwardKv;
  private final TunableDouble m_feedforwardKa;
  private final TunableDouble m_spinDownRateEntry;

  public FireControlSubsystem(FireControlIO io) {
    m_io = io;
    SubsystemTuningTab tuningTab = new SubsystemTuningTab("Fire Control");
    m_feedforwardKs = tuningTab.addDouble("Shooter Feedforward kS", 0.1);
    m_feedforwardKv = tuningTab.addDouble("Shooter Feedforward kV", 0.12);
    m_feedforwardKa = tuningTab.addDouble("Shooter Feedforward kA", 0.01);
    m_spinDownRateEntry = tuningTab.addDouble("Spin Down Rate RPM Per Sec", 5000.0);

    m_spinDownLimiter = new edu.wpi.first.math.filter.SlewRateLimiter(m_spinDownRateRpmPerSecond);
  }

  /**
   * Sets the shooter to a specific target RPM.
   *
   * @param targetRPM The target RPM for the flywheel.
   */
  public void setShooterRPM(double targetRPM) {
    syncTuning();
    if (targetRPM <= 0) {
      targetRPM = m_spinDownLimiter.calculate(0);
      if (targetRPM < 50) {
        stop();
        return;
      }
    } else {
      m_spinDownLimiter.reset(targetRPM);
    }
    // Convert target RPM to target revs/second for Feedforward
    double feedforwardVoltage =
        new SimpleMotorFeedforward(
                m_feedforwardKs.get(), m_feedforwardKv.get(), m_feedforwardKa.get())
            .calculate(targetRPM / 60.0);

    m_io.setVelocity(targetRPM, feedforwardVoltage);
  }

  /**
   * Checks if the flywheel is at the target RPM within a given tolerance.
   *
   * @param targetRPM The target RPM.
   * @param tolerance The allowed RPM difference.
   * @return True if the RPM is within the tolerance.
   */
  public boolean isAtRPM(double targetRPM, double tolerance) {
    double currentRPM = m_inputs.velocityRPM;
    return Math.abs(currentRPM - targetRPM) <= tolerance;
  }

  /** Stops the fire motor. */
  public void stop() {
    m_spinDownLimiter.reset(0);
    m_io.stop();
  }

  private void syncTuning() {
    double desiredSpinDownRate = m_spinDownRateEntry.get();
    if (Math.abs(desiredSpinDownRate - m_spinDownRateRpmPerSecond) > 1e-9) {
      m_spinDownRateRpmPerSecond = desiredSpinDownRate;
      m_spinDownLimiter = new edu.wpi.first.math.filter.SlewRateLimiter(m_spinDownRateRpmPerSecond);
      m_spinDownLimiter.reset(m_inputs.velocityRPM);
    }
  }

  @Override
  public void periodic() {
    syncTuning();
    m_io.updateInputs(m_inputs);
    Logger.processInputs("FireControl", m_inputs);

    // Debugging current fire motor speed and RPM
    RobotTelemetry.putNumber("Fire Motor Speed Output", m_inputs.appliedVolts / 12.0);
    RobotTelemetry.putNumber("Fire Motor RPM", m_inputs.velocityRPM);
  }

  @Override
  public void simulationPeriodic() {
    // Broadcast for Python App
    RobotTelemetry.putBoolean("Sim_IsFiring", Math.abs(m_inputs.appliedVolts) > 1.2);
  }
}
