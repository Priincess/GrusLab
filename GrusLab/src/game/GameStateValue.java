package game;

/**
 * @author lilpr
 * This enum holds the states which the game itself can have.
 */
public enum GameStateValue {
	INIT(0),
	WAIT(1),
	CALIBRATION(6),
	CALIBRATIONVALIDATION(7),
	READY(2),
	COUNTDOWN(8),
	PLAY(3),
	PAUSE(4),
	FINISHED(5);

	private final int _value;
	GameStateValue(int value){
		_value = value;
	}
	public int getValue() {
		return _value;
	}
}
