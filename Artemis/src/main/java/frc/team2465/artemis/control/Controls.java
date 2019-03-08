package frc.team2465.artemis.control;

public class Controls {

  private final DriverControls driverControls = new DriverControls(0);

  public DriverControls getDriverControls() {
    return driverControls;
  }

  private final GameControls gameControls = new GameControls(1);

  public GameControls getGameControls() {
    return gameControls;
  }
}
