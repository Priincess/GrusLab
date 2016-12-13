package game.game;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import game.SettingConstants;
import game.game.GameObjectType;
import game.game.Gameboard;
import game.game.GameboardScaler;
import game.game.I_GameMessages;
import org.opencv.core.Point;

import game.GameState;
import game.GameStateValue;
import game.Settings;
import game.camera.ObjTracker;
import game.gameObjects.I_GameObject;
import game.player.GamepadState;
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
    private TimerTask _gameCountdownTask;
    private TimerTask _gameRunningTimerTask;

    public Game(ObjTracker tracker, Player yellowPlayer, Player purplePlayer) {

        _gamePreferences = Settings.getInstance();

        _problemReported = false;
        _tracker = tracker;

        _yellowPlayer = yellowPlayer;
        _purplePlayer = purplePlayer;

        _scaler = new GameboardScaler();
        _gameState = GameState.getInstance();

    }

    private void initPreferences() {
        Point startPoint = new Point(_gamePreferences.getIntProperty(SettingConstants.GAMEBOARD_X),
                _gamePreferences.getIntProperty(SettingConstants.GAMEBOARD_Y));
        int width = _gamePreferences.getIntProperty(Settings.GAMEBOARD_WIDTH);
        int height = _gamePreferences.getIntProperty(Settings.GAMEBOARD_HEIGHT);

        _gameboard = new Gameboard(startPoint, width, height);

        _scaler.setGamePoints(startPoint,
                new Point(startPoint.x + width, startPoint.y),
                new Point(startPoint.x + width, startPoint.y + height),
                new Point(startPoint.x, startPoint.y + height));

        _gameMaxTime = _gamePreferences.getIntProperty(Settings.GAME_TIME);
        _gameTime = _gameMaxTime;
        _dropTime = getNewDropTime();

        _gameboard.createMinions(_gamePreferences.getIntProperty(Settings.MINION_OFFSET),
                _gamePreferences.getIntProperty(Settings.MINION_HEIGHT),
                _gamePreferences.getIntProperty(Settings.MINION_WIDTH),
                new Point(_gamePreferences.getIntProperty(Settings.YELLOW_MINION_STARTX),
                        _gamePreferences.getIntProperty(Settings.YELLOW_MINION_STARTY)),
                new Point(_gamePreferences.getIntProperty(Settings.PURPLE_MINION_STARTX),
                        _gamePreferences.getIntProperty(Settings.PURPLE_MINION_STARTY)));
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

    public GamepadState getPurpleCommand() {
        return _purplePlayer.getControllerState();
    }

    public GamepadState getYellowCommand() {
        return _yellowPlayer.getControllerState();
    }

    public List<I_GameObject> getAllItems() {
        return _gameboard.getAllItems();
    }

    public void cleanGameboard() {
        _gameboard.removeAllItems();
    }

    public String whichMinionWon() {
        if (_yellowPlayer != null && _purplePlayer != null) {
            if (_yellowPlayer.getPoints() > _purplePlayer.getPoints()) {
                return _yellowWon;
            } else if (_yellowPlayer.getPoints() < _purplePlayer.getPoints()) {
                return _purpleWon;
            }
        }
        return _tie;
    }

    // Not necessary gui already has a gamestatelistener, if a problem happens game sets pause and gui reacts
//	public boolean isMinionHere() {
//		if ((_tracker.getYellowPos() == null) || (_tracker.getEvilPos() == null)
//				|| (_gameboard.isOutsideOfGameboard())) {
//			return true;
//		}
//		return false;
//	}

    public int[] getGameboard() {
        return new int[]{_gamePreferences.getIntProperty(Settings.GAMEBOARD_X),
                _gamePreferences.getIntProperty(Settings.GAMEBOARD_Y),
                _gamePreferences.getIntProperty(Settings.GAMEBOARD_WIDTH),
                _gamePreferences.getIntProperty(Settings.GAMEBOARD_HEIGHT),
                _gamePreferences.getIntProperty(Settings.MINION_WIDTH)};
    }

    public boolean calibrate() {
        int radius = _gamePreferences.getIntProperty(SettingConstants.MINION_WIDTH) / 2;
        Point leftTop = _tracker.getYellowPos();
        leftTop.x -= radius;
        leftTop.y -= radius;
        Point rightBottom = _tracker.getEvilPos();
        rightBottom.x += radius;
        rightBottom.y += radius;
        _scaler.setCameraPoints(leftTop, rightBottom);
        _scaler.initScaler();
        return _scaler.isInitialized();
    }

    public void startGame() {
        initPreferences();

        _gameboard.createItem(GameObjectType.BANANA, _gamePreferences.getIntProperty(Settings.BANANA_WIDTH),
                _gamePreferences.getIntProperty(Settings.BANANA_HEIGHT));

        _gameState.setGameState(GameStateValue.PLAY);
        if (_yellowPlayer != null && _purplePlayer != null) {
            resetPoints();
        }

        if (_gameTimer == null) {
            _gameTimer = new Timer();
            initGameCountdown();
            initGameRunningTask();
            _gameTimer.scheduleAtFixedRate(_gameCountdownTask, 0, 1000);
            _gameTimer.scheduleAtFixedRate(_gameRunningTimerTask, 0, 5);
        }
    }

    private void runGame() {
        // TODO: we only check for solved problems during the game, so the game continues despite error :-/
        // TODO: we should pause game and continue timer after problem is solved
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
        _gameCountdownTask = new TimerTask() {
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
                    _yellowPlayer.setBlockedTime(_gameTime - _gamePreferences.getIntProperty(Settings.BEEDO_BLOCK_TIME));
                }
            case GOGGLES:
                player.setPlayerState(PlayerState.Speedy);
                player.setSpeedTime(_gameTime - _gamePreferences.getIntProperty(Settings.GOGGLE_SPEED_TIME));
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


    // TODO: Remove: Only for debugging;
    public Point getYellowMinionPosition() {
        return _gameboard.getYellowMinionPosition();
    }

    // TODO: Remove: Only for debugging;
    public Point getPurpleMinionPosition() {
        return _gameboard.getPurpleMinionPosition();
    }

    // TODO: Remove: Only for debugging;
    public void setYellowMinionPosition(int x, int y) {
        _gameboard.setMinionPosition(GameObjectType.YELLOWMINION, new Point(x, y));
    }

    // TODO: Remove: Only for debugging;
    public void setPurpleMinionPosition(int x, int y) {
        _gameboard.setMinionPosition(GameObjectType.PURPLEMINION, new Point(x, y));
    }


}
