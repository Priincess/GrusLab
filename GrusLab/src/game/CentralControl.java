package game;

import game.camera.ObjTracker;
import game.gameboard.Gameboard;
import game.gui.GuiManager;
import game.player.ControllerManager;
import game.player.Player;
import game.server.ServerService;
import org.opencv.highgui.VideoCapture;

/**
 * @author lilpr
 * This class manages the sequence of the tasks. 
 */
public class CentralControl {

	private static CentralControl _instance = new CentralControl();
	
	//components
	private GameState _gameState = null;
	private ServerService _server = null;
	private ControllerManager _controllerManager = null;
	private Player _player1 = null;
	private Player _player2 = null;
	private ObjTracker _tracker = null;
	private GuiManager _guiManager = null;
	private Game _game = null;
	
	private boolean _playing = true;
	
	//threads
	//controllers
	Thread _controllerInit;
	Thread _controllerWaitForStart;
	Thread _controllerStart;
	//server
	Thread _serverInit;
	Thread _serverStart;
	//gui
	Thread _guiRuntime;
	//minion tracker
	Thread _objectTracker;
	
	private CentralControl(){
		//initialize components
		_gameState = GameState.getInstance();
		_server = new ServerService();
		_controllerManager = new ControllerManager();
		_tracker = new ObjTracker();
		_game = new Game(_tracker);
		_guiManager = new GuiManager(_game);
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

		_controllerInit.start();
		_serverInit.start();

		while(!_controllerInit.getState().equals(Thread.State.TERMINATED) || !_serverInit.getState().equals(Thread.State.TERMINATED)){}


		while(_playing){

			_gameState.setGameState(GameStateValue.WAIT);

			System.out.println("Wait for Players to press start!");
			_controllerWaitForStart.start();

			while(!_controllerWaitForStart.getState().equals(Thread.State.TERMINATED)){}

			_gameState.setGameState(GameStateValue.READY);

			while(_gameState.getGameState() != GameStateValue.PLAY){}

			_controllerStart.start();
			_serverStart.start();
			_objectTracker.start();
			
			//TODO: wait for game to finish
			_server.stopMinionControl();
			_controllerManager.stopManage();
			_tracker.stopTracking();
			
			while(!_controllerStart.getState().equals(Thread.State.TERMINATED) || !_serverStart.getState().equals(Thread.State.TERMINATED)){}
		
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
		_controllerInit = new Thread(new Runnable() {
			@Override
			public void run() {
				Player[] players = _controllerManager.initController();
				_player1 = players[0];
				_player2 = players[1];
			}
		});
		
		//get confirmation from player for start
		_controllerWaitForStart = new Thread(new Runnable() {
			@Override
			public void run() {
				_controllerManager.waitForPlayer(_player1, _player2);
			}
		});
		
		//start controller recognition
		_controllerStart = new Thread(new Runnable() {
			@Override
			public void run() {
				_controllerManager.manageSignals(_player1, _player2);
			}
		});
		
		//connect both robots to server
		_serverInit = new Thread(new Runnable() {
			@Override
			public void run() {
				_server.connectWithMinions();
			}
		});
		
		//start robot control
		_serverStart = new Thread(new Runnable() {
			@Override
			public void run() {
				_server.startMinionControl(new Player[]{_player1, _player2});
			}
		});

		//starts gui and object-tracker
		_guiRuntime = new Thread(new Runnable() {
			@Override
			public void run() {
				_guiManager.launchGUI();
			}
		});

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
		_server.deinitServerService();
		
		_player1 = null;
		_player2 = null;
		_server = null;
		_controllerManager = null;
	}
}
