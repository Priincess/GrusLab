package game.gui;

/**
 * Created by Mark Mauerhofer on 11.12.2016.
 */
public enum ItemDropRate {
    HIGH,
    NORMAL,
    LOW;

    public static ItemDropRate getItemDropRateFromInt(int rate) {
        for(ItemDropRate v : values()){
            if(v.ordinal() == rate){
                return v;
            }
        }
        return null;
    }
}
