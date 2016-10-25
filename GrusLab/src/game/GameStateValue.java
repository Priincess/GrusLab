package game;

/**
 * @author lilpr
 * This enum holds the states which the game itself can have.
 */
public enum GameStateValue {
	INIT(0),
	WAIT(1),
	READY(2),
	PLAY(3),
	PAUSE(4),
	FINISHED(5);

	private final int value;
	GameStateValue(int value){
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
