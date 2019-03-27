package frc.team2465.artemis.subsystem;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.RobotMap;
import frc.team2465.artemis.command.ManArm;

public class ArmSubsystem extends PIDSubsystem {

  private static double armP = 0.0;
  private static double armI = 0.0;
  private static double armD = 0.0;
  public double physicalMaxHeight = 79.5;
  private double toleranceInches = .5;

  private double minAngle = 31.69; 
  public double maxAngle; //
  public double degreeOffset; //
  private double pwmMax;

  public enum manArm {UP, DOWN, STOP};

  Counter encoder;

  SpeedControllerGroup armGroup = RobotMap.armGroup;

  DigitalInput lowerLimit;
  DigitalInput upperLimit;

  private double tuneP = 0.0;
  private double tuneI = 0.0;
  private double tuneD = 0.0;
  private double tuneF = 0.0;


  public ArmSubsystem() {
    super("Arm", armP, armI, armD, 0.0, 0.02);
    try {
      getPIDController().setInputRange(0, physicalMaxHeight);
      getPIDController().setContinuous(false);
      getPIDController().setOutputRange(-.8, .8);
      getPIDController().setAbsoluteTolerance(toleranceInches);
      setSetpoint(0);
      disable();
    } catch (Exception e) {
      e.printStackTrace();
    }

    encoder = RobotMap.armEncoder;  
    lowerLimit = RobotMap.lowerLimit;
    upperLimit = RobotMap.upperLimit;

    maxAngle = Preferences.getInstance().getDouble("armMaxAngle", 123);
    degreeOffset = Preferences.getInstance().getDouble("armDegreeOffset", 317.87);

    setPWMMax();

    SmartDashboard.putNumber("ArmP", armP);
    SmartDashboard.putNumber("ArmI", armI);
    SmartDashboard.putNumber("ArmD", armD);
    SmartDashboard.putNumber("ArmF", 0.0);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new ManArm(0.0));
  }


//Returns a degree value relative to the zeroed position (usually from lowest)
  public double getPosition(){
    encoder.setSemiPeriodMode(true);
    double pwmHigh = encoder.getPeriod();

    double pwmRatio = pwmHigh/pwmMax;

    double degree = (pwmRatio*360-degreeOffset+360)%360;

    if (degree > 358){
      degree = 0;
    }

    return degree;
  }

  public double getRawPosition(){
    encoder.setSemiPeriodMode(true);
    double pwmHigh = encoder.getPeriod();

    double pwmRatio = pwmHigh/pwmMax;

    double degree = pwmRatio*360;

    return degree;
  }

  public double getHeight(){

    double ratio = getPosition()/maxAngle;

    double height = physicalMaxHeight*ratio;

    return height;
  }


  public void setPWMMax(){
    encoder.setSemiPeriodMode(false);
    Timer.delay(.1);
    double pwmLow = encoder.getPeriod();
    encoder.setSemiPeriodMode(true);
    Timer.delay(.1);
    double pwmHigh = encoder.getPeriod();

    pwmMax = pwmHigh+pwmLow;
  }

  public void zeroEnc(){
    degreeOffset = getRawPosition();
    SmartDashboard.putNumber("Degree Offset", degreeOffset);
  }


  public boolean isBottom(){
    return !lowerLimit.get();
  }

  public boolean isTop(){
    return !upperLimit.get();
  }

  public void setPower(double power){
    if (power > 0 && !isTop()){
      armGroup.set(power);
    }
    else if (power < 0 && !isBottom()){
      armGroup.set(power);
    }
    else {
      armGroup.set(0);
    }
  }


  public void updatePID() {
    tuneP = SmartDashboard.getNumber("ArmP", 0.0);
    tuneI = SmartDashboard.getNumber("ArmI", 0.0);
    tuneD = SmartDashboard.getNumber("ArmD", 0.0);
    tuneF = SmartDashboard.getNumber("ArmF", 0.0);
    getPIDController().setPID(tuneP, tuneI, tuneD, tuneF);
  }

  @Override
  protected double returnPIDInput() {
    return getHeight();
  }

  @Override
  protected void usePIDOutput(double output) {
    setPower(output);
    SmartDashboard.putNumber("Arm PID Speed", output);
  }

  public void enablePID(boolean b) {
    if (b) {
      getPIDController().enable();
    } else {
      getPIDController().disable();
      armGroup.set(0);
    }

    SmartDashboard.putBoolean("Arm PID Enabled", b);
  }

  public boolean getPIDEnabled() {
    return getPIDController().isEnabled();
  }

}
