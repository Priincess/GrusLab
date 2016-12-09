package game;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lilpr This class represents the state of the game.
 */
public class GameState {

	private static GameStateValue _state = GameStateValue.INIT;

	private static GameState _instance = new GameState();

	private static List<String> _problems = new ArrayList<>();

	private GameState() {
	}

	// **********PUBLIC METHODS**********

	/**
	 * @return the instance of this class
	 */
	public static GameState getInstance() {
		return _instance;
	}

	/**
	 * @return the state of the game
	 */
	public static GameStateValue getGameState() {
		return _state;
	}

	/**
	 * @param state
	 *            set the state of the game
	 */
	public void setGameState(GameStateValue state) {

		_state = state;
	}

	/**
	 * add problem to list
	 */
	public static void problemOccured(String key) {
		if (!_problems.contains(key)) {
			_state = GameStateValue.PAUSE;
			_problems.add(key);
		}
	}

	/**
	 * remove problem from list
	 */
	public static void problemSolved(String key) {
		_problems.remove(key);
	}

	/**
	 * check if no problems left
	 */
	public static boolean readyToResume() {
		return _problems.isEmpty();
	}
}
