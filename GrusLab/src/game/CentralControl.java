package game;

import game.camera.ObjTracker;
import game.controller.ControllerManager;
import game.gui.GuiManager;
import game.player.GamepadManager;
import game.player.Minion;
import game.player.Player;
import game.server.Server;

/**
 * @author lilpr
 * This class manages the sequence of the tasks. 
 */
public class CentralControl {

	private static final int AMOUNT_OF_PLAYERS = 2;
	private static CentralControl _instance = new CentralControl();
	
	//components
	private GameState _gameState = null;
	private Server _server = null;
	private GamepadManager _gamepadManager = null;
	private Player[] _players;
	private ObjTracker _tracker = null;
	
	//threads
	//controllers
	Thread _gamepadInit;
	Thread _gamepadWaitForStart;
	Thread _gamepadStart;
	//server
	Thread _serverStart;
	//gui
	Thread _guiRuntime;
	//minion tracker
	Thread _objectTracker;
	
	private CentralControl(){
		//initialize components
		_players = new Player[AMOUNT_OF_PLAYERS];
		_players[0] = new Player(Minion.Yellow);
		_players[1] = new Player(Minion.Purple);
		_gameState = GameState.getInstance();
		_server = new Server();
		_gamepadManager = new GamepadManager();
		_tracker = new ObjTracker();

		ControllerManager.createController(_tracker, _players[0], _players[1]);
	}
	
	//**********PUBLIC METHODS**********
	
	/**
	 * @return the instance of this class
	 */
	public static CentralControl getInstance(){
		return _instance;
	}
	
	/**
	 * This method starts the game
	 */
	public void startGame(){
		_gameState.setGameState(GameStateValue.INIT);
		
		//init threads
		initThreads();

		_guiRuntime.start();
		_objectTracker.start();
		_gamepadInit.start();
		_serverStart.start();


		//wait until all gamepads are connected
		while(!_gamepadInit.getState().equals(Thread.State.TERMINATED)){}
		
		//start manage of gamepad for preferences
		_gamepadStart.start();
		
		//wait until both minions are connected
		while(!_server.connectionsEstablished() || !_tracker.isReady()){}
		
		//all components are loaded
		GameState.getInstance().setGameState(GameStateValue.LOADED);
		
		//play until game exit
		while(GameState.getGameState() != GameStateValue.EXIT){
			
			//wait until game is ready
			while(GameState.getGameState() != GameStateValue.READY){}
			
			//wait for players to press start
			_gamepadManager.stopManage();
			_gamepadWaitForStart.start();

			while(!_gamepadWaitForStart.getState().equals(Thread.State.TERMINATED)){}

			//start countdown
			_gamepadStart.start();
			_gameState.setGameState(GameStateValue.COUNTDOWN);

			//wait until game is finished
			while(GameState.getGameState() != GameStateValue.FINISHED){}
			
			_gamepadManager.stopManage();
		}
		
		System.out.println("Shuting down...");		
		//quit after game exit
		quitGame();
	}
	
	//**********PRIVATE METHODS**********
	
	/**
	 * This method initializes all the threads
	 */
	private void initThreads(){
		//connect both game controller
		_gamepadInit = new Thread(new Runnable() {
			@Override
			public void run() {
				_gamepadManager.initGamepad(_players);
			}
		});
		
		//get confirmation from player for start
		_gamepadWaitForStart = new Thread(new Runnable() {
			@Override
			public void run() {
				_gamepadManager.waitForPlayer(_players);
			}
		});
		
		//start controller recognition
		_gamepadStart = new Thread(new Runnable() {
			@Override
			public void run() {
				_gamepadManager.manageSignals(_players);
			}
		});
		
		//start server
		_serverStart = new Thread(new Runnable() {
			@Override
			public void run() {
				_server.startServer(_players);
			}
		});

		//starts gui and object-tracker
		_guiRuntime = new Thread(new Runnable() {
			@Override
			public void run() {
				javafx.application.Application.launch(GuiManager.class);
			}
		});

		//start object-tracker
		_objectTracker = new Thread(new Runnable() {
			@Override
			public void run() {
				_tracker.startTracking();
			}
		});
	}

	/**
	 * This method sets quits everything 
	 */
	private void quitGame(){
		_server.stopServer();
		_tracker.stopTracking();
		_players = null;
		_server = null;
		_gamepadManager = null;
	}
}
