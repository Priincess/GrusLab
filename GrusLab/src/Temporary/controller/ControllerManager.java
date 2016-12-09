package Temporary.controller;

public class ControllerManager {
	
	private static GameSettingsController _gameSettingsController;	
	private static GameboardController _gameBoardController;
	private static GameMenuController _mainController;
	
	private ControllerManager(){}
	
	public static void createController(){
		_gameSettingsController = new GameSettingsController();
		_gameBoardController = new GameboardController();
		_mainController = new GameMenuController();
	}
	
	
	public static GameSettingsController getSettingsControllerInstance(){
		
		return _gameSettingsController;
	}
	
	public static GameboardController getGameBoardController(){
		return _gameBoardController;
	}
	
	
	public static GameMenuController getMainController(){
		return _mainController;
	}

	
}
