package frc.team2468.thirdcoastmulev2.control;

import javax.swing.text.StyleContext.SmallAttributeSet;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2468.thirdcoastmulev2.command.AutoRotate;
import frc.team2468.thirdcoastmulev2.command.DriveDistance;
import frc.team2468.thirdcoastmulev2.command.NoPidDriveDistance;

public class OI {

  public Joystick driveStick;

  public OI() {

    driveStick = new Joystick(0);

    JoystickButton AutoRotate0 = new JoystickButton(driveStick, 9);
    JoystickButton AutoRotate90 = new JoystickButton(driveStick, 10);
    JoystickButton AutoRotateN90 = new JoystickButton(driveStick, 11);
    JoystickButton AutoRotate180 = new JoystickButton(driveStick, 12);
    JoystickButton DriveDistanceP = new JoystickButton(driveStick, 7);
    JoystickButton DriveDistanceN = new JoystickButton(driveStick, 8);

    AutoRotate0.whenPressed(new AutoRotate(0, false));
    AutoRotate90.whenPressed(new AutoRotate(90, false));
    AutoRotateN90.whenPressed(new AutoRotate(-90, false));
    AutoRotate180.whenPressed(new AutoRotate(180, false));
    DriveDistanceP.whenPressed(new NoPidDriveDistance(60.0, 0.0));
    DriveDistanceN.whenPressed(new NoPidDriveDistance(-60.0, 0.0));
  }

  public Joystick getDriveStick() {
    return driveStick;
  }
}
