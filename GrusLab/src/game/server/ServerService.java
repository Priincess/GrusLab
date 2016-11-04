package game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Formatter;
import java.util.Scanner;

import game.Components;
import game.GameState;
import game.GameStateValue;
import game.player.ControllerState;
import game.player.Minion;
import game.player.Player;
import game.player.PlayerState;

/**
 * @author lilpr
 * This class is responsible for the communication the the minions.
 */
public class ServerService {

	private static int AMOUNT_OF_MINIONS = 2;
	private static int INDEX_MINION1 = 0;
	private static int INDEX_MINION2 = 1;
	
	//components to communicate with the clients
	private ServerSocket _server = null;
	private Formatter[] _outputs = new Formatter[2];
	private Scanner[] _inputs = new Scanner[2];
	private Socket[] _minions = new Socket[2];
	private boolean _controlMinions = true;
	
	//components for heart beat
	HeartBeat _hB1;
	HeartBeat _hB2;
	private Thread _heartBeatM1;
	private Thread _heartBeatM2;
	
	//**********PUBLIC METHODS**********
	
	public ServerService(){
		
		//initialize server socket
		try {
			_server = new ServerSocket(12345,2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//initialize components for heart beat
		_hB1 = new HeartBeat(INDEX_MINION1);
		_hB2 = new HeartBeat(INDEX_MINION2);
		_heartBeatM1 = new Thread(_hB1);
		_heartBeatM2 = new Thread(_hB2);
	}
	
	/**
	 * This method waits until both minions are connect
	 */
	public void connectWithMinions(Player[] players){
		
		try {
			//wait till both minions are connected
			for (int i=0; i<AMOUNT_OF_MINIONS; i++) {
				_minions[i] = _server.accept();
				_outputs[i] = new Formatter(_minions[i].getOutputStream());
				_inputs[i] = new Scanner(_minions[i].getInputStream());
				Components.setMinionConnected(i, true);
//				players[i].setMinion(Minion.getMinionByValue(Integer.parseInt(_inputs[i].nextLine())));
//				System.out.println(players[i].getMinion().toString()+" Minion connected!");
				System.out.println("Minion "+i+" connected!");
			}
			
//			_heartBeatM1.start();
//			_heartBeatM2.start();
//			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param players to get the controller state
	 */
	public void startMinionControl(Player[] players){
		int playerState;
		int controllerState;
		String bundle;
		
		//control until stop flag is set
		while (_controlMinions) {
			
			if(GameState.getGameState() == GameStateValue.PAUSE){
				stopBothMinions();
				while(GameState.getGameState() == GameStateValue.PAUSE){}
			}
			
			for (int i = 0; i< AMOUNT_OF_MINIONS; i++) {
				
				if (players[i].hasControllerChanged()) {
					players[i].resetControllerChanged();
					playerState = players[i].getPlayerState().getStateValue();
					controllerState = players[i].getControllerState().getControllerValue();
					bundle = playerState+""+controllerState+"\n";
					
					sendCommandToMinion(i, bundle);				
				}
			}
		}
		
		//stop both minions
		stopBothMinions();
	}
	
	/**
	 * This method stops the control of the minions
	 */
	public void stopMinionControl(){
		_controlMinions = false;
	}
	
	/**
	 * This method closes the connection to the minions
	 */
	public void deinitServerService(){
		
		//stop heart beat
		_hB1.stopHeartBeat();
		_hB2.stopHeartBeat();
		
		//disconnect minions
		for (int i = 0; i< AMOUNT_OF_MINIONS; i++) {
			try {
				_outputs[i].close();
				_minions[i].close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//close server
		try {
			_server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//**********PRIVATE METHODS**********
	
	/**
	 * This method send the stop command to both minions
	 */
	private void stopBothMinions(){
		String bundle;

		for (int i = 0; i< AMOUNT_OF_MINIONS; i++) {
			bundle = PlayerState.Normal.getStateValue()+""+ControllerState.None.getControllerValue()+"\n";
			sendCommandToMinion(i, bundle);
		}
	}
	
	/**
	 * @param minionIndex which minion to write
	 * @param bundle command to send to minion
	 */
	private void sendCommandToMinion(int minionIndex, String bundle){
		_outputs[minionIndex].format(bundle);
		_outputs[minionIndex].flush();	
	}
	
	//**********PRIVATE CLASSES**********
	
	/**
	 * @author lilpr
	 *	This private class is runnable. It is responsible for the heart beat between server and client. 
	 */
	private class HeartBeat implements Runnable{
		
		private int _minionIndex;
		private boolean _stop=false;
		
		public HeartBeat(int minionIndex) {
			_minionIndex = minionIndex;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			//check if stopped
			while(!_stop){
//				//send heart beat
//				_outputs[_minionIndex].format("-1");
//				_outputs[_minionIndex].flush();
				
				//try to get heart beat from client
				try{
					_inputs[_minionIndex].nextLine();
				}catch (Exception e){
					System.out.println("CLIENT MISS");
					//no client reachable -> pause game and try to reconnect to client
					GameState.getInstance().setGameState(GameStateValue.PAUSE);
					Components.setMinionConnected(_minionIndex, false);
					
					try {
						_minions[_minionIndex] = _server.accept();
						_outputs[_minionIndex] = new Formatter(_minions[_minionIndex].getOutputStream());
						_inputs[_minionIndex] = new Scanner(_minions[_minionIndex].getInputStream());
						
						//continue game
						Components.setMinionConnected(_minionIndex, true);
						GameState.getInstance().setGameState(GameStateValue.PLAY);
						System.out.println("CLIENT RECON");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		
		/**
		 * This method stops the heart beat
		 */
		public void stopHeartBeat(){
			_stop = true;
		}
		
	}
}
