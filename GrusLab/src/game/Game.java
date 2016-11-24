package game;

import game.camera.ObjTracker;
import game.gameboard.GameObject;
import game.gameboard.GameObjectType;
import game.gameboard.Gameboard;
import game.gameboard.GameboardScaler;
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

import static game.GameStateValue.CALIBRATION;
import static game.GameStateValue.FINISHED;
import static game.GameStateValue.PAUSE;


/**
 * Created by Mark Mauerhofer on 21.10.2016.
 */
public class Game {

    private IntegerProperty pointsYellowMinion = new SimpleIntegerProperty(0);
    private IntegerProperty pointsPurpleMinion = new SimpleIntegerProperty(0);

    private Preferences gamePreferences;
    private Gameboard gameboard;
    private GameState gameState;

    private ObjTracker tracker;
    private GameboardScaler scaler;

    private IntegerProperty gameTime = new SimpleIntegerProperty(0);    // Overwritten by Preferences
    private Timer gameTimer;
    private TimerTask gameTimerTask;
    private TimerTask gameRunningTimerTask;

    // TODO: add to gui
    private int goggleCounterMax = 1;
    private int goggleCounter = 1;          // one game start there is already a goggle;
    private int goggleDropRateTop = 20;
    private int goggleDropRateBottom = 5;   // after that time item drops
    private int goggleDropOnGameTime = 0;   // TODO: work with several goggles

    // TODO: add to gui
    private int beedoCounterMax = 1;
    private int beedoCounter = 1;           // one game start there is already a beedo;
    private int beedoDropRateTop = 15;
    private int beedoDropRateBottom = 5;    // after that time item drops
    private int beedoDropOnGameTime = 0;    // TODO: work with several beedos

    public Game(final ObjTracker tracker){
        this.tracker = tracker;
        this.scaler = new GameboardScaler();
        this.gameState = GameState.getInstance();
        this.gamePreferences = Preferences.userNodeForPackage(this.getClass());
        loadGameSettings();

        this.gameboard = new Gameboard();
        addGameStateListener();
        addGameTimeListener();
    }

    public Gameboard getGameboard(){
        return this.gameboard;
    }

    public void saveGameSettings(){
        gamePreferences.putInt("GAME_TIME", gameTime.intValue());
    }

    public void loadGameSettings(){
        gameTime.set(gamePreferences.getInt("GAME_TIME", 300));
    }

    public IntegerProperty getGameTime(){
        return gameTime;
    }

    public IntegerProperty getPointsYellowMinion(){ return pointsYellowMinion; }

    public IntegerProperty getPointsPurpleMinion(){ return pointsPurpleMinion; }

    private void startGame(){
        startGameTimer();
        gameboard.gameboardStartSetup();
    }

    private void runGame(){
        updateYellowMinionPosition();
        updateEvilMinionPosition();
        checkForCollisions();
    }

    private void startGameTimer(){
        if (gameTimer == null) {
            gameTimer = new Timer();
            initGameCountdown();
            initGameRunningTask();
            gameTimer.scheduleAtFixedRate(gameTimerTask, 0, 1000);
            gameTimer.scheduleAtFixedRate(gameRunningTimerTask, 0, 5);
        }
    }

    private void stopGameTimer(){
        if (gameTimer != null) {
            gameTimer.cancel();
            gameTimer = null;
            gameState.setGameState(PAUSE);
        }
    }

    private void gameOver(){
        stopGameTimer();
        cleanGameboard();
        resetGameTime();
        gameState.setGameState(GameStateValue.FINISHED);
    }

