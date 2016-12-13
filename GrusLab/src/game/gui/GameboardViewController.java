package game.gui;

import game.GameState;
import game.GameStateValue;
import game.controller.ControllerManager;
import game.game.Game;
import game.game.GameObjectType;
import game.gameObjects.I_GameObject;
import game.gameObjects.Item;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static game.GameStateValue.*;

/**
 * Created by Mark Mauerhofer on 11.12.2016.
 */
public class GameboardViewController {

    private Game _game;
    private GameState _gameState;
    private GameStateValue _oldGameState;

    private Task _updateTask;

    @FXML
    private Pane _pane_GameboardView;
    private Font _font;
    private Rectangle _rect_gameboard;

    Circle _yellowMinion;
    Circle _purpleMinion;

    private IntegerProperty _yellowPoints;
    private IntegerProperty _purplePoints;
    private IntegerProperty _gameTime;

    private Label _label_InfoText;
    private Label _label_yellow;
    private Label _label_purple;
    private Label _label_gameTime;

    private SequentialTransition _gameInfoTextReadyTransition;
    private boolean _isGameInfoTextReadyTransitionRunning = false;

    private SequentialTransition _gameInfoTextCountdownTransition;
    private boolean _isGameInfoTextCountdownTransitionRunning = false;

    private SequentialTransition _gameInfoTextGameOverTransition;
    private boolean _isGameInfoTextGameOverTransitionRunning = false;
    private int _gameInfoTextGameOverStatus = 0;

    private SequentialTransition _gameInfoTextPauseTransition;
    private boolean _isGameInfoTextPauseTransitionRunning = false;

    private List<I_GameObject> _gameObjects;
    private ObservableList<ImageView> _gameObjectImages;

    private MediaPlayer _bananaMediaPlayer;
    private MediaPlayer _goggleMediaPlayer;
    private MediaPlayer _beedoMediaPlayer;

    @FXML
    public void initialize(){
        _game = ControllerManager.getGame();
        _gameState = GameState.getInstance();
        _oldGameState = _gameState.getGameState();

        _pane_GameboardView.setStyle("-fx-background-color: black;");
        _font = new Font("Minion", 50);

        _gameObjects = new ArrayList<I_GameObject>();
        _gameObjectImages = FXCollections.observableArrayList();
        initUpdateTask();
        initRectGameboard();
        initMinions();
        initLabels();
        setLabelBindings();
        hideGameLabels();

        initGameInfoTextReady();
        initGameInfoTextCountdown();
        initGameInfoTextGameOver();
        initGameInfoTextPause();

        addGameObjectListener();
        addMouseListenerToPane();

        startUpdateTask();
        _gameState.setGameState(CALIBRATION);
    }


