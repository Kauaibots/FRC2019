package frc.team2465.artemis.command;

import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.RobotMap;
import frc.team2465.artemis.subsystem.ClawSubsystem;
import frc.team2465.artemis.subsystem.ClawSubsystem.e_roller;

public final class Rollers extends Command {

    private static final ClawSubsystem claw = Robot.CLAW;

    PWMVictorSPX roller = RobotMap.roller;

    private double speed = 1.0;
    
    e_roller state;

    public Rollers(e_roller state){
        requires(claw);
        this.state = state;
    }

    @Override
    protected void initialize() {
        
    }

    @Override
    protected void execute() {
        if (state == e_roller.IN && RobotMap.ballDetect.getValue() < 400) {
            roller.set(speed);
        }
        else if (state == e_roller.OUT){
            roller.set(-speed);
        }
        else {
            roller.set(0);
        }

        SmartDashboard.putNumber("Get Value", RobotMap.ballDetect.getValue());
        SmartDashboard.putNumber("Get Voltage", RobotMap.ballDetect.getVoltage());
        SmartDashboard.putNumber("Get Average Voltage", RobotMap.ballDetect.getAverageVoltage());
    }




    @Override
    protected boolean isFinished() {
        return false;
    }
  
    @Override
    protected void interrupted() {
        roller.set(0);
    }
  
    @Override
    protected void end() {
        roller.set(0);
    }


}