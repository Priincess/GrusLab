package game.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import game.Components;
import game.GameState;
import game.GameStateValue;
import game.player.Player;

public class Server {

	//server information
	private static int IDLE = 0;
	private static int DISABLED = 0;
	private static int ENABLED = 1;
	private static int PORT = 2432;
	private static int VERSION = 1;
	private static int SAFETYBIT = 2;
	private static long WAITINGTIME_REPLYREQUEST = 1000;
	private static long WAITINGTIME_DISCONNECED = 3000;
//	private static long WAITINGTIME_REPLYREQUEST = 500;
//	private static long WAITINGTIME_DISCONNECED = 1000;
	
	private DatagramSocket _socket;
	private boolean _running = false;
	
	private List<Communicator> _communicators = new LinkedList<>();
	
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
	public void startServer(Player[] players){
		
		List<Thread> threads = new LinkedList<Thread>();
		//init communicators
		for(Player p : players){
			Communicator c = new Communicator(p);
			_communicators.add(c);
			
			Thread t = new Thread(c);
			threads.add(t);
			t.start();
		}
		
		_running = true;
		
		while(_running){
			if(GameState.getGameState() == GameStateValue.PLAY){
				checkIfRobotsConnected();
				for(Communicator c : _communicators){
					c.setIdel(false);
				}
			}else{
				for(Communicator c : _communicators){
					c.setIdel(true);
				}
			}
		}
		
		//tell both communicators to finish communication
		for(Communicator c : _communicators){
			c.setFinished();
		}

		while(!allThreadsTerminated(threads)){}
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
		boolean ret=false;;
		//check if all players pressed start
		for(int i =0; i<_communicators.size(); i++){
			if(i == 0){
				ret = _communicators.get(i)._isConnected;
			}else{
				ret &=  _communicators.get(i)._isConnected;
			}
		}
		return ret;
	}
	
	//**********PRIVATE METHODS**********
	
	/**
	 * @param threads list with all running threads
	 * @return if all threads in list are terminated
	 */
	private boolean allThreadsTerminated(List<Thread> threads){
		boolean ret = true;
		for(Thread t : threads){
			if(t.getState() != Thread.State.TERMINATED){
				ret = false;
			}
		}
		return ret;
	}
	
	/**
	 * This method checks if both robots are still connected and pause game if not
	 */
	private void checkIfRobotsConnected(){
		
		//check if both robots are connected
		for(Communicator c : _communicators){
			if(!c.isConnected()){
				//pause game
				GameState.getInstance().setGameState(GameStateValue.PAUSE);
				
				//set idle
				for(Communicator c1 : _communicators){
					c1.setIdel(true);
				}
				Components.setMinionConnected(c._player.getMinion().getValue(), false);
				
				//wait until reconnect
				while(!c.isConnected()){}
				
				//release idle
				for(Communicator c1 : _communicators){
					c1.setIdel(false);
				}
				//continue game
				Components.setMinionConnected(c._player.getMinion().getValue(), true);
				GameState.getInstance().setGameState(GameStateValue.PLAY);
			}
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
		private int _reply = DISABLED;
		
		private long _timestampReply = 0;
		private long _timestampDisconnected = 0;
		
		
		public Communicator(Player player){
			_player = player;
		}
		
		@Override
		public void run() {
			System.out.println("Start");
			//run until game exit
			while(!_finished){
				connect();
				//get current time
				_timestampReply = System.currentTimeMillis();
				
				while(_isConnected){
					
					//check if game is idle or not
					if(_isIdle){
						sendIdleCMD(_reply);
					}else{
						sendCMD(_reply);
					}
					
					//check if it is time to get a reply from robot
					if(_reply == DISABLED && _timestampReply + WAITINGTIME_REPLYREQUEST <= System.currentTimeMillis()){
						_reply = ENABLED;
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								_received = false;
								while(!_received){
									waitForReply(ENABLED);
								}
								_received = false;
								_timestampReply = System.currentTimeMillis();
								_reply = DISABLED;
							}
						}).start();
						_timestampDisconnected = System.currentTimeMillis();
					}
					
					//if reply is requested check if waiting time is over -> robot is disconnected
					if(_reply == ENABLED && _timestampDisconnected + WAITINGTIME_DISCONNECED <= System.currentTimeMillis()){
						_isConnected = false;
						_received = true;
						System.out.println("Disconnected");
						_reply = DISABLED;
					}
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
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
					System.out.println("Connected Minion "+_player.getMinion().getValue()+"!");
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
			DatagramPacket packet = Packet.createPacket(SAFETYBIT, requestReply, _player.getMinion().getValue(), VERSION, _player.getPlayerState().getStateValue(), _player.getControllerState().getControllerValue(), DISABLED);
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
							Thread.sleep(10);
						} catch (IOException | InterruptedException e) {
							e.printStackTrace();
						}
					}
					_received = false;
				}
			}).start();
			
			_received = false;
			while(!_received){
				waitForReply(DISABLED);
			}
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
