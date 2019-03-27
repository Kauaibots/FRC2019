package frc.team2465.artemis;

import ch.qos.logback.core.LogbackException;
import edu.wpi.first.hal.sim.mockdata.AnalogOutDataJNI;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.VictorSP;

//Any swerve related parts (azimuth talons, drive talons) remain in the DriveSubsystem, where Strykeforce put them
public class RobotMap {

    public static Spark arm1;
    public static Spark arm2;
    public static SpeedControllerGroup armGroup;

    public static DigitalInput rawArmEncoder;
    public static Counter armEncoder;
    public static DigitalInput lowerLimit;
    public static DigitalInput upperLimit;

    public static DoubleSolenoid pincher;
    public static DoubleSolenoid tilter;
    public static PWMVictorSPX roller;
    public static AnalogInput ballDetect;

    public static void init() {

        arm1 = new Spark(0);
        arm2 = new Spark(1);
        armGroup = new SpeedControllerGroup(arm1, arm2);


        rawArmEncoder = new DigitalInput(0);
        armEncoder = new Counter(rawArmEncoder);
        armEncoder.setPulseLengthMode(4096);

        lowerLimit = new DigitalInput(1);
        
        upperLimit = new DigitalInput(2);

        pincher = new DoubleSolenoid(0, 1);
        tilter = new DoubleSolenoid(2,3);

        roller = new PWMVictorSPX(2);

        ballDetect = new AnalogInput(0);

    }

}