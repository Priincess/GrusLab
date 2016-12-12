package game.player;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;

import game.Components;
import game.GameState;
import game.GameStateValue;

/**
 * @author lilpr
 * This class is responsible for the registered gamepads. Among other things it polls the pressed buttons.
 */
public class GamepadManager {
	
	private boolean _manage = false;
	
	//**********PUBLIC METHODS**********
	
	/**
	 * This method waits until two gamepads are registered
	 * @param players array with players
	 */
	public void initGamepad(Player[] players){
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
				if(amountOfPlayer < players.length){
					if(Controllers.getController(i).getName().equals(Gamepad.PS4.getName())){
						if(amountOfPlayer ==0 || !players[amountOfPlayer-1].getController().equals(Controllers.getController(i))){
							players[amountOfPlayer].setController(Controllers.getController(i));
							players[amountOfPlayer].setGamepad(Gamepad.PS4);
							amountOfPlayer++;
						}
					}else if(Controllers.getController(i).getName().equals(Gamepad.Xbox.getName())){
						if(amountOfPlayer ==0 || !players[amountOfPlayer-1].getController().equals(Controllers.getController(i))){
							players[amountOfPlayer].setController(Controllers.getController(i));
							players[amountOfPlayer].setGamepad(Gamepad.Xbox);
							amountOfPlayer++;
						}
					}else if(Controllers.getController(i).getName().equals(Gamepad.XboxS.getName())){
						if(amountOfPlayer ==0 || !players[amountOfPlayer-1].getController().equals(Controllers.getController(i))){
							players[amountOfPlayer].setController(Controllers.getController(i));
							players[amountOfPlayer].setGamepad(Gamepad.XboxS);
							amountOfPlayer++;
						}
					}
				}
			}
			
			Controllers.destroy();
			System.gc();
			System.runFinalization();
		}while(amountOfPlayer < players.length);
		System.out.println("Controllers connected!");
	}
	
	/**
	 * This method waits for both player to press the button to start
	 * 
	 * @param players array of players
	 */
	public void waitForPlayer(Player[] players){
		
		System.out.println("Wait for Players to press start!");
		
		boolean[] pressed = new boolean[players.length];
		boolean condition=false;
		
		//wait until both player pressed the button
		do{
			for(int i =0; i<players.length; i++){
				if(!players[i].getController().poll()){
					initGamepad(players);
					System.out.println("Wait for Players to press start!");
				}
				
				//check if player pressed the button
				if (!pressed[i] && players[i].getController().isButtonPressed(players[i].getGamepad().getStartIndex())) {
					pressed[i] =true;
					System.out.println("Player"+i+" pressed start!");
				}
			}
			
			//check if all players pressed start
			for(int i =0; i<pressed.length; i++){
				if(i == 0){
					condition = pressed[i];
				}else{
					condition &= pressed[i];
				}
			}
		}while(!condition);
	}
		
	/**
	 * @param players array with players - check controller
	 */
	public void manageSignals(Player[] players){
		_manage = true;
		
		//manage the state the controller until stop signal is set
		while (_manage) {
			if(GameState.getGameState() != GameStateValue.PAUSE){
				//poll from controllers
				for(int i = 0; i < players.length; i++){
					if(!players[i].getController().poll()){
						GameState.problemOccured(this.toString());
						Components.setControllerConnected(i, false);
						players[i].setControllerState(false, false, false, false);
						initGamepad(players);
						Components.setControllerConnected(i, true);
						GameState.problemSolved(this.toString());
					}else{
						//check state of controllers
						checkControllerState(players[i]);
					}
				}
			}else {
				if(GameState.readyToResume()){
					waitForPlayer(players);
					GameState.getInstance().setGameState(GameStateValue.PLAY);
				}
			}
		}
		
		//reset player
		for(int i = 0; i < players.length; i++){
			players[i].reset();
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
		
		//set controller state
		player.setControllerState(forward, backward, left, right);
	}
}
