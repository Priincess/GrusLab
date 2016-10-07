package Game.Player;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;

public class ControllerManager {

	private static String PS4_NAME = "Wireless Controller";
	private static String XBOX_NAME = "Controller (Xbox 360 Wireless Receiver for Windows)";
	
	private boolean _manage = false;
	
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
	
	public void waitForPlayer(Player p1, Player p2){
		boolean p1pressed=false;
		boolean p2pressed=false;
		
		while(!p1pressed || !p2pressed){
			p1.getController().poll();
			p2.getController().poll();
			
			if(!p1pressed && p1.getController().isButtonPressed(p1.getGamepad().getForwardIndex())){
				p1pressed =true;
				System.out.println("Player1 pressed start!");
			}
			
			if(!p2pressed && p2.getController().isButtonPressed(p2.getGamepad().getForwardIndex())){
				p2pressed =true;
				System.out.println("Player2 pressed start!");
			}
		}
	}
	
	public void manageSignals(Player p1, Player p2){
		_manage = true;
		
		while(_manage){
			p1.getController().poll();
			p2.getController().poll();
			
			//check backward button
			checkBackward(p1);
			checkBackward(p2);
			
			//check forward button
			checkForward(p1);
			checkForward(p2);
			
			//check left - right button
			checkLeftRight(p1);
			checkLeftRight(p2);
			
			//TODO - remove only test
			if(p1.isForward()){
				if(p1.isLeft()){
					System.out.println("Forward - Left");
				}else if(p1.isRight()){
					System.out.println("Forward - Right");
				}else{
					System.out.println("Forward");
				}
			}else if(p1.isBackward()){
				if(p1.isLeft()){
					System.out.println("Backward - Left");
				}else if(p1.isRight()){
					System.out.println("Backward - Right");
				}else{
					System.out.println("Backward");
				}
			}else{
				if(p1.isRight()){
					System.out.println("Right");
				}		
				if(p1.isLeft()){
					System.out.println("Left");
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void stopManage(){
		_manage = false;
	}
	
	private void checkForward(Player p){
		if(p.getController().isButtonPressed(p.getGamepad().getForwardIndex())){
			p.setForward(true);
			p.setBackward(false);
		}else{
			p.setForward(false);
		}
	}
	
	private void checkBackward(Player p){
		if(p.getController().isButtonPressed(p.getGamepad().getBackwardIndex())){
			p.setBackward(true);
			p.setForward(false);
		}else{
			p.setBackward(false);
		}
	}
	
	private void checkLeftRight(Player p){
		if(p.getController().getPovX() == p.getGamepad().getLeftValue()){
			p.setLeft(true);
			p.setRight(false);
		}else if(p.getController().getPovX() == p.getGamepad().getRightValue()){
			p.setLeft(false);
			p.setRight(true);
		}else{
			p.setLeft(false);
			p.setRight(false);
		}
	}
}
