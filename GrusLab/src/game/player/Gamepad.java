package game.player;

/**
 * @author lilpr
 * This enum holds the index of the different buttons of the different controller
 */
public enum Gamepad {
	
	PS4, Xbox, XboxS;
	
	private static String PS4_NAME = "Wireless Controller";
	private static String XBOX_NAME = "Controller (Xbox 360 Wireless Receiver for Windows)";
	private static String XBOX_S_NAME = "Bluetooth XINPUT compatible input device";
	private static int PS4_FORWARD = 1;
	private static int XBOX_FORWARD = 0;
	private static int PS4_BACKWARD = 2;
	private static int XBOX_BACKWARD = 1;
	private static int PS4_START = 12;
	private static int XBOX_START = 7;
	private static int LEFT = -1;
	private static int RIGHT = 1;
	
	public String getName(){
		switch (this) {
			case PS4:
				return PS4_NAME;
			case Xbox:
				return XBOX_NAME;
			case XboxS:
				return XBOX_S_NAME;
			default:
				return "";
		}
	}
	
	public int getForwardIndex(){
		switch (this) {
			case PS4:
				return PS4_FORWARD;
			case Xbox:
				return XBOX_FORWARD;
			case XboxS:
				return XBOX_FORWARD;
			default:
				return -1;
		}
	}
	
	public int getBackwardIndex(){
		switch (this) {
			case PS4:
				return PS4_BACKWARD;
			case Xbox:
				return XBOX_BACKWARD;
			case XboxS:
				return XBOX_BACKWARD;
			default:
				return -1;
		}
	}
	
	public int getStartIndex(){
		switch (this) {
			case PS4:
				return PS4_START;
			case Xbox:
				return XBOX_START;
			case XboxS:
				return XBOX_START;
			default:
				return -1;
		}
	}
	
	public int getLeftValue(){
		return LEFT;
	}
	
	public int getRightValue(){
		return RIGHT;
	}
}




