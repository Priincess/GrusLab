package Game.Player;

import org.lwjgl.input.Controller;

public class Player {
	
	private Gamepad _gamepad;
	private Controller _controller=null;
	
	private boolean _forward = false;
	private boolean _backward = false;
	private boolean _left = false;
	private boolean _right = false;
	
	public Player(Controller controller, Gamepad gamepad){
		_controller = controller;
		_gamepad = gamepad;
	}
	
	public void setForward(boolean forward){
		_forward = forward;
	}
	
	public boolean isForward(){
		return _forward;
	}
	
	public void setBackward(boolean backward){
		_backward = backward;
	}
	
	public boolean isBackward(){
		return _backward;
	}
	
	public void setLeft(boolean left){
		_left = left;
	}
	
	public boolean isLeft(){
		return _left;
	}
	
	public void setRight(boolean right){
		_right = right;
	}
	
	public boolean isRight(){
		return _right;
	}
	
	public Controller getController(){
		return _controller;
	}
	
	public Gamepad getGamepad(){
		return _gamepad;
	}
}
