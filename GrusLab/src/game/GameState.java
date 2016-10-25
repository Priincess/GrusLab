package game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * @author lilpr
 * This class represents the state of the game.
 */
public class GameState {

	private static GameStateValue _state = GameStateValue.INIT;
	private static IntegerProperty _stateNumber = new SimpleIntegerProperty(0);
	private static GameState _instance = new GameState();
		
	private GameState(){}
	
	//**********PUBLIC METHODS**********
	
	/**
	 * @return the instance of this class
	 */
	public static GameState getInstance() {
		return _instance;
	}
	
	/**
	 * @return the state of the game
	 */
	public static GameStateValue getGameState(){
		return _state;
	}

	public static IntegerProperty getGameStateNumber() {
		return _stateNumber;
	}
	
	/**
	 * @param state set the state of the game
	 */
	public void setGameState(GameStateValue state){
		_state = state;
		_stateNumber.setValue(state.getValue());
	}
}
