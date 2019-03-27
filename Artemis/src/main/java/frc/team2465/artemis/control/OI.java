package frc.team2465.artemis.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.team2465.artemis.command.AutoRotate;
import frc.team2465.artemis.command.CalibrateArm;
import frc.team2465.artemis.command.GoToHeight;
import frc.team2465.artemis.command.GoToHeightNoPID;
import frc.team2465.artemis.command.ManArm;
import frc.team2465.artemis.command.NoPidDriveDistance;
import frc.team2465.artemis.command.Pincher;
import frc.team2465.artemis.command.Rollers;
import frc.team2465.artemis.command.Tilter;
import frc.team2465.artemis.subsystem.ClawSubsystem.e_pincher;
import frc.team2465.artemis.subsystem.ClawSubsystem.e_roller;
import frc.team2465.artemis.subsystem.ClawSubsystem.e_tilter;

public class OI {

  public Joystick driveStick;
  public Joystick arduino;

  public JoystickButton AutoRotate0;
  public JoystickButton AutoRotate90;
  public JoystickButton AutoRotateN90;
  public JoystickButton AutoRotate180;


  public OI() {

    driveStick = new Joystick(0);
    arduino = new Joystick(1);

    //AutoRotate0 = new JoystickButton(driveStick, 9);
    //AutoRotate90 = new JoystickButton(driveStick, 10);
    //AutoRotateN90 = new JoystickButton(driveStick, 11);
    //AutoRotate180 = new JoystickButton(driveStick, 12);
    //JoystickButton DriveDistanceP = new JoystickButton(arduino, 7);
    //JoystickButton DriveDistanceN = new JoystickButton(arduino, 8);

    JoystickButton PincherOpen = new JoystickButton(arduino, 9);
    JoystickButton TilterUp = new JoystickButton(arduino, 8);
    JoystickButton RollersIn = new JoystickButton(arduino, 1);
    JoystickButton RollersOut = new JoystickButton(arduino, 5);

    JoystickButton ArmUp = new JoystickButton(arduino, 7);
    JoystickButton ArmDown = new JoystickButton(arduino, 6);

    JoystickButton ArmLow = new JoystickButton(arduino, 2);
    JoystickButton ArmMed = new JoystickButton(arduino, 3);
    JoystickButton ArmHig = new JoystickButton(arduino, 4);

    JoystickButton CalibrateArmBut = new JoystickButton(driveStick, 7);



    //AutoRotate0.whenPressed(new AutoRotate(0, false));
    //AutoRotate90.whenPressed(new AutoRotate(90, false));
    //AutoRotateN90.whenPressed(new AutoRotate(-90, false));
    //AutoRotate180.whenPressed(new AutoRotate(180, false));
    //DriveDistanceP.whenPressed(new NoPidDriveDistance(60.0, 0.0));
    //DriveDistanceN.whenPressed(new NoPidDriveDistance(-60.0, 0.0));

    PincherOpen.whenPressed(new Pincher(e_pincher.RELEASE));
    PincherOpen.whenReleased(new Pincher(e_pincher.GRAB));
    TilterUp.whenPressed(new Tilter(e_tilter.UP));
    TilterUp.whenReleased(new Tilter(e_tilter.DOWN));
    RollersIn.whileHeld(new Rollers(e_roller.IN));
    RollersOut.whileHeld(new Rollers(e_roller.OUT));

    ArmUp.whileHeld(new ManArm(0.3));
    ArmDown.whileHeld(new ManArm(-0.3));

    ArmLow.whenPressed(new GoToHeightNoPID(7.8));
    ArmMed.whenPressed(new GoToHeightNoPID(35));
    ArmHig.whenPressed(new GoToHeightNoPID(62));

    CalibrateArmBut.whenPressed(new CalibrateArm());


    


  }

  public Joystick getDriveStick() {
    return driveStick;
  }
}
