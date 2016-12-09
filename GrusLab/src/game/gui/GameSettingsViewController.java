package game.gui;

import game.controller.GameSettingsController;
import javafx.beans.binding.NumberBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class GameSettingsViewController {
    @FXML
    Pane _pane_GameSettingsView;
    @FXML
    TabPane _tabs;
    @FXML
    AnchorPane _paneOfTab_game;
    @FXML
    AnchorPane _paneOfTab_gameboard;
    @FXML
    AnchorPane _paneOfTab_banana;
    @FXML
    AnchorPane _paneOfTab_goggle;
    @FXML
    AnchorPane _paneOfTab_beedo;
    @FXML
    AnchorPane _paneOfTab_camera;


    ImageView _imageView_yellowMinion;
    ImageView _imageView_banana;
    ImageView _imageView_goggle;
    ImageView _imageView_beedo;
    Rectangle _rect_gameboard;

    private GameSettingsController _gameSettingsController;
    private GuiManager _guiManager;

    @FXML
    public void initialize(){
        _gameSettingsController = new GameSettingsController(); // TODO: getInstance from ControllerManager
        _guiManager = new GuiManager();

        initRectGameboard();
        setRectGameboardBindings();
        initImages();
        setImagesBindings();
        addObjectsToTabPane();
        addKeyListener();
    }


    private void initImages(){
        // TODO: use final static...global path
        _imageView_yellowMinion = new ImageView(getClass().getResource("media/minion1.png").toExternalForm());
        _imageView_banana = new ImageView(getClass().getResource("media/banana.png").toExternalForm());
        _imageView_goggle = new ImageView(getClass().getResource("media/goggles.png").toExternalForm());
        _imageView_beedo = new ImageView(getClass().getResource("media/beedo.png").toExternalForm());

        _imageView_yellowMinion.setPreserveRatio(true);
        _imageView_banana.setPreserveRatio(true);
        _imageView_goggle.setPreserveRatio(true);
        _imageView_beedo.setPreserveRatio(true);
    }

    private void setImagesBindings(){
        NumberBinding bananaX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.layoutXProperty().add(_paneOfTab_game.widthProperty())).divide(2))
                .subtract(_imageView_banana.fitWidthProperty().divide(2));


        NumberBinding bananaY = _pane_GameSettingsView.layoutYProperty()
                .add((_pane_GameSettingsView.layoutYProperty().add(_paneOfTab_game.heightProperty())).divide(2))
                .subtract(_imageView_banana.fitHeightProperty().divide(2));

        NumberBinding goggleX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.layoutXProperty().add(_paneOfTab_game.widthProperty())).divide(2))
                .subtract(_imageView_goggle.fitWidthProperty().divide(2));


        NumberBinding goggleY = _pane_GameSettingsView.layoutYProperty()
                .add((_pane_GameSettingsView.layoutYProperty().add(_paneOfTab_game.heightProperty())).divide(2))
                .subtract(_imageView_goggle.fitHeightProperty().divide(2));

        NumberBinding beedoX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.layoutXProperty().add(_paneOfTab_game.widthProperty())).divide(2))
                .subtract(_imageView_beedo.fitWidthProperty().divide(2));


        NumberBinding beedoY = _pane_GameSettingsView.layoutYProperty()
                .add((_pane_GameSettingsView.layoutYProperty().add(_paneOfTab_game.heightProperty())).divide(2))
                .subtract(_imageView_beedo.fitHeightProperty().divide(2));

        //_imageView_yellowMinion.xProperty().bind(iX);
        //_imageView_yellowMinion.xProperty().bind(itemY);
        _imageView_banana.xProperty().bind(bananaX);
        _imageView_banana.yProperty().bind(bananaY);
        _imageView_goggle.xProperty().bind(goggleX);
        _imageView_goggle.yProperty().bind(goggleY);
        _imageView_beedo.xProperty().bind(beedoX);
        _imageView_beedo.yProperty().bind(beedoY);
    }


    private void initRectGameboard(){
        _rect_gameboard = new Rectangle();
        _rect_gameboard.setFill(Color.BLACK);
        _rect_gameboard.setStrokeType(StrokeType.OUTSIDE);
        _rect_gameboard.setStroke(Color.RED);
    }

    private void setRectGameboardBindings(){
//        Bindings.bindBidirectional(_rect_gameboard.xProperty(), _gameSettingsController.getGameboardXProperty());
//        Bindings.bindBidirectional(_rect_gameboard.yProperty(), _gameSettingsController.getGameboardYProperty());
//        Bindings.bindBidirectional(_rect_gameboard.widthProperty(), _gameSettingsController.getGameboardWidthProperty());
//        Bindings.bindBidirectional(_rect_gameboard.heightProperty(), _gameSettingsController.getGameboardHeightProperty());
//
        NumberBinding strokeWidth = _imageView_yellowMinion.fitWidthProperty().divide(2);
        _rect_gameboard.strokeWidthProperty().bind(strokeWidth);
    }

    private void addObjectsToTabPane(){
        //_paneOfTab_game
        _paneOfTab_gameboard.getChildren().add(_rect_gameboard);
        _paneOfTab_banana.getChildren().add(_imageView_banana);
        _paneOfTab_goggle.getChildren().add(_imageView_goggle);
        _paneOfTab_beedo.getChildren().add(_imageView_beedo);
        //_paneOfTab_camera
    }

    private void addKeyListener(){
        _pane_GameSettingsView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (_tabs.getSelectionModel().getSelectedIndex()){
                    case 0: gameTabKeyEventHandler(event.getCode());
                        break;
                    case 1: gameboardKeyEventHandler(event.getCode());
                        break;
                    case 2: bananaKeyEventHandler(event.getCode());
                        break;
                    case 3: goggleKeyEventHandler(event.getCode());
                        break;
                    case 4: beedoKeyEventHandler(event.getCode());
                        break;
                    case 5: cameraKeyEventHandler(event.getCode());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void gameTabKeyEventHandler(KeyCode key){

    }

    private void gameboardKeyEventHandler(KeyCode key){
//        switch (key){
//            case A:
//                break;
//            case D:
//                break;
//            case S:
//                break;
//            case W:
//                break;
//        }
    }

    private void bananaKeyEventHandler(KeyCode key){
        switch (key){
            case A:
                if (_imageView_banana.getBoundsInParent().getWidth() > 50) {
                    _imageView_banana.setScaleX(_imageView_banana.getScaleX() - 0.1);
                    _imageView_banana.setScaleY(_imageView_banana.getScaleY() - 0.1);
                    _imageView_banana.setScaleZ(_imageView_banana.getScaleZ() - 0.1);
                }
                break;
            case D:
                if (_imageView_banana.getBoundsInParent().getWidth() < _rect_gameboard.getWidth()/2) {
                    _imageView_banana.setScaleX(_imageView_banana.getScaleX() + 0.1);
                    _imageView_banana.setScaleY(_imageView_banana.getScaleY() + 0.1);
                    _imageView_banana.setScaleZ(_imageView_banana.getScaleZ() + 0.1);
                }
                break;
            case S:
                if (_imageView_banana.getScaleX() > 1) {
                    _imageView_banana.setScaleX(1);
                    _imageView_banana.setScaleY(1);
                    _imageView_banana.setScaleZ(1);
                }
                //_gameSettingsController.saveGameObjectSettings();
                // TODO: show that user saved data
                break;
        }
    }

    private void goggleKeyEventHandler(KeyCode key){

    }

    private void beedoKeyEventHandler(KeyCode key){

    }

    private void cameraKeyEventHandler(KeyCode key){

    }


//    private void addMouseListenerToPane(){
//        _pane_GameSettingsView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                int x = (int) mouseEvent.getX();
//                int y = (int) mouseEvent.getY();
//
//            }
//        });
//    }


}
