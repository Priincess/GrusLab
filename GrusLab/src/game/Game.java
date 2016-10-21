package game;

import game.gameboard.Gameboard;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

/**
 * Created by Mark Mauerhofer on 21.10.2016.
 */
public class Game {

    private Preferences gamePreferences;
    private Gameboard gameboard;

    private IntegerProperty gameTime = new SimpleIntegerProperty(0);    // Overwritten by Preferences
    private Timer timer = new Timer();
    private TimerTask timerTask;

    public Game(){
        loadGameSettings();
    }

    public Gameboard getGameboard(){
        return this.gameboard;
    }

    public void saveGameSettings(){
        gamePreferences.putInt("GAME_TIME", gameTime.intValue());
    }

    public void loadGameSettings(){
        gameTime.set(gamePreferences.getInt("GAME_TIME", 120));
    }

    public IntegerProperty getGameTime(){
        return gameTime;
    }

    public void startGameCountdown(){
        if (timerTask == null){
            timerTask = new TimerTask() {
                public void run() {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            gameTime.set(gameTime.intValue()-1);
                            // TODO: GameOver
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
        }
    }

    public void stopGameCountdown(){
        timerTask.cancel();
        timerTask = null;
    }



}
