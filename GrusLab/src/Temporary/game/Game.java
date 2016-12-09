package Temporary.game;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.core.Point;

import game.GameState;
import game.GameStateValue;
import game.camera.ObjTracker;
import game.player.Player;
import game.player.PlayerState;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class Game implements I_GameMessages {

	private boolean _problemReported;
	// private Settings _gamePreferences;
	private Player _yellowPlayer;
	private Player _purplePlayer;

	private Gameboard _gameboard;

	private GameState _gameState;

	private ObjTracker _tracker;
	private GameboardScaler _scaler;

	private int _dropTime;
	private int _gameMaxTime;
	private int _gameTime;
	private Timer _gameTimer;
	private TimerTask _gameTimerTask;
	private TimerTask _gameRunningTimerTask;

	public Game(ObjTracker tracker, Player yellowPlayer, Player purplePlayer) {
		// _gamePreferences = new Settings();
		_message = "";
		_problemReported = false;
		_tracker = tracker;
		_yellowPlayer = yellowPlayer;
		_purplePlayer = purplePlayer;

		_scaler = new GameboardScaler();
		_scaler.initScaler();

		_gameState = GameState.getInstance();

		_dropTime = getNewDropTime();

	}

	public int getGameTime() {
		return _gameTime;
	}

	public void cleanGameboard() {
		_gameboard.removeAllItems();
	}

	public String whichMinionWon() {
		if (_yellowPlayer.getPoints() > _purplePlayer.getPoints()) {
			return _yellowWon;
		} else if (_yellowPlayer.getPoints() < _purplePlayer.getPoints()) {
			return _purpleWon;
		}
		return _tie;
	}

	public boolean isMinionHere() {

		if ((_tracker.getYellowPos() == null) || (_tracker.getEvilPos() == null)
				|| (_gameboard.isOutsideOfGameboard())) {
			return true;
		}
		return false;
	}

	// TODO:call from GUI after countdown
	public void startGame() {
		_gameState.setGameState(GameStateValue.PLAY);
		resetPoints();

		if (_gameTimer == null) {
			_gameTimer = new Timer();
			initGameCountdown();
			initGameRunningTask();
			_gameTimer.scheduleAtFixedRate(_gameTimerTask, 0, 1000);
			_gameTimer.scheduleAtFixedRate(_gameRunningTimerTask, 0, 5);
		}
	}

	private void runGame() {
		if (_problemReported) {
			checkProblemSolved();
		} else {
			if (_tracker != null) {
				updateYellowMinionPosition();
				updateEvilMinionPosition();

				checkForCollisions();
			}
		}
	}

	private void stopGameTimer() {
		if (_gameTimer != null) {
			_gameTimer.cancel();
			_gameTimer = null;
		}
	}

	private void gameOver() {
		stopGameTimer();
		cleanGameboard();
		resetGameTime();
		_gameState.setGameState(GameStateValue.FINISHED);
	}

	private void dropSpecialItem() {

		Random type = new Random();

		int random = type.nextInt(2);

		if ((random == 0) && !_gameboard.containsObjectType(GameObjectType.BEEDO)) {
			// TODO: PREFERENCES
			_gameboard.createItem(GameObjectType.BEEDO, 0, 0);
		} else if ((random == 1) && !_gameboard.containsObjectType(GameObjectType.GOGGLES)) {
			// TODO: PREFERENCES
			_gameboard.createItem(GameObjectType.GOGGLES, 0, 0);
		}

	}

	private void resetPoints() {
		_yellowPlayer.resetPoints();
		_purplePlayer.resetPoints();
	}

	private void resetGameTime() {
		_gameTime = _gameMaxTime;
	}

	private void loadGameSettings() {
		// _gameMaxTime = _gamePreferences.getInt(Settings.GAME_TIME,
		// Settings.GAME_TIME_DEFAULT);
	}

	private void updateYellowMinionPosition() {

		Point point = _tracker.getYellowPos();
		// TODO: not VAlid value
		if (point == null) {

			GameState.problemOccured(this.toString());
			_problemReported = true;

		} else {

			_gameboard.setMinionPosition(GameObjectType.YELLOWMINION,
					_scaler.transformCameraPointToGameboardPoint(point));

		}

	}

	private void updateEvilMinionPosition() {
		// TODO: is minion lost for a while or only once?
		Point point = _tracker.getEvilPos();
		if (point == null) {
			GameState.problemOccured(this.toString());
			_problemReported = true;
		} else {
			_gameboard.setMinionPosition(GameObjectType.PURPLEMINION,
					_scaler.transformCameraPointToGameboardPoint(point));
		}
	}

	private void initGameCountdown() {
		_gameTimerTask = new TimerTask() {
			public void run() {
				if (_gameTime != 0 && GameState.getGameState() == GameStateValue.PLAY) {
					_gameTime--;

					if (--_dropTime <= 0) {

						dropSpecialItem();

						_dropTime = getNewDropTime();
					}

				} else {
					gameOver();
				}
			}
		};
	}

	private int getNewDropTime() {
		// TODO: PREFERENCES!
		int maxBound = 0;
		int minBound = 0;

		Random r = new Random();
		return r.nextInt((maxBound - minBound) + 1) + minBound;

	}

	private void initGameRunningTask() {
		_gameRunningTimerTask = new TimerTask() {
			public void run() {
				runGame();
			}
		};
	}

	private String checkForCollisions() {

		if (_gameboard.isOutsideOfGameboard()) {
			GameState.problemOccured(this.toString());
			_problemReported = true;
		}

		GameObjectType item = _gameboard.CollidesWithMinion(GameObjectType.YELLOWMINION);

		if (item != null) {
			itemCollisionHandler(_yellowPlayer, item);
		}
		item = _gameboard.CollidesWithMinion(GameObjectType.PURPLEMINION);
		if (item != null) {
			itemCollisionHandler(_purplePlayer, item);
		}

		if (_gameboard.checkIfMinionsTooClose()) {
			return _tooClose;
		}
		return "";
	}

	private void itemCollisionHandler(Player player, GameObjectType item) {
		switch (item) {
		case BANANA:
			player.addPoint();
			// TODO: PREFERENCES BANANA SIZE
			_gameboard.createItem(GameObjectType.BANANA, 0, 0);
		case BEEDO:
			if (player == _yellowPlayer)
				_purplePlayer.setPlayerState(PlayerState.Blocked);
			else {
				_yellowPlayer.setPlayerState(PlayerState.Blocked);
			}
		case GOGGLES:
			player.setPlayerState(PlayerState.Speedy);
		default:
			break;
		}
	}

	private void checkProblemSolved() {

		if ((_tracker.getYellowPos() != null) && (_tracker.getEvilPos() != null)
				&& !_gameboard.isOutsideOfGameboard()) {
			GameState.problemSolved(this.toString());
			_problemReported = false;
		}

	}

}
