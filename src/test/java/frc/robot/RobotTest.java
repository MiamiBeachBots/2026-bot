package frc.robot;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Robot class.
 */
public class RobotTest {

  @Test
  public void testRobotInstantiation() {
    Robot robot = new Robot();
    assertNotNull(robot, "Robot should be instantiated");
  }

  @Test
  public void testRobotInitDoesNotThrow() {
    Robot robot = new Robot();
    assertDoesNotThrow(() -> robot.robotInit(), "robotInit should not throw exceptions");
  }

  @Test
  public void testRobotPeriodicDoesNotThrow() {
    Robot robot = new Robot();
    robot.robotInit();
    assertDoesNotThrow(() -> robot.robotPeriodic(), "robotPeriodic should not throw exceptions");
  }

  @Test
  public void testAutonomousInitDoesNotThrow() {
    Robot robot = new Robot();
    robot.robotInit();
    assertDoesNotThrow(() -> robot.autonomousInit(), "autonomousInit should not throw exceptions");
  }

  @Test
  public void testTeleopInitDoesNotThrow() {
    Robot robot = new Robot();
    robot.robotInit();
    assertDoesNotThrow(() -> robot.teleopInit(), "teleopInit should not throw exceptions");
  }
}
