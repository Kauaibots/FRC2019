package frc.team2468.thirdcoastmulev2.control;

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
