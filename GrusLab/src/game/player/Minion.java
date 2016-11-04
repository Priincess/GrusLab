package game.player;

public enum Minion {
	Yellow(0), Purple(1);
	
private int _value;
	
	Minion(int value) {
		 _value = value;
	 }
	 
	 public int getValue() {
	     return _value;
	 }
	 
	 public static Minion getMinionByValue(int value){
		 for(Minion m : Minion.values()){
			 if(m.getValue() == value){
				 return m;
			 }
		 }
		 return null;
	 }
}
