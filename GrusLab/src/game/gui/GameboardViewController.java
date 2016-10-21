package game.gui;

import game.gameboard.GameObject;
import game.gameboard.Gameboard;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.converter.NumberStringConverter;

/**
 * Created by Mark Mauerhofer on 08.10.2016.
 */
public class GameboardViewController {

    private Gameboard gameboard;

    NumberStringConverter numStringConver = new NumberStringConverter();

    @FXML
    private Pane pane_GameboardView;

    @FXML
    private TextField textField_Timer;

    @FXML
    private TextField textField_GameboardX;
    @FXML
    private TextField textField_GameboardY;
    @FXML
    private TextField textField_GameboardWidth;
    @FXML
    private TextField textField_GameboardHeight;
    @FXML
    private TextField textField_RedzoneWidth;


    @FXML
    private TextField textField_MinionSize;
    @FXML
    private TextField textField_ItemSize;
    @FXML
    private TextField textField_GameObjectDistance;

    @FXML
    private Label label_Timer;
    @FXML
    private Pane pane_Player1;
    @FXML
    private Label label_Player1Points;
    @FXML
    private Pane pane_Player2;
    @FXML
    private Label label_Player2Points;

    public void initGameboardViewController(Gameboard gameboard){
        this.gameboard = gameboard;
        pane_GameboardView.setStyle("-fx-background-color: black;");
        pane_GameboardView.getChildren().add(gameboard.getRect_Gameboard());
        pane_GameboardView.getChildren().add(gameboard.getRect_GameboardCollisionBox());

        addGameObjectsListener();
        addMouseListenerToPane();

        setGameboardBindings();
        setGameObjectBindings();
        setGameTextBindings();
    }

    private void setGameboardBindings(){
        Rectangle temp = gameboard.getRect_Gameboard();
        Bindings.bindBidirectional(textField_GameboardX.textProperty(), temp.xProperty(), numStringConver);
        Bindings.bindBidirectional(textField_GameboardY.textProperty(), temp.yProperty(), numStringConver);
        Bindings.bindBidirectional(textField_GameboardWidth.textProperty(), temp.widthProperty(), numStringConver);
        Bindings.bindBidirectional(textField_GameboardHeight.textProperty(), temp.heightProperty(), numStringConver);
        Bindings.bindBidirectional(textField_RedzoneWidth.textProperty(), temp.strokeWidthProperty(), numStringConver);
    }

    private void setGameObjectBindings(){
        Bindings.bindBidirectional(textField_MinionSize.textProperty(), gameboard.getMinionSize(), numStringConver);
        Bindings.bindBidirectional(textField_ItemSize.textProperty(), gameboard.getItemSize(), numStringConver);
        Bindings.bindBidirectional(textField_GameObjectDistance.textProperty(), gameboard.getGameObjectDistance(), numStringConver);
    }

    private void setGameTextBindings(){
        //Binding Gametext Position
        pane_Player1.layoutXProperty().bind(gameboard.getRect_Gameboard().xProperty().add(10));
        NumberBinding labelGameTextPositionY = gameboard.getRect_Gameboard().yProperty().
                add(gameboard.getRect_Gameboard().heightProperty()).
                subtract(pane_Player1.prefHeightProperty());
        pane_Player1.layoutYProperty().bind(labelGameTextPositionY);
        pane_Player1.toFront();

        pane_Player2.layoutXProperty().bind(gameboard.getRect_Gameboard().xProperty().
                add(gameboard.getRect_Gameboard().widthProperty()).
                subtract(pane_Player2.getPrefWidth()-20));
        pane_Player2.layoutYProperty().bind(labelGameTextPositionY);
        pane_Player2.toFront();

        label_Timer.layoutXProperty().bind(gameboard.getRect_Gameboard().xProperty().
                add(gameboard.getRect_Gameboard().widthProperty().divide(2)).
                subtract(label_Timer.getPrefHeight()));
        label_Timer.layoutYProperty().bind(labelGameTextPositionY);
        label_Timer.toFront();

    }


    private void addGameObjectsListener(){
        // Load and add GameObjects already in List...
        for (GameObject gameObject : gameboard.getGameObjects()){
            pane_GameboardView.getChildren().add(gameObject.getImageView());
        }

        gameboard.getGameObjects().addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change){
                while (change.next()){
                    if (change.wasAdded() == true){
                        for (int i = 0; i < change.getAddedSize(); i++){
                            GameObject gameObject = (GameObject) change.getAddedSubList().get(i);
                            pane_GameboardView.getChildren().add(gameObject.getImageView());
                        }
                    }
                    if (change.wasRemoved()){
                        for (int i = 0; i < change.getRemovedSize(); i++){
                            GameObject gameObject = (GameObject) change.getRemoved().get(i);
                            pane_GameboardView.getChildren().remove(gameObject.getImageView());
                        }
                    }

                }
            }
        });
    }

    private void addMouseListenerToPane(){
        pane_GameboardView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int x = (int) mouseEvent.getX() - gameboard.getMinionSize().intValue()/2;
                int y = (int) mouseEvent.getY() - gameboard.getMinionSize().intValue()/2;
                if (y > 70) {
                    if (mouseEvent.isPrimaryButtonDown() == true) {
                        gameboard.setMinionPosition(0, x, y);
                        gameboard.updateGameRoutine();
                    }
                    if (mouseEvent.isSecondaryButtonDown()) {
                        gameboard.setMinionPosition(1, x, y);
                        gameboard.updateGameRoutine();
                    }
                }
            }
        });
    }

    public void toggleCollisionBox(){
        Rectangle temp = gameboard.getRect_GameboardCollisionBox();
        temp.setVisible(!temp.isVisible());
    }


    public void generateBanana(){
        gameboard.generateBanana();
    }

    public void generateBeedo(){
        gameboard.generateBeedo();
    }

    public void generateGoggles(){
        gameboard.generateGoggles();
    }

    public void loadGameSetup(){
        gameboard.gameStartSetup();
        Bindings.bindBidirectional(label_Player1Points.textProperty(), gameboard.getPointsOf(0), numStringConver);  // TODO: Make it in a better Way
        Bindings.bindBidirectional(label_Player2Points.textProperty(), gameboard.getPointsOf(1), numStringConver);  // TODO: Make it in a better Way
    }

    public void saveGameboardPreferences(){
        gameboard.saveGameboardPreferences();
    }




}
