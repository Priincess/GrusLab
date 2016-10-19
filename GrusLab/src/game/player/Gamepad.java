package game.player;

/**
 * @author lilpr
 * This enum holds the index of the different buttons of the different controller
 */
public enum Gamepad {
	
	PS4, Xbox;
	
	private static int PS4_FORWARD = 1;
	private static int XBOX_FORWARD = 0;
	private static int PS4_BACKWARD = 2;
	private static int XBOX_BACKWARD = 1;
	private static int LEFT = -1;
	private static int RIGHT = 1;
	
	public int getForwardIndex(){
		if(this.equals(PS4))
			return PS4_FORWARD;
		else
			return XBOX_FORWARD;
	}
	
	public int getBackwardIndex(){
		if(this.equals(PS4))
			return PS4_BACKWARD;
		else
			return XBOX_BACKWARD;
	}
	
	public int getLeftValue(){
		return LEFT;
	}
	
	public int getRightValue(){
		return RIGHT;
	}
}




