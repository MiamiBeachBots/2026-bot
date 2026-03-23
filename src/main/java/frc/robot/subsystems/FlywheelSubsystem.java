package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Volts;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.DriveConstants;
import frc.robot.RobotTelemetry;
import frc.robot.constants.Constants.CANConstants;
import frc.robot.utils.HelperFunctions;
import frc.robot.utils.TunableControls.ControlConstants;
import frc.robot.utils.TunableControls.TunableProfiledController;
import frc.robot.utils.TunableControls.TunableControlConstants;


import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.LoggedNetworkNumber;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.sim.SparkMaxSim;
import com.revrobotics.sim.SparkRelativeEncoderSim;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import frc.robot.constants.Constants.CANConstants;

public class FlywheelSubsystem extends SubsystemBase {
  //Declare Motor
  private final SparkMax m_flywheelMotor; // Main Flywheel Motor
  private final RelativeEncoder m_flywheelEncoder; // Encoder for the flywheel motor

  //Declare Simulated motor
  private final DCMotor m_mainGearbox; //Required for simulated motor;
  private final SparkMaxSim m_flywheelMotorSim; 
  private final SparkRelativeEncoderSim m_flywheelEncoderSim; // Simulated encoder for the flywheel motor

  //Motor Configs
  private final SparkMaxConfig m_flywheelConfig = new SparkMaxConfig();
  //PID 
  private final ControlConstants CONTROL_CONSTANTS;
  private final TunableControlConstants TUNABLE_CONSTANTS;
  private final TunableProfiledController m_flywheelPIDController;
  // general drive constants
  // https://www.chiefdelphi.com/t/encoders-velocity-to-m-s/390332/2
  // https://sciencing.com/convert-rpm-linear-speed-8232280.html
  
  private final double kWheelDiameter = Units.inchesToMeters(4); // meters
  private final double kGearRatio = 1;  
  // basically converted from rotations to to radians to then meters using the wheel diameter.
  // the diameter is already *2 so we don't need to multiply by 2 again.
  private final double kPositionConversionRatio = (Math.PI * kWheelDiameter) / kGearRatio;
  private final double kVelocityConversionRatio = kPositionConversionRatio / 60;


  //PID coefficients
    //TODO: Tune PID
    private final double kP = 0;
    private final double kI = 0;
    private final double kD = 0;
    private final double kIz = 0;
    private final double kMaxOutput = 1;
    private final double kMinOutput = -1;
   // setup feedforward
   //TODO: Profile feedforward
  private final double kS = 0;
  private final double kV = 0;
  private final double kA = 0;


  // setup SysID for auto profiling
  private final SysIdRoutine m_sysIdRoutine;

  // current limit
  private final int k_CurrentLimit = 60;

  private boolean isTuning = false;

  public FlywheelSubsystem() {
    //Create Motor
    m_flywheelMotor = new SparkMax(CANConstants.MOTOR_FIRE_ID, SparkMax.MotorType.kBrushless);

    //Create simulated Motor
    m_mainGearbox = DCMotor.getNEO(1);
    m_flywheelMotorSim = new SparkMaxSim(m_flywheelMotor, m_mainGearbox);

    //Copilot please stop giving me multi-line suggestions, single line autofills are all i need
    //Danke

    //Get Encoders
    m_flywheelEncoder = m_flywheelMotor.getEncoder();
    m_flywheelEncoderSim = m_flywheelMotorSim.getRelativeEncoderSim();

    //Set Brake Mode
    m_flywheelConfig.idleMode(IdleMode.kBrake);
    
    //Set Current Limit
    m_flywheelConfig.smartCurrentLimit(k_CurrentLimit);




    m_flywheelConfig.encoder.positionConversionFactor(kPositionConversionRatio);
    m_flywheelConfig.encoder.velocityConversionFactor(kVelocityConversionRatio);


    m_flywheelConfig.closedLoop.pid(kP, kI, kD, DriveConstants.kDrivetrainVelocityPIDSlot);
    m_flywheelConfig.closedLoop.iZone(kIz);
    m_flywheelConfig.closedLoop.outputRange(kMinOutput, kMaxOutput, DriveConstants.kDrivetrainVelocityPIDSlot);

    //TODO: TUNE
    CONTROL_CONSTANTS = new ControlConstants()
      .withPID(kP, kI, kD)
      .withTolerance(0.04, 0.1);
    TUNABLE_CONSTANTS = new TunableControlConstants("Flywheel", CONTROL_CONSTANTS);
    m_flywheelPIDController = new TunableProfiledController(TUNABLE_CONSTANTS);
  
    // setup SysID for auto profiling
    m_sysIdRoutine =
        new SysIdRoutine(
            new SysIdRoutine.Config(
              null, null, null,
              (state) -> Logger.recordOutput("SysIdTestState", state.toString())
            ),
            new SysIdRoutine.Mechanism(
                (voltage) -> this.setVoltage(voltage),
                null, // No log consumer, since data is recorded by AdvantageKit
                this));
    m_flywheelMotor.configure(m_flywheelConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

   public void setVoltage(Voltage voltage) {
    m_flywheelMotor.setVoltage(voltage.in(Volts));
  }
  public Command sysIdQuasistatic(SysIdRoutine.Direction direction) {
    return m_sysIdRoutine.quasistatic(direction);
  }

  public Command sysIdDynamic(SysIdRoutine.Direction direction) {
    return m_sysIdRoutine.dynamic(direction);
  }

  //Sets the shooter speed in m/s
  public void setShooterSpeed(double targetVelocity){
    m_flywheelPIDController.setGoal(targetVelocity);
  }
//TODO: Add method to set shooter speed in RPM if needed

  

  /** Check if shooter is at a given Speed */
  public Boolean isAtSpeedTolerance(double speed) {
    return HelperFunctions.inDeadzone(m_flywheelEncoder.getVelocity(), 0.1);
  }

  /** Stops the fire motor. */
  public void stop() {
    setShooterSpeed(0);
  }


  @Override
  public void periodic() {
    if(isTuning) {
    
    }
    //TODO: Telemetry


  }

  @Override
  public void simulationPeriodic() {
    // Broadcast for Python App
    //Sorry thalia
    //RobotTelemetry.putBoolean("Sim_IsFiring", Math.abs(m_inputs.appliedVolts) > 1.2);
  }
}
