package frc.team2465.artemis.command;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.RobotMap;
import frc.team2465.artemis.subsystem.ClawSubsystem;
import frc.team2465.artemis.subsystem.ClawSubsystem.e_tilter;

public final class Tilter extends Command {

    private static final ClawSubsystem claw = Robot.CLAW;

    DoubleSolenoid pincher = RobotMap.tilter;

    e_tilter state;

    public Tilter(e_tilter state){
        this.state = state;

    }

    @Override
    protected void initialize() {
        if (state == e_tilter.DOWN){
            pincher.set(Value.kForward);
        }
        else if (state == e_tilter.UP){
            pincher.set(Value.kReverse);
        }


    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}