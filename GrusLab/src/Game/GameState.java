package Game;

/**
 * @author lilpr
 * This class represents the state of the game.
 */
public class GameState {

	private static GameStateValue _state = GameStateValue.INIT;
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
	
	/**
	 * @param state set the state of the game
	 */
	public void setGameState(GameStateValue state){
		_state = state;
	}
}
