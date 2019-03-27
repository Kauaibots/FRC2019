package frc.team2465.artemis.command;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.RobotMap;
import frc.team2465.artemis.subsystem.ClawSubsystem;
import frc.team2465.artemis.subsystem.ClawSubsystem.e_pincher;

public final class Pincher extends Command {

    private static final ClawSubsystem claw = Robot.CLAW;

    DoubleSolenoid pincher = RobotMap.pincher;

    e_pincher state;

    public Pincher(e_pincher state){
        this.state = state;

    }

    @Override
    protected void initialize() {
        if (state == e_pincher.GRAB){
            pincher.set(Value.kForward);
        }
        else if (state == e_pincher.RELEASE){
            pincher.set(Value.kReverse);
        }


    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}