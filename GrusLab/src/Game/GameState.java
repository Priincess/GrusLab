package Game;

public class GameState {

	private static GameStateValue _state = GameStateValue.INIT;
	private static GameState _instance = new GameState();
		
	private GameState(){}
	
	public static GameState getInstance() {
		return _instance;
	}
	
	public static GameStateValue getGameState(){
		return _state;
	}
	
	public void setGameState(GameStateValue state){
		_state = state;
	}
}
