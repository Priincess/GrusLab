package game;

import game.gameboard.GameObject;
import game.gameboard.GameObjectType;
import game.gameboard.Gameboard;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;


/**
 * Created by Mark Mauerhofer on 21.10.2016.
 */
public class Game {

    private Preferences gamePreferences;
    private Gameboard gameboard;
    private GameState gameState;

    private ArrayList<IntegerProperty> points;

    private IntegerProperty gameTime = new SimpleIntegerProperty(0);    // Overwritten by Preferences
    private Timer gameTimer = new Timer();
    private TimerTask gameTimerTask;
    private TimerTask gameRunningTimerTask;

    public Game(){
        gameState = GameState.getInstance();
        gamePreferences = Preferences.userNodeForPackage(this.getClass());
        loadGameSettings();
        gameboard = new Gameboard();
        points = new ArrayList<IntegerProperty>();
        points.add(new SimpleIntegerProperty(0));
        points.add(new SimpleIntegerProperty(0));

        gameTime.set(10);   // TODO: Remove
        gameState.setGameState(GameStateValue.READY);   // TODO: Remove
    }

    public IntegerProperty getPoints(int minion){
        return points.get(minion);
    }

    public void startGame(){
        resetPoints();
        startGameCountdown();
        startGameRunningTask();
        gameboard.gameboardStartSetup();
        gameState.setGameState(GameStateValue.PLAY);
    }

    public void runGame(){
        // TODO: Poll Position of Minions
        checkForCollisions();
    }

    public void gameOver(){
        stopGameRunningTask();
        stopGameCountdown();
        cleanGameboard();
        gameState.setGameState(GameStateValue.FINISHED);
    }

    public Gameboard getGameboard(){
        return this.gameboard;
    }

    public void saveGameSettings(){
        gamePreferences.putInt("GAME_TIME", gameTime.intValue());
    }

    private void loadGameSettings(){
        gameTime.set(gamePreferences.getInt("GAME_TIME", 30));
    }

    public IntegerProperty getGameTime(){
        return gameTime;
    }

    private void startGameCountdown(){
        if (gameTimerTask == null){
            gameTimerTask = new TimerTask() {
                public void run() {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            gameTime.set(gameTime.intValue()-1);
                            if (gameTime.intValue() == 0){
                                gameOver();
                            }
                        }
                    });
                }
            };
            gameTimer.scheduleAtFixedRate(gameTimerTask, 0, 1000);
        }
    }

    private void stopGameCountdown(){
        gameTimerTask.cancel();
        gameTimerTask = null;
    }

    private void startGameRunningTask(){
        if (gameRunningTimerTask == null){
            gameRunningTimerTask = new TimerTask() {
                public void run() {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            runGame();
                        }
                    });
                }
            };
            gameTimer.scheduleAtFixedRate(gameRunningTimerTask, 0, 1);
        }
    }

    private void stopGameRunningTask(){
        gameRunningTimerTask.cancel();
        gameRunningTimerTask = null;
        gameTime.set(gamePreferences.getInt("GAME_TIME", 10));  // TODO: Set 120
    }

    private void cleanGameboard(){
        gameboard.removeObjects(GameObjectType.MINION);
        gameboard.removeObjects(GameObjectType.BANANA);
        gameboard.removeObjects(GameObjectType.BEEDO);
        gameboard.removeObjects(GameObjectType.GOGGLES);
    }

    private void resetPoints(){
        for (IntegerProperty prop : points) {
            prop.set(0);
        }
    }

    private void checkForCollisions(){
        for (GameObject minion : gameboard.getMinions()){
            if (gameboard.isOutsideOfGameboard(minion) == true){
                // TODO: Stop Robot
            } else {
//                GameObject minion2 = gameboard.isCollidingWithMinion(minion);
//                if (minion2 != null){
//                    // TODO: doSomething();
//                }
                GameObject item = gameboard.isCollidingWithItem(minion);
                if (item != null){
                    itemCollisionHandler(minion, item);
                }
            }
        }
    }

    public void itemCollisionHandler(GameObject minion, GameObject item){
        switch(item.getType()){
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
        int index = gameboard.getMinions().indexOf(minion);
        points.get(index).set(points.get(index).intValue()+1);
        item.playSound();
        gameboard.removeGameObject(item);
        gameboard.generateBanana();
    }

    private void beedoCollisionHandler(GameObject minion, GameObject item){
        // TODO: stop other robot
        item.playSound();
        gameboard.removeGameObject(item);
    }

    private void gogglesCollisionHandler(GameObject minion, GameObject item){
        // TODO: speedup robot
        item.playSound();
        gameboard.removeGameObject(item);
    }

    public int whichMinionWon(){
        int m1 = points.get(0).intValue();
        int m2 = points.get(1).intValue();
        if (m1 > m2){
            return 0;
        } else if (m1 < m2){
            return 1;
        }
        return -1;
    }


}
