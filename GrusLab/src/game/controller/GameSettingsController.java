package game.controller;

import com.github.sarxos.webcam.Webcam;
import game.Settings;
import game.gui.ItemDropRate;
import game.gui.SettingSteps;
import game.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class GameSettingsController {

    private Settings _settings;

    private int _gameTime;
    private ItemDropRate _itemDropRate;

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
    private int _goggleSpeedTime;

    private int _beedoWidth;
    private int _beedoHeight;
    private int _beedoStopTime;

    private List<String> _cameraList;
    private int _cameraID;


    public GameSettingsController(Player yellowPlayer, Player purplePlayer){
        _settings = Settings.getInstance();

        _cameraList = new ArrayList<String>();
        reloadCameras();
        loadValuesFromPreferences();
    }

    public int getGameTime() {
        return _gameTime;
    }

    public ItemDropRate getItemDropRate() { return _itemDropRate; }

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

    public int getGoggleSpeedTime() { return _goggleSpeedTime; }

    public int getBeedoWidth() {
        return _beedoWidth;
    }

    public int getBeedoHeight() {
        return _beedoHeight;
    }

    public int getBeedoStopTime() { return _beedoStopTime; }

    public int getCameraID() {
        return _cameraID;
    }

    public List<String> getCameraList(){
        return _cameraList;
    }


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
        _goggleSpeedTime = _settings.getIntProperty(Settings.GOGGLE_SPEED_TIME);

        _beedoWidth = _settings.getIntProperty(Settings.BEEDO_WIDTH);
        _beedoHeight = _settings.getIntProperty(Settings.BEEDO_HEIGHT);
        _beedoStopTime = _settings.getIntProperty(Settings.BEEDO_BLOCK_TIME);

        _gameTime = _settings.getIntProperty(Settings.GAME_TIME);
        _itemDropRate = ItemDropRate.getItemDropRateFromInt(_settings.getIntProperty(Settings.ITEM_DROPRATE));

        _cameraID = _settings.getIntProperty(Settings.CAMERA_ID);
    }

    public void updateGameSettings(int gameTime){
        _gameTime = gameTime;
        _settings.setIntProperty(Settings.GAME_TIME, _gameTime);
    }

    public void updateMinionSettings(int rad){
        _minionWidth = rad;
        _minionHeight = rad;
        _settings.setIntProperty(Settings.MINION_WIDTH, _minionWidth);
        _settings.setIntProperty(Settings.MINION_HEIGHT, _minionHeight);
    }

    public void updateGameboardSettings(int x, int y, int width, int height){
        _gameboardX = x;
        _gameboardY = y;
        _gameboardWidth = width;
        _gameboardHeight = height;
        _settings.setIntProperty(Settings.GAMEBOARD_X, _gameboardX);
        _settings.setIntProperty(Settings.GAMEBOARD_Y, _gameboardY);
        _settings.setIntProperty(Settings.GAMEBOARD_WIDTH, _gameboardWidth);
        _settings.setIntProperty(Settings.GAMEBOARD_HEIGHT, _gameboardHeight);
    }

    public void updateBananaSettings(int width, int height){
        _bananaWidth = width;
        _bananaHeight = height;
        _settings.setIntProperty(Settings.BANANA_WIDTH, _bananaWidth);
        _settings.setIntProperty(Settings.BANANA_HEIGHT, _bananaHeight);
    }

    public void updateGoggleSettings(int width, int height){
        _goggleWidth = width;
        _goggleHeight = height;
        _settings.setIntProperty(Settings.GOGGLE_WIDTH, _goggleWidth);
        _settings.setIntProperty(Settings.GOGGLE_HEIGHT, _goggleHeight);
    }

    public void updateBeedoSettings(int width, int height){
        _beedoWidth = width;
        _beedoHeight = height;
        _settings.setIntProperty(Settings.BEEDO_WIDTH, _beedoWidth);
        _settings.setIntProperty(Settings.BEEDO_HEIGHT, _beedoHeight);
    }

    public void updateDropRate(ItemDropRate rate){
        _itemDropRate = rate;
        _settings.setIntProperty(Settings.ITEM_DROPRATE, _itemDropRate.ordinal());
        switch(rate){
            case HIGH:
                _settings.setIntProperty(Settings.ITEM_MIN_DROPRATE,  5);
                _settings.setIntProperty(Settings.ITEM_MAX_DROPRATE,  10);
                break;
            case NORMAL:
                _settings.setIntProperty(Settings.ITEM_MIN_DROPRATE,  10);
                _settings.setIntProperty(Settings.ITEM_MAX_DROPRATE,  20);
                break;
            case LOW:
                _settings.setIntProperty(Settings.ITEM_MIN_DROPRATE,  20);
                _settings.setIntProperty(Settings.ITEM_MAX_DROPRATE,  30);
                break;
        }
    }

    public void updateGoggleSpeedTime(int goggleSpeedTime){
        _goggleSpeedTime = goggleSpeedTime;
        _settings.setIntProperty(Settings.GOGGLE_SPEED_TIME, _goggleSpeedTime);
    }

    public void updateBeedoStopTime(int beedoStopTime){
        _beedoStopTime = beedoStopTime;
        _settings.setIntProperty(Settings.BEEDO_BLOCK_TIME, _beedoStopTime);
    }

    public void saveSettings(){
        updateMinionStartPositions();
        _settings.writeProperties();
    }

    private void updateMinionStartPositions(){
        _settings.setIntProperty(Settings.YELLOW_MINION_STARTX, _gameboardX);
        _settings.setIntProperty(Settings.YELLOW_MINION_STARTY, _gameboardY);
        _settings.setIntProperty(Settings.PURPLE_MINION_STARTX, _gameboardX+_gameboardWidth-_minionWidth);
        _settings.setIntProperty(Settings.PURPLE_MINION_STARTY, _gameboardY+_gameboardHeight-_minionHeight);
    }

    public void updateCamera(int camera){
        _cameraID = camera;
        if (_cameraID > 0){
            _settings.setIntProperty(Settings.CAMERA_ID, _cameraID);
        }
    }


    public void resetAllSettings(){
        _settings.resetProperties();
    }


    public void reloadCameras(){
        _cameraList.clear();
        List<Webcam> webcams = Webcam.getWebcams();
        for (int i = 0; i < webcams.size(); i++) {
            _cameraList.add(webcams.get(i).toString());
        }
    }
}
