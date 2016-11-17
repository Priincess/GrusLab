package game.server;

import java.net.DatagramPacket;

/**
 * @author lilpr
 * This class contains all information about a packet send to the robots
 */
public class Packet {
	
	//packet specification
	private static int PACKET_SIZE = 2;
	private static int HEADER = 0;
	private static int DATA = 1;
	
	//header information
	private static int VERSION_SHIFT = 0;
	private static int VERSION_LEGALBITS = 0x7;
	private static int ROBOTID_SHIFT = 3;
	private static int ROBOTID_LEGALBITS = 0x3;
	private static int REQUEST_SHIFT = 5;
	private static int REQUEST_LEGALBITS = 0x1;
	private static int SAFETYBIT_SHIFT = 6;
	private static int SAFETYBIT_LEGALBITS = 0x3;
	
	//data information - sending
	private static int STATE_SHIFT = 0;
	private static int STATE_LEGALBITS = 0x3;
	private static int CMD_SHIFT = 2;
	private static int CMD_LEGALBITS = 0xf;
	private static int EXIT_SHIFT = 6;
	private static int EXIT_LEGALBITS = 0x1;
	
	//data information - receiving
	private static int ISALIVE_SHIFT = 0;
	private static int ISALIVE_LEGALBITS = 0x1;

	private Packet(){}
	
	//**********PUBLIC METHODS**********
	
	/**
	 * @return the packet to receive data from the robot
	 */
	public static DatagramPacket getReceivePacket(){
		byte[] content = new byte[PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(content, content.length);
		return packet;
	}
	
	/**
	 * @param saftybit 
	 * @param requestReply marks if a reply is expected
	 * @param robotID for which robot this packet is intended
	 * @param version of the protocol
	 * @param state of the robot
	 * @param cmd command for the robot
	 * @param exitGame if the game is finished
	 * @return
	 */
	public static DatagramPacket createPacket(int saftybit, int requestReply, int robotID, int version, int state, int cmd, int exitGame){
		
		byte[] content = new byte[PACKET_SIZE];
		
		//fill byte array with data
		content[HEADER] = (byte) ((saftybit & SAFETYBIT_LEGALBITS) << SAFETYBIT_SHIFT | (requestReply & REQUEST_LEGALBITS) << REQUEST_SHIFT | (robotID & ROBOTID_LEGALBITS) << ROBOTID_SHIFT | (version & VERSION_LEGALBITS) << VERSION_SHIFT);
		content[DATA] = (byte) ((exitGame & EXIT_LEGALBITS)<< EXIT_SHIFT | (cmd & CMD_LEGALBITS) << CMD_SHIFT | (state & STATE_LEGALBITS) << STATE_SHIFT);
		
		DatagramPacket packet = new DatagramPacket(content, content.length);
		return packet;
	} 

	/**
	 * @param packet received packet from robot
	 * @return the version from header
	 */
	public static int getVersion(DatagramPacket packet){
		byte header = packet.getData()[HEADER];
		return (header >> VERSION_SHIFT) & VERSION_LEGALBITS;
	}
	
	/**
	 * @param packet received packet from robot
	 * @return the robot id from header
	 */
	public static int getRobotID(DatagramPacket packet){
		byte header = packet.getData()[HEADER];
		return (header >> ROBOTID_SHIFT) & ROBOTID_LEGALBITS;
	}
	
	/**
	 * @param packet received packet from robot
	 * @return the reply request from header
	 */
	public static int getReplyRequest(DatagramPacket packet){
		byte header = packet.getData()[HEADER];
		return (header >> REQUEST_SHIFT) & REQUEST_LEGALBITS;
	}
	
	/**
	 * @param packet received packet from robot
	 * @return the safety bit from header
	 */
	public static int getSafetyBit(DatagramPacket packet){
		byte header = packet.getData()[HEADER];
		return (header >> SAFETYBIT_SHIFT) & SAFETYBIT_LEGALBITS;
	}

	/**
	 * @param packet received packet from robot
	 * @return is alive from data
	 */
	public static int getIsAlive(DatagramPacket packet){
		byte header = packet.getData()[DATA];
		return (header >> ISALIVE_SHIFT) & ISALIVE_LEGALBITS;
	}
}
