
package frc.robot;

import frc.robot.commands.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;


/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
    // Joystick stick = new Joystick(port);
    // Button button = new JoystickButton(stick, buttonNumber);

    // There are a few additional built in buttons you can use. Additionally,
    // by subclassing Button you can create custom triggers and bind those to
    // commands the same as any other Button.

    //// TRIGGERING COMMANDS WITH BUTTONS
    // Once you have a button, it's trivial to bind it to a button in one of
    // three ways:

    // Start the command when the button is pressed and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenPressed(new ExampleCommand());

    // Run the command while the button is being held down and interrupt it once
    // the button is released.
    // button.whileHeld(new ExampleCommand());

    // Start the command when the button is released  and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenReleased(new ExampleCommand());


    public Joystick driveStick;
    public Joystick arduino;



    public OI() {

        driveStick = new Joystick(0);
 /*       arduino = new Joystick(1);
    
        Button elevatorManUp = new JoystickButton(arduino, 2);
        Button elevatorManDown = new JoystickButton(arduino, 1);

        Button clawOpen = new JoystickButton(arduino, 6);
        Button clawOpenStick = new JoystickButton(driveStick, 2);
        Button clawIn = new JoystickButton(arduino, 7);
        Button clawOut = new JoystickButton(arduino, 8);
        
        Button rotate0 = new JoystickButton(arduino, 10);
        Button rotate90 = new JoystickButton(arduino, 9);
        Button rotateNeg90 = new JoystickButton(arduino, 11);
        Button rotate180 = new JoystickButton(arduino, 12);

        // rotate buttons
        rotate90.whenPressed(new AutoRotate(90));
        rotateNeg90.whenPressed(new AutoRotate(-90));
        rotate0.whenPressed(new AutoRotate(0));
        rotate180.whenPressed(new AutoRotate(180));
       //Elevator
        elevatorManUp.whenPressed(new ElevatorManual(Elevator.Motion.UP));
        elevatorManUp.whenReleased(new ElevatorManual(Elevator.Motion.HOLD));
        elevatorManDown.whenPressed(new ElevatorManual(Elevator.Motion.DOWN));
        elevatorManDown.whenReleased(new ElevatorManual(Elevator.Motion.HOLD));
        //Claw
        clawOpen.whenPressed(new ClawGrab(Value.kForward));
        clawOpen.whenReleased(new ClawGrab(Value.kReverse));
        clawOpenStick.whenPressed(new ClawGrab(Value.kForward));
        clawOpenStick.whenReleased(new ClawGrab(Value.kReverse));
        clawIn.whileHeld(new ClawSpin(Motion.IN));
        clawIn.whenReleased(new ClawSpin(Motion.STOP));
        clawOut.whileHeld(new ClawSpin(Motion.OUT));
        clawOut.whenReleased(new ClawSpin(Motion.STOP));
        
        //elevatorScaleLow.whenReleased(new ElevatorManual(Elevator.Motion.HOLD));
        
        SmartDashboard.putData("DriveStraight", new DriveDistance(20, false));
    */
    }

    public Joystick getDriveStick() {
        return driveStick;
    }

}

