package frc.team2468.thirdcoastmulev2.command;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2468.thirdcoastmulev2.Robot;
import frc.team2468.thirdcoastmulev2.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TeleOpDriveCommand extends Command {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final DriveSubsystem swerve = Robot.DRIVE;
  // private final static DriverControls controls = Robot.CONTROLS.getDriverControls();

  Joystick driver = Robot.oi.driveStick;

  boolean releaseButton;

  public TeleOpDriveCommand() {
    requires(swerve);
  }

  @Override
  protected void initialize() {
    System.out.println("Teleop Command Initialized");
    swerve.setDriveMode(TELEOP);

    swerve.setDriveBrake();
  }

  @Override
  protected void execute() {
    System.out.println("Teleop execution");
    double forward = swerve.getStickVal()[0];
    double strafe = swerve.getStickVal()[1];
    double azimuth = swerve.getStickVal()[2];

    swerve.updatePID();


    if (driver.getRawButton(1)){
      swerve.setFOD(false);
    }
    else {
      swerve.setFOD(true);
    }


    if (driver.getRawButton(5)) {
      swerve.zeroGyro();

    }

    if (!swerve.getPIDController().isEnabled()){
    swerve.drive(forward, strafe, azimuth);
    }

  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    swerve.drive(0.0, 0.0, 0.0);
  }
}
