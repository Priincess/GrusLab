package game;

public class Components {
	
	private static boolean _controller1=false;
	private static boolean _controller2=false;
	private static boolean _minion1=false;
	private static boolean _minion2=false;
	
	public static boolean areAllComponentsConnecected(){
		return _controller1 && _controller2 && _minion1  && _minion2;
	}
	
	public static void setControllerConnected(int index, boolean connected){
		switch (index) {
			case 0:
				_controller1 = connected;
				break;
			case 1:
				_controller2 = connected;
				break;
			default:
				break;
		}
	}
	
	public static boolean isController1Connected(int index){
		switch (index) {
			case 0:
				return _controller1;
			case 1:
				return _controller2;
			default:
				break;
		}
		return false;
	}
	
	public static void setMinionConnected(int index, boolean connected){
		switch (index) {
			case 0:
				_minion1 = connected;
				break;
			case 1:
				_minion2 = connected;
				break;
			default:
				break;
		}
	}
	
	public static boolean isMinionConnected(int index){
		switch (index) {
			case 0:
				return _minion1;
			case 1:
				return _minion2;
			default:
				break;
		}
		return false;
	}
}
