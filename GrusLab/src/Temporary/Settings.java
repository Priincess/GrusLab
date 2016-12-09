package Temporary;

import java.io.*;
import java.util.Properties;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class Settings {
    public static final String PROPERTIESPATH = "config.properties";

    public static final String GAME_TIME = "GAME_TIME";
    public static final String GAME_TIME_DEFAULT = "100";

    public static final String GAMEBOARD_X = "GAMEBOARD_X";
    public static final String GAMEBOARD_X_DEFAULT = "100";
    public static final String GAMEBOARD_Y = "GAMEBOARD_Y";
    public static final String GAMEBOARD_Y_DEFAULT = "100";
    public static final String GAMEBOARD_WIDTH = "GAMEBOARD_WIDTH";
    public static final String GAMEBOARD_WIDTH_DEFAULT = "1000";
    public static final String GAMEBOARD_HEIGHT = "GAMEBOARD_HEIGHT";
    public static final String GAMEBOARD_HEIGHT_DEFAULT = "500";

    public static final String GAMEOBJECT_DISTANCE = "GAMEOBJECT_DISTANCE";
    public static final String GAMEOBJECT_DISTANCE_DEFAULT = "100";

    public static final String MINION_WIDTH = "BANANA_WIDTH";
    public static final String MINION_WIDTH_DEFAULT = "175";
    public static final String MINION_HEIGHT = "BANANA_HEIGHT";
    public static final String MINION_HEIGHT_DEFAULT = "175";

    public static final String BANANA_WIDTH = "BANANA_WIDTH";
    public static final String BANANA_WIDTH_DEFAULT = "175";
    public static final String BANANA_HEIGHT = "BANANA_HEIGHT";
    public static final String BANANA_HEIGHT_DEFAULT = "129";

    public static final String BEEDO_WIDTH = "BEEDO_WIDTH";
    public static final String BEEDO_WIDTH_DEFAULT = "175";
    public static final String BEEDO_HEIGHT = "BEEDO_HEIGHT";
    public static final String BEEDO_HEIGHT_DEFAULT = "184";

    public static final String GOGGLE_WIDTH = "GOGGLE_WIDTH";
    public static final String GOGGLE_WIDTH_DEFAULT = "175";
    public static final String GOGGLE_HEIGHT = "GOGGLE_HEIGHT";
    public static final String GOGGLE_HEIGHT_DEFAULT = "81";

    public static final String CAMERA_ID = "CAMERA_ID";
    public static final String CAMERA_ID_DEFAULT = "0";

    public static final String FONT_SIZE = "FONT_SIZE";
    public static final String FONT_SIZE_DEFAULT = "24";

    private static Settings _instance = new Settings();

    private Properties _properties;
    private InputStream _input;
    private OutputStream _output;


    private Settings(){
        _properties = new Properties();
        readProperties();
    }

    public static Settings getInstance(){
        return _instance;
    }


    public void writeProperties(){
        try {
            _output = new FileOutputStream(PROPERTIESPATH);
            // save properties to project root folder
            _properties.store(_output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (_output != null) {
                try {
                    _output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getIntProperty(String key, String defaultValue){
        String property = _properties.getProperty(key, defaultValue);
        return Integer.parseInt(property);
    }

    public void setIntProperty(String key, int value){
        _properties.setProperty(key, Integer.toString(value));
    }

    public void resetProperties(){
        _properties.clear();
    }

    private void readProperties(){
        try {
            File file = new File(PROPERTIESPATH);
            if(file.exists()) {
                _input = new FileInputStream(file);
                _properties.load(_input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (_input != null) {
                try {
                    _input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