    private void initUpdateTask(){
        _updateTask = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while(_gameState.getGameState() != EXIT) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            gamestateWatchdog();
                            gamepadWatchdog();
                            if (_gameState.getGameState() == PLAY) {
                                updateGameTime();
                                updatePlayerPoints();
                                //updateMinionPositions();
                                updateGameObjectsList();
                            }
                        }
                    });
                    Thread.sleep(5);
                }
                return null;
            }
        };
    }

    private void startUpdateTask(){
        Thread th = new Thread(_updateTask);
        th.setDaemon(true);
        th.start();
    }

    private void addGameObjectListener(){
        _gameObjectImages.addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change){
                while (change.next()){
                    if (change.wasAdded()){
                        for (int i = 0; i < change.getAddedSize(); i++){
                            _pane_GameboardView.getChildren().add((ImageView) change.getAddedSubList().get(i));
                        }
                    }
                    if (change.wasRemoved()){
                        for (int i = 0; i < change.getRemovedSize(); i++){
                            _pane_GameboardView.getChildren().remove(change.getRemoved().get(i));
                        }
                    }

                }
            }
        });
    }

    private void updateGameObjectsList(){
        List<I_GameObject> newGameObjects = _game.getAllItems();
        Iterator<I_GameObject> iterGameObjects = _gameObjects.iterator();
        Iterator<ImageView> iterImages = _gameObjectImages.iterator();
        while (iterGameObjects.hasNext()){
            I_GameObject gObj = iterGameObjects.next();
            iterImages.next();
            // item removed
            if (!newGameObjects.contains(gObj)){
                if (gObj instanceof Item){
                    playSound(((Item) gObj).getType());
                    iterGameObjects.remove();
                    iterImages.remove();
                }
            }
        }

        for (int i = 0; i < newGameObjects.size(); i++){
            // new item
            if (!_gameObjects.contains(newGameObjects.get(i))){
                _gameObjects.add(newGameObjects.get(i));
                _gameObjectImages.add(createGameObjectImages(newGameObjects.get(i)));
            }
        }
    }

    private void updateGameTime(){
        _gameTime.setValue(_game.getGameTime());
    }

    private void updatePlayerPoints(){
        _yellowPoints.setValue(_game.getYellowScore());
        _purplePoints.setValue(_game.getPurpleScore());
    }

    private void gamestateWatchdog(){
        if (_oldGameState != _gameState.getGameState()){
            switch (_gameState.getGameState()){
                case CALIBRATION:
                    hideGameLabels();
                    break;
                case READY:
                    showGameLabels();
                    if (_oldGameState == FINISHED) {
                        stopGameInfoTextGameOver();
                    }
                    startGameInfoTextReady();
                    break;
                case COUNTDOWN:
                    stopGameInfoTextReady();
                    startGameInfoTextCountdown();
                    break;
                case PLAY:
                    showMinions();  // TODO: Remove: Only for Debugging
                    if (_oldGameState == PAUSE) {
                        stopGameInfoTextPause();
                    }
                    break;
                case PAUSE:
                    startGameInfoTextPause();
                    break;
                case FINISHED:
                    _gameObjects.clear();
                    _gameObjectImages.clear();
                    startGameInfoTextGameOver();
                    break;
                default:
                    break;
            }
            _oldGameState = _gameState.getGameState();
        }
    }

    private void gamepadWatchdog(){
//        if (_game.calibrate()){
//            _gameState.setGameState(READY);
//            //hideMinions();
//        } else {
//            _label_InfoText.setText("Calibration failed\nTry Again");
//        }
    }

    private void playSound(GameObjectType type){
        switch (type){
            case BANANA:
                _bananaMediaPlayer = new MediaPlayer(new Media(getClass().getResource("media/Banana.mp3").toExternalForm()));   // dirty, but i did not find another way to restart sound
                _bananaMediaPlayer.play();
                break;
            case BEEDO:
                _beedoMediaPlayer = new MediaPlayer(new Media(getClass().getResource("media/Beedo.mp3").toExternalForm()));
                _beedoMediaPlayer.play();
                break;
            case GOGGLES:
                _goggleMediaPlayer = new MediaPlayer(new Media(getClass().getResource("media/What.mp3").toExternalForm()));
                _goggleMediaPlayer.play();
                break;
            default:
                break;
        }
    }

    private ImageView createGameObjectImages(I_GameObject gObj){
        ImageView image = null;
        if (gObj instanceof Item){
            switch (((Item) gObj).getType()){
                case BANANA:
                    image = new ImageView(getClass().getResource("media/banana.png").toExternalForm());
                    break;
                case GOGGLES:
                    image = new ImageView(getClass().getResource("media/goggles.png").toExternalForm());
                    break;
                case BEEDO:
                    image = new ImageView(getClass().getResource("media/beedo.png").toExternalForm());
                    break;
                default:
                    break;
            }
        }
        if (image != null){
            image.setX(gObj.getBox().getLeftTop().x);
            image.setY(gObj.getBox().getLeftTop().y);
            image.setFitWidth(gObj.getBox().getWidth());
            image.setFitHeight(gObj.getBox().getHeight());
            return image;
        }
        return null;
    }

    private void initRectGameboard(){
        int[] rect = _game.getGameboard();
        _rect_gameboard = new Rectangle(rect[0], rect[1], rect[2], rect[3]);
        _rect_gameboard.setStrokeWidth(rect[4]);
        _rect_gameboard.setFill(Color.BLACK);
        _rect_gameboard.setStroke(Color.RED);
        _rect_gameboard.setStrokeType(StrokeType.OUTSIDE);
        _pane_GameboardView.getChildren().add(_rect_gameboard);
    }

    private void initLabels(){
        _label_InfoText = new Label();
        _label_InfoText.setText("Calibration");
        _label_InfoText.setFont(_font);
        _label_InfoText.setTextFill(Color.WHITE);
        _pane_GameboardView.getChildren().add(_label_InfoText);
        _label_InfoText.toFront();

        _gameTime = new SimpleIntegerProperty(0);
        _label_gameTime = new Label();
        _label_gameTime.textProperty().bind(_gameTime.asString());
        _label_gameTime.setTextAlignment(TextAlignment.CENTER);
        _label_gameTime.setFont(_font);
        _label_gameTime.setTextFill(Color.WHITE);
        _pane_GameboardView.getChildren().add(_label_gameTime);
        _label_gameTime.toFront();

        _yellowPoints = new SimpleIntegerProperty(0);
        _label_yellow = new Label();
        _label_yellow.textProperty().bind(Bindings.concat("Bob: ").concat(_yellowPoints.asString()));
        _label_yellow.setFont(_font);
        _label_yellow.setTextFill(Color.YELLOW);
        _pane_GameboardView.getChildren().add(_label_yellow);
        _label_yellow.toFront();

        _purplePoints = new SimpleIntegerProperty(0);
        _label_purple = new Label();
        _label_purple.textProperty().bind(Bindings.concat("Robert: ").concat(_purplePoints.asString()));
        _label_purple.setFont(_font);
        _label_purple.setTextFill(Color.PURPLE);
        _pane_GameboardView.getChildren().add(_label_purple);
        _label_purple.toFront();
    }

    private void setLabelBindings(){
        NumberBinding labelPositionY = _rect_gameboard.yProperty().add(_rect_gameboard.heightProperty()).
                subtract(_label_yellow.heightProperty());

        _label_yellow.layoutXProperty().bind(_rect_gameboard.xProperty().add(10));
        _label_yellow.layoutYProperty().bind(labelPositionY);

        _label_gameTime.layoutXProperty().bind(_rect_gameboard.xProperty().
                add(_rect_gameboard.widthProperty().divide(2)).
                subtract(_label_gameTime.widthProperty().divide(2)));
        _label_gameTime.layoutYProperty().bind(labelPositionY);

        _label_purple.layoutXProperty().bind(_rect_gameboard.xProperty().
                add(_rect_gameboard.widthProperty()).
                subtract(_label_purple.widthProperty().add(10)));
        _label_purple.layoutYProperty().bind(labelPositionY);


        _label_InfoText.layoutXProperty().bind(_rect_gameboard.xProperty().
                add(_rect_gameboard.widthProperty().divide(2)).
                subtract(_label_InfoText.widthProperty().divide(2)));

        _label_InfoText.layoutYProperty().bind(_rect_gameboard.yProperty().
                add(_rect_gameboard.heightProperty().divide(2)).
                subtract(_label_InfoText.heightProperty().divide(2)));
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

    // Attention: Countdown starts game;
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
                        _game.startGame();  // you can play while "GO" is fading out
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
                        _label_InfoText.setText(_game.whichMinionWon());
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

    private void hideGameLabels(){
        _label_yellow.setVisible(false);
        _label_purple.setVisible(false);
        _label_gameTime.setVisible(false);
    }

    private void showGameLabels(){
        _label_yellow.setVisible(true);
        _label_purple.setVisible(true);
        _label_gameTime.setVisible(true);
    }

    private void hideMinions(){
        _yellowMinion.setVisible(false);
        _purpleMinion.setVisible(false);
    }

    private void showMinions(){
        _yellowMinion.setVisible(true);
        _purpleMinion.setVisible(true);
    }

    private void initMinions(){
        _yellowMinion = new Circle();
        _yellowMinion.setFill(Color.YELLOW);
        _yellowMinion.setRadius(_rect_gameboard.getStrokeWidth());
        _yellowMinion.setCenterX(_rect_gameboard.getX()  + _rect_gameboard.getStrokeWidth());
        _yellowMinion.setCenterY(_rect_gameboard.getY()  + _rect_gameboard.getStrokeWidth());
        _pane_GameboardView.getChildren().add(_yellowMinion);

        _purpleMinion = new Circle();
        _purpleMinion.setFill(Color.PURPLE);
        _purpleMinion.setRadius(_rect_gameboard.getStrokeWidth());
        _purpleMinion.setCenterX(_rect_gameboard.getX() + _rect_gameboard.getWidth() - _rect_gameboard.getStrokeWidth());
        _purpleMinion.setCenterY(_rect_gameboard.getY() + _rect_gameboard.getHeight() - _rect_gameboard.getStrokeWidth());
        _pane_GameboardView.getChildren().add(_purpleMinion);
    }

    // TODO: Remove/Deactivate: Only for Debugging
    private void updateMinionPositions(){
        Point yellow = _game.getYellowMinionPosition();
        _yellowMinion.setCenterX(yellow.x);
        _yellowMinion.setCenterY(yellow.y);
        Point purple = _game.getPurpleMinionPosition();
        _purpleMinion.setCenterX(purple.x);
        _purpleMinion.setCenterY(purple.y);
    }

    // TODO: Remove/Deactivate: Only for Debugging/Simulating Gamepad
    private void addMouseListenerToPane(){
        _pane_GameboardView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isPrimaryButtonDown() == true && _gameState.getGameState() == CALIBRATION) {
                    _gameState.setGameState(READY);
                } else if (mouseEvent.isPrimaryButtonDown() == true && _gameState.getGameState() == READY) {
                    _gameState.setGameState(COUNTDOWN);
                } else if (mouseEvent.isPrimaryButtonDown() == true && _gameState.getGameState() == FINISHED) {
                    _gameState.setGameState(READY);
                } else if (_gameState.getGameState() == PLAY) {
                    int x = (int) mouseEvent.getX();
                    int y = (int) mouseEvent.getY();
                    if (mouseEvent.isPrimaryButtonDown() == true) {
                        _game.setYellowMinionPosition(x,y);
                        _yellowMinion.setCenterX(x);
                        _yellowMinion.setCenterY(y);
                    }
                    if (mouseEvent.isSecondaryButtonDown()) {
                        _game.setPurpleMinionPosition(x,y);
                        _purpleMinion.setCenterX(x);
                        _purpleMinion.setCenterY(y);
                    }
                }
            }
        });
    }
}
