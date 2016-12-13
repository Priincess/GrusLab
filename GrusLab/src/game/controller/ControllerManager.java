package game.controller;

import game.camera.ObjTracker;
import game.game.Game;
import game.player.Player;

public class ControllerManager {
	private static Game _game;
	private static GameSettingsController _gameSettingsController;	
	private static GameMenuController _gameMenuController;
	
	private ControllerManager(){}
	
	public static void createController(ObjTracker tracker, Player yellowPlayer, Player purplePlayer){
		_game = new Game(tracker, yellowPlayer, purplePlayer);
		_gameSettingsController = new GameSettingsController(yellowPlayer, purplePlayer);
		_gameMenuController = new GameMenuController(yellowPlayer, purplePlayer);
	}


	public static Game getGame() { return _game; }
	public static GameSettingsController getGameSettingsController() { return _gameSettingsController; }
	public static GameMenuController getGameMenuController(){
		return _gameMenuController;
	}

	
}
