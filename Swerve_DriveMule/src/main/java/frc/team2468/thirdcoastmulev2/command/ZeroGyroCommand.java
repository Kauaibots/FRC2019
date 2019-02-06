package frc.team2468.thirdcoastmulev2.command;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2468.thirdcoastmulev2.Robot;
import frc.team2468.thirdcoastmulev2.subsystem.DriveSubsystem;

public final class ZeroGyroCommand extends InstantCommand {

  private static final DriveSubsystem swerve = Robot.DRIVE;

  public ZeroGyroCommand() {
    requires(swerve);
  }

  @Override
  protected void initialize() {
    swerve.zeroGyro();
  }
}
