package game;

import game.camera.ObjTracker;
import game.gameboard.GameObject;
import game.gameboard.GameObjectType;
import game.gameboard.Gameboard;
import game.gameboard.GameboardScaler;
import game.player.Player;
import game.player.PlayerState;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.opencv.core.Point;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.prefs.Preferences;

import static game.GameStateValue.*;


/**
 * Created by Mark Mauerhofer on 21.10.2016.
 */
public class Game {
    private Player _yellowPlayer;
    private Player _purplePlayer;

    private IntegerProperty _pointsYellowMinion = new SimpleIntegerProperty(0);
    private IntegerProperty _pointsPurpleMinion = new SimpleIntegerProperty(0);

    private Preferences _gamePreferences;
    private Gameboard _gameboard;
    private GameState _gameState;

    private ObjTracker _tracker;
    private GameboardScaler _scaler;

    private IntegerProperty _gameTime = new SimpleIntegerProperty(0);    // Overwritten by Preferences
    private Timer _gameTimer;
    private TimerTask _gameTimerTask;
    private TimerTask _gameRunningTimerTask;

    // TODO: add to gui
    private int _goggleCounterMax = 1;
    private int _goggleCounter = 1;          // on game start there is already a goggle;
    private int _goggleDropRateTop = 20;
    private int _goggleDropRateBottom = 5;   // after that time item drops
    private int _goggleDropOnGameTime = 0;   // TODO: work with several goggles

    // TODO: add to gui
    private int _beedoCounterMax = 1;
    private int _beedoCounter = 1;           // on game start there is already a beedo;
    private int _beedoDropRateTop = 15;
    private int _beedoDropRateBottom = 5;    // after that time item drops
    private int _beedoDropOnGameTime = 0;    // TODO: work with several beedos

    public Game(ObjTracker tracker, Player yellowPlayer, Player purplePlayer){
        _tracker = tracker;
        _yellowPlayer = yellowPlayer;
        _purplePlayer = purplePlayer;
        _scaler = new GameboardScaler();
        _gameState = GameState.getInstance();
        _gamePreferences = Preferences.userNodeForPackage(this.getClass());
        loadGameSettings();

        _gameboard = new Gameboard();

        addGameStateListener();
        addGameTimeListener();
    }
    
    //

    public void saveGameSettings(){
        _gamePreferences.putInt("GAME_TIME", _gameTime.intValue());
    }

    public void loadGameSettings(){
        _gameTime.set(_gamePreferences.getInt("GAME_TIME", 300));
    }

    public IntegerProperty getGameTime(){
        return _gameTime;
    }

    public IntegerProperty getPointsYellowMinion(){ return _pointsYellowMinion; }

    public IntegerProperty getPointsPurpleMinion(){ return _pointsPurpleMinion; }

    private void startGame(){
        startGameTimer();
        _gameboard.gameboardStartSetup();
    }

    private void runGame(){
        if (_tracker != null) {
            updateYellowMinionPosition();
            updateEvilMinionPosition();
        }
        checkForCollisions();
    }

    private void startGameTimer(){
        if (_gameTimer == null) {
            _gameTimer = new Timer();
            initGameCountdown();
            initGameRunningTask();
            _gameTimer.scheduleAtFixedRate(_gameTimerTask, 0, 1000);
            _gameTimer.scheduleAtFixedRate(_gameRunningTimerTask, 0, 5);
        }
    }

    private void stopGameTimer(){
        if (_gameTimer != null) {
            _gameTimer.cancel();
            _gameTimer = null;
            _gameState.setGameState(PAUSE);
        }
    }

    private void gameOver(){
        stopGameTimer();
        cleanGameboard();
        resetGameTime();
        _gameState.setGameState(GameStateValue.FINISHED);
    }

