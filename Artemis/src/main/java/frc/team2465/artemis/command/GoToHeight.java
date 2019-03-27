package frc.team2465.artemis.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.subsystem.ArmSubsystem;

public final class GoToHeight extends Command {

    private static final ArmSubsystem arm = Robot.ARM;

    private double targetInch;

    private int counter = 0;


    public GoToHeight(double inches){
        targetInch = inches;
        requires(arm);
    }

    @Override
    protected void initialize() {
        arm.enablePID(true);
        arm.setSetpoint(targetInch);
        counter = 0;
    }

    @Override
    protected void execute() {
        arm.updatePID();
    }

    @Override
    protected boolean isFinished() {
        if (counter >= 4 && arm.onTarget()) {
            return true;
        } else if (!arm.onTarget()) {
            counter = 0;
        } else if (arm.onTarget()) {
            counter++;
        }
        return false;    
    }

    @Override
    protected void end() {
        arm.enablePID(false);
        arm.setPower(0.0);
    }

    @Override
    protected void interrupted() {
        arm.enablePID(false);
        arm.setPower(0.0);
    }

}