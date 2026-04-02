package frc.robot.commands;

import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.CameraConstants;
import frc.robot.Constants;
import frc.robot.subsystems.CameraSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FireControlSubsystem;
import frc.robot.subsystems.TurretSubsystem;
import org.photonvision.targeting.PhotonTrackedTarget;

public class AutoAimCommand extends Command {
  private final TurretSubsystem m_turret;
  private final DriveSubsystem m_driveSubsystem;
  private final FireControlSubsystem m_fireSubsystem;

  private static class ShotData {
    double angle;
    double force;

    public ShotData(double angle, double force) {
      this.angle = angle;
      this.force = force;
    }
  }

  public AutoAimCommand(
      TurretSubsystem turret, DriveSubsystem d_subsystem, FireControlSubsystem f_subsystem) {
    m_turret = turret;
    m_driveSubsystem = d_subsystem;
    m_fireSubsystem = f_subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(turret);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("AutoAimCommand Scheduled - Handing over to AutoAim");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // TODO: Friend's auto-aim logic here
    ShotData shotData = calculateShot(Constants.HUB_POSITION, Constants.HUB_HEIGHT);
    double turretRotations = radiansToRotations(shotData.angle - m_driveSubsystem.getRotation2d().getRadians());
    double shooterRPM = linearToRotationalVelocity(shotData.force, Constants.SHOOTER_RADIUS) * 60;
    m_turret.setTargetPosition(turretRotations);
    m_fireSubsystem.setRPM(shooterRPM);
    Commands.waitUntil(m_turret.isAtPosition(turretRotations, ));

  }

  private double linearToRotationalVelocity(double velocity, double radius) {
      return velocity / radius;

  }

  private double radiansToRotations(double theta) {
      return theta / (Math.PI * 2);

  }

  private ShotData calculateShot(Translation2d target, double targetHeight) {
    double shooterAngleCos = Math.cos(Constants.SHOOTER_ANGLE);
    double shooterAngleTan = Math.tan(Constants.SHOOTER_ANGLE);

    Pose2d pose = m_driveSubsystem.getPose();
    Translation2d position = pose.getTranslation();
    double distance = position.getDistance(target);
    Translation2d xyDisplacement = target.minus(position);
    double angleToTarget = Math.atan2(xyDisplacement.getY(), xyDisplacement.getX());

    double verticalDisplacement =
        (targetHeight - Constants.SHOOTER_HEIGHT - (Constants.BALL_DIAMETER / 2));
    double launchSpeed =
        Math.sqrt(
            (Constants.AUTOAIM_GRAVITY * Math.pow(distance, 2))
                / (2
                    * Math.pow(shooterAngleCos, 2)
                    * (distance * shooterAngleTan - verticalDisplacement)));
    double xySpeed = shooterAngleCos * launchSpeed;
    double airtime = distance / xySpeed;

    // Velocity from a top-down view
    double xVelocity = Math.cos(angleToTarget) * xySpeed;
    double yVelocity = Math.sin(angleToTarget) * xySpeed;

    ChassisSpeeds chassisSpeeds = m_driveSubsystem.getSpeeds();
    double xPrediction = (xVelocity + chassisSpeeds.vxMetersPerSecond) * airtime;
    double yPrediction = (yVelocity + chassisSpeeds.vyMetersPerSecond) * airtime;

    Translation2d correctedTargetXYDisplacement =
        new Translation2d(
            xyDisplacement.getX() * 2 - xPrediction,
            xyDisplacement.getY() * 2 - yPrediction);
    double correctedAngle =
        Math.atan2(correctedTargetXYDisplacement.getY(), correctedTargetXYDisplacement.getX());
    double correctedForce =
        correctedTargetXYDisplacement.getY() / (Math.sin(correctedAngle) * airtime);

    return new ShotData(correctedAngle, correctedForce);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false; // Run until interrupted/toggled off
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_turret.stop();
    System.out.println("AutoAimCommand Ended - Returning to Manual Control");
  }
}
