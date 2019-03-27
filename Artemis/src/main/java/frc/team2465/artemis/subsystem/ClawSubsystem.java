package frc.team2465.artemis.subsystem;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2465.artemis.command.Rollers;

public class ClawSubsystem extends Subsystem {


    public enum e_roller {IN, OUT, STOP};
    public enum e_tilter {UP, DOWN};
    public enum e_pincher {GRAB, RELEASE};

    public ClawSubsystem(){

    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(new Rollers(e_roller.STOP));
    }



}