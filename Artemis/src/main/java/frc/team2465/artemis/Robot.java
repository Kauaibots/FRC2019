package frc.team2465.artemis;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2465.artemis.command.CalibrateArm;
import frc.team2465.artemis.control.OI;
import frc.team2465.artemis.subsystem.ArmSubsystem;
import frc.team2465.artemis.subsystem.ClawSubsystem;
import frc.team2465.artemis.subsystem.DriveSubsystem;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class Robot extends TimedRobot {
  // Instantiate this before Subsystems because they use telemetry service.
  public static final TelemetryService TELEMETRY = new TelemetryService(TelemetryController::new);

	Command autonomousCommand;
	SendableChooser autoChooser;

  public static final DriveSubsystem DRIVE = new DriveSubsystem();
  public static ArmSubsystem ARM;
  public static ClawSubsystem CLAW;

  // Controls initialize Commands so this should be instantiated last to prevent
  // NullPointerExceptions in commands that require() Subsystems above.
  // public static final Controls CONTROLS = new Controls();

  public static OI oi;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void robotInit() {
    RobotMap.init();
    logger.info("Today is {}", new Date());
    DRIVE.zeroAzimuthEncoders();

    ARM = new ArmSubsystem();
    CLAW = new ClawSubsystem();

    oi = new OI();

    TELEMETRY.start();


    autoChooser = new SendableChooser();
    autoChooser.addDefault("None", 0);
    autoChooser.addObject("Calibrate Arm", 1);
    
    SmartDashboard.putData("Autonomous Mode", autoChooser);
  }

  @Override
  public void autonomousInit() {

	/*	switch ((int) autoChooser.getSelected()) {
		case 1:
			autonomousCommand = new CalibrateArm();
			break;
		default:
			autonomousCommand = null;
    }
    
    if (autonomousCommand != null){
      autonomousCommand.start();
    }*/

    teleopInit();
  }

  @Override
  public void autonomousPeriodic() {
    teleopPeriodic();
  }


  @Override
  public void teleopInit() {
    if (autonomousCommand != null){
      autonomousCommand.cancel();
    }
  }
  
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }
}
