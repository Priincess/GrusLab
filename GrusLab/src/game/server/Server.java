package game.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import game.Components;
import game.GameState;
import game.GameStateValue;
import game.player.Minion;
import game.player.Player;

public class Server {

	//server information
	private static int IDLE = 0;
	private static int DISABLED = 0;
	private static int ENABLED = 1;
	private static int PORT = 2432;
	private static int VERSION = 1;
	private static int SAFETYBIT = 2;
	private static long WAITINGTIME_REPLYREQUEST = 500;
	private static long WAITINGTIME_DISCONNECED = 1000;
	
	private DatagramSocket _socket;
	private boolean _running = false;
	
	private Communicator _com1;
	private Communicator _com2;
	
	
	//**********PUBLIC METHODS**********
	
 	public Server(){
		try {
			_socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method starts the server
	 * @param p1 player 1
	 * @param p2 player 2
	 */
	public void startServer(Player p1, Player p2){
		
		//init minions and communicator
		p1.setMinion(Minion.Yellow);
		p2.setMinion(Minion.Purple);
		_com1 = new Communicator(p1);
		_com2 = new Communicator(p2);
		
		Thread t1 = new Thread(_com1);
		Thread t2 = new Thread(_com2);
		t1.run();
		t2.run();
		
		_running = true;
		
		while(_running){
			
			if(GameState.getGameState() == GameStateValue.PLAY){
				checkIfRobotsConnected();
				_com1.setIdel(false);
				_com2.setIdel(false);
			}else{
				_com1.setIdel(true);
				_com2.setIdel(true);
			}
		}
		
		//tell both communicators to finish communication
		_com1.setFinished();
		_com2.setFinished();
		
		while(t1.getState() != Thread.State.TERMINATED || t2.getState() != Thread.State.TERMINATED){}
		_socket.close();
	}
	
	/**
	 * This method stops the server
	 */
	public void stopServer(){
		_running = false;
	}
	
	/**
	 * @return if both robots are connected
	 */
	public boolean connectionsEstablished(){
		return _com1._isConnected && _com2.isConnected();
	}
	
	//**********PRIVATE METHODS**********
	
	/**
	 * This method checks if both robots are still connected and pause game if not
	 */
	private void checkIfRobotsConnected(){
		
		//check if both robots are connected
		if(!_com1.isConnected()){
			
			//pause game
			GameState.getInstance().setGameState(GameStateValue.PAUSE);
			
			//set idle
			_com1.setIdel(true);
			_com2.setIdel(true);
			Components.setMinionConnected(_com1._player.getMinion().getValue(), false);
			
			//wait until reconnect
			while(!_com1.isConnected()){}
			
			//continue game
			Components.setMinionConnected(_com1._player.getMinion().getValue(), true);
			GameState.getInstance().setGameState(GameStateValue.PLAY);
		
		}else if(!_com2.isConnected()){
			
			//pause game
			GameState.getInstance().setGameState(GameStateValue.PAUSE);
			
			//set idle
			_com1.setIdel(true);
			_com2.setIdel(true);
			Components.setMinionConnected(_com2._player.getMinion().getValue(), false);
			
			//wait until reconnect
			while(!_com2.isConnected()){}
			
			//continue game
			Components.setMinionConnected(_com2._player.getMinion().getValue(), true);
			GameState.getInstance().setGameState(GameStateValue.PLAY);
		}
	}
	
	//**********PRIVATE CLASSES**********
	
	private class Communicator implements Runnable{
		
		private Player _player = null;
		private boolean _finished = false;
		private boolean _isConnected = false;
		private boolean _isIdle = true;
		private boolean _received = false;
		private InetAddress _robotAddress = null;
		
		
		public Communicator(Player player){
			_player = player;
		}
		
		@Override
		public void run() {
			
			long timestampReply = 0;
			long timestampDisconnected = 0;
			int reply=DISABLED;
			
			//run until game exit
			while(!_finished){
				
				connect();
				//get current time
				timestampReply = System.currentTimeMillis();
				
				while(_isConnected){
					
					//check if game is idle or not
					if(_isIdle){
						sendIdleCMD(reply);
					}else{
						sendCMD(reply);
					}
					
					//check if it is time to get a reply from robot
					if(reply == DISABLED && timestampReply + WAITINGTIME_REPLYREQUEST <= System.currentTimeMillis()){
						reply = ENABLED;
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								waitForReply(ENABLED);
							}
						}).run();
						timestampDisconnected = System.currentTimeMillis();
					}
					
					//if reply is requested check if waiting time is over -> robot is disconnected
					if(reply == ENABLED && timestampDisconnected + WAITINGTIME_DISCONNECED <= System.currentTimeMillis()){
						_isConnected = false;
						reply = DISABLED;
					}
					
				}
			}		
			exit();
		}
		
