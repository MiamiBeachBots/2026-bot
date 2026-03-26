package frc.robot.commands;

import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.CameraConstants;
import frc.robot.Constants;
import frc.robot.subsystems.CameraSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.TurretSubsystem;
import org.photonvision.targeting.PhotonTrackedTarget;

public class AutoAimCommand extends Command {
  private final TurretSubsystem m_turret;
  private final CameraSubsystem m_cameraSubsystem;
  private final DriveSubsystem m_driveSubsystem;

  private static class ShotData {
    double angle;
    double force;

    public ShotData(double angle, double force) {
      this.angle = angle;
      this.force = force;

    }

  }

  public AutoAimCommand(TurretSubsystem turret, CameraSubsystem c_subsystem, DriveSubsystem d_subsystem) {
    m_turret = turret;
    m_cameraSubsystem = c_subsystem;
    m_driveSubsystem = d_subsystem;
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
    m_cameraSubsystem.targetingCamera1Result.ifPresent(result -> {
      for (PhotonTrackedTarget target : result.getTargets()) {
        if (target.getFiducialId() != CameraConstants.HUB_FIDUCIUAL_ID) continue;
        ShotData shotData = calculateShot(target.getBestCameraToTarget(), m_driveSubsystem.getRotation2d(), m_driveSubsystem.getSpeeds());
        m_turret.setTargetPosition(shotData.angle / (Math.PI * 2));
      }
    });
  }

  private ShotData calculateShot(Transform3d target, Rotation2d chassisRotation, ChassisSpeeds chassisSpeeds) {
    double shooterAngleCos = Math.cos(Constants.SHOOTER_ANGLE);
    double shooterAngleTan = Math.tan(Constants.SHOOTER_ANGLE);

    Translation2d relativeXYDisplacement = target.getTranslation().toTranslation2d();
    double distance = relativeXYDisplacement.getDistance(Translation2d.kZero);
    Translation2d xyDisplacement = relativeXYDisplacement.rotateBy(chassisRotation.times(-1));
    Rotation2d angleToTarget = target.getRotation().toRotation2d();
    double radiansToTarget = angleToTarget.getRadians();

    double verticalDisplacement = (Constants.HUB_HEIGHT - Constants.SHOOTER_HEIGHT - (Constants.BALL_DIAMETER / 2));
    double launchSpeed = Math.sqrt((Constants.AUTOAIM_GRAVITY * Math.pow(distance, 2)) / (2 * Math.pow(shooterAngleCos, 2) * (distance * shooterAngleTan - verticalDisplacement)));
    double xySpeed = shooterAngleCos * launchSpeed;
    double airtime = distance / xySpeed;

    // Velocity from a top-down view
    double xVelocity = Math.cos(radiansToTarget) * xySpeed;
    double yVelocity = Math.sin(radiansToTarget) * xySpeed;

    double xPrediction = (xVelocity + chassisSpeeds.vxMetersPerSecond) * airtime;
    double yPrediction = (yVelocity + chassisSpeeds.vyMetersPerSecond) * airtime;

    Transform2d correctedTargetXYDisplacement = new Transform2d(xyDisplacement.getX() * 2 - xPrediction, xyDisplacement.getY() * 2 - yPrediction, Rotation2d.kZero);
    double correctedAngle = Math.atan2(correctedTargetXYDisplacement.getY(), correctedTargetXYDisplacement.getX());
    double correctedForce = correctedTargetXYDisplacement.getY() / (Math.sin(correctedAngle) * airtime);

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
