package frc.team2465.artemis.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.subsystem.DriveSubsystem;

public final class NoPidDriveDistance extends Command {

  private static final DriveSubsystem swerve = Robot.DRIVE;

  double curveSlope = 0.00;
  double curveYInt = -0.0025;
  double ticksPerRevolution = 350;
  double wheelDiameter = 2.5; // Inches
  double inchesPerRevolution = (wheelDiameter * Math.PI);
  int ticksPerInch = (int) (ticksPerRevolution / inchesPerRevolution);
  double targetInch;
  double driveAngle = 0;
  double error = 0;
  int targetTick = 0;
  int currentTick = 0;
  double errorTolerance = 10;
  boolean finished = false;
  int onTarget = 0;
  int staleCount = 0;
  int lastPosition = 0;
  boolean azimuthOriented = false;

  double yOut = 0;
  double xOut = 0;
  double curveOut = 0;

  // Deceleration curve function (x=error, y=output power%): y = Math.pow(10,(-0.005*x+0.8)+10)/10

  public NoPidDriveDistance(double distance, double angle) {
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
  }

  @Override
  protected void execute() {
    currentTick = swerve.averageDriveEncoder();
    error = getError();
    curveOut = getCurveOut();
    // curveOut = ((-1)*Math.pow(17,((-0.001)*Math.abs(error)+0.8))+10)/10;
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

  public double getError() {
    return (targetTick - (Math.abs(currentTick)));
  }

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

  public double getCurveOut() {
    double curve = 0;
    double abserror = Math.abs(error);

    if (abserror < ticksPerInch * 3) {
      curve = 0.15;
    } else if (abserror < ticksPerInch * 15) {
      curve = 0.25;
    } else if (abserror < ticksPerInch * 20) {
      curve = 0.55;
    } else if (abserror > ticksPerInch * 20) {
      curve = 0.8;
    }

    /*   if (Math.abs(error) < 450) {
      curve =
         // 0.000004 * (Math.pow(Math.abs(error), 2)) + 0.07; // curveSlope*Math.abs(error)+curveYInt;
      if (curve < 0) {
        curve = 0;
      }
    } else {
      curve = 1.0;
    }*/
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
    return finished;
  }

  @Override
  protected void interrupted() {
    swerve.drive(0, 0, 0);
    System.out.flush();
    swerve.setFOD(true);
    finished = false;
  }

  @Override
  protected void end() {
    swerve.setFOD(true);
    swerve.drive(0, 0, 0);
    System.out.flush();
    finished = false;
  }
}
