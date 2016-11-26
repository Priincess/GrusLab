package game.gui;

import game.GameState;
import game.GameStateValue;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Created by Mark Mauerhofer on 20.10.2016.
 */
public class GameStartViewController {

    private GameState _gameState;
    private GuiManager _guiManager;

    private MediaPlayer _mediaPlayer;

    @FXML
    Pane _pane_GameStartView;
    @FXML
    MediaView _mediaView_Background;
    @FXML
    Label _label_GameInfoText;

    private SequentialTransition _gameInfoTextLoadingTransition;
    private boolean _isGameInfoTextLoadingTransitionRunning = false;

    @FXML
    public void initialize(){
        _gameState = GameState.getInstance();
        _guiManager = new GuiManager();
        initPane();
        initBackgroundVideo();
        initGameInfoTextLoadingTransition();
        initBindings();

        addMouseListenerToPane();
        addGameStateListener();
        startGameInfoTextLoadingTransition();
    }

    private void initPane(){
        _pane_GameStartView.setStyle("-fx-background-color: black;");
    }


    private void initBackgroundVideo(){
        Media video = new Media(getClass().getResource("media/minionsWantsBanana.mp4").toExternalForm());
        _mediaPlayer = new MediaPlayer(video);
        _mediaPlayer.setAutoPlay(true);
        _mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        _mediaView_Background.setMediaPlayer(_mediaPlayer);
        _mediaView_Background.setSmooth(true);
    }

    private void initBindings(){
        DoubleProperty width = _mediaView_Background.fitWidthProperty();
        DoubleProperty height = _mediaView_Background.fitHeightProperty();

        width.bind(Bindings.selectDouble(_mediaView_Background.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(_mediaView_Background.sceneProperty(), "height"));

        _label_GameInfoText.layoutXProperty().bind(_mediaView_Background.fitWidthProperty().divide(2).
                subtract(_label_GameInfoText.widthProperty().divide(2)));
    }

    private void initGameInfoTextLoadingTransition(){
        _gameInfoTextLoadingTransition = new SequentialTransition();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), _label_GameInfoText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (_label_GameInfoText.getText()) {
                    case "Loading":
                        _label_GameInfoText.setText("Loading.");
                        break;
                    case "Loading.":
                        _label_GameInfoText.setText("Loading..");
                        break;
                    case "Loading..":
                        _label_GameInfoText.setText("Loading...");
                        break;
                    case "Loading...":
                        _label_GameInfoText.setText("Loading");
                        break;
                }
            }
        });
        _gameInfoTextLoadingTransition.getChildren().add(fadeOut);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), _label_GameInfoText);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        _gameInfoTextLoadingTransition.getChildren().add(fadeIn);
        _gameInfoTextLoadingTransition.setCycleCount(SequentialTransition.INDEFINITE);
    }

    private void startGameInfoTextLoadingTransition(){
        if (_isGameInfoTextLoadingTransitionRunning == false) {
            resetGameInfoTextLoadingTransition();
            _isGameInfoTextLoadingTransitionRunning = true;
            _gameInfoTextLoadingTransition.play();
        }
    }

    private void stopGameInfoTextLoadingTransition(){
        if (_gameInfoTextLoadingTransition != null) {
            _isGameInfoTextLoadingTransitionRunning = false;
            _gameInfoTextLoadingTransition.stop();
        }
    }

    private void resetGameInfoTextLoadingTransition(){
        _label_GameInfoText.setText("Loading");
        _label_GameInfoText.setTextFill(Color.WHITE);
        _label_GameInfoText.setVisible(true);
    }

    // TODO: Remove: Only for Debugging/Simulating
    private void addMouseListenerToPane(){
        _pane_GameStartView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isPrimaryButtonDown() == true){
                    _gameState.setGameState(GameStateValue.CALIBRATION);
                }
            }
        });
    }

    private void addGameStateListener(){
        _gameState.getGameStateNumber().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal, Object newVal){
                switch (_gameState.getGameState()){
                    case CALIBRATION:
                        stopGameInfoTextLoadingTransition();
                        _mediaPlayer.stop();
                        _guiManager.gotoGameboardView();
                        break;
                }
            }
        });
    }

}
