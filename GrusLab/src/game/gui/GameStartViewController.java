package game.gui;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

/**
 * Created by Mark Mauerhofer on 20.10.2016.
 */
public class GameStartViewController {

    @FXML
    Pane pane_GameStartView;
    @FXML
    MediaView mediaView_Background;
    @FXML
    Label label_WaitingFor;

    @FXML
    public void initialize(){
        initPane();
        initBackgroundVideo();
        initLoadingText();
        initBindings();
    }

    public void initPane(){
        pane_GameStartView.setStyle("-fx-background-color: black;");
    }


    public void initBackgroundVideo(){
        Media video = new Media(getClass().getResource("media/minionsWantsBanana.mp4").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(video);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaView_Background.setMediaPlayer(mediaPlayer);
        mediaView_Background.setSmooth(true);
    }

    public void initBindings(){
        DoubleProperty width = mediaView_Background.fitWidthProperty();
        DoubleProperty height = mediaView_Background.fitHeightProperty();

        width.bind(Bindings.selectDouble(mediaView_Background.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView_Background.sceneProperty(), "height"));
    }

    public void initLoadingText(){
        SequentialTransition transition = new SequentialTransition();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), label_WaitingFor);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        transition.getChildren().add(fadeOut);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), label_WaitingFor);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        transition.getChildren().add(fadeIn);
        transition.setCycleCount(SequentialTransition.INDEFINITE);
        transition.play();

    }

}
