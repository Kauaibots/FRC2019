package frc.team2465.artemis.command;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.subsystem.DriveSubsystem;

public final class AutoRotate extends Command {

  private static final DriveSubsystem swerve = Robot.DRIVE;

  double target_angle;
  boolean previousAutoRotate = false;
  int counter;
  boolean auton = true;


  //When auton is true, the joystick x and y values are not passed in. When false, allows for movment of
  //  robot while rotating and locking to an angle
  public AutoRotate(float rotationAngle, boolean auton) {
    target_angle = rotationAngle;
    this.auton = auton;
    requires(swerve);
  }

  // Called just before this Command runs the first time
  protected void initialize() {
    swerve.setPidRotate();
    previousAutoRotate = swerve.getPIDEnabled();
    swerve.enablePID(true);
    swerve.setSetpoint(target_angle);
    System.out.println("Auto-rotate command initialized.   Angle: " + target_angle + "\n");
    System.out.flush();
    setTimeout(2.3);
    counter = 0;
  }

  // Called repeatedly when this Command is scheduled to run
  protected void execute() {
     swerve.updatePID();

    SmartDashboard.putNumber("P", swerve.getPIDController().getP());

    SmartDashboard.putNumber("AutoRotate Error", swerve.getPIDController().getError());
    SmartDashboard.putNumber("AutoRotate Setpoint", swerve.getPIDController().getSetpoint());
    SmartDashboard.putBoolean("AutoRotate On Target", swerve.getPIDController().onTarget());
    SmartDashboard.putNumber("Current Angle", swerve.getCurrAngle());

    if (auton) {
      swerve.drive(0.0, 0.0, swerve.getPidOutput());
    } else {
      swerve.drive(swerve.getStickVal()[0], swerve.getStickVal()[1], swerve.getPidOutput());
    }
  }

  // Make this return true when this Command no longer needs to run execute()
  protected boolean isFinished() {
    if (isTimedOut() && !swerve.rotationHeld()) {
      return true;
    }
    if (counter >= 6 && swerve.onTarget() && !swerve.rotationHeld()) {
      return true;
    } else if (!swerve.onTarget()) {
      counter = 0;
    } else if (swerve.onTarget()) {
      counter++;
    }
    return false;
  }

  // Called once after isFinished returns true
  protected void end() {
    System.out.println("Auto-rotate command complete." + "\n");
    swerve.enablePID(previousAutoRotate);
    swerve.drive(0.0, 0.0, 0.0);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  protected void interrupted() {
    System.out.println("Auto-rotate command interrupted. Stopping" + "\n");
    swerve.enablePID(previousAutoRotate);
    swerve.drive(0.0, 0.0, 0.0);
  }
}
