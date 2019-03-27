package frc.team2465.artemis.command;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.subsystem.ArmSubsystem;

public final class CalibrateArm extends Command {

    private static final ArmSubsystem arm = Robot.ARM;

    private enum Step {ORIGIN, TOP, BOTTOM, DONE};
    Step step = Step.ORIGIN;

    private double power = 0.25;

    private boolean finished = false;

    public void CalibrateArm(){
        requires(arm);
    }

    @Override
    protected void initialize() {
        step = Step.ORIGIN;
        finished = false;
    }


    @Override
    protected void execute() {
        SmartDashboard.putNumber("Arm Height", arm.getHeight());
        SmartDashboard.putBoolean("Top", arm.isTop());
        SmartDashboard.putBoolean("Bottom", arm.isBottom());

        if (step == Step.ORIGIN){
            if (!arm.isBottom()){
                arm.setPower(-power);
            }
            else if (arm.isBottom()){
                Timer.delay(1);
                arm.zeroEnc();
                step = Step.TOP;
            }
        }
        if (step == Step.TOP){
            if (!arm.isTop()){
                arm.setPower(power);
            }
            else {
                Timer.delay(1);
                Preferences.getInstance().putDouble("armMaxAngle", arm.getPosition());
                step = Step.BOTTOM;
            }
        }
        if (step == Step.BOTTOM){
            if (!arm.isBottom()){
                arm.setPower(-power);
            }
            else {
                Timer.delay(2);
                Preferences.getInstance().putDouble("armDegreeOffset", arm.getRawPosition());
                step = Step.DONE;
            }
        }
        if (step == Step.DONE){
            arm.setPower(0);
            updateArm();
            finished = true;
        }
    }



    private void updateArm(){
        arm.maxAngle = Preferences.getInstance().getDouble("armMaxAngle", 123);
        arm.degreeOffset = Preferences.getInstance().getDouble("armDegreeOffset", 317.87);
    }

    @Override
    protected boolean isFinished() {
        return finished;
    }

}