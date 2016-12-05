package Temporary.controller;

public class ControllerManager {
	
	private static GameSettingsController _gameSettingsController;	
	private static GameBoardController _gameBoardController;
	private static MainController _mainController;
	
	private ControllerManager(){}
	
	public static void createController(){
		_gameSettingsController = new GameSettingsController();
		_gameBoardController = new GameBoardController();
		_mainController = new MainController();
	}
	
	
	public static GameSettingsController getSettingsControllerInstance(){
		
		return _gameSettingsController;
	}
	
	
	public static GameBoardController getGameBoardController(){
		return _gameBoardController;
	}
	
	
	public static MainController getMainController(){
		return _mainController;
	}

	
}
