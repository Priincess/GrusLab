package game.gui;

import game.Game;
import game.GameState;
import game.GameStateValue;
import game.gameboard.GameObject;
import game.gameboard.GameObjectType;
import game.gameboard.Gameboard;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;

import java.awt.*;

import static game.GameStateValue.*;

/**
 * Created by Mark Mauerhofer on 08.10.2016.
 */
public class GameboardViewController {

    private Game _game;
    private Gameboard _gameboard;
    private GameState _gameState;

    private NumberStringConverter _numStringConver = new NumberStringConverter();

    @FXML
    private Pane _pane_GameboardView;
    @FXML
    private MenuBar _menubar;

    @FXML
    private TextField _textField_GameTimer;
    @FXML
    private TextField _textField_GameboardX;
    @FXML
    private TextField _textField_GameboardY;
    @FXML
    private TextField _textField_GameboardWidth;
    @FXML
    private TextField _textField_GameboardHeight;

    @FXML
    private TextField _textField_MinionSize;
    @FXML
    private TextField _textField_ItemSize;
    @FXML
    private TextField _textField_GameObjectDistance;

    @FXML
    private Label _label_Timer;
    @FXML
    private Pane _pane_YellowMinion;
    @FXML
    private Label _label_YellowMinionPoints;
    @FXML
    private Pane _pane_EvilMinion;
    @FXML
    private Label _label_EvilMinionPoints;
    @FXML
    private Label _label_InfoText;

    private SequentialTransition _gameInfoTextReadyTransition;
    private boolean _isGameInfoTextReadyTransitionRunning = false;

    private SequentialTransition _gameInfoTextCountdownTransition;
    private boolean _isGameInfoTextCountdownTransitionRunning = false;

    private SequentialTransition _gameInfoTextGameOverTransition;
    private boolean _isGameInfoTextGameOverTransitionRunning = false;
    private int _gameInfoTextGameOverStatus = 0;

    private SequentialTransition _gameInfoTextPauseTransition;
    private boolean _isGameInfoTextPauseTransitionRunning = false;


    public void initGameboardViewController(Game game){
        _game = game;
        _gameboard = game.getGameboard();
        _gameState = GameState.getInstance();

        _pane_GameboardView.setStyle("-fx-background-color: black;");
        _pane_GameboardView.getChildren().add(_gameboard.getRect_Gameboard());
        _menubar.toFront();

        addGameObjectsListener();
        addGameStateListener();
        addMouseListenerToPane();

        setGameBindings();
        setGameboardBindings();
        setGameObjectBindings();
        setGameTextBindings();

        initGameInfoTextReady();
        initGameInfoTextCountdown();
        initGameInfoTextGameOver();
        initGameInfoTextPause();

        // Set first text here - because gamestate change from wait to calibration will not be recognized since the controller does not exist at that time
        _label_InfoText.setText("Calibration\nPut Minions on Position");
        hideLabels();   //  during calibration hide labels
    }

    // Method which is called after the scene is set, otherwise window size is not known
    public void initGameboardSize(){
        if (_gameboard.getUseGameboardPreferences() == false) {
            _gameboard.getRect_Gameboard().setX(_gameboard.getRect_Gameboard().getStrokeWidth());
            _gameboard.getRect_Gameboard().setY(_gameboard.getRect_Gameboard().getStrokeWidth());
            _gameboard.getRect_Gameboard().setWidth(_pane_GameboardView.getWidth() - 2 * _gameboard.getRect_Gameboard().getStrokeWidth());
            _gameboard.getRect_Gameboard().setHeight(_pane_GameboardView.getHeight() - 2 * _gameboard.getRect_Gameboard().getStrokeWidth());
        }
    }

