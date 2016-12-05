package Temporary;

import Temporary.gameboard.GameObject;
import Temporary.gameboard.GameObjectType;
import Temporary.gameboard.Gameboard;
import Temporary.gameboard.GameboardScaler;
import game.GameState;
import game.GameStateValue;
import game.camera.ObjTracker;
import game.player.Player;
import game.player.PlayerState;
import org.opencv.core.Point;

import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class Game {
    private Preferences _gamePreferences;
    private Player _yellowPlayer;
    private Player _purplePlayer;

    private Gameboard _gameboard;
    private GameState _gameState;

    private ObjTracker _tracker;
    private GameboardScaler _scaler;


    private int _pointsYellowMinion = 0;
    private int _pointsPurpleMinion = 0;

    private int _gameMaxTime;
    private int _gameTime;
    private Timer _gameTimer;
    private TimerTask _gameTimerTask;
    private TimerTask _gameRunningTimerTask;


    public Game(ObjTracker tracker, Player yellowPlayer, Player purplePlayer){
        _gamePreferences = Preferences.userRoot().node(Settings.SETTINGSPATH);
        loadGameSettings();

        _tracker = tracker;
        _yellowPlayer = yellowPlayer;
        _purplePlayer = purplePlayer;
        _scaler = new GameboardScaler();
        _scaler.initScaler();

        _gameState = GameState.getInstance();
        _gameboard = new Gameboard();
        addGameStateListener();
    }

    public Gameboard getGameboard(){
        return _gameboard;
    }

    public int getGameTime(){
        return _gameTime;
    }

    public int getPointsYellowMinion(){ return _pointsYellowMinion; }

    public int getPointsPurpleMinion(){ return _pointsPurpleMinion; }


    private void loadGameSettings(){
        _gameMaxTime = _gamePreferences.getInt(Settings.GAME_TIME, Settings.GAME_TIME_DEFAULT);
    }

    private void updateYellowMinionPosition(){
        // TODO: is minion lost for a while or only once?
        Point point = _tracker.getYellowPos();
        if (point == null){
            _gameState.setGameState(GameStateValue.PAUSE);
        } else {
            // TODO: replace with global enum
            _gameboard.setMinionPosition(GameObjectType.YELLOWMINION, _scaler.transformCameraPointToGameboardPoint(_tracker.getYellowPos()));
        }
    }

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
            _gameState.setGameState(GameStateValue.PAUSE);
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
                _gameTime--;
            }
        };
    }

    private void initGameRunningTask(){
        _gameRunningTimerTask = new TimerTask() {
            public void run() {
                runGame();
            }
        };
    }

    private void updateEvilMinionPosition(){
        // TODO: is minion lost for a while or only once?
        Point point = _tracker.getEvilPos();
        if (point == null){
            _gameState.setGameState(GameStateValue.PAUSE);
        } else {
            // TODO: replace with global enum
            _gameboard.setMinionPosition(GameObjectType.PURPLEMINION, _scaler.transformCameraPointToGameboardPoint(_tracker.getEvilPos()));
        }
    }

    private void checkForCollisions(){
        GameObject yellowMinion = _gameboard.getYellowMinion();
        GameObject purpleMinion = _gameboard.getPurpleMinion();

        if(_gameboard.isOutsideOfGameboard(yellowMinion) || _gameboard.isOutsideOfGameboard(purpleMinion)){
            _gameState.setGameState(GameStateValue.PAUSE);
        }

        GameObject item = _gameboard.collidingWithGameObject(yellowMinion);
        if (item != null){
            itemCollisionHandler(yellowMinion, item);
        }
        item = _gameboard.collidingWithGameObject(purpleMinion);
        if (item != null){
            itemCollisionHandler(purpleMinion, item);
        }
    }

    private void bananaCollisionHandler(GameObject minion, GameObject item){
        _gameboard.removeGameObject(item);
        _gameboard.generateItem(GameObjectType.BANANA);

        switch (minion.getType()){
            case YELLOWMINION:
                _pointsYellowMinion++;
                break;
            case PURPLEMINION:
                _pointsPurpleMinion++;
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
        _gameboard.removeGameObject(item);
//        _beedoDropOnGameTime = _gameTime.intValue() - ThreadLocalRandom.current().nextInt(_beedoDropRateBottom, _beedoDropRateTop+1);
//        _beedoCounter--;
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
        _gameboard.removeGameObject(item);
//        _goggleDropOnGameTime = _gameTime.intValue() - ThreadLocalRandom.current().nextInt(_goggleDropRateBottom, _goggleDropRateTop+1);
//        _goggleCounter--;
    }

    private void itemCollisionHandler(GameObject minion, GameObject item){
        switch(item.getType()){
            case MINION:
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

    // TODO: Replace with global enum
    public GameObjectType whichMinionWon(){
        if (_pointsYellowMinion > _pointsPurpleMinion){
            return GameObjectType.YELLOWMINION;
        } else if (_pointsYellowMinion < _pointsPurpleMinion){
            return GameObjectType.PURPLEMINION;
        }
        return null;
    }

    private void resetPoints(){
        _pointsYellowMinion = 0;
        _pointsPurpleMinion = 0;
    }

    private void resetGameTime(){
        _gameTime = _gameMaxTime;
    }

    public void cleanGameboard(){
        _gameboard.removeObjects(GameObjectType.MINION);
        _gameboard.removeObjects(GameObjectType.BANANA);
        _gameboard.removeObjects(GameObjectType.BEEDO);
        _gameboard.removeObjects(GameObjectType.GOGGLES);
    }

    // TODO: observe gamestate
    private void addGameStateListener(){
        // TODO: add observer
        int oldState = 0;   // TODO:

        switch(_gameState.getGameState()){
            case READY: resetPoints();
                break;
            case PLAY:
                if (oldState == GameStateValue.PAUSE.getValue()){
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

//    private void addGameStateListener(){
//        _gameState.getGameStateNumber().addListener(new ChangeListener(){
//            @Override public void changed(ObservableValue o, Object oldVal, Object newVal){
//                int old = (int) oldVal;
//                switch (_gameState.getGameState()){
//                    case READY:
//                        resetPoints();
//                        break;
//                    case PLAY:
//                        if (old == PAUSE.getValue()){
//                            startGameTimer();
//                        } else {
//                            startGame();
//                        }
//                        break;
//                    case PAUSE:
//                        stopGameTimer();
//                        break;
//                }
//            }
//        });
//    }

}
