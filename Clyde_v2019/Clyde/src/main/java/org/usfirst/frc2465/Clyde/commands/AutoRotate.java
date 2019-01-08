// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package org.usfirst.frc2465.Clyde.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc2465.Clyde.Robot;
import org.usfirst.frc2465.Clyde.RobotPreferences;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

/**
 *
 */
public class AutoRotate extends Command {

	double target_angle;
	boolean previousAutoRotate = false;
	int counter;

	public AutoRotate(float rotationAngle) {
		target_angle = rotationAngle;
		// Use requires() here to declare subsystem dependencies
		requires(Robot.drive);

		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		previousAutoRotate = Robot.drive.getAutoRotation();
		Robot.drive.setAutoRotation(true);
		Robot.drive.setSetpoint(target_angle);
		System.out.println("Auto-rotate command initialized.   Angle: " + target_angle + "\n");
		System.out.flush();
		setTimeout(2.3);
		counter = 0;
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		//Robot.drive.updatePID();

		Robot.drive.arcadeDrive(0.0, 0.0);

		SmartDashboard.putNumber("P", Robot.drive.getPIDController().getP());

		SmartDashboard.putNumber("AutoRotate Error", Robot.drive.getPIDController().getError());
		SmartDashboard.putNumber("AutoRotate Setpoint", Robot.drive.getPIDController().getSetpoint());
		SmartDashboard.putBoolean("AutoRotate On Target", Robot.drive.getPIDController().onTarget());
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		if (isTimedOut()) {
			return true;
		}
		if (counter >= 4 && Robot.drive.onTarget()) {
			return Robot.drive.onTarget();
		} else if (!Robot.drive.onTarget()) {
			counter = 0;
		} else if (Robot.drive.onTarget()) {
			counter++;
		}
		return false;
	}

	// Called once after isFinished returns true
	protected void end() {
		System.out.println("Auto-rotate command complete." + "\n");
		Robot.drive.setAutoRotation(previousAutoRotate);
		Robot.drive.arcadeDrive(0, 0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		System.out.println("Auto-rotate command interrupted. Stopping" + "\n");
		Robot.drive.setAutoRotation(previousAutoRotate);
		Robot.drive.arcadeDrive(0, 0);
	}
}
