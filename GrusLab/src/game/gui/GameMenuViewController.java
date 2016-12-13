package game.gui;

import game.GameState;
import game.GameStateValue;
import game.controller.ControllerManager;
import game.controller.GameMenuController;
import game.player.GamepadState;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;

import static game.GameStateValue.LOADED;


/**
 * Created by Mark Mauerhofer on 06.12.2016.
 */
public class GameMenuViewController {

    private GameMenuController _gameMenuController;
    private GuiManager _guiManager;
    private MediaPlayer _mediaPlayer;
    private GameState _gameState;
    private GameStateValue _oldGameState;
    private Task _watchdogTask;

    @FXML
    Pane _pane_GameMenuView;
    @FXML
    MediaView _mediaView_Background;

    private Font _font;
    private ToggleGroup _menubuttonGroup;
    private RadioButton _buttonStartGame;
    private RadioButton _buttonSettings;

    @FXML
    public void initialize(){
        _pane_GameMenuView.setStyle("-fx-background-color: black;");
        _font = new Font("Minion", 50);

        _gameState = GameState.getInstance();
        _oldGameState = GameState.getInstance().getGameState();

        _guiManager = new GuiManager();
        _gameMenuController = ControllerManager.getGameMenuController();

        initBackgroundVideo();
        initMediaPlayerBindings();
        initButtons();
        initWatchdogTask();
        startWatchdogTask();

        addKeyListener();
    }

    private void initBackgroundVideo(){
        Media video = new Media(getClass().getResource("media/minionsWantsBanana.mp4").toExternalForm());
        _mediaPlayer = new MediaPlayer(video);
        _mediaPlayer.setAutoPlay(true);
        _mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        _mediaView_Background.setMediaPlayer(_mediaPlayer);
        _mediaView_Background.setSmooth(true);
    }

    private void initButtons(){
        _menubuttonGroup = new ToggleGroup();
        _buttonStartGame = new RadioButton("Start Game");
        _buttonStartGame.setFont(_font);
        _buttonStartGame.setToggleGroup(_menubuttonGroup);
        _buttonStartGame.setDisable(true);
        _pane_GameMenuView.getChildren().add(_buttonStartGame);

        _buttonSettings = new RadioButton("Settings");
        _buttonSettings.setFont(_font);
        _buttonSettings.setToggleGroup(_menubuttonGroup);
        _buttonSettings.setSelected(true);
        _pane_GameMenuView.getChildren().add(_buttonSettings);

        _buttonStartGame.layoutYProperty().setValue(100);
        _buttonSettings.layoutYProperty().setValue(100);

        _buttonStartGame.layoutXProperty().bind(_pane_GameMenuView.widthProperty().divide(2).
                subtract(_buttonStartGame.widthProperty()));
        _buttonSettings.layoutXProperty().bind(_buttonStartGame.layoutXProperty().add(_buttonStartGame.widthProperty()).add(50));

    }

    private void initMediaPlayerBindings() {
        DoubleProperty width = _mediaView_Background.fitWidthProperty();
        DoubleProperty height = _mediaView_Background.fitHeightProperty();

        width.bind(Bindings.selectDouble(_mediaView_Background.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(_mediaView_Background.sceneProperty(), "height"));
    }

    private void initWatchdogTask(){
        _watchdogTask = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while(true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            gamepadWatchdog();
                            gamestateWatchdog();
                        }
                    });
                    Thread.sleep(5);
                }
            }
        };
    }

    private void startWatchdogTask(){
        Thread th = new Thread(_watchdogTask);
        th.setDaemon(true);
        th.start();
    }

    private void gamepadWatchdog() {
        GamepadState yellow = _gameMenuController.getYellowCommand();
        if (yellow != GamepadState.None ) {
            gamepadAction(yellow);
        }
    }

    private void gamepadAction(GamepadState command){
        switch (command) {
            case Forward:
                gotoView();
                break;
            case Right:
                changeSelectedButton();
                break;
            case Left:
                changeSelectedButton();
                break;
            default:
                break;
        }
    }

    private void gamestateWatchdog(){
        if (_oldGameState != _gameState.getGameState()){
            switch (_gameState.getGameState()){
                case LOADED:
                    _buttonStartGame.setDisable(false);
                    _buttonStartGame.setSelected(true);
                    _buttonSettings.setSelected(false);
                    break;
                default:
                    break;
            }
            _oldGameState = _gameState.getGameState();
        }
    }

    private void changeSelectedButton(){
        if (!_buttonStartGame.isDisable()){
            if (_buttonStartGame.isSelected()){
                _buttonSettings.setSelected(true);
                _buttonStartGame.setSelected(false);
            } else {
                _buttonStartGame.setSelected(true);
                _buttonSettings.setSelected(false);
            }
        }
    }

    private void gotoView(){
        _mediaPlayer.stop();
        if (_buttonStartGame.isSelected()){
            _guiManager.gotoView(GuiManager.GAMEBOARD_VIEW);
        } else {
            _guiManager.gotoView(GuiManager.GAMESETTING_VIEW);
        }
    }


    // TODO: Remove: Only for debugging
    private void addKeyListener(){
        _pane_GameMenuView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()){
                    case LEFT:
                        changeSelectedButton();
                        break;
                    case RIGHT:
                        changeSelectedButton();
                        break;
                    case F:
                        _gameState.setGameState(LOADED);
                        break;
                    case ENTER:
                        gotoView();
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
