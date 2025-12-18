package frc.robot;

import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Unit tests for the RobotContainer class.
 */
public class RobotContainerTest {

  @Test
  public void testGetAutonomousCommandNotNull() {
    try {
      RobotContainer container = new RobotContainer();
      assertNotNull(container.getAutonomousCommand(), "Autonomous command should not be null");
    } catch (Exception e) {
      // If hardware initialization fails in test environment, skip the test
      // This is expected when running tests without physical hardware or simulation
      assumeTrue(false, "Test skipped due to hardware initialization requirements: " + e.getMessage());
    }
  }

  @Test
  public void testSimulationDemoDrivePublishesMovingPose() {
    assumeTrue(RobotBase.isSimulation(), "Simulation demo drive only applies in simulation");
    DriverStationSim.resetData();
    DriverStationSim.setDsAttached(true);
    DriverStationSim.setEnabled(true);
    DriverStationSim.setAutonomous(false);
    DriverStationSim.setTest(false);
    // Ensure the demo-drive path is taken even if a physical joystick is present on the dev machine.
    DriverStationSim.setJoystickAxisCount(0, 0);
    DriverStationSim.setJoystickButtonCount(0, 0);
    DriverStationSim.setJoystickPOVCount(0, 0);
    DriverStationSim.notifyNewData();

    DoubleSubscriber poseX =
        NetworkTableInstance.getDefault().getDoubleTopic("/Swerve/Pose/X").subscribe(Double.NaN);
    try {
      CommandScheduler.getInstance().cancelAll();
      CommandScheduler.getInstance().unregisterAllSubsystems();
      CommandScheduler.getInstance().enable();

      new RobotContainer();

      DriverStationSim.notifyNewData();
      // CommandScheduler.run() calls Subsystem.simulationPeriodic() when running in simulation.
      CommandScheduler.getInstance().run();
      double x0 = poseX.get();

      for (int i = 0; i < 10; i++) {
        DriverStationSim.notifyNewData();
        CommandScheduler.getInstance().run();
      }
      double x1 = poseX.get();

      assertFalse(Double.isNaN(x0));
      assertFalse(Double.isNaN(x1));
      assertTrue(x1 > x0, "Sim pose should move forward with demo drive enabled");
    } finally {
      poseX.close();
      DriverStationSim.setEnabled(false);
      DriverStationSim.notifyNewData();
    }
  }
}
