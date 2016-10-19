package game.player;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;

/**
 * @author lilpr
 * This class is responsible for the registered controllers. Among other things it polls the pressed buttons.
 */
public class ControllerManager {

	private static String PS4_NAME = "Wireless Controller";
	private static String XBOX_NAME = "Controller (Xbox 360 Wireless Receiver for Windows)";
	
	private boolean _manage = false;
	
	//**********PUBLIC METHODS**********
	
	/**
	 * This method waits until two controller are registered
	 * @return list of players
	 */
	public Player[] initController(){
		Player[] players = new Player[2];
		int amountOfPlayer=0;
		
		do{
			amountOfPlayer = 0;
			//create Controller
			try {
				Controllers.create();
			} catch (LWJGLException e1) {
			}
			
			Controllers.poll();
			//check if xbox or ps4 controller is connected
			for(int i=0; i<Controllers.getControllerCount(); i++){
				if(amountOfPlayer < 2){
					if(Controllers.getController(i).getName().equals(PS4_NAME)){
						if(amountOfPlayer ==0 || !players[amountOfPlayer-1].getController().equals(Controllers.getController(i))){
							players[amountOfPlayer] = new Player(Controllers.getController(i), Gamepad.PS4);
							amountOfPlayer++;
						}
					}else if(Controllers.getController(i).getName().equals(XBOX_NAME)){
						if(amountOfPlayer ==0 || !players[amountOfPlayer-1].getController().equals(Controllers.getController(i))){
							players[amountOfPlayer] = new Player(Controllers.getController(i), Gamepad.Xbox);
							amountOfPlayer++;
						}
					}
				}
			}
			
			Controllers.destroy();
		}while(amountOfPlayer < 2);
		
		return players;
	}
	
	/**
	 * This method waits for both player to press the button to start
	 * 
	 * @param p1 player 1
	 * @param p2 player 2
	 */
	public void waitForPlayer(Player p1, Player p2){
		boolean p1pressed=false;
		boolean p2pressed=false;
		
		//wait until both player pressed the button
		while (!p1pressed || !p2pressed) {
			p1.getController().poll();
			p2.getController().poll();
			
			//check if player 1 pressed the button
			if (!p1pressed && p1.getController().isButtonPressed(p1.getGamepad().getForwardIndex())) {
				p1pressed =true;
				System.out.println("Player1 pressed start!");
			}
			
			//check if player 2 pressed the button
			if (!p2pressed && p2.getController().isButtonPressed(p2.getGamepad().getForwardIndex())) {
				p2pressed =true;
				System.out.println("Player2 pressed start!");
			}
		}
	}
	
	/**
	 * @param p1 player 1 - check controller
	 * @param p2 player 2 - check controller
	 */
	public void manageSignals(Player p1, Player p2){
		_manage = true;
		
		//manage the state the controller until stop signal is set
		while (_manage) {
			//poll from controllers
			p1.getController().poll();
			p2.getController().poll();
			
			//check state of controllers
			checkControllerState(p1);
			checkControllerState(p2);
		}
	}
	
	/**
	 * This method stops the management of the controller state of the players
	 */
	public void stopManage(){
		_manage = false;
	}
	
	//**********PRIVATE METHODS**********
	
	/**
	 * @param player whose controller to checks
	 */
	private void checkControllerState(Player player){
		boolean forward=false;
		boolean backward=false;
		boolean right=false;
		boolean left=false;
		
		//check if forward or backward button is pressed
		forward = player.getController().isButtonPressed(player.getGamepad().getForwardIndex());
		backward = player.getController().isButtonPressed(player.getGamepad().getBackwardIndex());
		
		//check if left or right button is pressed
		if (player.getController().getPovX() == player.getGamepad().getLeftValue()) {
			left = true;
		} else if (player.getController().getPovX() == player.getGamepad().getRightValue()) {
			right = true;
		}	
		
		//set conrtoller state
		player.setControllerState(forward, backward, left, right);
	}
}
