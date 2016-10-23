package game.gui;

import game.GameState;
import game.GameStateValue;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    private GameState gameState;
    private GuiManager guiManager;

    @FXML
    Pane pane_GameStartView;
    @FXML
    MediaView mediaView_Background;
    @FXML
    Label label_GameInfoText;

    private SequentialTransition gameInfoTextLoadingTransition;
    private boolean isGameInfoTextLoadingTransitionRunning = false;

    @FXML
    public void initialize(){
        guiManager = new GuiManager();
        gameState = GameState.getInstance();
        initPane();
        initBackgroundVideo();
        initGameInfoTextLoadingTransition();
        initBindings();

        addMouseListenerToPane();
        startGameInfoTextLoadingTransition();
    }

    private void initPane(){
        pane_GameStartView.setStyle("-fx-background-color: black;");
    }


    private void initBackgroundVideo(){
        Media video = new Media(getClass().getResource("media/minionsWantsBanana.mp4").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(video);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaView_Background.setMediaPlayer(mediaPlayer);
        mediaView_Background.setSmooth(true);
    }

    private void initBindings(){
        DoubleProperty width = mediaView_Background.fitWidthProperty();
        DoubleProperty height = mediaView_Background.fitHeightProperty();

        width.bind(Bindings.selectDouble(mediaView_Background.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView_Background.sceneProperty(), "height"));

        label_GameInfoText.layoutXProperty().bind(mediaView_Background.fitWidthProperty().divide(2).
                subtract(label_GameInfoText.widthProperty().divide(2)));
    }

    private void initGameInfoTextLoadingTransition(){
        gameInfoTextLoadingTransition = new SequentialTransition();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), label_GameInfoText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (label_GameInfoText.getText()) {
                    case "Loading":
                        label_GameInfoText.setText("Loading.");
                        break;
                    case "Loading.":
                        label_GameInfoText.setText("Loading..");
                        break;
                    case "Loading..":
                        label_GameInfoText.setText("Loading...");
                        break;
                    case "Loading...":
                        label_GameInfoText.setText("Loading");
                        break;
                }
            }
        });
        gameInfoTextLoadingTransition.getChildren().add(fadeOut);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), label_GameInfoText);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        gameInfoTextLoadingTransition.getChildren().add(fadeIn);
        gameInfoTextLoadingTransition.setCycleCount(SequentialTransition.INDEFINITE);
    }

    private void startGameInfoTextLoadingTransition(){
        if ( isGameInfoTextLoadingTransitionRunning == false) {
            resetGameInfoTextLoadingTransition();
            isGameInfoTextLoadingTransitionRunning = true;
            gameInfoTextLoadingTransition.play();
        }
    }

    private void stopGameInfoTextLoadingTransition(){
        if (gameInfoTextLoadingTransition != null) {
            isGameInfoTextLoadingTransitionRunning = false;
            gameInfoTextLoadingTransition.stop();
        }
    }

    private void resetGameInfoTextLoadingTransition(){
        label_GameInfoText.setText("Loading");
        label_GameInfoText.setTextFill(Color.WHITE);
        label_GameInfoText.setVisible(true);
    }

    private void addMouseListenerToPane(){
        pane_GameStartView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isPrimaryButtonDown() == true){
                    gameState.setGameState(GameStateValue.READY);
                    stopGameInfoTextLoadingTransition();
                    guiManager.gotoGameboardView();
                }
            }
        });
    }

}