    private void initGameCountdown() {
        gameTimerTask = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        gameTime.set(gameTime.intValue() - 1);
                    }
                });
            }
        };
    }

    public boolean initCamPoints1(){
        return scaler.setCamCalibrationPoints1(tracker.getYellowPos(), tracker.getEvilPos());
    }

    public boolean initCamPoints2(){
        return scaler.setCamCalibrationPoints2(tracker.getYellowPos(), tracker.getEvilPos());
    }

    private void initScaler(){
        scaler.initScaler(gameboard.getStartPositions());
    }

    private void updateYellowMinionPosition(){
        // TODO: Minion lost for a while
        Point point = tracker.getYellowPos();
        if (point == null){
            gameState.setGameState(PAUSE);
        } else {
            gameboard.setMinionPosition(GameObjectType.YELLOWMINION, scaler.transformCameraPointToGameboardPoint(tracker.getYellowPos()));
        }
    }

    private void updateEvilMinionPosition(){
        // TODO: Minion lost for a while
        Point point = tracker.getEvilPos();
        if (point == null){
            gameState.setGameState(PAUSE);
        } else {
            gameboard.setMinionPosition(GameObjectType.PURPLEMINION, scaler.transformCameraPointToGameboardPoint(tracker.getEvilPos()));
        }
    }


    private void dropItems(){
        if (goggleCounter < goggleCounterMax && goggleDropOnGameTime == gameTime.intValue()){
            gameboard.generateGoggles();
            goggleCounter++;
        }
        if (beedoCounter < beedoCounterMax && beedoDropOnGameTime == gameTime.intValue()){
            gameboard.generateBeedo();
            beedoCounter++;
        }
    }

    private void initGameRunningTask(){
        gameRunningTimerTask = new TimerTask() {
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
        gameTime.set(gamePreferences.getInt("GAME_TIME", 10));
    }


    public void cleanGameboard(){
        gameboard.removeObjects(GameObjectType.YELLOWMINION);
        gameboard.removeObjects(GameObjectType.PURPLEMINION);
        gameboard.removeObjects(GameObjectType.BANANA);
        gameboard.removeObjects(GameObjectType.BEEDO);
        gameboard.removeObjects(GameObjectType.GOGGLES);
    }

    private void resetPoints(){
        pointsYellowMinion.set(0);
        pointsPurpleMinion.set(0);
    }

    private void checkForCollisions(){
        GameObject yellowMinion = gameboard.getYellowMinion();
        GameObject purpleMinion = gameboard.getPurpleMinion();

        if(gameboard.isOutsideOfGameboard(yellowMinion) || gameboard.isOutsideOfGameboard(purpleMinion)){
            gameState.setGameState(PAUSE);
        }

        GameObject item = gameboard.isCollidingGameObject(yellowMinion);
        if (item != null){
            itemCollisionHandler(yellowMinion, item);
        }
        item = gameboard.isCollidingGameObject(purpleMinion);
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
        gameboard.removeGameObject(item);
        gameboard.generateBanana();

        switch (minion.getType()){
            case YELLOWMINION:
                pointsYellowMinion.set(pointsYellowMinion.intValue()+1);
                break;
            case PURPLEMINION:
                pointsPurpleMinion.set(pointsPurpleMinion.intValue()+1);
                break;
        }
    }

    private void beedoCollisionHandler(GameObject minion, GameObject item){
        // TODO: stop other robot
        item.playSound();
        gameboard.removeGameObject(item);
        beedoDropOnGameTime = gameTime.intValue() - ThreadLocalRandom.current().nextInt(beedoDropRateBottom, beedoDropRateTop+1);
        beedoCounter--;
    }

    private void gogglesCollisionHandler(GameObject minion, GameObject item){
        // TODO: speedup robot
        item.playSound();
        gameboard.removeGameObject(item);
        goggleDropOnGameTime = gameTime.intValue() - ThreadLocalRandom.current().nextInt(goggleDropRateBottom, goggleDropRateTop+1);
        goggleCounter--;
    }

    public int whichMinionWon(){
        if (pointsYellowMinion.intValue() > pointsPurpleMinion.intValue()){
            return 0;
        } else if (pointsYellowMinion.intValue() < pointsPurpleMinion.intValue()){
            return 1;
        }
        return -1;
    }

    private void addGameTimeListener(){
        gameTime.addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal,
                                          Object newVal){
                //int old = (int) oldVal;
                int act = (int) newVal;
                if (gameTime.intValue() == 0) {
                    gameOver();
                }
                dropItems();
            }
        });
    }

    private void addGameStateListener(){
        gameState.getGameStateNumber().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal,
                                          Object newVal){
                int old = (int) oldVal;
                switch (gameState.getGameState()){
                    case READY:
                        if (old == CALIBRATION.getValue()){
                            cleanGameboard();
                            initScaler();
                        }
                        if (old == FINISHED.getValue()){
                            resetPoints();
                        }
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
                    case FINISHED:
                        break;
                }
            }
        });
    }

}
