package Temporary.gui;

import Temporary.controller.GameSettingsController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.converter.NumberStringConverter;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class GameSettingsViewController {
    @FXML
    Pane _pane_GameSettingsView;
    @FXML
    MenuBar _menuBar_menu;

    @FXML
    TextField _textField_GameTime;

    @FXML
    Slider _slider_gameboardX;
    @FXML
    Slider _slider_gameboardY;
    @FXML
    Slider _slider_gameboardWidth;
    @FXML
    Slider _slider_gameboardHeight;

    @FXML
    Slider _slider_minionSize;
    @FXML
    Slider _slider_itemSize;
    @FXML
    Slider _slider_distance;
    @FXML
    ComboBox _comboBox_goggleDropRate;
    @FXML
    ComboBox _comboBox_beedoDropRate;

    @FXML
    ComboBox _comboBox_Camera;

    ImageView _imageView_yellowMinion;
    ImageView _imageView_purpleMinion;
    ImageView _imageView_banana;
    Rectangle _rect_gameboard;

    private GameSettingsController _gameSettingsController;
    private NumberStringConverter _numStringConver;


    public void initGameSettingsViewController(GameSettingsController gameSettingsController){
        _gameSettingsController = gameSettingsController;

        _numStringConver = new NumberStringConverter();

        initRectGameboard();
        initImages();
        initGameValues();

        _pane_GameSettingsView.setStyle("-fx-background-color: black;");
        _menuBar_menu.toFront();    // otherwise after adding images, menubar could be behind them
    }

    private void initImages(){
        // TODO: use final static...global path
        _imageView_yellowMinion = new ImageView(getClass().getResource("gameObjectMedia/minion1.png").toExternalForm());
        _imageView_purpleMinion = new ImageView(getClass().getResource("gameObjectMedia/minion2.png").toExternalForm());
        _imageView_banana = new ImageView(getClass().getResource("gameObjectMedia/banana.png").toExternalForm());

        Bindings.bindBidirectional(_imageView_yellowMinion.xProperty(), _gameSettingsController.getGameboardXProperty());
        Bindings.bindBidirectional(_imageView_yellowMinion.yProperty(), _gameSettingsController.getGameboardYProperty());
        Bindings.bindBidirectional(_imageView_yellowMinion.fitWidthProperty(), _gameSettingsController.getMinionWidthProperty());
        Bindings.bindBidirectional(_imageView_yellowMinion.fitHeightProperty(), _gameSettingsController.getMinionHeightProperty());

        NumberBinding purpleMinionX = _gameSettingsController.getGameboardXProperty()
                .add(_gameSettingsController.getGameboardWidthProperty())
                .subtract(_gameSettingsController.getMinionWidthProperty());
        _imageView_purpleMinion.xProperty().bind(purpleMinionX);

        NumberBinding purpleMinionY = _gameSettingsController.getGameboardYProperty()
                .add(_gameSettingsController.getGameboardHeightProperty())
                .subtract(_gameSettingsController.getMinionHeightProperty());
        _imageView_purpleMinion.yProperty().bind(purpleMinionY);
        Bindings.bindBidirectional(_imageView_purpleMinion.fitWidthProperty(), _gameSettingsController.getMinionWidthProperty());
        Bindings.bindBidirectional(_imageView_purpleMinion.fitHeightProperty(), _gameSettingsController.getMinionHeightProperty());


        NumberBinding bananaX = _gameSettingsController.getGameboardXProperty()
                .add(_gameSettingsController.getGameboardWidthProperty().divide(2))
                .subtract(_gameSettingsController.getBananaWidthProperty().divide(2));
        _imageView_banana.xProperty().bind(bananaX);

        NumberBinding bananaY = _gameSettingsController.getGameboardYProperty()
                .add(_gameSettingsController.getGameboardHeightProperty().divide(2))
                .subtract(_gameSettingsController.getBananaHeightProperty().divide(2));
        _imageView_banana.yProperty().bind(bananaY);
        Bindings.bindBidirectional(_imageView_banana.fitWidthProperty(), _gameSettingsController.getBananaWidthProperty());
        Bindings.bindBidirectional(_imageView_banana.fitHeightProperty(), _gameSettingsController.getBananaHeightProperty());


        _pane_GameSettingsView.getChildren().add(_imageView_yellowMinion);
        _pane_GameSettingsView.getChildren().add(_imageView_purpleMinion);
        _pane_GameSettingsView.getChildren().add(_imageView_banana);
    }

    private void initRectGameboard(){
        _rect_gameboard = new Rectangle();
        _rect_gameboard.setFill(Color.BLACK);
        _rect_gameboard.setStrokeType(StrokeType.OUTSIDE);
        _rect_gameboard.setStroke(Color.RED);

        Bindings.bindBidirectional(_rect_gameboard.xProperty(), _gameSettingsController.getGameboardXProperty());
        Bindings.bindBidirectional(_rect_gameboard.yProperty(), _gameSettingsController.getGameboardYProperty());
        Bindings.bindBidirectional(_rect_gameboard.widthProperty(), _gameSettingsController.getGameboardWidthProperty());
        Bindings.bindBidirectional(_rect_gameboard.heightProperty(), _gameSettingsController.getGameboardHeightProperty());

        NumberBinding strokeWidth = _gameSettingsController.getMinionWidthProperty().divide(2);
        _rect_gameboard.strokeWidthProperty().bind(strokeWidth);

        _pane_GameSettingsView.getChildren().add(_rect_gameboard);
    }

    private void initGameValues(){
        //Bindings.bindBidirectional(_textField_GameTime.textProperty(), _gameSettingsController.getGameTime(), _numStringConver);
    }


    public void gotoStartView(){
        _gameSettingsController.gotoStartView();
    }

    public void saveGameSettings(){
        _gameSettingsController.saveGameSettings();
    }

    public void maximizeGameboard(){
        _gameSettingsController.maximizeGameboard();
    }

    public void saveGameboardSettings(){
        _gameSettingsController.saveGameboardSettings();
    }

    public void saveGameObjectSettings(){
        _gameSettingsController.saveGameObjectSettings();
    }

    public void saveCameraSettings(){
        _gameSettingsController.saveCameraSettings();
    }

    public void saveCalibration(){
        _gameSettingsController.saveCalibration();
    }

}
