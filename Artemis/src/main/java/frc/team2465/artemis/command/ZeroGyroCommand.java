package frc.team2465.artemis.command;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2465.artemis.Robot;
import frc.team2465.artemis.subsystem.DriveSubsystem;

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
