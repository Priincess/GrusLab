package game.gui;

import game.controller.ControllerManager;
import game.controller.GameSettingsController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */
public class GameSettingsViewController {
    @FXML
    private Pane _pane_GameSettingsView;
    @FXML
    private Pagination _pages;

    private Font _font;
    private ImageView _imageView_banana;
    private ImageView _imageView_goggle;
    private ImageView _imageView_beedo;
    private Rectangle _rect_gameboard;
    private Circle _circle_minion;
    private Label _label_stepInfoText;
    private Label _label_centerText;
    private IntegerProperty _gameTime;
    private IntegerProperty _goggleSpeedTime;
    private IntegerProperty _beedoStopTime;
    private IntegerProperty _cameraID;
    private StringProperty _cameraText;

    private ToggleGroup _itemDropGroup;
    private RadioButton _itemDropHigh;
    private RadioButton _itemDropNormal;
    private RadioButton _itemDropLow;
    private ItemDropRate _itemDropRate;
    
    private ImageView _imageView_step_minionSize;
    private ImageView _imageView_step_bananaSize;
    private ImageView _imageView_step_goggleSize;
    private ImageView _imageView_step_beedoSize;
    private ImageView _imageView_step_gameTime;
    private ImageView _imageView_step_itemDrop;
    private ImageView _imageView_step_goggleSpeed;
    private ImageView _imageView_step_beedoStop;
    private ImageView _imageView_step_camera;
    private ImageView _imageView_step_save;


    private GameSettingsController _gameSettingsController;
    private GuiManager _guiManager;

    @FXML
    public void initialize(){
        _gameSettingsController = ControllerManager.getGameSettingsController();
        _guiManager = new GuiManager();

        _pane_GameSettingsView.setStyle("-fx-background-color: black;");
        _font = new Font("Minion", 50);

        initPagination();
        setPageFactory();

        initCircleMinion();
        setMinionBindings();
        initItemImages();
        setImagesBindings();
        initRectGameboard();
        setRectGameboardBindings();
        initLabelStepInfo();
        initCenterText();
        setCenterPositionBindings();
        setStepViewLabelBindings();
        initItemDrop();
        setItemDropBindings();
        initPropertyBindings();

        initAdditionalImages();
        addKeyListener();
    }

    private void initCenterText(){
        _label_centerText = new Label();
        _label_centerText.setTextAlignment(TextAlignment.CENTER);
        _label_centerText.setText("Center-Text:");
        _label_centerText.setFont(_font);
        _label_centerText.setTextFill(Color.WHITE);
    }

    private void initPropertyBindings(){
        _gameTime = new SimpleIntegerProperty(_gameSettingsController.getGameTime());
        _goggleSpeedTime = new SimpleIntegerProperty(_gameSettingsController.getGoggleSpeedTime());
        _beedoStopTime = new SimpleIntegerProperty(_gameSettingsController.getBeedoStopTime());
        _cameraID = new SimpleIntegerProperty(_gameSettingsController.getCameraID());
        _cameraText = new SimpleStringProperty("No Cameras Available");
    }

