package frc.team2465.artemis.command;

import java.sql.Time;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.subsystem.ArmSubsystem;

public final class GoToHeightNoPID extends Command {

    double targetInch = 0;

    double maxSpeed = 0.9;

    int counter = 0;

    boolean finished = false;

    ArmSubsystem arm = Robot.ARM;

    Timer time;


    public GoToHeightNoPID(double height) {
        targetInch = height;
    }

    @Override
    protected void initialize() {
        finished = false;
    }

    @Override
    protected void execute() {
        if (getError() > 0.7){
            arm.setPower(getCurve());
        }
        else {
            arm.setPower(0.0);
        }

    }

    double getCurve(){
        double speed = 0;
        double error = Math.abs(getError());

        if (error < 2){
            speed = 0.25;
        }
        else if (error < 4){
            speed = 0.35;
        }
        else if (error < 8){
            speed = 0.5;
        }
        else if (error < 13){
            speed = 0.7;
        }
        else if (error >= 13){
            speed = maxSpeed;
        }

        if (timeSinceInitialized() < 1.5){
            speed *= (timeSinceInitialized()/1.5);
        }

        if (getError() < 0){
            speed *= -1;
        }

       return speed;
    }

    double getError(){
        return (targetInch-arm.getHeight());
    }



    @Override
    protected boolean isFinished() {
        if (counter >= 4 && getError() < 0.7) {
            return true;
        } else if (!(getError() < 0.7)) {
            counter = 0;
        } else if (getError() < 0.7) {
            counter++;
        }
        return false;     }

    @Override
    protected void end() {
        arm.setPower(0.0);
    }

    @Override
    protected void interrupted() {
        arm.setPower(0.0);
    }


}