package Game.Player;

/**
 * @author lilpr
 * This enum holds the states which a controller can have. Each state has a value.
 */
public enum ControllerState {
	Forward(0), Backward(1), ForwardRight(2), ForwardLeft(3), BackwardRight(4),
	BackwardLeft(5), Right(6), Left(7), None(8);
	
	private int _controllerValue;
	
	ControllerState(int controllerValue) {
		 _controllerValue = controllerValue;
	 }
	 
	 public int getControllerValue() {
	     return _controllerValue;
	 }
}
