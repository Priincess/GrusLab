package Temporary.controller;

import Temporary.Settings;
import Temporary.gui.GuiManager;

import java.util.List;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class GameSettingsController {

    private Settings _settings;

    private int _gameTime;
    private int _gameObjectDistance;

    private int _gameboardX;
    private int _gameboardY;
    private int _gameboardWidth;
    private int _gameboardHeight;

    private int _minionWidth;
    private int _minionHeight;

    private int _bananaWidth;
    private int _bananaHeight;

    private int _goggleWidth;
    private int _goggleHeight;

    private int _beedoWidth;
    private int _beedoHeight;

    //private ObservableList<String> _cameraList;
    private int _cameraID;


    public GameSettingsController(){
        _settings = Settings.getInstance();

        //_cameraList = FXCollections.observableArrayList();
        reloadCameras();
        loadValuesFromPreferences();
    }

    public int getGameTime() {
        return _gameTime;
    }

    public int getGameObjectDistance() {
        return _gameObjectDistance;
    }

    public int getGameboardX() {
        return _gameboardX;
    }

    public int getGameboardY() {
        return _gameboardY;
    }

    public int getGameboardWidth() {
        return _gameboardWidth;
    }

    public int getGameboardHeight() {
        return _gameboardHeight;
    }

    public int getMinionWidth() {
        return _minionWidth;
    }

    public int getMinionHeight() {
        return _minionHeight;
    }

    public int getBananaWidth() {
        return _bananaWidth;
    }

    public int getBananaHeight() {
        return _bananaHeight;
    }

    public int getGoggleWidth() {
        return _goggleWidth;
    }

    public int getGoggleHeight() {
        return _goggleHeight;
    }

    public int getBeedoWidth() {
        return _beedoWidth;
    }

    public int getBeedoHeight() {
        return _beedoHeight;
    }

    public int getCameraID() {
        return _cameraID;
    }

//    public ObservableList<String> getCameraList(){
//        return _cameraList;
//    }


    public void loadValuesFromPreferences(){
        _gameboardX = _settings.getIntProperty(Settings.GAMEBOARD_X);
        _gameboardY = _settings.getIntProperty(Settings.GAMEBOARD_Y);
        _gameboardWidth = _settings.getIntProperty(Settings.GAMEBOARD_WIDTH);
        _gameboardHeight = _settings.getIntProperty(Settings.GAMEBOARD_HEIGHT);

        _minionWidth = _settings.getIntProperty(Settings.MINION_WIDTH);
        _minionHeight = _settings.getIntProperty(Settings.MINION_HEIGHT);

        _bananaWidth = _settings.getIntProperty(Settings.BANANA_WIDTH);
        _bananaHeight = _settings.getIntProperty(Settings.BANANA_HEIGHT);

        _goggleWidth = _settings.getIntProperty(Settings.GOGGLE_WIDTH);
        _goggleHeight = _settings.getIntProperty(Settings.GOGGLE_HEIGHT);

        _beedoWidth = _settings.getIntProperty(Settings.BEEDO_WIDTH);
        _beedoHeight = _settings.getIntProperty(Settings.BEEDO_HEIGHT);

        _gameTime = _settings.getIntProperty(Settings.GAME_TIME);
        _gameObjectDistance = _settings.getIntProperty(Settings.GAMEOBJECT_DISTANCE);

        _cameraID = _settings.getIntProperty(Settings.CAMERA_ID);
    }

    public void saveGameSettings(){
        _settings.setIntProperty(Settings.GAME_TIME, _gameTime);
    }

    public void saveGameboardSettings(){
        _settings.setIntProperty(Settings.GAMEBOARD_X, _gameboardX);
        _settings.setIntProperty(Settings.GAMEBOARD_Y, _gameboardY);
        _settings.setIntProperty(Settings.GAMEBOARD_WIDTH, _gameboardWidth);
        _settings.setIntProperty(Settings.GAMEBOARD_HEIGHT, _gameboardHeight);
    }

    public void saveBananaSettings(int bananaWidth, int bananaHeight){
        _bananaWidth = bananaWidth;
        _bananaHeight = bananaHeight;
        _settings.setIntProperty(Settings.BANANA_WIDTH, _bananaWidth);
        _settings.setIntProperty(Settings.BANANA_HEIGHT, _bananaHeight);
        _settings.writeProperties();
    }
//    public void saveGameObjectSettings(){
//        _settings.setIntProperty(Settings.BANANA_WIDTH, _bananaWidth);
//        _settings.setIntProperty(Settings.BANANA_HEIGHT, _bananaHeight);
//
//        _settings.setIntProperty(Settings.GOGGLE_WIDTH, _goggleWidth);
//        _settings.setIntProperty(Settings.GOGGLE_HEIGHT, _goggleHeight);
//
//        _settings.setIntProperty(Settings.BEEDO_WIDTH, _beedoWidth);
//        _settings.setIntProperty(Settings.BEEDO_HEIGHT, _beedoHeight);
//
//        _settings.writeProperties();
//    }

    public void saveCameraSettings(){
        if (_cameraID > 0){
            _settings.setIntProperty(Settings.CAMERA_ID, _cameraID);
        }
    }


    public void resetAllSettings(){
        _settings.resetProperties();
        // TODO: reload everything
    }


    public void reloadCameras(){
//        _cameraList.clear();
//        List<Webcam> webcams = Webcam.getWebcams();
//        for (int i = 0; i < webcams.size(); i++) {
//            _cameraList.add(webcams.get(i).toString());
//        }
    }
}
