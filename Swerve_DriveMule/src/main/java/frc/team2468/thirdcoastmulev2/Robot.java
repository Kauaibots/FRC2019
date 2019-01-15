package frc.team2468.thirdcoastmulev2;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.team2468.thirdcoastmulev2.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import java.util.Date;
import frc.team2468.thirdcoastmulev2.control.Controls;


public class Robot extends TimedRobot {
// Instantiate this before Subsystems because they use telemetry service.
  public static final TelemetryService TELEMETRY = new TelemetryService(TelemetryController::new);

public static final DriveSubsystem DRIVE = new DriveSubsystem();

// Controls initialize Commands so this should be instantiated last to prevent
  // NullPointerExceptions in commands that require() Subsystems above.
  public static final Controls CONTROLS = new Controls();


private final Logger logger = LoggerFactory.getLogger(this.getClass());


  @Override
  public void robotInit() {
  logger.info("Today is {}", new Date());
  DRIVE.zeroAzimuthEncoders();
  
  TELEMETRY.start();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }

}