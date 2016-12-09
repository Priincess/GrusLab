package Temporary.game;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.core.Point;

import Temporary.Settings;
import Temporary.gameObjects.I_CollisionBox;
import Temporary.gameObjects.I_GameObject;
import game.GameState;
import game.GameStateValue;
import game.camera.ObjTracker;
import game.player.ControllerState;
import game.player.Player;
import game.player.PlayerState;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class Game implements I_GameMessages {

	private boolean _problemReported;

	private Settings _gamePreferences;

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

		_gamePreferences = Settings.getInstance();

		_problemReported = false;
		_tracker = tracker;

		_yellowPlayer = yellowPlayer;
		_purplePlayer = purplePlayer;

		_scaler = new GameboardScaler();
		_scaler.initScaler();

		_gameState = GameState.getInstance();


	}
	
	

	public int getGameTime() {
		return _gameTime;
	}

	public int getYellowScore() {
		return _yellowPlayer.getPoints();
	}

	public int getPurpleScore() {
		return _purplePlayer.getPoints();
	}
	
	public ControllerState getPurpleCommand(){
		return _purplePlayer.getControllerState();
	}
	
	public ControllerState getYellowCommand(){
		return _yellowPlayer.getControllerState();
	}

	public List<I_GameObject> getAllItems() {
		return _gameboard.getGameObjects();
	}
	
	public int getCameraReadyPercet(){
		return _tracker.CameraReadyPercent();
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
		initPreferences();
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

	private void initPreferences(){
		
		_gameboard = new Gameboard(_gamePreferences.getIntProperty(Settings.GAMEBOARD_WIDTH),
				_gamePreferences.getIntProperty(Settings.GAMEBOARD_HEIGHT));
		
		_gameMaxTime = _gamePreferences.getIntProperty(Settings.GAME_TIME);
		_dropTime = getNewDropTime();
		
		_gameboard.createMinions(_gamePreferences.getIntProperty(Settings.MINION_OFFSET),
				_gamePreferences.getIntProperty(Settings.MINION_HEIGHT),
				_gamePreferences.getIntProperty(Settings.MINION_WIDTH),
				new Point(_gamePreferences.getIntProperty(Settings.YELLOW_MINION_STARTX),
						_gamePreferences.getIntProperty(Settings.YELLOW_MINION_STARTY)),
				new Point(_gamePreferences.getIntProperty(Settings.PURPLE_MINION_STARTX),
						_gamePreferences.getIntProperty(Settings.PURPLE_MINION_STARTY)));
		
		_tracker.setCamID(_gamePreferences.getIntProperty(Settings.CAMERA_ID));
	}
	
	private void runGame() {
		if (_problemReported) {
			checkProblemSolved();
		} else {
			if (_tracker != null) {
				updateYellowMinionPosition();
				updateEvilMinionPosition();

				checkForCollisions();
				//TODO: checkMethod
				checkPlayerState(_yellowPlayer);
				checkPlayerState(_purplePlayer);
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
			_gameboard.createItem(GameObjectType.BEEDO, _gamePreferences.getIntProperty(Settings.BEEDO_WIDTH),
					_gamePreferences.getIntProperty(Settings.BEEDO_HEIGHT));
		} else if ((random == 1) && !_gameboard.containsObjectType(GameObjectType.GOGGLES)) {
			_gameboard.createItem(GameObjectType.GOGGLES, _gamePreferences.getIntProperty(Settings.GOGGLE_WIDTH),
					_gamePreferences.getIntProperty(Settings.GOGGLE_HEIGHT));
		}

	}

	private void resetPoints() {
		_yellowPlayer.resetPoints();
		_purplePlayer.resetPoints();
	}

	private void resetGameTime() {
		_gameTime = _gameMaxTime;
	}

	private void updateYellowMinionPosition() {

		Point point = _tracker.getYellowPos();

		if (point == null) {

			GameState.problemOccured(this.toString());
			_problemReported = true;

		} else {

			_gameboard.setMinionPosition(GameObjectType.YELLOWMINION,
					_scaler.transformCameraPointToGameboardPoint(point));

		}

	}

	private void updateEvilMinionPosition() {
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

	private void checkPlayerState(Player p) {

		if (p.getPlayerState().equals(PlayerState.Blocked)) {
			if ((p.getBlockedTime() - _gameTime) <= 0) {
				p.setPlayerState(PlayerState.Normal);
			} else if (p.getPlayerState().equals(PlayerState.Speedy)) {
				if ((p.getSpeedTime() - _gameTime) <= 0) {
					p.setPlayerState(PlayerState.Normal);
				}
			}
		}

	}

	private int getNewDropTime() {
		int maxBound = _gamePreferences.getIntProperty(Settings.ITEM_MAX_DROPRATE);
		int minBound = _gamePreferences.getIntProperty(Settings.ITEM_MIN_DROPRATE);

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
			_gameboard.createItem(GameObjectType.BANANA, _gamePreferences.getIntProperty(Settings.BANANA_WIDTH),
					_gamePreferences.getIntProperty(Settings.BANANA_HEIGHT));
		case BEEDO:
			if (player == _yellowPlayer) {
				_purplePlayer.setPlayerState(PlayerState.Blocked);
				_purplePlayer.setBlockedTime(_gameTime - _gamePreferences.getIntProperty(Settings.BEEDO_BLOCK_TIME));
			} else {
				_yellowPlayer.setPlayerState(PlayerState.Blocked);
				_yellowPlayer.setBlockedTime(_gameTime- _gamePreferences.getIntProperty(Settings.BEEDO_BLOCK_TIME));
			}
		case GOGGLES:
			player.setPlayerState(PlayerState.Speedy);
			player.setSpeedTime(_gameTime- _gamePreferences.getIntProperty(Settings.GOGGLE_SPEED_TIME));
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
