package Temporary.controller;

import Temporary.Settings;
import Temporary.gui.GuiManager;

import java.util.List;
import com.github.sarxos.webcam.*;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class GameSettingsController {

    private Settings _settings;
    private GuiManager _guiManager;

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
        _guiManager = new GuiManager();

        //_cameraList = FXCollections.observableArrayList();
        reloadCameras();
        loadValuesFromPreferences();
    }

    public int getGameTime() {
        return _gameTime;
    }

    public void setGameTime(int _gameTime) {
        this._gameTime = _gameTime;
    }

    public int getGameObjectDistance() {
        return _gameObjectDistance;
    }

    public void setGameObjectDistance(int _gameObjectDistance) {
        this._gameObjectDistance = _gameObjectDistance;
    }

    public int getGameboardX() {
        return _gameboardX;
    }

    public void setGameboardX(int _gameboardX) {
        this._gameboardX = _gameboardX;
    }

    public int getGameboardY() {
        return _gameboardY;
    }

    public void setGameboardY(int _gameboardY) {
        this._gameboardY = _gameboardY;
    }

    public int getGameboardWidth() {
        return _gameboardWidth;
    }

    public void setGameboardWidth(int _gameboardWidth) {
        this._gameboardWidth = _gameboardWidth;
    }

    public int getGameboardHeight() {
        return _gameboardHeight;
    }

    public void setGameboardHeight(int _gameboardHeight) {
        this._gameboardHeight = _gameboardHeight;
    }

    public int getMinionWidth() {
        return _minionWidth;
    }

    public void setMinionWidth(int _minionWidth) {
        this._minionWidth = _minionWidth;
    }

    public int getMinionHeight() {
        return _minionHeight;
    }

    public void setMinionHeight(int _minionHeight) {
        this._minionHeight = _minionHeight;
    }

    public int getBananaWidth() {
        return _bananaWidth;
    }

    public void setBananaWidth(int _bananaWidth) {
        this._bananaWidth = _bananaWidth;
    }

    public int getBananaHeight() {
        return _bananaHeight;
    }

    public void setBananaHeight(int _bananaHeight) {
        this._bananaHeight = _bananaHeight;
    }

    public int getGoggleWidth() {
        return _goggleWidth;
    }

    public void setGoggleWidth(int _goggleWidth) {
        this._goggleWidth = _goggleWidth;
    }

    public int getGoggleHeight() {
        return _goggleHeight;
    }

    public void setGoggleHeight(int _goggleHeight) {
        this._goggleHeight = _goggleHeight;
    }

    public int getBeedoWidth() {
        return _beedoWidth;
    }

    public void setBeedoWidth(int _beedoWidth) {
        this._beedoWidth = _beedoWidth;
    }

    public int getBeedoHeight() {
        return _beedoHeight;
    }

    public void setBeedoHeight(int _beedoHeight) {
        this._beedoHeight = _beedoHeight;
    }

    public int getCameraID() {
        return _cameraID;
    }

    public void setCameraID(int _cameraID) {
        this._cameraID = _cameraID;
    }

//    public ObservableList<String> getCameraList(){
//        return _cameraList;
//    }


    public void loadValuesFromPreferences(){
        _gameboardX = _settings.getIntProperty(Settings.GAMEBOARD_X, Settings.GAMEBOARD_X_DEFAULT);
        _gameboardY = _settings.getIntProperty(Settings.GAMEBOARD_Y, Settings.GAMEBOARD_Y_DEFAULT);
        _gameboardWidth = _settings.getIntProperty(Settings.GAMEBOARD_WIDTH, Settings.GAMEBOARD_WIDTH_DEFAULT);
        _gameboardHeight = _settings.getIntProperty(Settings.GAMEBOARD_HEIGHT, Settings.GAMEBOARD_HEIGHT_DEFAULT);

        _minionWidth = _settings.getIntProperty(Settings.MINION_WIDTH, Settings.MINION_WIDTH_DEFAULT);
        _minionHeight = _settings.getIntProperty(Settings.MINION_HEIGHT, Settings.MINION_HEIGHT_DEFAULT);

        _bananaWidth = _settings.getIntProperty(Settings.BANANA_WIDTH, Settings.BANANA_WIDTH_DEFAULT);
        _bananaHeight = _settings.getIntProperty(Settings.BANANA_HEIGHT, Settings.BANANA_HEIGHT_DEFAULT);

        _goggleWidth = _settings.getIntProperty(Settings.GOGGLE_WIDTH, Settings.GOGGLE_WIDTH_DEFAULT);
        _goggleHeight = _settings.getIntProperty(Settings.GOGGLE_HEIGHT, Settings.GOGGLE_HEIGHT_DEFAULT);

        _beedoWidth = _settings.getIntProperty(Settings.BEEDO_WIDTH, Settings.BEEDO_WIDTH_DEFAULT);
        _beedoHeight = _settings.getIntProperty(Settings.BEEDO_HEIGHT, Settings.BEEDO_HEIGHT_DEFAULT);

        _gameTime = _settings.getIntProperty(Settings.GAME_TIME, Settings.GAME_TIME_DEFAULT);
        _gameObjectDistance = _settings.getIntProperty(Settings.GAMEOBJECT_DISTANCE, Settings.GAMEOBJECT_DISTANCE_DEFAULT);

        _cameraID = _settings.getIntProperty(Settings.CAMERA_ID, Settings.CAMERA_ID_DEFAULT);
    }

    public void gotoMenuView(){
        _guiManager.gotoView(GuiManager.GAMEMENU_VIEW);
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

    public void saveGameObjectSettings(){
        _settings.setIntProperty(Settings.BANANA_WIDTH, _bananaWidth);
        _settings.setIntProperty(Settings.BANANA_HEIGHT, _bananaHeight);

        _settings.setIntProperty(Settings.GOGGLE_WIDTH, _goggleWidth);
        _settings.setIntProperty(Settings.GOGGLE_HEIGHT, _goggleHeight);

        _settings.setIntProperty(Settings.BEEDO_WIDTH, _beedoWidth);
        _settings.setIntProperty(Settings.BEEDO_HEIGHT, _beedoHeight);

        _settings.writeProperties();
    }

    public void saveCameraSettings(){
        if (_cameraID > 0){
            _settings.setIntProperty(Settings.CAMERA_ID, _cameraID);
        }
    }


    public void resetAllSettings(){
        _settings.resetProperties();
    }


    public void reloadCameras(){
//        _cameraList.clear();
//        List<Webcam> webcams = Webcam.getWebcams();
//        for (int i = 0; i < webcams.size(); i++) {
//            _cameraList.add(webcams.get(i).toString());
//        }
    }
}
