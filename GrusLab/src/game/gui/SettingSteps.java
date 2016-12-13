package game.gui;

/**
 * Created by Mark Mauerhofer on 10.12.2016.
 */
public enum SettingSteps {
    // This enum defines the order of the pages in the SettingsView
    Step_MinionSize,
    Step_GameboardPoint,
    Step_GameboardSize,
    Step_BananaSize,
    Step_GoggleSize,
    Step_BeedoSize,
    Step_GameTime,
    Step_ItemDrop,
    Step_GoggleSpeed,
    Step_BeedoStop,
    Step_Camera,
    Step_Save,
    Step_Error;

    public static SettingSteps getStepFromInt(int step) {
        for(SettingSteps v : values()){
            if(v.ordinal() == step){
                return v;
            }
        }
        return Step_Error;
    }
}
