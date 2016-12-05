package Temporary.controller;

import Temporary.Settings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.prefs.Preferences;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class GameSettingsController {

    private Preferences _preferences;
    private IntegerProperty _gameTime;

    private IntegerProperty _gameboardX;
    private IntegerProperty _gameboardY;
    private IntegerProperty _gameboardWidth;
    private IntegerProperty _gameboardHeight;

    private IntegerProperty _minionX;
    private IntegerProperty _minionY;
    private IntegerProperty _minionWidth;
    private IntegerProperty _minionHeight;

    private IntegerProperty _bananaX;
    private IntegerProperty _bananaY;
    private IntegerProperty _bananaWidth;
    private IntegerProperty _bananaHeight;


    public GameSettingsController(){
        _preferences = Preferences.userRoot().node(Settings.SETTINGSPATH);

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

    public IntegerProperty getBananaXProperty() {
        return _bananaX;
    }

    public IntegerProperty getBananaYProperty() {
        return _bananaY;
    }

    public IntegerProperty getBananaWidthProperty() {
        return _bananaWidth;
    }

    public IntegerProperty getBananaHeightProperty() {
        return _bananaHeight;
    }

    private void loadValuesFromPreferences(){
        _gameboardX = new SimpleIntegerProperty(_preferences.getInt(Settings.GAMEBOARD_X, Settings.GAMEBOARD_X_DEFAULT));
        _gameboardY = new SimpleIntegerProperty(_preferences.getInt(Settings.GAMEBOARD_Y, Settings.GAMEBOARD_Y_DEFAULT));
        _gameboardWidth = new SimpleIntegerProperty(_preferences.getInt(Settings.GAMEBOARD_WIDTH, Settings.GAMEBOARD_WIDTH_DEFAULT));
        _gameboardHeight = new SimpleIntegerProperty(_preferences.getInt(Settings.GAMEBOARD_HEIGHT, Settings.GAMEBOARD_HEIGHT_DEFAULT));

        _minionWidth = new SimpleIntegerProperty(_preferences.getInt(Settings.MINION_WIDTH, Settings.MINION_WIDTH_DEFAULT));
        _minionHeight = new SimpleIntegerProperty(_preferences.getInt(Settings.MINION_HEIGHT, Settings.MINION_HEIGHT_DEFAULT));

        _bananaWidth = new SimpleIntegerProperty(_preferences.getInt(Settings.BANANA_WIDTH, Settings.BANANA_WIDTH_DEFAULT));
        _bananaHeight = new SimpleIntegerProperty(_preferences.getInt(Settings.BANANA_HEIGHT, Settings.BANANA_HEIGHT_DEFAULT));
    }


    public void gotoStartView(){

    }

    public void saveGameSettings(){

    }

    public void maximizeGameboard(){

    }

    public void saveGameboardSettings(){

    }

    public void saveGameObjectSettings(){

    }

    public void saveCameraSettings(){

    }

    public void saveCalibration(){

    }
}
