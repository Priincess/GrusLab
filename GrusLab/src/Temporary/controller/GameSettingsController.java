package Temporary.controller;

import Temporary.Settings;
import game.camera.ObjTracker;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class GameSettingsController {

    private Preferences _preferences;
    private ObjTracker _objTracker;

    private IntegerProperty _gameTime;
    private IntegerProperty _gameObjectDistance;

    private IntegerProperty _gameboardX;
    private IntegerProperty _gameboardY;
    private IntegerProperty _gameboardWidth;
    private IntegerProperty _gameboardHeight;

    private IntegerProperty _minionWidth;
    private IntegerProperty _minionHeight;

    private IntegerProperty _bananaWidth;
    private IntegerProperty _bananaHeight;

    private IntegerProperty _goggleWidth;
    private IntegerProperty _goggleHeight;

    private IntegerProperty _beedoWidth;
    private IntegerProperty _beedoHeight;

    private ObservableList<String> _cameraList;
    private IntegerProperty _cameraID;


    public GameSettingsController(){
        _preferences = Preferences.userRoot().node(Settings.SETTINGSPATH);
        _objTracker = new ObjTracker();
        _cameraList = FXCollections.observableArrayList();
        reloadCameras();
        loadValuesFromPreferences();
    }

    public IntegerProperty getGameboardXProperty() {
        return _gameboardX;
    }

    public IntegerProperty getGameboardYProperty() {
        return _gameboardY;
    }

    public IntegerProperty getGameboardWidthProperty() {
        return _gameboardWidth;
    }

    public IntegerProperty getGameboardHeightProperty() {
        return _gameboardHeight;
    }

    public IntegerProperty getMinionWidthProperty() {
        return _minionWidth;
    }

    public IntegerProperty getMinionHeightProperty() {
        return _minionHeight;
    }

    public IntegerProperty getBananaWidthProperty() {
        return _bananaWidth;
    }

    public IntegerProperty getBananaHeightProperty() {
        return _bananaHeight;
    }

    public IntegerProperty getGoggleWidthProperty() {
        return _goggleWidth;
    }

    public IntegerProperty getGoggleHeightProperty() {
        return _goggleHeight;
    }

    public IntegerProperty getBeedoWidthProperty() {
        return _beedoWidth;
    }

    public IntegerProperty getBeedoHeightProperty() {
        return _beedoHeight;
    }

    public IntegerProperty getGameTime() { return _gameTime; }

    public IntegerProperty getGameObjectDistance() { return _gameObjectDistance; }

    public ObservableList<String> getCameraList(){
        return _cameraList;
    }

    public IntegerProperty getCameraID(){ return _cameraID; }


    public void loadValuesFromPreferences(){
        _gameboardX = new SimpleIntegerProperty(_preferences.getInt(Settings.GAMEBOARD_X, Settings.GAMEBOARD_X_DEFAULT));
        _gameboardY = new SimpleIntegerProperty(_preferences.getInt(Settings.GAMEBOARD_Y, Settings.GAMEBOARD_Y_DEFAULT));
        _gameboardWidth = new SimpleIntegerProperty(_preferences.getInt(Settings.GAMEBOARD_WIDTH, Settings.GAMEBOARD_WIDTH_DEFAULT));
        _gameboardHeight = new SimpleIntegerProperty(_preferences.getInt(Settings.GAMEBOARD_HEIGHT, Settings.GAMEBOARD_HEIGHT_DEFAULT));

        _minionWidth = new SimpleIntegerProperty(_preferences.getInt(Settings.MINION_WIDTH, Settings.MINION_WIDTH_DEFAULT));
        _minionHeight = new SimpleIntegerProperty(_preferences.getInt(Settings.MINION_HEIGHT, Settings.MINION_HEIGHT_DEFAULT));

        _bananaWidth = new SimpleIntegerProperty(_preferences.getInt(Settings.BANANA_WIDTH, Settings.BANANA_WIDTH_DEFAULT));
        _bananaHeight = new SimpleIntegerProperty(_preferences.getInt(Settings.BANANA_HEIGHT, Settings.BANANA_HEIGHT_DEFAULT));

        _goggleWidth = new SimpleIntegerProperty(_preferences.getInt(Settings.GOGGLE_WIDTH, Settings.GOGGLE_WIDTH_DEFAULT));
        _goggleHeight = new SimpleIntegerProperty(_preferences.getInt(Settings.GOGGLE_HEIGHT, Settings.GOGGLE_HEIGHT_DEFAULT));

        _beedoWidth = new SimpleIntegerProperty(_preferences.getInt(Settings.BEEDO_WIDTH, Settings.BEEDO_WIDTH_DEFAULT));
        _beedoHeight = new SimpleIntegerProperty(_preferences.getInt(Settings.BEEDO_HEIGHT, Settings.BEEDO_HEIGHT_DEFAULT));

        _gameTime = new SimpleIntegerProperty(_preferences.getInt(Settings.GAME_TIME, Settings.GAME_TIME_DEFAULT));
        _gameObjectDistance = new SimpleIntegerProperty(_preferences.getInt(Settings.GAMEOBJECT_DISTANCE, Settings.GAMEOBJECT_DISTANCE_DEFAULT));

        _cameraID = new SimpleIntegerProperty(_preferences.getInt(Settings.CAMERA_ID, Settings.CAMERA_DEFAULT));
    }

    public void gotoStartView(){

    }

    public void saveGameSettings(){
        _preferences.putInt(Settings.GAME_TIME, _gameTime.intValue());
    }

    public void maximizeGameboard(int width, int height){
        _gameboardX.set(_minionWidth.divide(2).intValue());
        _gameboardY.set(_minionHeight.divide(2).intValue());
        _gameboardWidth.set(width-_minionWidth.intValue());
        _gameboardHeight.set(height-_minionHeight.intValue());
    }

    public void saveGameboardSettings(){
        _preferences.putInt(Settings.GAMEBOARD_X, _gameboardX.intValue());
        _preferences.putInt(Settings.GAMEBOARD_Y, _gameboardY.intValue());
        _preferences.putInt(Settings.GAMEBOARD_WIDTH, _gameboardWidth.intValue());
        _preferences.putInt(Settings.GAMEBOARD_HEIGHT, _gameboardHeight.intValue());
    }

    public void saveGameObjectSettings(){
        _preferences.putInt(Settings.MINION_WIDTH, _minionWidth.intValue());
        _preferences.putInt(Settings.MINION_HEIGHT, _minionHeight.intValue());

        _preferences.putInt(Settings.BANANA_WIDTH, _bananaWidth.intValue());
        _preferences.putInt(Settings.BANANA_HEIGHT, _bananaHeight.intValue());

        _preferences.putInt(Settings.GOGGLE_WIDTH, _goggleWidth.intValue());
        _preferences.putInt(Settings.GOGGLE_HEIGHT, _goggleHeight.intValue());

        _preferences.putInt(Settings.BEEDO_WIDTH, _beedoWidth.intValue());
        _preferences.putInt(Settings.BEEDO_HEIGHT, _beedoHeight.intValue());

        _preferences.putInt(Settings.GAMEOBJECT_DISTANCE, _gameObjectDistance.intValue());
    }

    public void saveCameraSettings(){
        if (_cameraID.intValue() > 0){
            _preferences.putInt(Settings.CAMERA_ID, _cameraID.intValue());
        }
    }


    public void resetAllSettings(){
        try {
            _preferences.removeNode();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }


    public void reloadCameras(){
        _cameraList.clear();
        List<Webcam> webcams = Webcam.getWebcams();
        for (int i = 0; i < webcams.size(); i++) {
            _cameraList.add(webcams.get(i).toString());
        }
    }
}