		/**
		 * @return if robot is connected
		 */
		public boolean isConnected(){
			return _isConnected;
		}
		
		/**
		 * @param idle set if game idle
		 */
		public void setIdel(boolean idle){
			_isIdle = idle;
		}
		
		/**
		 * Set to finished
		 */
		public void setFinished(){
			_finished = true;
		}
		
		//**********PRIVATE METHODS**********
		
		/**
		 * Wait for packet from the specific robot
		 */
		private void connect(){
			try {
				//receive packet from robot
				DatagramPacket packet = Packet.getReceivePacket();
				_socket.receive(packet);
				
				//check if header data fits - Version, RobotID, Safetybit, RequestReply and IsAlive
				if(VERSION == Packet.getVersion(packet) && _player.getMinion().getValue() == Packet.getRobotID(packet) && SAFETYBIT == Packet.getSafetyBit(packet) && ENABLED == Packet.getReplyRequest(packet) && ENABLED == Packet.getIsAlive(packet)){
					_robotAddress = packet.getAddress();
					_isConnected = true;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Sends the commands from the controller to the robots
		 */
		private void sendCMD(int requestReply){
			//create command packet
			DatagramPacket packet = Packet.createPacket(SAFETYBIT, requestReply, _player.getMinion().getValue(), VERSION, IDLE, IDLE, DISABLED);
			packet.setAddress(_robotAddress);
			packet.setPort(PORT);
			try {
				_socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Sends the command to stop and wait to the robots
		 */
		private void sendIdleCMD(int requestReply){
			//create idle command packet
			DatagramPacket packet = Packet.createPacket(SAFETYBIT, requestReply, _player.getMinion().getValue(), VERSION, IDLE, IDLE, DISABLED);
			packet.setAddress(_robotAddress);
			packet.setPort(PORT);
			try {
				_socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Sends a command to the robot to quit
		 */
		private void exit(){
			//create exit packet
			DatagramPacket packet = Packet.createPacket(SAFETYBIT, ENABLED, _player.getMinion().getValue(), VERSION, IDLE, IDLE, ENABLED);
			packet.setAddress(_robotAddress);
			packet.setPort(PORT);
			
			//send exit packet to robot until reply is received
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(!_received){
						try {
							_socket.send(packet);
							Thread.sleep(5);
						} catch (IOException | InterruptedException e) {
							e.printStackTrace();
						}
					}
					_received = false;
				}
			}).run();
			
			waitForReply(DISABLED);
		}
		
		/**
		 * Waits for a reply packet from the specific robot
		 */
		private void waitForReply(int isAlive){
			try {
				//receive packet from robot
				DatagramPacket packet = Packet.getReceivePacket();
				_socket.receive(packet);
				
				//check if header data fits - Version, RobotID, Safetybit, RequestReply and is alive
				if(VERSION == Packet.getVersion(packet) && _player.getMinion().getValue() == Packet.getRobotID(packet) && SAFETYBIT == Packet.getSafetyBit(packet) && DISABLED == Packet.getReplyRequest(packet) && isAlive == Packet.getIsAlive(packet)){
					//set received
					_received = true;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
