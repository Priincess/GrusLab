package game.controller;

public class ControllerManager {
	
	private static GameSettingsController _gameSettingsController;	
	private static GameMenuController _mainController;
	
	private ControllerManager(){}
	
	public static void createController(){
		_gameSettingsController = new GameSettingsController();
		_mainController = new GameMenuController();
	}
	
	
	public static GameSettingsController getSettingsControllerInstance(){
		
		return _gameSettingsController;
	}
	

	public static GameMenuController getMainController(){
		return _mainController;
	}

	
}
