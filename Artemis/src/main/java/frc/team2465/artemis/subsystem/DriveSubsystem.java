package frc.team2465.artemis.subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.command.TeleOpDriveCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;
import org.strykeforce.thirdcoast.swerve.SwerveDriveConfig;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class DriveSubsystem extends PIDSubsystem {

  private static final double DRIVE_SETPOINT_MAX = 0.0;
  private static final double ROBOT_LENGTH = 20.5;
  private static final double ROBOT_WIDTH = 22.0;

  public static final double DEADBAND = 0.1;

  public boolean pidOwner = true; // True is rotate, false is drive distance.
  // Rotation PID values
  public static double rP = 0.03;
  public static double rI = 0.0000000000000007;
  public static double rD = 0.16;
  public double tolerance_degrees = 0.3;

  // Drive Distance PID Values
  public static double dP = 0.0;
  public static double dI = 0.0;
  public static double dD = 0.0;
  public int tolerance_ticks = 8;
  public double pidOutput = 0;
  public int driveError = 0;

  public static double tuneP = 0;
  public static double tuneI = 0;
  public static double tuneD = 0;
  public static double tuneF = 0;

  public TalonSRX[] azimuthTalons = new TalonSRX[4];
  public TalonSRX[] driveTalons = new TalonSRX[4];

  private final SwerveDrive swerve = getSwerve();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public DriveSubsystem() {
    super("Drive", rP, rI, rD, 0.0, 0.02);
    try {
      getPIDController().setInputRange(-180, 180);
      getPIDController().setContinuous(true);
      getPIDController().setOutputRange(-1, 1);
      getPIDController().setAbsoluteTolerance(tolerance_degrees);
      setSetpoint(0);
      disable();
    } catch (Exception e) {
      e.printStackTrace();
    }

    SmartDashboard.putNumber("RotateP", tuneP);
    SmartDashboard.putNumber("RotateI", tuneI);
    SmartDashboard.putNumber("RotateD", tuneD);
    SmartDashboard.putNumber("RotateF", tuneF);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new TeleOpDriveCommand());
  }

  public void setDriveMode(DriveMode mode) {
    logger.debug("setting drive mode to {}", mode);
    swerve.setDriveMode(mode);
  }

  public void zeroAzimuthEncoders() {
    swerve.zeroAzimuthEncoders();
  }

  public void drive(double forward, double strafe, double azimuth) {
    swerve.drive(forward, strafe, azimuth);
    SmartDashboard.putString("Drive Values", forward + "  " + strafe + "  " + azimuth);
  }

  public void zeroGyro() {
    AHRS gyro = swerve.getGyro();
    gyro.setAngleAdjustment(0);
    double adj = gyro.getAngle() % 360;
    gyro.setAngleAdjustment(-adj);
    logger.info("resetting gyro zero ({})", adj);
  }

  // Swerve configuration

  private SwerveDrive getSwerve() {
    SwerveDriveConfig config = new SwerveDriveConfig();
    config.wheels = getWheels();
    config.gyro = new AHRS(SPI.Port.kMXP);
    config.length = ROBOT_LENGTH;
    config.width = ROBOT_WIDTH;
    config.gyroLoggingEnabled = true;
    config.summarizeTalonErrors = false;

    return new SwerveDrive(config);
  }

  private Wheel[] getWheels() {
    TalonSRXConfiguration azimuthConfig = new TalonSRXConfiguration();
    azimuthConfig.primaryPID.selectedFeedbackSensor =
        FeedbackDevice.CTRE_MagEncoder_Absolute; // CTRE_MagEncoder_Relative;
    azimuthConfig.auxiliaryPID.selectedFeedbackSensor =
        FeedbackDevice.CTRE_MagEncoder_Absolute; // CTRE_MagEncoder_Relative;
    azimuthConfig.continuousCurrentLimit = 10;
    azimuthConfig.peakCurrentDuration = 0;
    azimuthConfig.peakCurrentLimit = 0;
    azimuthConfig.slot0.kP = 10.0;
    azimuthConfig.slot0.kI = 0.0;
    azimuthConfig.slot0.kD = 100.0;
    azimuthConfig.slot0.kF = 0.0;
    azimuthConfig.slot0.integralZone = 0;
    azimuthConfig.slot0.allowableClosedloopError = 0;
    azimuthConfig.motionAcceleration = 10_000;
    azimuthConfig.motionCruiseVelocity = 800;
    azimuthConfig.pulseWidthPeriod_FilterWindowSz = 5;

    TalonSRXConfiguration driveConfig = new TalonSRXConfiguration();
    driveConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    driveConfig.continuousCurrentLimit = 40;
    driveConfig.peakCurrentDuration = 0;
    driveConfig.peakCurrentLimit = 0;

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();

    Wheel[] wheels = new Wheel[4];

    for (int i = 0; i < 4; i++) {
      TalonSRX azimuthTalon = new TalonSRX(i);
      azimuthTalon.configAllSettings(azimuthConfig);
      azimuthTalon.setInverted(true);
      azimuthTalon.setSensorPhase(true);

      TalonSRX driveTalon = new TalonSRX(i + 10);
      driveTalon.configAllSettings(driveConfig);
      driveTalon.setNeutralMode(NeutralMode.Brake);
      driveTalon.setNeutralMode(NeutralMode.Brake);

      telemetryService.register(azimuthTalon);
      telemetryService.register(driveTalon);

      Wheel wheel = new Wheel(azimuthTalon, driveTalon, DRIVE_SETPOINT_MAX);
      wheels[i] = wheel;
      azimuthTalons[i] = azimuthTalon;
      driveTalons[i] = driveTalon;
    }

    return wheels;
  }

  public void calibrateAzimuth() {
    swerve.saveAzimuthPositions();
  }

  public void zeroDriveEncoders() {
    for (int i = 0; i < 4; i++) {
      driveTalons[i].setSelectedSensorPosition(0);
    }
  }

  public int averageDriveEncoder() {
    return (int)
        ((Math.abs(driveTalons[0].getSelectedSensorPosition())
                + Math.abs(driveTalons[1].getSelectedSensorPosition())
                + Math.abs(driveTalons[2].getSelectedSensorPosition())
                + Math.abs(driveTalons[3].getSelectedSensorPosition()))
            / 4);
  }

  public void setDriveBrake() {
    for (int i = 0; i < 4; i++) {
      driveTalons[i].setNeutralMode(NeutralMode.Brake);
    }
  }

  public void setFOD(boolean enabled) {
    swerve.setFieldOriented(enabled);
  }

  public double deadband(double value) {
    if (Math.abs(value) < DEADBAND) return 0.0;
    return value;
  }

  public double[] getStickVal() {
    double[] stickVal = new double[3];

    stickVal[0] = deadband(-Robot.oi.driveStick.getY());
    stickVal[1] = deadband(Robot.oi.driveStick.getX());
    stickVal[2] = deadband(Robot.oi.driveStick.getRawAxis(4));

    return stickVal;
  }

  public boolean rotationHeld() {
    if (Robot.oi.getDriveStick().getRawButton(9)
        | Robot.oi.getDriveStick().getRawButton(10)
        | Robot.oi.getDriveStick().getRawButton(11)
        | Robot.oi.getDriveStick().getRawButton(12)) {
      return true;
    } else return false;
  }

  @Override
  protected double returnPIDInput() {
    if (pidOwner) {
      return swerve.getGyro().pidGet();
    } else {
      return driveError;
    }
  }

  @Override
  protected void usePIDOutput(double output) {
    SmartDashboard.putNumber("PID Out", output);
    pidOutput = output;
  }

  public void updatePID() {
    tuneP = SmartDashboard.getNumber("RotateP", 0.0);
    tuneI = SmartDashboard.getNumber("RotateI", 0.0);
    tuneD = SmartDashboard.getNumber("RotateD", 0.0);
    tuneF = SmartDashboard.getNumber("RotateF", 0.0);
    getPIDController().setPID(tuneP, tuneI, tuneD, tuneF);
    SmartDashboard.putNumber("P", tuneP);
  }

  public void enablePID(boolean b) {
    // TODO Auto-generated method stub
    if (b) {
      getPIDController().enable();
    } else {
      getPIDController().disable();
    }
  }

  public boolean getPIDEnabled() {
    SmartDashboard.putBoolean("AutoRotateEnabled", getPIDController().isEnabled());
    return getPIDController().isEnabled();
  }

  public void setPidRotate() {
    getPIDController().setPID(rP, rI, rD, 0.0);
    getPIDController().setInputRange(-180, 180);
    getPIDController().setContinuous(true);
    getPIDController().setOutputRange(-1, 1);
    getPIDController().setAbsoluteTolerance(tolerance_degrees);
    setSetpoint(0);
    disable();
  }

  public void setPidDrive(int targetTick) {
    targetTick = Math.abs(targetTick);
    // getPIDController().setPID(dP, dI, dD);
    getPIDController().setPID(tuneP, tuneI, tuneD, tuneF);
    getPIDController().setInputRange(-targetTick, targetTick);
    getPIDController().setContinuous(true);
    getPIDController().setOutputRange(-1, 1);
    getPIDController().setAbsoluteTolerance(tolerance_ticks);
    setSetpoint(0);
    disable();
  }

  public double getPidOutput() {
    return pidOutput;
  }
}
