package frc.team2468.thirdcoastmulev2.command;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2468.thirdcoastmulev2.Robot;
import frc.team2468.thirdcoastmulev2.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import frc.team2468.thirdcoastmulev2.control.Controls;
import frc.team2468.thirdcoastmulev2.control.DriverControls;


import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRXPIDSetConfiguration;

public final class TeleOpDriveCommand extends Command {
private final static double DEADBAND = 0.05;


private final Logger logger = LoggerFactory.getLogger(this.getClass());


  private final static DriveSubsystem swerve = Robot.DRIVE;
  private final static DriverControls controls = Robot.CONTROLS.getDriverControls();
  

  public TeleOpDriveCommand() {
    requires(swerve);
  }

  @Override
  protected void initialize() {
    swerve.setDriveMode(TELEOP);
    SmartDashboard.putNumber("Azimuth P", 0);
    SmartDashboard.putNumber("Azimuth I", 0);
    SmartDashboard.putNumber("Azimuth D", 0);
    SmartDashboard.putNumber("Azimuth F", 0);
    //SmartDashboard.putNumber("Azimuth #", 0);

  }

  @Override
  protected void execute() {
    double forward = deadband(controls.getForward());
    double strafe = deadband(controls.getStrafe());
    double azimuth = deadband(controls.getYaw());

    swerve.drive(forward, strafe, azimuth);

    //for fine positioning the azimuths
    //swerve.azimuthTalons[(int) SmartDashboard.getNumber("Azimuth #", 0)].set(ControlMode.PercentOutput, controls.joystick.getRawAxis(3)/2-.25);
    
    if(controls.joystick.getRawButton(8)){
      swerve.calibrateAzimuth();
    }

    updatePID();

    SmartDashboard.putString("Azimuth Control", swerve.azimuthTalons[1].getControlMode().toString());
    SmartDashboard.putNumber("Azimuth ", swerve.azimuthTalons[1].getClosedLoopTarget());
  }

  public void updatePID(){

    for(int i = 0; i < 4; i++){
      swerve.azimuthTalons[i].config_kP(0, SmartDashboard.getNumber("Azimuth P", 0));
      swerve.azimuthTalons[i].config_kI(0, SmartDashboard.getNumber("Azimuth I", 0));
      swerve.azimuthTalons[i].config_kD(0, SmartDashboard.getNumber("Azimuth D", 0));
      swerve.azimuthTalons[i].config_kF(0, SmartDashboard.getNumber("Azimuth F", 0));
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

private double deadband(double value) {
    if (Math.abs(value) < DEADBAND) return 0.0;
    return value;
  }

}
