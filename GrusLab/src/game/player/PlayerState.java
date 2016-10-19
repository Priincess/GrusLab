package game.player;

/**
 * @author lilpr
 * This enum holds the states which a player can have. Each state has a value.
 */
public enum PlayerState {
	Blocked(0),Normal(1),Speedy(2);
	
	 private int _stateValue;

	 PlayerState(int stateValue) {
		 _stateValue = stateValue;
	 }
	 
	 public int getStateValue() {
	     return _stateValue;
	 }
}