    private void setCenterPositionBindings(){
        NumberBinding centerTextX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.widthProperty())).divide(2)
                .subtract(_label_centerText.widthProperty().divide(2));
        _label_centerText.layoutXProperty().bind(centerTextX);

        NumberBinding gameTimeTextY = _pane_GameSettingsView.layoutYProperty()
                .add((_pane_GameSettingsView.heightProperty())).divide(2)
                .subtract(_label_centerText.heightProperty().divide(2));
        _label_centerText.layoutYProperty().bind(gameTimeTextY);
    }

    private void initItemDrop(){
        _itemDropGroup = new ToggleGroup();
        _itemDropHigh = new RadioButton("High");
        _itemDropHigh.setFont(_font);
        _itemDropHigh.setTextFill(Color.WHITE);
        _itemDropHigh.setToggleGroup(_itemDropGroup);
        _itemDropNormal = new RadioButton("Normal");
        _itemDropNormal.setFont(_font);
        _itemDropNormal.setTextFill(Color.WHITE);
        _itemDropNormal.setToggleGroup(_itemDropGroup);
        _itemDropLow = new RadioButton("Low");
        _itemDropLow.setTextFill(Color.WHITE);
        _itemDropLow.setFont(_font);
        _itemDropLow.setToggleGroup(_itemDropGroup);

        _itemDropRate = _gameSettingsController.getItemDropRate();
        switch(_itemDropRate){
            case HIGH:
                _itemDropHigh.setSelected(true);
                break;
            case NORMAL:
                _itemDropNormal.setSelected(true);
                break;
            case LOW:
                _itemDropLow.setSelected(true);
                break;
            default:
                break;
        }
    }

    private void setItemDropBindings(){
        NumberBinding itemDropRateX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.widthProperty())).divide(2)
                .subtract(_itemDropHigh.widthProperty().divide(2));

        _itemDropHigh.layoutXProperty().bind(itemDropRateX);
        _itemDropNormal.layoutXProperty().bind(itemDropRateX);
        _itemDropLow.layoutXProperty().bind(itemDropRateX);

        NumberBinding itemDropRateY1 = _pane_GameSettingsView.layoutYProperty()
                .add((_pane_GameSettingsView.heightProperty())).divide(2)
                .subtract(_itemDropHigh.heightProperty().divide(2).multiply(3));
        NumberBinding itemDropRateY2 = itemDropRateY1.add(_itemDropHigh.heightProperty()).add(10);
        NumberBinding itemDropRateY3 = itemDropRateY2.add(_itemDropHigh.heightProperty()).add(10);

        _itemDropHigh.layoutYProperty().bind(itemDropRateY1);
        _itemDropNormal.layoutYProperty().bind(itemDropRateY2);
        _itemDropLow.layoutYProperty().bind(itemDropRateY3);


    }

    private void initLabelStepInfo(){
        _label_stepInfoText = new Label();
        _label_stepInfoText.setText("Some Info1");
        _label_stepInfoText.setFont(_font);
        _label_stepInfoText.setTextFill(Color.WHITE);

        _label_centerText = new Label();
        _label_centerText.setTextAlignment(TextAlignment.CENTER);
        _label_centerText.setText("Some Info2");
        _label_centerText.setFont(Font.font("Cambria", 50));
        _label_centerText.setTextFill(Color.WHITE);
    }

    private void setStepViewLabelBindings(){
        NumberBinding stepInfoTextX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.widthProperty())).divide(2)
                .subtract(_label_stepInfoText.widthProperty().divide(2));
        _label_stepInfoText.layoutXProperty().bind(stepInfoTextX);

        NumberBinding saveTextX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.widthProperty())).divide(2)
                .subtract(_label_centerText.widthProperty().divide(2));
        _label_centerText.layoutXProperty().bind(saveTextX);

        NumberBinding saveTextY = _pane_GameSettingsView.layoutYProperty()
                .add((_pane_GameSettingsView.heightProperty())).divide(2)
                .subtract(_label_centerText.heightProperty().divide(2));
        _label_centerText.layoutYProperty().bind(saveTextY);
    }

    private void initItemImages(){
        _imageView_banana = new ImageView(getClass().getResource("media/banana.png").toExternalForm());
        _imageView_goggle = new ImageView(getClass().getResource("media/goggles.png").toExternalForm());
        _imageView_beedo = new ImageView(getClass().getResource("media/beedo.png").toExternalForm());

        _imageView_banana.setFitWidth(_gameSettingsController.getBananaWidth());
        _imageView_banana.setFitHeight(_gameSettingsController.getBananaHeight());

        _imageView_goggle.setFitWidth(_gameSettingsController.getGoggleWidth());
        _imageView_goggle.setFitHeight(_gameSettingsController.getGoggleWidth());

        _imageView_beedo.setFitWidth(_gameSettingsController.getBeedoWidth());
        _imageView_beedo.setFitHeight(_gameSettingsController.getBeedoWidth());

        _imageView_banana.setPreserveRatio(true);
        _imageView_goggle.setPreserveRatio(true);
        _imageView_beedo.setPreserveRatio(true);
    }

    private void setImagesBindings(){
        NumberBinding bananaX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.widthProperty())).divide(2)
                .subtract(_imageView_banana.fitWidthProperty().divide(2));

        NumberBinding bananaY = _pane_GameSettingsView.layoutYProperty()
                .add((_pane_GameSettingsView.heightProperty())).divide(2)
                .subtract(_imageView_banana.fitHeightProperty().divide(2));

        NumberBinding goggleX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.widthProperty())).divide(2)
                .subtract(_imageView_goggle.fitWidthProperty().divide(2));


        NumberBinding goggleY = _pane_GameSettingsView.layoutYProperty()
                .add((_pane_GameSettingsView.heightProperty())).divide(2)
                .subtract(_imageView_goggle.fitHeightProperty().divide(3));

        NumberBinding beedoX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.widthProperty())).divide(2)
                .subtract(_imageView_beedo.fitWidthProperty().divide(2));


        NumberBinding beedoY = _pane_GameSettingsView.layoutYProperty()
                .add((_pane_GameSettingsView.heightProperty())).divide(2)
                .subtract(_imageView_beedo.fitHeightProperty().divide(2));

        _imageView_banana.xProperty().bind(bananaX);
        _imageView_banana.yProperty().bind(bananaY);
        _imageView_goggle.xProperty().bind(goggleX);
        _imageView_goggle.yProperty().bind(goggleY);
        _imageView_beedo.xProperty().bind(beedoX);
        _imageView_beedo.yProperty().bind(beedoY);
    }

    private void initAdditionalImages() {
        _imageView_step_minionSize = new ImageView(getClass().getResource("media/Step_MinionSize.png").toExternalForm());
        _imageView_step_minionSize.xProperty().bind(_pane_GameSettingsView.widthProperty().subtract(387));
        _imageView_step_minionSize.yProperty().bind(_pane_GameSettingsView.heightProperty().subtract(482));

        _imageView_step_beedoStop = new ImageView(getClass().getResource("media/Step_BeedoStop.png").toExternalForm());
        _imageView_step_beedoStop.xProperty().bind(_pane_GameSettingsView.widthProperty().subtract(338));
        _imageView_step_beedoStop.yProperty().bind(_pane_GameSettingsView.heightProperty().subtract(326));

//        _imageView_step_bananaSize;
//        _imageView_step_goggleSize;
//        _imageView_step_beedoSize;
//        _imageView_step_gameTime;
//        _imageView_step_itemDrop;
//        _imageView_step_goggleSpeed;
//        _imageView_step_beedoStop;
//        _imageView_step_camera;
//        _imageView_step_save;
//        _imageView_step_minionSize = new ImageView(getClass().getResource("media/Step_MinionSize.png").toExternalForm());
//        _imageView_step_minionSize.xProperty().bind(_pane_GameSettingsView.widthProperty().subtract(387));
//        _imageView_step_minionSize.yProperty().bind(_pane_GameSettingsView.heightProperty().subtract(482));
    }

    private void initCircleMinion(){
        _circle_minion = new Circle(0, 0 , _gameSettingsController.getMinionWidth());
        _circle_minion.setFill(Color.YELLOW);
        _circle_minion.setStroke(Color.WHITE);
        _circle_minion.setStrokeWidth(5);
    }

    private void setMinionBindings(){
        NumberBinding minionX = _pane_GameSettingsView.layoutXProperty()
                .add((_pane_GameSettingsView.widthProperty())).divide(2);

        NumberBinding minionY = _pane_GameSettingsView.layoutYProperty()
                .add((_pane_GameSettingsView.heightProperty())).divide(2);

        _circle_minion.centerXProperty().bind(minionX);
        _circle_minion.centerYProperty().bind(minionY);
    }

    private void initRectGameboard(){
        _rect_gameboard = new Rectangle();
        _rect_gameboard.setFill(Color.BLACK);
        _rect_gameboard.setStrokeType(StrokeType.OUTSIDE);
        _rect_gameboard.setStroke(Color.RED);
    }

    private void setRectGameboardBindings(){
        _rect_gameboard.setX(_gameSettingsController.getGameboardX());
        _rect_gameboard.setY(_gameSettingsController.getGameboardY());
        _rect_gameboard.setWidth(_gameSettingsController.getGameboardWidth());
        _rect_gameboard.setHeight(_gameSettingsController.getGameboardHeight());

        NumberBinding strokeWidth = _circle_minion.radiusProperty().divide(2);
        _rect_gameboard.strokeWidthProperty().bind(strokeWidth);
    }

    private void setPageFactory(){
        _pages.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                SettingSteps step = SettingSteps.getStepFromInt(pageIndex);
                switch (step){
                    case Step_MinionSize:
                        return createMinionStepView();
                    case Step_GameboardPoint:
                        return createGameboardPositionView();
                    case Step_GameboardSize:
                        return createGameboardSizeView();
                    case Step_BananaSize:
                        return createBananaStepView();
                    case Step_GoggleSize:
                        return createGoggleStepView();
                    case Step_BeedoSize:
                        return createBeedoStepView();
                    case Step_GameTime:
                        return createGameTimeView();
                    case Step_ItemDrop:
                        return createItemDropRateView();
                    case Step_GoggleSpeed:
                        return createGoggleSpeedView();
                    case Step_BeedoStop:
                        return createBeedoStopView();
                    case Step_Camera:
                        return createCameraView();
                    case Step_Save:
                        return createSaveView();
                    default:
                        break;
                }
                return null; // TODO: Page error
            }
        });
    }

    private void initPagination(){
        _pages.setMaxPageIndicatorCount(SettingSteps.values().length);
        _pages.setPageCount(SettingSteps.values().length);
    }

    private void addKeyListener(){
        _pane_GameSettingsView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()){
                    case A:
                        padLeftPressed();
                        break;
                    case S:
                        padDownPressed();
                        break;
                    case D:
                        padRightPressed();
                        break;
                    case W:
                        padUpPressed();
                        break;
                    case J:
                        buttonDownPressed();
                        break;
                    case K:
                        buttonRightPressed();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void goStepBack(){
        if (_pages.getCurrentPageIndex() > -1){
            _pages.setCurrentPageIndex(_pages.getCurrentPageIndex()-1);
        }
    }

    private void goStepForward(){
        if (_pages.getCurrentPageIndex() < _pages.getMaxPageIndicatorCount()){
            _pages.setCurrentPageIndex(_pages.getCurrentPageIndex()+1);
        }
    }

    private void padUpPressed(){
        SettingSteps step = SettingSteps.getStepFromInt(_pages.getCurrentPageIndex());
        switch (step){
            case Step_MinionSize:
                resizeMinion(5);
                break;
            case Step_GameboardPoint:
                moveGameboard(_rect_gameboard.yProperty(), -5);
                break;
            case Step_GameboardSize:
                resizeGameboard(_rect_gameboard.heightProperty(), -10);
                break;
            case Step_BananaSize:
                resizeImage(0.1, _imageView_banana);
                break;
            case Step_GoggleSize:
                resizeImage(0.1, _imageView_goggle);
                break;
            case Step_BeedoSize:
                resizeImage(0.1, _imageView_beedo);
                break;
            case Step_GameTime:
                changeValue(_gameTime, 5, 60, 3600);
                break;
            case Step_ItemDrop:
                selectDropRate(true);
                break;
            case Step_GoggleSpeed:
                changeValue(_goggleSpeedTime, 1, 5, 15);
                break;
            case Step_BeedoStop:
                changeValue(_beedoStopTime, 1, 5, 15);
                break;
            case Step_Camera:
                selectCamera(1);
            default:
                break;
        }
    }

    private void padDownPressed(){
        SettingSteps step = SettingSteps.getStepFromInt(_pages.getCurrentPageIndex());
        switch (step){
            case Step_MinionSize:
                resizeMinion(-5);
                break;
            case Step_GameboardPoint:
                moveGameboard(_rect_gameboard.yProperty(), 5);
                break;
            case Step_GameboardSize:
                resizeGameboard(_rect_gameboard.heightProperty(), 10);
                break;
            case Step_BananaSize:
                resizeImage(-0.1, _imageView_banana);
                break;
            case Step_GoggleSize:
                resizeImage(-0.1, _imageView_goggle);
                break;
            case Step_BeedoSize:
                resizeImage(-0.1, _imageView_beedo);
                break;
            case Step_GameTime:
                changeValue(_gameTime, -5, 60, 3600);
                break;
            case Step_ItemDrop:
                selectDropRate(false);
                break;
            case Step_GoggleSpeed:
                changeValue(_goggleSpeedTime, -1, 5, 15);
                break;
            case Step_BeedoStop:
                changeValue(_beedoStopTime, -1, 5, 15);
                break;
            case Step_Camera:
                selectCamera(-1);
            default:
                break;
        }
    }

    private void padLeftPressed(){
        SettingSteps step = SettingSteps.getStepFromInt(_pages.getCurrentPageIndex());
        switch (step){
            case Step_GameboardPoint:
                moveGameboard(_rect_gameboard.xProperty(), -5);
                break;
            case Step_GameboardSize:
                resizeGameboard(_rect_gameboard.widthProperty(), -10);
                break;
            default:
                break;
        }
    }

    private void padRightPressed(){
        SettingSteps step = SettingSteps.getStepFromInt(_pages.getCurrentPageIndex());
        switch (step){
            case Step_GameboardPoint:
                moveGameboard(_rect_gameboard.xProperty(), 5);
                break;
            case Step_GameboardSize:
                resizeGameboard(_rect_gameboard.widthProperty(), 10);
                break;
            default:
                break;
        }
    }

    private void buttonRightPressed(){
        SettingSteps step = SettingSteps.getStepFromInt(_pages.getCurrentPageIndex());
        switch (step){
            case Step_MinionSize:
                _guiManager.gotoView(GuiManager.GAMEMENU_VIEW);
            default:
                goStepBack();
                break;
        }
    }

    private void buttonDownPressed(){
        SettingSteps step = SettingSteps.getStepFromInt(_pages.getCurrentPageIndex());
        switch (step){
            case Step_Save:
                saveSettings();
                _guiManager.gotoView(GuiManager.GAMEMENU_VIEW);
                break;
            default:
                goStepForward();
                break;
        }
    }

    private Pane createMinionStepView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Minion Size");
        pane.getChildren().add(_label_stepInfoText);
        pane.getChildren().add(_imageView_step_minionSize);
        pane.getChildren().add(_circle_minion);
        return pane;
    }

    private Pane createGameboardPositionView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Start-Position");
        pane.getChildren().add(_rect_gameboard);
        pane.getChildren().add(_label_stepInfoText);
        return pane;
    }

    private Pane createGameboardSizeView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Size");
        pane.getChildren().add(_rect_gameboard);
        pane.getChildren().add(_label_stepInfoText);
        return pane;
    }

    private Pane createBananaStepView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Banana Size");
        pane.getChildren().add(_imageView_banana);
        pane.getChildren().add(_label_stepInfoText);
        return pane;
    }

    private Pane createGameTimeView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Game Time");
        pane.getChildren().add(_label_stepInfoText);
        pane.getChildren().add(_label_centerText);
        _label_centerText.textProperty().bind(Bindings.concat("Game-Time: ").concat(_gameTime).concat(" seconds"));
        return pane;
    }

    private Pane createGoggleStepView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Goggle Size");
        pane.getChildren().add(_imageView_goggle);
        pane.getChildren().add(_label_stepInfoText);
        return pane;
    }

    private Pane createBeedoStepView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Beedo Size");
        pane.getChildren().add(_imageView_beedo);
        pane.getChildren().add(_label_stepInfoText);
        return pane;
    }

    private Pane createItemDropRateView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Item Droprate");
        pane.getChildren().add(_label_stepInfoText);
        pane.getChildren().add(_itemDropHigh);
        pane.getChildren().add(_itemDropNormal);
        pane.getChildren().add(_itemDropLow);
        return pane;
    }

    private Pane createGoggleSpeedView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Goggle Speed-Up Time");
        pane.getChildren().add(_label_stepInfoText);
        pane.getChildren().add(_label_centerText);
        _label_centerText.textProperty().bind(Bindings.concat("Goggle Speed-Up Time ").concat(_goggleSpeedTime).concat(" seconds"));
        return pane;
    }

    private Pane createBeedoStopView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Beedo Stop-Time");
        pane.getChildren().add(_label_stepInfoText);
        pane.getChildren().add(_imageView_step_beedoStop);
        pane.getChildren().add(_label_centerText);
        _label_centerText.textProperty().bind(Bindings.concat("Beedo Stop Time ").concat(_beedoStopTime).concat(" seconds"));
        return pane;
    }

    private Pane createCameraView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Set Camera");
        pane.getChildren().add(_label_stepInfoText);
        pane.getChildren().add(_label_centerText);
        _label_centerText.textProperty().bind(Bindings.concat("Camera: ").concat(_cameraText));
        _gameSettingsController.reloadCameras();
        selectCamera(0);
        return pane;
    }

    private Pane createSaveView(){
        Pane pane = new Pane();
        _label_stepInfoText.setText("Save Preferences?");
        _label_centerText.textProperty().unbind();
        _label_centerText.setText("Save And Return ( 'X' Or 'A' )\n Go Back ( 'Circle' Or 'B' )");
        pane.getChildren().add(_label_stepInfoText);
        pane.getChildren().add(_label_centerText);
        return pane;
    }

    private void resizeMinion(int change){
        double radius = _circle_minion.getRadius() + change;
        if (radius > 50 && radius < 400) {
            _circle_minion.setRadius(radius);
        }
    }

    private void moveGameboard(DoubleProperty property, int change){
        double newVal = property.getValue() + change;
        if (newVal > 0){
            property.setValue(newVal);
        }
    }

    private void resizeGameboard(DoubleProperty property, int change){
        double newVal = property.getValue() + change;
        if (change > 0) {
            property.setValue(newVal);
        } else if (change < 0 && newVal > _circle_minion.getRadius()*6){
            property.setValue(newVal);
        }
    }

    private void resizeImage(double change, ImageView image){
        double width = image.getBoundsInParent().getWidth();
        double height = image.getBoundsInParent().getHeight();
        if (change > 0 && width < _rect_gameboard.getWidth()/3 && height < _rect_gameboard.getHeight()/3){
            image.setScaleX(image.getScaleX() + change);
            image.setScaleY(image.getScaleY() + change);
            image.setScaleZ(image.getScaleZ() + change);
        } else if (change < 0 && width > 50){
            image.setScaleX(image.getScaleX() + change);
            image.setScaleY(image.getScaleY() + change);
            image.setScaleZ(image.getScaleZ() + change);
        }
    }

    private void changeValue(IntegerProperty value, int change, int low, int high){
        int newVal = value.getValue() + change;
        if (low <= newVal && newVal <= high){
            value.setValue(newVal);
        }
    }

    private void selectDropRate(boolean isUp){
        if (isUp) {
            switch (_itemDropRate) {
                case NORMAL:
                    _itemDropRate = ItemDropRate.HIGH;
                    _itemDropHigh.setSelected(true);
                    break;
                case LOW:
                    _itemDropRate = ItemDropRate.NORMAL;
                    _itemDropNormal.setSelected(true);
                    break;
                default:
                    break;
            }
        } else {
            switch (_itemDropRate) {
                case HIGH:
                    _itemDropRate = ItemDropRate.NORMAL;
                    _itemDropNormal.setSelected(true);
                    break;
                case NORMAL:
                    _itemDropRate = ItemDropRate.LOW;
                    _itemDropLow.setSelected(true);
                    break;
                default:
                    break;
            }

        }
    }

    private void selectCamera(int change){
        int index = _cameraID.intValue() + change;
        if (index < _gameSettingsController.getCameraList().size() && index > -1){
            _cameraID.setValue(index);
            _cameraText.setValue(_gameSettingsController.getCameraList().get(index));
        }
    }


    private void saveSettings(){
        _gameSettingsController.updateMinionSettings((int) _circle_minion.getRadius());
        _gameSettingsController.updateGameboardSettings((int)_rect_gameboard.getX(),(int) _rect_gameboard.getY(),
            (int) _rect_gameboard.getWidth(), (int) _rect_gameboard.getHeight());
        _gameSettingsController.updateBananaSettings((int)_imageView_banana.getBoundsInParent().getWidth(), (int) _imageView_banana.getBoundsInParent().getHeight());
        _gameSettingsController.updateGoggleSettings((int)_imageView_goggle.getBoundsInParent().getWidth(), (int) _imageView_goggle.getBoundsInParent().getHeight());
        _gameSettingsController.updateBeedoSettings((int)_imageView_beedo.getBoundsInParent().getWidth(), (int) _imageView_beedo.getBoundsInParent().getHeight());
        _gameSettingsController.updateDropRate(_itemDropRate);
        _gameSettingsController.updateGoggleSpeedTime(_goggleSpeedTime.intValue());
        _gameSettingsController.updateBeedoStopTime(_goggleSpeedTime.intValue());
        _gameSettingsController.updateGameSettings(_gameTime.intValue());
        _gameSettingsController.updateCamera(_cameraID.intValue());
        _gameSettingsController.saveSettings();
    }
}
