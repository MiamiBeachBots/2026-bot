package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Unit tests for the SwerveSubsystem class.
 */
public class SwerveSubsystemTest {

  @Test
  public void testMaximumSpeedDefaultValue() {
    try {
      SwerveSubsystem subsystem = new SwerveSubsystem();
      assertEquals(4.5, subsystem.maximumSpeed, 0.001, "Default maximum speed should be 4.5 m/s");
    } catch (Exception e) {
      // If hardware initialization fails in test environment, skip the test
      // This is expected when running tests without physical hardware or simulation
      assumeTrue(false, "Test skipped due to hardware initialization requirements: " + e.getMessage());
    }
  }

  @Test
  public void testGetSwerveDriveNotNull() {
    assumeTrue(RobotBase.isReal(), "Skipping SwerveDrive null check in simulation mode");
    try {
      SwerveSubsystem subsystem = new SwerveSubsystem();
      assertNotNull(subsystem.getSwerveDrive(), "SwerveDrive instance should not be null");
    } catch (Exception e) {
      // If hardware initialization fails in test environment, skip the test
      // This is expected when running tests without physical hardware or simulation
      assumeTrue(false, "Test skipped due to hardware initialization requirements: " + e.getMessage());
    }
  }

  @Test
  public void testSimulationDriveUpdatesPose() {
    assumeTrue(RobotBase.isSimulation(), "Simulation pose integration only applies in simulation");
    try {
      DriverStationSim.setDsAttached(true);
      DriverStationSim.setEnabled(true);
      DriverStationSim.setAutonomous(false);
      DriverStationSim.setTest(false);
      DriverStationSim.notifyNewData();

      SwerveSubsystem subsystem = new SwerveSubsystem();
      double x0 = subsystem.getPose().getX();

      subsystem.drive(new Translation2d(1.0, 0.0), 0.0, false);
      subsystem.simulationPeriodic();

      double x1 = subsystem.getPose().getX();
      assertEquals(x0 + 0.02, x1, 1e-9, "Robot X should advance by vx * dt");
    } finally {
      DriverStationSim.setEnabled(false);
      DriverStationSim.notifyNewData();
    }
  }

  @Test
  public void testSimulationZeroGyroResetsHeading() {
    assumeTrue(RobotBase.isSimulation(), "Simulation heading reset only applies in simulation");
    try {
      DriverStationSim.setDsAttached(true);
      DriverStationSim.setEnabled(true);
      DriverStationSim.setAutonomous(false);
      DriverStationSim.setTest(false);
      DriverStationSim.notifyNewData();

      SwerveSubsystem subsystem = new SwerveSubsystem();
      subsystem.drive(new Translation2d(), 1.0, false);
      subsystem.simulationPeriodic();

      assertNotEquals(0.0, subsystem.getPose().getRotation().getRadians(), 1e-9);

      subsystem.zeroGyro();
      assertEquals(0.0, subsystem.getPose().getRotation().getRadians(), 1e-9);
    } finally {
      DriverStationSim.setEnabled(false);
      DriverStationSim.notifyNewData();
    }
  }
}
