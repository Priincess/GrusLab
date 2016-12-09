package game.player;

/**
 * @author lilpr
 * This enum holds the states which a gamepad can have. Each state has a value.
 */
public enum GamepadState {
	Forward(0), Backward(1), ForwardRight(2), ForwardLeft(3), BackwardRight(4),
	BackwardLeft(5), Right(6), Left(7), None(8);
	
	private int _gamepadValue;
	
	GamepadState(int gamepadValue) {
		 _gamepadValue = gamepadValue;
	 }
	 
	 public int getGamepadValue() {
	     return _gamepadValue;
	 }
}
