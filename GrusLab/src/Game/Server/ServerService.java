package Game.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Formatter;

import Game.Player.Player;

/**
 * @author lilpr
 * This class is responsible for the communication the the minions.
 */
public class ServerService {

	private static int AMOUNT_OF_MINIONS = 2;
	
	private ServerSocket _server = null;
	private Formatter[] _outputs = new Formatter[2];
	private Socket[] _minions = new Socket[2];
	private boolean _controlMinions = true;
	
	//**********PUBLIC METHODS**********
	
	public ServerService(){
		
		//initialize server socket
		try {
			_server = new ServerSocket(12345,2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method waits until both minions are connect
	 */
	public void connectWithMinions(){
		
		try {
			//wait till both minions are connected
			for (int i=0; i<AMOUNT_OF_MINIONS; i++) {
				_minions[i] = _server.accept();
				_outputs[i] = new Formatter(_minions[i].getOutputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	/**
//	 * @param players to get the controller state
//	 */
//	public void startMinionControl(Player[] players){
//		int playerState;
//		int controllerState;
//		String bundle;
//		
//		//control until stop flag is set
//		while (_controlMinions) {
//			
//			for (int i = 0; i< AMOUNT_OF_MINIONS; i++) {
//				playerState = players[i].getPlayerState().getStateValue();
//				controllerState = players[i].getControllerState().getControllerValue();
//				bundle = playerState+""+controllerState+"\n";
//				
//				_outputs[i].format(bundle);
//				_outputs[i].flush();
//			}
//			//TODO: remove only because console isnt that fast
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
	
	/**
	 * @param players to get the controller state
	 */
	public void startMinionControl(Player[] players){
		int playerState;
		int controllerState;
		String bundle;
		
		//control until stop flag is set
		while (_controlMinions) {
			
			for (int i = 0; i< AMOUNT_OF_MINIONS; i++) {
				
				if (players[i].hasControllerChanged()) {
					players[i].resetControllerChanged();
					playerState = players[i].getPlayerState().getStateValue();
					controllerState = players[i].getControllerState().getControllerValue();
					bundle = playerState+""+controllerState+"\n";
					
					_outputs[i].format(bundle);
					_outputs[i].flush();
				}
			}
		}
		
		for (int i = 0; i< AMOUNT_OF_MINIONS; i++) {
			try {
				_outputs[i].close();
				_minions[i].close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method stops the control of the minions
	 */
	public void stopMinionControl(){
		_controlMinions = false;
	}
}
