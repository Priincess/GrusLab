package Temporary.gui;

import Temporary.controller.GameSettingsController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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
    ComboBox _comboBox_camera;

    ImageView _imageView_yellowMinion;
    ImageView _imageView_purpleMinion;
    ImageView _imageView_banana;
    ImageView _imageView_goggle;
    ImageView _imageView_beedo;
    Rectangle _rect_gameboard;
    Line _line_gamedistance;

    private GameSettingsController _gameSettingsController;
    private NumberStringConverter _numStringConver;

    @FXML
    public void initialize(){
        _gameSettingsController = new GameSettingsController(); // TODO: getInstance from ControllerManager

        _numStringConver = new NumberStringConverter();

        initRectGameboard();
        initRectGameboardSlider();
        setRectGameboardBindings();

        initImages();
        initImagesSlider();
        setImagesBindings();

        initGameObjectDistance();
        initGameObjectDistanceSlider();
        setGameObjectDistanceBindings();

        initCameraComboBox();
        setCameraBindings();

        setGameValuesBindings();

        _pane_GameSettingsView.setStyle("-fx-background-color: black;");
        _menuBar_menu.toFront();    // otherwise after adding images, menubar could be behind them
    }


    private void initImages(){
        // TODO: use final static...global path
        _imageView_yellowMinion = new ImageView(getClass().getResource("media/minion1.png").toExternalForm());
        _imageView_purpleMinion = new ImageView(getClass().getResource("media/minion2.png").toExternalForm());
        _imageView_banana = new ImageView(getClass().getResource("media/banana.png").toExternalForm());
        _imageView_goggle = new ImageView(getClass().getResource("media/goggles.png").toExternalForm());
        _imageView_beedo = new ImageView(getClass().getResource("media/beedo.png").toExternalForm());

        _imageView_yellowMinion.setPreserveRatio(true);
        _imageView_purpleMinion.setPreserveRatio(true);
        _imageView_banana.setPreserveRatio(true);
        _imageView_goggle.setPreserveRatio(true);
        _imageView_beedo.setPreserveRatio(true);

        _pane_GameSettingsView.getChildren().add(_imageView_yellowMinion);
        _pane_GameSettingsView.getChildren().add(_imageView_purpleMinion);
        _pane_GameSettingsView.getChildren().add(_imageView_banana);
        // goggle and beedo not added, use the same width like banana and use their own fitting height
    }

    private void initImagesSlider(){
        _slider_minionSize.setMin(50);
        _slider_minionSize.setValue(_gameSettingsController.getMinionWidthProperty().intValue());
        _slider_minionSize.setMax(500);

        _slider_itemSize.setMin(20);
        _slider_itemSize.setValue(_gameSettingsController.getBananaWidthProperty().intValue());
        _slider_itemSize.setMax(500);
    }

    private void setImagesBindings(){
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

        Bindings.bindBidirectional(_imageView_goggle.fitWidthProperty(), _gameSettingsController.getGoggleWidthProperty());
        Bindings.bindBidirectional(_imageView_goggle.fitHeightProperty(), _gameSettingsController.getGoggleHeightProperty());

        Bindings.bindBidirectional(_imageView_beedo.fitWidthProperty(), _gameSettingsController.getBeedoWidthProperty());
        Bindings.bindBidirectional(_imageView_beedo.fitHeightProperty(), _gameSettingsController.getBeedoHeightProperty());


        Bindings.bindBidirectional(_slider_minionSize.valueProperty(), _imageView_yellowMinion.fitWidthProperty());
        Bindings.bindBidirectional(_slider_minionSize.valueProperty(), _imageView_yellowMinion.fitHeightProperty());

        Bindings.bindBidirectional(_slider_minionSize.valueProperty(), _imageView_purpleMinion.fitWidthProperty());
        Bindings.bindBidirectional(_slider_minionSize.valueProperty(), _imageView_purpleMinion.fitHeightProperty());

        Bindings.bindBidirectional(_slider_itemSize.valueProperty(), _imageView_banana.fitWidthProperty());
        Bindings.bindBidirectional(_slider_itemSize.valueProperty(), _imageView_banana.fitHeightProperty());

        Bindings.bindBidirectional(_slider_itemSize.valueProperty(), _imageView_goggle.fitWidthProperty());
        Bindings.bindBidirectional(_slider_itemSize.valueProperty(), _imageView_goggle.fitHeightProperty());

        Bindings.bindBidirectional(_slider_itemSize.valueProperty(), _imageView_beedo.fitWidthProperty());
        Bindings.bindBidirectional(_slider_itemSize.valueProperty(), _imageView_beedo.fitHeightProperty());

    }


    private void initRectGameboard(){
        _rect_gameboard = new Rectangle();
        _rect_gameboard.setFill(Color.BLACK);
        _rect_gameboard.setStrokeType(StrokeType.OUTSIDE);
        _rect_gameboard.setStroke(Color.RED);

        _pane_GameSettingsView.getChildren().add(_rect_gameboard);
    }

    private void setRectGameboardBindings(){
        Bindings.bindBidirectional(_rect_gameboard.xProperty(), _gameSettingsController.getGameboardXProperty());
        Bindings.bindBidirectional(_rect_gameboard.yProperty(), _gameSettingsController.getGameboardYProperty());
        Bindings.bindBidirectional(_rect_gameboard.widthProperty(), _gameSettingsController.getGameboardWidthProperty());
        Bindings.bindBidirectional(_rect_gameboard.heightProperty(), _gameSettingsController.getGameboardHeightProperty());

        Bindings.bindBidirectional(_slider_gameboardX.valueProperty(), _rect_gameboard.xProperty());
        Bindings.bindBidirectional(_slider_gameboardY.valueProperty(), _rect_gameboard.yProperty());
        Bindings.bindBidirectional(_slider_gameboardWidth.valueProperty(), _rect_gameboard.widthProperty());
        Bindings.bindBidirectional(_slider_gameboardHeight.valueProperty(), _rect_gameboard.heightProperty());

        NumberBinding strokeWidth = _gameSettingsController.getMinionWidthProperty().divide(2);
        _rect_gameboard.strokeWidthProperty().bind(strokeWidth);
    }

    private void initRectGameboardSlider(){
        _slider_gameboardX.setMin(0);
        _slider_gameboardX.setValue(_rect_gameboard.getX());
        _slider_gameboardX.setMax(1000);

        _slider_gameboardY.setMin(0);
        _slider_gameboardY.setValue(_rect_gameboard.getY());
        _slider_gameboardY.setMax(1000);

        _slider_gameboardWidth.setMin(200);
        _slider_gameboardWidth.setValue(_rect_gameboard.getWidth());
        _slider_gameboardWidth.setMax(2500);

        _slider_gameboardHeight.setMin(200);
        _slider_gameboardHeight.setValue(_rect_gameboard.getHeight());
        _slider_gameboardHeight.setMax(2500);
    }


    private void initGameObjectDistance(){
        _line_gamedistance = new Line();
        _line_gamedistance.setStrokeWidth(15);
        _line_gamedistance.setStroke(Color.WHITE);
        _line_gamedistance.setStartX(7.5);
        _line_gamedistance.setEndX(_gameSettingsController.getGameObjectDistance().intValue());
        _line_gamedistance.setStartY(34);
        _line_gamedistance.setEndY(34);
        _pane_GameSettingsView.getChildren().add(_line_gamedistance);
    }

    private void initGameObjectDistanceSlider(){
        _slider_distance.setMin(0);
        _slider_distance.setValue(_line_gamedistance.getEndX()-_line_gamedistance.getStartX());
        _slider_distance.setMax(1000);
    }

    private void setGameObjectDistanceBindings(){
        Bindings.bindBidirectional(_line_gamedistance.endXProperty(), _gameSettingsController.getGameObjectDistance());
        Bindings.bindBidirectional(_slider_distance.valueProperty(), _line_gamedistance.endXProperty());
    }

    private void initCameraComboBox(){
        _comboBox_camera.setItems(_gameSettingsController.getCameraList());
        if (_comboBox_camera.getItems().size() > 0 && _gameSettingsController.getCameraID().intValue() < _comboBox_camera.getItems().size()){
            _comboBox_camera.setValue(_gameSettingsController.getCameraID().intValue());
        }
    }

    private void setCameraBindings(){
        _gameSettingsController.getCameraID().bind(_comboBox_camera.getSelectionModel().selectedIndexProperty());
    }


    private void setGameValuesBindings(){
        Bindings.bindBidirectional(_textField_GameTime.textProperty(), _gameSettingsController.getGameTime(), _numStringConver);
    }


    public void gotoMenuView(){
        _gameSettingsController.gotoMenuView();
    }

    public void saveGameSettings(){
        _gameSettingsController.saveGameSettings();
    }

    public void maximizeGameboard(){
        _gameSettingsController.maximizeGameboard((int) _pane_GameSettingsView.getWidth(), (int) _pane_GameSettingsView.getHeight());
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

    public void resetAllSettings(){
        _gameSettingsController.resetAllSettings();
    }

    public void reloadCameras(){
        _gameSettingsController.reloadCameras();
    }

}
