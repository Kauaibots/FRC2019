package frc.team2465.artemis.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.subsystem.DriveSubsystem;

public final class DriveDistance extends Command {

  private static final DriveSubsystem swerve = Robot.DRIVE;

  double curveSlope = 0.00;
  double curveYInt = -0.0025;
  double ticksPerRevolution = 350;
  double wheelDiameter = 2.5; // Inches
  double inchesPerRevolution = (wheelDiameter * Math.PI);
  int ticksPerInch = (int) (ticksPerRevolution / inchesPerRevolution);
  double targetInch;
  double driveAngle = 0;
  int error = 0;
  int targetTick = 0;
  int currentTick = 0;
  int errorTolerance = 10;
  boolean finished = false;
  int onTarget = 0;
  int staleCount = 0;
  int lastPosition = 0;
  boolean azimuthOriented = false;

  double yOut = 0;
  double xOut = 0;
  double curveOut = 0;

  public DriveDistance(double distance, double angle) {
    targetInch = distance;
    driveAngle = angle;
    requires(swerve);
  }

  @Override
  protected void initialize() {
    swerve.setFOD(false);
    onTarget = 0;
    staleCount = 0;
    azimuthOriented = false;
    swerve.zeroDriveEncoders();
    targetTick = (int) Math.abs((targetInch * ticksPerInch));
    System.out.println("\n Drive Distance Initialized \n");
    System.out.flush();
    swerve.setPidDrive(targetTick);
    swerve.enablePID(true);
    swerve.setSetpoint(targetTick);
    swerve.getPIDEnabled();
    setTimeout(5);
  }

  @Override
  protected void execute() {
    currentTick = swerve.averageDriveEncoder();
    error = swerve.driveError = getError();
    curveOut = swerve.getPidOutput(); // getCurveOut();
    System.out.println("Curve Output Value     " + curveOut);
    System.out.println("Current Error Value:  " + error);
    if (curveOut > 1.0) {
      curveOut = 1.0;
    }
    driveCalculation();
    setDrive(yOut, xOut);
    checkStale();
    System.out.flush();
  }

  public int getError() {
    return (targetTick - Math.abs(currentTick));
  }

  //Converts the raw power value (from PID) and angle into the format that allows angular driving
  public void driveCalculation() {
    if (driveAngle == 0) {
      yOut = 1.0;
      xOut = 0.0;
    }
    if (Math.abs(driveAngle) > 0 && Math.abs(driveAngle) <= 45) {
      xOut = driveAngle / 45;
      yOut = 1.0;
    } else if (Math.abs(driveAngle) > 45 && Math.abs(driveAngle) < 90) {
      yOut = driveAngle % 45 / 45;
      xOut = 1.0;
    }

    if (driveAngle < 0) {
      xOut *= -1;
    }

    if (driveAngle == 0) { // Protect against 0/0
      yOut *= curveOut;
    } else {
      yOut *= curveOut * (driveAngle / Math.abs(driveAngle));
    }
    xOut *= curveOut;

    if (onTarget >= 5) {
      finished = true;
    } else if (errorTolerance > Math.abs(error)) {
      yOut = 0.0;
      xOut = 0.0;
      onTarget++;
    } else {
      onTarget = 0;
    }

    if (error < 0) {
      yOut *= -1;
      xOut *= -1;
    }

    if (targetInch < 0) {
      yOut *= -1;
      xOut *= -1;
    }

    System.out.println("Motor Output Value-  y:  " + yOut + "   x:  " + xOut);
  }

  public void setDrive(double yVal, double xVal) {
    double waitTime = 0.2;
    if (timeSinceInitialized() < waitTime) {
      swerve.drive(yVal / 19, xVal / 19, 0.0);
    } else if (timeSinceInitialized() < waitTime + 1) { // Accelerate over the course of 1 second.
      yVal *= timeSinceInitialized() - .30;
      xVal *= timeSinceInitialized() - .30;
      swerve.drive(yVal, xVal, 0.0);
    } else {
      azimuthOriented = true;
      swerve.drive(yVal, xVal, 0.0);
    }
  }

  //A non-pid decelerator. Replace getPidOutput with this to change.
  public double getCurveOut() {
    double curve = 0;

    if (Math.abs(error) < 450) {
      curve =
          0.000004 * (Math.pow(Math.abs(error), 2)) + 0.07; // curveSlope*Math.abs(error)+curveYInt;
      if (curve < 0) {
        curve = 0;
      }
    } else {
      curve = 1.0;
    }
    return curve;
  }

  public void checkStale() {
    if (lastPosition == currentTick && azimuthOriented) {
      staleCount++;
    }

    if (staleCount > 20) {
      finished = true;
    }

    lastPosition = currentTick;
  }

  @Override
  protected boolean isFinished() {
    if (isTimedOut()) {
      finished = true;
    }
    return finished;
  }

  @Override
  protected void interrupted() {
    swerve.enablePID(false);
    swerve.drive(0, 0, 0);
    System.out.flush();
    swerve.setFOD(true);
    finished = false;
  }

  @Override
  protected void end() {
    swerve.enablePID(false);
    swerve.setFOD(true);
    swerve.drive(0, 0, 0);
    System.out.flush();
    finished = false;
  }
}
