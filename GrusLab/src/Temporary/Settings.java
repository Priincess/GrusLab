package Temporary;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class Settings implements SettingConstants{
    private HashMap<String, String> _hashmap;
    private static final String PROPERTIESPATH = "config.properties";

    private static Settings _instance = new Settings();

    private Properties _properties;
    private InputStream _input;
    private OutputStream _output;


    private Settings(){
        _properties = new Properties();
        _hashmap = new HashMap<String, String>();
        fillHashmap();
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

    public int getIntProperty(String key){
        String defaultValue = _hashmap.get(key);
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

    private void fillHashmap(){
        _hashmap.put(GAME_TIME, GAME_TIME_DEFAULT);

        _hashmap.put(GAMEBOARD_X, GAMEBOARD_X_DEFAULT);
        _hashmap.put(GAMEBOARD_Y,GAMEBOARD_Y_DEFAULT);
        _hashmap.put(GAMEBOARD_WIDTH, GAMEBOARD_WIDTH_DEFAULT);
        _hashmap.put(GAMEBOARD_HEIGHT, GAMEBOARD_HEIGHT_DEFAULT);
        _hashmap.put(GAMEOBJECT_DISTANCE, GAMEOBJECT_DISTANCE_DEFAULT);

        _hashmap.put(MINION_WIDTH, MINION_WIDTH_DEFAULT);
        _hashmap.put(MINION_HEIGHT, MINION_HEIGHT_DEFAULT);
        _hashmap.put(MINION_OFFSET, MINION_OFFSET_DEFAULT);

        _hashmap.put(ITEM_MIN_DROPRATE, ITEM_MIN_DROPRATE_DEFAULT);
        _hashmap.put(ITEM_MAX_DROPRATE, ITEM_MAX_DROPRATE_DEFAULT);

        _hashmap.put(BANANA_WIDTH, BANANA_WIDTH_DEFAULT);
        _hashmap.put(BANANA_HEIGHT, BANANA_HEIGHT_DEFAULT);

        _hashmap.put(BEEDO_WIDTH, BEEDO_WIDTH_DEFAULT);
        _hashmap.put(BEEDO_HEIGHT, BEEDO_HEIGHT_DEFAULT);

        _hashmap.put(GOGGLE_WIDTH, GOGGLE_WIDTH_DEFAULT);
        _hashmap.put(GOGGLE_HEIGHT, GOGGLE_HEIGHT_DEFAULT);

        _hashmap.put(GAMEBOARD_X, GAMEBOARD_X_DEFAULT);
        _hashmap.put(GAMEBOARD_Y,GAMEBOARD_Y_DEFAULT);

        _hashmap.put(CAMERA_ID, CAMERA_ID_DEFAULT);

        _hashmap.put(FONT_SIZE, FONT_SIZE_DEFAULT);
    }


}