    private void initGameCountdown() {
        _gameTimerTask = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        _gameTime.set(_gameTime.intValue() - 1);
                    }
                });
            }
        };
    }

    public boolean initCamPoints1(){
        if (_tracker != null) {
            return _scaler.setCamCalibrationPoints1(_tracker.getYellowPos(), _tracker.getEvilPos());
        }
        return false;
    }

    public boolean initCamPoints2(){
        return _scaler.setCamCalibrationPoints2(_tracker.getYellowPos(), _tracker.getEvilPos());
    }

    private void initScaler(){
        _scaler.initScaler(_gameboard.getStartPositions());
    }

    private void updateYellowMinionPosition(){
        // TODO: is minion lost for a while or only once?
        Point point = _tracker.getYellowPos();
        if (point == null){
            _gameState.setGameState(PAUSE);
        } else {
            _gameboard.setMinionPosition(GameObjectType.YELLOWMINION, _scaler.transformCameraPointToGameboardPoint(_tracker.getYellowPos()));
        }
    }

    private void updateEvilMinionPosition(){
        // TODO: is minion lost for a while or only once?
        Point point = _tracker.getEvilPos();
        if (point == null){
            _gameState.setGameState(PAUSE);
        } else {
            _gameboard.setMinionPosition(GameObjectType.PURPLEMINION, _scaler.transformCameraPointToGameboardPoint(_tracker.getEvilPos()));
        }
    }


    private void dropItems(){
        if (_goggleCounter < _goggleCounterMax && _goggleDropOnGameTime == _gameTime.intValue()){
            _gameboard.generateGoggles();
            _goggleCounter++;
        }
        if (_beedoCounter < _beedoCounterMax && _beedoDropOnGameTime == _gameTime.intValue()){
            _gameboard.generateBeedo();
            _beedoCounter++;
        }
    }

    private void initGameRunningTask(){
        _gameRunningTimerTask = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                        public void run() {
                            runGame();
                        }
                    });
            }
        };
    }

    private void resetGameTime(){
        _gameTime.set(_gamePreferences.getInt("GAME_TIME", 10));
    }


    public void cleanGameboard(){
        _gameboard.removeObjects(GameObjectType.YELLOWMINION);
        _gameboard.removeObjects(GameObjectType.PURPLEMINION);
        _gameboard.removeObjects(GameObjectType.BANANA);
        _gameboard.removeObjects(GameObjectType.BEEDO);
        _gameboard.removeObjects(GameObjectType.GOGGLES);
    }

    private void resetPoints(){
        _pointsYellowMinion.set(0);
        _pointsPurpleMinion.set(0);
    }

    private void checkForCollisions(){
        GameObject yellowMinion = _gameboard.getYellowMinion();
        GameObject purpleMinion = _gameboard.getPurpleMinion();

        if(_gameboard.isOutsideOfGameboard(yellowMinion) || _gameboard.isOutsideOfGameboard(purpleMinion)){
            _gameState.setGameState(PAUSE);
        }

        GameObject item = _gameboard.isCollidingGameObject(yellowMinion);
        if (item != null){
            itemCollisionHandler(yellowMinion, item);
        }
        item = _gameboard.isCollidingGameObject(purpleMinion);
        if (item != null){
            itemCollisionHandler(purpleMinion, item);
        }
    }

    private void itemCollisionHandler(GameObject minion, GameObject item){
        switch(item.getType()){
            case PURPLEMINION:
                // TODO: doSomething (Collision between minions)
                break;
            case BANANA:
                bananaCollisionHandler(minion,item);
                break;
            case BEEDO:
                beedoCollisionHandler(minion,item);
                break;
            case GOGGLES:
                gogglesCollisionHandler(minion,item);
                break;
        }
    }

    private void bananaCollisionHandler(GameObject minion, GameObject item){
        item.playSound();
        _gameboard.removeGameObject(item);
        _gameboard.generateBanana();

        switch (minion.getType()){
            case YELLOWMINION:
                _pointsYellowMinion.set(_pointsYellowMinion.intValue()+1);
                break;
            case PURPLEMINION:
                _pointsPurpleMinion.set(_pointsPurpleMinion.intValue()+1);
                break;
        }
    }

    private void beedoCollisionHandler(GameObject minion, GameObject item){
        if (minion.getType() == GameObjectType.YELLOWMINION){
            if (_yellowPlayer != null) {
                _yellowPlayer.setPlayerState(PlayerState.Blocked);
            }
        } else {
            if (_purplePlayer != null) {
                _purplePlayer.setPlayerState(PlayerState.Blocked);
            }
        }
        item.playSound();
        _gameboard.removeGameObject(item);
        _beedoDropOnGameTime = _gameTime.intValue() - ThreadLocalRandom.current().nextInt(_beedoDropRateBottom, _beedoDropRateTop+1);
        _beedoCounter--;
    }

    private void gogglesCollisionHandler(GameObject minion, GameObject item){
        if (minion.getType() == GameObjectType.YELLOWMINION){
            if (_yellowPlayer != null) {
                _yellowPlayer.setPlayerState(PlayerState.Speedy);
            }
        } else {
            if (_purplePlayer != null) {
                _purplePlayer.setPlayerState(PlayerState.Speedy);
            }
        }
        item.playSound();
        _gameboard.removeGameObject(item);
        _goggleDropOnGameTime = _gameTime.intValue() - ThreadLocalRandom.current().nextInt(_goggleDropRateBottom, _goggleDropRateTop+1);
        _goggleCounter--;
    }

    public GameObjectType whichMinionWon(){
        if (_pointsYellowMinion.intValue() > _pointsPurpleMinion.intValue()){
            return GameObjectType.YELLOWMINION;
        } else if (_pointsYellowMinion.intValue() < _pointsPurpleMinion.intValue()){
            return GameObjectType.PURPLEMINION;
        }
        return null;
    }

    private void addGameTimeListener(){
        _gameTime.addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal, Object newVal){
                //int old = (int) oldVal;
                int act = (int) newVal;
                if (_gameTime.intValue() == 0) {
                    gameOver();
                }
                dropItems();
            }
        });
    }

    private void addGameStateListener(){
        _gameState.getGameStateNumber().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal, Object newVal){
                int old = (int) oldVal;
                switch (_gameState.getGameState()){
                    case CALIBRATION:
                        _gameboard.createMinions();
                        break;
                    case CALIBRATIONVALIDATION:
                        if (initCamPoints1() == true){
                            _gameState.setGameState(READY);
                            cleanGameboard();
                            initScaler();
                        } else {
                            _gameState.setGameState(CALIBRATION);   // Go back to Calibration
                        }
                        break;
                    case READY:
                        resetPoints();
                        break;
                    case PLAY:
                        if (old == PAUSE.getValue()){
                            startGameTimer();
                        } else {
                            startGame();
                        }
                        break;
                    case PAUSE:
                        stopGameTimer();
                        break;
                }
            }
        });
    }

}
