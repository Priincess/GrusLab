package game.gui;

import game.controller.GameMenuController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Created by Mark Mauerhofer on 06.12.2016.
 */
public class GameMenuViewController {

    private GameMenuController _gameMenuController;
    private MediaPlayer _mediaPlayer;


    @FXML
    Pane _pane_GameMenuView;
    @FXML
    MediaView _mediaView_Background;
    @FXML
    Label _label_GameInfoText;


    @FXML
    public void initialize(){
        _gameMenuController = new GameMenuController(); // TODO: getInstance from ControllerManager
        initBackgroundVideo();
        initMediaPlayerBindings();
        initGameInfoTextBindings();
        _pane_GameMenuView.setStyle("-fx-background-color: black;");
    }

    // TODO: use final static...global path
    private void initBackgroundVideo(){
        Media video = new Media(getClass().getResource("media/minionsWantsBanana.mp4").toExternalForm());
        _mediaPlayer = new MediaPlayer(video);
        _mediaPlayer.setAutoPlay(true);
        _mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        _mediaView_Background.setMediaPlayer(_mediaPlayer);
        _mediaView_Background.setSmooth(true);
    }

    private void initGameInfoTextBindings(){
        _label_GameInfoText.layoutXProperty().bind(_mediaView_Background.fitWidthProperty().divide(2).
                subtract(_label_GameInfoText.widthProperty().divide(2)));
    }

    private void initMediaPlayerBindings() {
        DoubleProperty width = _mediaView_Background.fitWidthProperty();
        DoubleProperty height = _mediaView_Background.fitHeightProperty();

        width.bind(Bindings.selectDouble(_mediaView_Background.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(_mediaView_Background.sceneProperty(), "height"));
    }

}