    private void addGameObjectsListener(){
        // Load and add GameObjects already in List...
        for (GameObject gameObject : _gameboard.getGameObjects()){
            _pane_GameboardView.getChildren().add(gameObject.getImageView());
        }

        _gameboard.getGameObjects().addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change){
                while (change.next()){
                    if (change.wasAdded() == true){
                        for (int i = 0; i < change.getAddedSize(); i++){
                            GameObject gameObject = (GameObject) change.getAddedSubList().get(i);
                            _pane_GameboardView.getChildren().add(gameObject.getImageView());
                        }
                    }
                    if (change.wasRemoved()){
                        for (int i = 0; i < change.getRemovedSize(); i++){
                            GameObject gameObject = (GameObject) change.getRemoved().get(i);
                            _pane_GameboardView.getChildren().remove(gameObject.getImageView());
                        }
                    }

                }
            }
        });
    }

    private void addGameStateListener(){
        _gameState.getGameStateNumber().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal,
                                          Object newVal){
                int old = (int) oldVal;
                switch (_gameState.getGameState()){
                    case CALIBRATION:
                        if (old == CALIBRATIONVALIDATION.getValue()){
                            _label_InfoText.setText("Calibration failed - Try again");  // TODO: BUG - It does not change text
                        }
                        break;
                    case READY:
                        if (old == CALIBRATIONVALIDATION.getValue()) {
                            showLabels();
                            //hideMenu();   // TODO: uncomment
                        } else if (old == FINISHED.getValue()) {
                            stopGameInfoTextGameOver();
                        }
                        startGameInfoTextReady();
                        break;
                    case COUNTDOWN:
                        stopGameInfoTextReady();
                        startGameInfoTextCountdown();
                        break;
                    case PLAY:
                        if (old == PAUSE.getValue()) {
                            stopGameInfoTextPause();
                        }
                        break;
                    case PAUSE:
                        startGameInfoTextPause();
                        break;
                    case FINISHED:
                        startGameInfoTextGameOver();
                        break;
                }
            }
        });
    }

    // TODO: Remove/Deactivate: Only for Debugging/Simulating Gamepad
    private void addMouseListenerToPane(){
        _pane_GameboardView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int x = (int) mouseEvent.getX();
                int y = (int) mouseEvent.getY();
                Point point = new Point(x,y);

                if (y > 75) {   // so you can use the menubar without moving minions with mouseclick
                    if (mouseEvent.isPrimaryButtonDown() == true && _gameState.getGameState() == CALIBRATION) {
                        _gameState.setGameState(CALIBRATIONVALIDATION);
                    } else if (mouseEvent.isPrimaryButtonDown() == true && _gameState.getGameState() == READY) {
                        _gameState.setGameState(COUNTDOWN);
                    } else if (mouseEvent.isPrimaryButtonDown() == true && _gameState.getGameState() == GameStateValue.FINISHED) {
                        _gameState.setGameState(READY);
                    } else if (mouseEvent.isPrimaryButtonDown() == true && _gameState.getGameState() == PAUSE){
                        _gameState.setGameState(PLAY);
                    } else if (_gameState.getGameState() == PLAY) {
                        if (mouseEvent.isPrimaryButtonDown() == true) {
                            _gameboard.setMinionPosition(GameObjectType.YELLOWMINION, point);
                        }
                        if (mouseEvent.isSecondaryButtonDown()) {
                            _gameboard.setMinionPosition(GameObjectType.PURPLEMINION, point);
                        }
                    }
                }
            }
        });
    }

    private void setGameBindings(){
        Bindings.bindBidirectional(_textField_GameTimer.textProperty(), _game.getGameTime(), _numStringConver);
    }

    private void setGameboardBindings(){
        Rectangle temp = _gameboard.getRect_Gameboard();
        Bindings.bindBidirectional(_textField_GameboardX.textProperty(), temp.xProperty(), _numStringConver);
        Bindings.bindBidirectional(_textField_GameboardY.textProperty(), temp.yProperty(), _numStringConver);
        Bindings.bindBidirectional(_textField_GameboardWidth.textProperty(), temp.widthProperty(), _numStringConver);
        Bindings.bindBidirectional(_textField_GameboardHeight.textProperty(), temp.heightProperty(), _numStringConver);
    }

    private void setGameObjectBindings(){
        Bindings.bindBidirectional(_textField_MinionSize.textProperty(), _gameboard.getMinionSize(), _numStringConver);
        Bindings.bindBidirectional(_textField_ItemSize.textProperty(), _gameboard.getItemSize(), _numStringConver);
        Bindings.bindBidirectional(_textField_GameObjectDistance.textProperty(), _gameboard.getGameObjectDistance(), _numStringConver);
    }

    private void setGameTextBindings(){
        //Binding Gametext Position
        _pane_YellowMinion.layoutXProperty().bind(_gameboard.getRect_Gameboard().xProperty().add(10));
        NumberBinding labelGameTextPositionY = _gameboard.getRect_Gameboard().yProperty().
                add(_gameboard.getRect_Gameboard().heightProperty()).
                subtract(_pane_YellowMinion.prefHeightProperty());
        _pane_YellowMinion.layoutYProperty().bind(labelGameTextPositionY);
        _pane_YellowMinion.toFront();

        _pane_EvilMinion.layoutXProperty().bind(_gameboard.getRect_Gameboard().xProperty().
                add(_gameboard.getRect_Gameboard().widthProperty()).
                subtract(_pane_EvilMinion.getPrefWidth()-30));
        _pane_EvilMinion.layoutYProperty().bind(labelGameTextPositionY);
        _pane_EvilMinion.toFront();

        _label_Timer.layoutXProperty().bind(_gameboard.getRect_Gameboard().xProperty().
                add(_gameboard.getRect_Gameboard().widthProperty().divide(2)).
                subtract(_label_Timer.getPrefHeight()));
        _label_Timer.layoutYProperty().bind(labelGameTextPositionY);
        _label_Timer.toFront();

        // Gametimer
        Bindings.bindBidirectional(_label_Timer.textProperty(), _game.getGameTime(), _numStringConver);

        // InfoText
        _label_InfoText.layoutXProperty().bind(_gameboard.getRect_Gameboard().xProperty().
                add(_gameboard.getRect_Gameboard().widthProperty().divide(2)).
                subtract(_label_InfoText.widthProperty().divide(2)));
        _label_InfoText.layoutYProperty().bind(_gameboard.getRect_Gameboard().yProperty().
                add(_gameboard.getRect_Gameboard().heightProperty().divide(2)).
                subtract(_label_InfoText.heightProperty().divide(2)));
        _label_InfoText.toFront();

        // Points
        Bindings.bindBidirectional(_label_YellowMinionPoints.textProperty(), _game.getPointsYellowMinion(), _numStringConver);
        Bindings.bindBidirectional(_label_EvilMinionPoints.textProperty(), _game.getPointsPurpleMinion(), _numStringConver);
    }


    private void initGameInfoTextReady(){
        _gameInfoTextReadyTransition = new SequentialTransition();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), _label_InfoText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.1);
        _gameInfoTextReadyTransition.getChildren().add(fadeOut);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), _label_InfoText);
        fadeIn.setFromValue(0.1);
        fadeIn.setToValue(1.0);

        _gameInfoTextReadyTransition.getChildren().add(fadeIn);
        _gameInfoTextReadyTransition.setCycleCount(SequentialTransition.INDEFINITE);
    }

    private void startGameInfoTextReady(){
        if (_isGameInfoTextReadyTransitionRunning == false) {
            resetGameInfoTextReady();
            _isGameInfoTextReadyTransitionRunning = true;
            _gameInfoTextReadyTransition.play();
        }
    }

    private void stopGameInfoTextReady(){
        if (_gameInfoTextReadyTransition != null) {
            _isGameInfoTextReadyTransitionRunning = false;
            _gameInfoTextReadyTransition.stop();
        }
    }

    private void resetGameInfoTextReady(){
        _label_InfoText.setText("Players Press Start!");
        _label_InfoText.setTextFill(Color.WHITE);
        _label_InfoText.setVisible(true);
    }

    // Attention: Here is game-logic: Countdown starts game;
    private void initGameInfoTextCountdown(){
        _gameInfoTextCountdownTransition = new SequentialTransition();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), _label_InfoText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (_label_InfoText.getText()) {
                    case "READY":
                        _label_InfoText.setText("SET");
                        _label_InfoText.setTextFill(Color.YELLOW);
                        break;
                    case "SET":
                        _label_InfoText.setText("GO!");
                        _label_InfoText.setTextFill(Color.GREEN);
                        _gameState.setGameState(PLAY);  // Already set here play, so you can start playing while "GO" is fading out
                        break;
                    case "GO!":
                        stopGameInfoTextCountdown();
                        break;
                }
            }
        });
        _gameInfoTextCountdownTransition.getChildren().add(fadeOut);
        _gameInfoTextCountdownTransition.setCycleCount(SequentialTransition.INDEFINITE);
    }

    private void startGameInfoTextCountdown(){
        if (_isGameInfoTextCountdownTransitionRunning == false) {
            resetGameInfoTextCountdown();
            _isGameInfoTextCountdownTransitionRunning = true;
            _gameInfoTextCountdownTransition.play();
        }
    }

    private void stopGameInfoTextCountdown(){
        if (_gameInfoTextCountdownTransition != null) {
            _label_InfoText.setVisible(false);
            _isGameInfoTextCountdownTransitionRunning = false;
            _gameInfoTextCountdownTransition.stop();
        }
    }

    private void resetGameInfoTextCountdown(){
        _label_InfoText.setText("READY");
        _label_InfoText.setTextFill(Color.RED);
        _label_InfoText.setVisible(true);
    }

    // Attention: Here is game-logic: asks game which minion won and sets text accordingly
    private void initGameInfoTextGameOver(){
        _gameInfoTextGameOverTransition = new SequentialTransition();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), _label_InfoText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (_gameInfoTextGameOverStatus) {
                    case 0:
                        GameObjectType minion = _game.whichMinionWon();
                        if ( minion == GameObjectType.YELLOWMINION ) {
                            _label_InfoText.setText("Minion Won!");
                            _label_InfoText.setTextFill(Color.YELLOW);
                        } else if (minion == GameObjectType.PURPLEMINION) {
                            _label_InfoText.setText("Evil Minion Won!");
                            _label_InfoText.setTextFill(Color.PURPLE);
                        } else {
                            _label_InfoText.setText("DRAW!");
                        }
                        _gameInfoTextGameOverStatus++;
                        break;
                    case 1:
                        _label_InfoText.setText("Press X or A to Restart");
                        _label_InfoText.setTextFill(Color.WHITE);
                        break;
                }
            }
        });
        _gameInfoTextGameOverTransition.getChildren().add(fadeOut);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), _label_InfoText);
        fadeIn.setFromValue(0.1);
        fadeIn.setToValue(1.0);

        _gameInfoTextGameOverTransition.getChildren().add(fadeIn);
        _gameInfoTextGameOverTransition.setCycleCount(SequentialTransition.INDEFINITE);
    }

    private void startGameInfoTextGameOver(){
        if (_isGameInfoTextGameOverTransitionRunning == false) {
            resetGameInfoTextGameOver();
            _isGameInfoTextGameOverTransitionRunning = true;
            _gameInfoTextGameOverTransition.play();
        }
    }

    private void stopGameInfoTextGameOver(){
        if (_gameInfoTextGameOverTransition != null) {
            _label_InfoText.setVisible(false);
            _isGameInfoTextGameOverTransitionRunning = false;
            _gameInfoTextGameOverTransition.stop();
        }
    }

    private void resetGameInfoTextGameOver(){
        _label_InfoText.setText("GAME OVER");
        _label_InfoText.setTextFill(Color.WHITE);
        _label_InfoText.setVisible(true);
        _gameInfoTextGameOverStatus = 0;
    }

    private void initGameInfoTextPause(){
        _gameInfoTextPauseTransition = new SequentialTransition();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), _label_InfoText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.4);
        _gameInfoTextPauseTransition.getChildren().add(fadeOut);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), _label_InfoText);
        fadeIn.setFromValue(0.4);
        fadeIn.setToValue(1.0);

        _gameInfoTextPauseTransition.getChildren().add(fadeIn);
        _gameInfoTextPauseTransition.setCycleCount(SequentialTransition.INDEFINITE);
    }

    private void startGameInfoTextPause(){
        if (_isGameInfoTextPauseTransitionRunning == false) {
            resetGameInfoTextPause();
            _isGameInfoTextPauseTransitionRunning = true;
            _gameInfoTextPauseTransition.play();
        }
    }

    private void stopGameInfoTextPause(){
        if (_gameInfoTextPauseTransition != null) {
            _isGameInfoTextPauseTransitionRunning = false;
            _gameInfoTextPauseTransition.stop();
            _label_InfoText.setVisible(false);
        }
    }

    private void resetGameInfoTextPause(){
        _label_InfoText.setText("PAUSE");
        _label_InfoText.setTextFill(Color.WHITE);
        _label_InfoText.setVisible(true);
    }


    private void hideLabels(){
        _label_Timer.setVisible(false);
        _pane_YellowMinion.setVisible(false);
        _pane_EvilMinion.setVisible(false);
    }

    private void showLabels(){
        _label_Timer.setVisible(true);
        _pane_YellowMinion.setVisible(true);
        _pane_EvilMinion.setVisible(true);
    }

    public void saveGameboardPreferences(){
        _gameboard.saveGameboardPreferences();
        _game.saveGameSettings();    // TODO: make real save game settings button
    }
    // TODO: make private and remove it from gui
    public void hideMenu(){
        _pane_GameboardView.getChildren().remove(_menubar);
    }

    // method for GUI
    public void maximizeGameboard(){
        _gameboard.setUseGameboardPreferences(false);
        initGameboardSize();
    }

    // TODO: Remove (also from gui)
    public void pauseGame(){
        _gameState.setGameState(PAUSE);
    }

    // TODO: Remove (also from gui)
    public void continueGame(){
        _gameState.setGameState(PLAY);
    }

}
