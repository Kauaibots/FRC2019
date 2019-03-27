package frc.team2465.artemis.command;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.subsystem.ArmSubsystem;

public final class ManArm extends Command {

    private static final ArmSubsystem arm = Robot.ARM;

    private double power = 0.3;

    boolean down = false;

    public ManArm(double power) {
        requires(arm);
        this.power = power;

        SmartDashboard.putBoolean("Man Arm Init", true);
    }

    @Override
    protected void initialize() {
        //SmartDashboard.putNumber("Arm Speed", 0.3);

        if (power < 0){
            down = true;
        }
        else {
            down = false;
        }

   }

    @Override
    protected void execute() {

        SmartDashboard.putNumber("Arm Position", arm.getPosition());
        SmartDashboard.putNumber("Arm Height", arm.getHeight());
        //power = SmartDashboard.getNumber("Arm Speed", 0);
        SmartDashboard.putBoolean("Top", arm.isTop());
        SmartDashboard.putBoolean("Bottom", arm.isBottom());

       // applyCurve();

        arm.setPower(power);

    }

    void applyCurve(){
        if (arm.getHeight() < 5 && down ){
            power = -0.25;
        }
        else if (arm.getHeight() > arm.physicalMaxHeight-5 && !down){
            power = 0.25;
        }



    }

    @Override
    protected boolean isFinished() {
        return false;
    }
  
    @Override
    protected void interrupted() {
        arm.setPower(0.0);
    }
  
    @Override
    protected void end() {
        arm.setPower(0.0);
    }

}
