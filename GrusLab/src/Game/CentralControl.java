package Game;

import Game.Player.ControllerManager;
import Game.Player.Player;

public class CentralControl {

	private static CentralControl _instance = new CentralControl();
	
	//components
	private ControllerManager _controllerManager;
	private Player _player1=null;
	private Player _player2=null;
	
	//threads
	//controllers
	Thread _controllerInit;
	Thread _controllerWaitForStart;
	Thread _controllerStart;
	//server
	Thread _serverInit;
	Thread _serverStart;
	
	private CentralControl(){
		//initialize components
		_controllerManager = new ControllerManager();
	}
	
	public static CentralControl getInstance(){
		return _instance;
	}
	
	public void startGame(){
		GameState.getInstance().setGameState(GameStateValue.INIT);
		
		//init threads
		initThreads();
		
		_controllerInit.start();
//		_serverInit.start();
		
		while(!_controllerInit.getState().equals(Thread.State.TERMINATED)){};
//		while(!_controllerInit.getState().equals(Thread.State.TERMINATED) && !_serverInit.getState().equals(Thread.State.TERMINATED)){}
		
		GameState.getInstance().setGameState(GameStateValue.WAIT);
		
		System.out.println("Wait for Players to press X/A!");
		_controllerWaitForStart.start();
		
		while(!_controllerWaitForStart.getState().equals(Thread.State.TERMINATED)){}
		
		GameState.getInstance().setGameState(GameStateValue.PLAY);
		
		_controllerStart.start();
		
		while(!_controllerStart.getState().equals(Thread.State.TERMINATED)){}
		
		System.out.println("Finished");
//		_controllerStart.start();
//		_serverStart.start();
		
		//reset after game finish
		resetGame();
	}
	
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
				//TODO
			}
		});
		
		//start robot control
		_serverStart = new Thread(new Runnable() {
			@Override
			public void run() {
				//TODO
			}
		});
	}

	private void resetGame(){
		_player1 = null;
		_player2 = null;
		
	}
}
