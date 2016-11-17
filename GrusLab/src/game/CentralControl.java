package game;

import game.camera.ObjTracker;
import game.gui.GuiManager;
import game.player.ControllerManager;
import game.player.Minion;
import game.player.Player;
import game.server.Server;

/**
 * @author lilpr
 * This class manages the sequence of the tasks. 
 */
public class CentralControl {

	private static CentralControl _instance = new CentralControl();
	
	//components
	private GameState _gameState = null;
	private Server _server = null;
	private ControllerManager _controllerManager = null;
	private Player[] _players;
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
	Thread _serverStart;
	//gui
	Thread _guiRuntime;
	//minion tracker
	Thread _objectTracker;
	
	private CentralControl(){
		//initialize components
		//TODO player = 2; id minion purple = 1
		_players = new Player[1];
		//_players[0] = new Player(Minion.Yellow);
		_players[0] = new Player(Minion.Purple);
		_gameState = GameState.getInstance();
		_server = new Server();
		_controllerManager = new ControllerManager();
		_tracker = new ObjTracker();
		//_game = new Game(_tracker);
		_guiManager = new GuiManager();
		//_guiManager.setGame(_game);
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

		//_guiRuntime.start();
		//_objectTracker.start();
//
		_controllerInit.start();
		_serverStart.start();
//
		
		while(!_controllerInit.getState().equals(Thread.State.TERMINATED) || !_server.connectionsEstablished()){}
//

		while(_playing){

//			_gameState.setGameState(GameStateValue.WAIT);
//
			System.out.println("Wait for Players to press start!");
			_controllerWaitForStart.start();
//
			while(!_controllerWaitForStart.getState().equals(Thread.State.TERMINATED)){}
//
//			_gameState.setGameState(GameStateValue.READY);

	//		while(GameState.getGameState() != GameStateValue.PLAY){}

			_controllerStart.start();
			_gameState.setGameState(GameStateValue.PLAY);
//			_serverStart.start();
			
			//TODO: wait for game to finish
//			_server.stopMinionControl();
//			_controllerManager.stopManage();
//			_tracker.stopTracking();
			
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
				_controllerManager.initController(_players);
			}
		});
		
		//get confirmation from player for start
		_controllerWaitForStart = new Thread(new Runnable() {
			@Override
			public void run() {
				_controllerManager.waitForPlayer(_players);
			}
		});
		
		//start controller recognition
		_controllerStart = new Thread(new Runnable() {
			@Override
			public void run() {
				_controllerManager.manageSignals(_players);
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
				_guiManager.launchGUI();
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
		
		_players = null;
		_server = null;
		_controllerManager = null;
	}
}
