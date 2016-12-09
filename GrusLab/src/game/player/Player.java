package game.player;

import org.lwjgl.input.Controller;

/**
 * @author lilpr
 * This class contain all information about the player, e.g. controller, state, pressed buttons
 */
public class Player {
	
	private Gamepad _gamepad;
	private Controller _controller=null;	
	private PlayerState _playerState = PlayerState.Normal;
	private ControllerState _controllerState = ControllerState.None;	
	private boolean _controllerChanged=false;
	private Minion _minion;
    private int _points = 0;

    private int _blockedTime;
    private int _speedTime;
	
	//**********PUBLIC METHODS**********
	
	public Player (Minion minion){
		_minion = minion;
	}
	
	public int getBlockedTime(){
		return _blockedTime;
	}
	
	public void setBlockedTime(int blockedTime){
		_blockedTime = blockedTime;
	}
	
	public void setSpeedTime(int speedTime){
		_speedTime = speedTime;
	}
	
	public int getSpeedTime(){
		return _speedTime;
	}

	/**
	 * This method sets the state of the pressed controller buttons
	 * 
	 * @param forward if X/A button is pressed
	 * @param backward if O/B button is pressed
	 * @param left if left button is pressed
	 * @param right if right button is pressed
	 */
	public void setControllerState(boolean forward, boolean backward, boolean left, boolean right){
		
		//check which button combination is pressed and set matching state
		if (forward&&right) {
			if (_controllerState != ControllerState.ForwardRight) {
				_controllerState = ControllerState.ForwardRight;
				_controllerChanged = true;
			}
		} else if (forward && left) {
			if (_controllerState != ControllerState.ForwardLeft) {
				_controllerState = ControllerState.ForwardLeft;
				_controllerChanged = true;
			}
		} else if (forward) {
			if (_controllerState != ControllerState.Forward) {
				_controllerState = ControllerState.Forward;
				_controllerChanged = true;
			}
		} else if (backward && right) {
			if (_controllerState != ControllerState.BackwardRight) {
				_controllerState = ControllerState.BackwardRight;
				_controllerChanged = true;
			}
		} else if (backward && left) {
			if (_controllerState != ControllerState.BackwardLeft) {
				_controllerState = ControllerState.BackwardLeft;
				_controllerChanged = true;
			}
		} else if (backward) {
			if (_controllerState != ControllerState.Backward) {
				_controllerState = ControllerState.Backward;
				_controllerChanged = true;
			}
		} else if (left) {
			if (_controllerState != ControllerState.Left) {
				_controllerState = ControllerState.Left;
				_controllerChanged = true;
			}
		} else if (right) {
			if (_controllerState != ControllerState.Right) {
				_controllerState = ControllerState.Right;
				_controllerChanged = true;
			}
		}else {
			if (_controllerState != ControllerState.None) {
				_controllerState = ControllerState.None;
				_controllerChanged = true;
			}
		}
	}
	

	/**
	 * resets the points the player reached
	 */
	public void resetPoints(){
		_points = 0;
	}
	
	/**
	 * @return the points the player reached
	 */
	public void addPoint(){
		_points++;
	}
	
	/**
	 * @return the points the player reached
	 */
	public int getPoints(){
		return _points;
	}
	
	/**
	 * @return state of the pressed controller buttons
	 */
	public ControllerState getControllerState(){
		return _controllerState;
	}
	
	/**
	 * @return controller instance
	 */
	public Controller getController(){
		return _controller;
	}
	
	/**
	 * @param controller instance of controller
	 */
	public void setController(Controller controller){
		_controller = controller;
	}
	
	/**
	 * @return used gamepad (Xbox or PS4)
	 */
	public Gamepad getGamepad(){
		return _gamepad;
	}
	
	/**
	 * @param gamepad which gamepad is used (PS4 or Xbox)
	 */
	public void setGamepad(Gamepad gamepad){
		_gamepad = gamepad;
	}
	
	/**
	 * @return the state of the player
	 */
	public PlayerState getPlayerState(){
		return _playerState;
	}
	
	/**
	 * @param state set the state of the player
	 */
	public void setPlayerState(PlayerState state){
		if (_playerState == state) {
			_playerState = state;
			_controllerChanged = true;
		}
	}
	
	/**
	 * @return if controller value has changed
	 */
	public boolean hasControllerChanged(){
		return _controllerChanged;
	}
	
	/**
	 * This method set the change value to false
	 */
	public void resetControllerChanged(){
		_controllerChanged=false;
	}
	
	/**
	 * @return the minion of the player (yellow or purple)
	 */
	public Minion getMinion(){
		return _minion;
	}
	
	/**
	 * @param minion set the minion of the player (yellow or purple)
	 */
	public void setMinion(Minion minion){
		_minion = minion;
	}
	
	/**
	 * Reset player for new game
	 */
	public void reset(){
		resetControllerChanged();
		_playerState = PlayerState.Normal;
		_controllerState = ControllerState.None;
	}
}
