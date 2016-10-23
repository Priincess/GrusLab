package game.gui;

import game.Game;
import game.GameState;
import game.GameStateValue;
import game.gameboard.GameObject;
import game.gameboard.Gameboard;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;

/**
 * Created by Mark Mauerhofer on 08.10.2016.
 */
public class GameboardViewController {

    private Game game;
    private Gameboard gameboard;
    private GameState gameState;
    private GuiManager guiManager;

    private NumberStringConverter numStringConver = new NumberStringConverter();

    @FXML
    private Pane pane_GameboardView;

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
    private Pane pane_YellowMinion;
    @FXML
    private Label label_YellowMinionPoints;
    @FXML
    private Pane pane_EvilMinion;
    @FXML
    private Label label_EvilMinionPoints;
    @FXML
    private Label label_InfoText;

    private SequentialTransition gameInfoTextReadyTransition;
    private boolean isGameInfoTextReadyTransitionRunning = false;

    private SequentialTransition gameInfoTextCountdownTransition;
    private boolean isGameInfoTextCountdownTransitionRunning = false;


    public void initGameboardViewController(Game game){
        this.game = game;
        this.gameboard = game.getGameboard();
        gameState = GameState.getInstance();
        guiManager = new GuiManager();

        pane_GameboardView.setStyle("-fx-background-color: black;");
        pane_GameboardView.getChildren().add(gameboard.getRect_Gameboard());
        pane_GameboardView.getChildren().add(gameboard.getRect_GameboardCollisionBox());

        addGameObjectsListener();
        addGameTimeListener();
        addMouseListenerToPane();

        setGameboardBindings();
        setGameObjectBindings();
        setGameTextBindings();

        initGameInfoTextReady();
        initGameInfoTextCountdown();
        initGameInfoTextGameOver();

        startGameInfoTextReady();
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
        pane_YellowMinion.layoutXProperty().bind(gameboard.getRect_Gameboard().xProperty().add(10));
        NumberBinding labelGameTextPositionY = gameboard.getRect_Gameboard().yProperty().
                add(gameboard.getRect_Gameboard().heightProperty()).
                subtract(pane_YellowMinion.prefHeightProperty());
        pane_YellowMinion.layoutYProperty().bind(labelGameTextPositionY);
        pane_YellowMinion.toFront();

        pane_EvilMinion.layoutXProperty().bind(gameboard.getRect_Gameboard().xProperty().
                add(gameboard.getRect_Gameboard().widthProperty()).
                subtract(pane_EvilMinion.getPrefWidth()-30));
        pane_EvilMinion.layoutYProperty().bind(labelGameTextPositionY);
        pane_EvilMinion.toFront();

        label_Timer.layoutXProperty().bind(gameboard.getRect_Gameboard().xProperty().
                add(gameboard.getRect_Gameboard().widthProperty().divide(2)).
                subtract(label_Timer.getPrefHeight()));
        label_Timer.layoutYProperty().bind(labelGameTextPositionY);
        label_Timer.toFront();

        // Gametimer
        Bindings.bindBidirectional(label_Timer.textProperty(), game.getGameTime(), numStringConver);

        // InfoText
        label_InfoText.layoutXProperty().bind(gameboard.getRect_Gameboard().xProperty().
                add(gameboard.getRect_Gameboard().widthProperty().divide(2)).
                subtract(label_InfoText.widthProperty().divide(2)));
        label_InfoText.layoutYProperty().bind(gameboard.getRect_Gameboard().yProperty().
                add(gameboard.getRect_Gameboard().heightProperty().divide(2)).
                subtract(label_InfoText.heightProperty().divide(2)));
        label_InfoText.toFront();

        // Points
        Bindings.bindBidirectional(label_YellowMinionPoints.textProperty(), game.getPoints(0), numStringConver);
        Bindings.bindBidirectional(label_EvilMinionPoints.textProperty(), game.getPoints(1), numStringConver);
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

    private void addGameTimeListener(){
        game.getGameTime().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal,
                                          Object newVal){
                if ((int) newVal == 0 && (int) oldVal != 0){
                    gameInfoTextGameOverStatus = 0;
                    startGameInfoTextGameOver();
                }
            }
        });
    }

    private void addMouseListenerToPane(){
        pane_GameboardView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isPrimaryButtonDown() == true && gameState.getGameState() == GameStateValue.READY){
                    stopGameInfoTextReady();
                    startGameInfoTextCountdown();
                }
                if (mouseEvent.isPrimaryButtonDown() == true && gameState.getGameState() == GameStateValue.FINISHED){
                    gameState.setGameState(GameStateValue.READY);
                    stopGameInfoTextGameOver();
                    startGameInfoTextReady();
                }
                if (gameState.getGameState() == GameStateValue.PLAY) {
                    int x = (int) mouseEvent.getX() - gameboard.getMinionSize().intValue() / 2;
                    int y = (int) mouseEvent.getY() - gameboard.getMinionSize().intValue() / 2;
                    if (y > 70 && gameState.getGameState() == GameStateValue.PLAY) {
                        if (mouseEvent.isPrimaryButtonDown() == true) {
                            gameboard.setMinionPosition(0, x, y);
                        }
                        if (mouseEvent.isSecondaryButtonDown()) {
                            gameboard.setMinionPosition(1, x, y);
                        }
                    }
                }
            }
        });
    }


    private void initGameInfoTextReady(){
        gameInfoTextReadyTransition = new SequentialTransition();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), label_InfoText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.1);
        gameInfoTextReadyTransition.getChildren().add(fadeOut);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), label_InfoText);
        fadeIn.setFromValue(0.1);
        fadeIn.setToValue(1.0);

        gameInfoTextReadyTransition.getChildren().add(fadeIn);
        gameInfoTextReadyTransition.setCycleCount(SequentialTransition.INDEFINITE);
    }

    private void startGameInfoTextReady(){
        if ( isGameInfoTextReadyTransitionRunning == false) {
            resetGameInfoTextReady();
            isGameInfoTextReadyTransitionRunning = true;
            gameInfoTextReadyTransition.play();
        }
    }

    private void stopGameInfoTextReady(){
        if (gameInfoTextReadyTransition != null) {
            isGameInfoTextReadyTransitionRunning = false;
            gameInfoTextReadyTransition.stop();
        }
    }

    private void resetGameInfoTextReady(){
        label_InfoText.setText("Players Press Start!");
        label_InfoText.setTextFill(Color.WHITE);
        label_InfoText.setVisible(true);
    }


    private void initGameInfoTextCountdown(){
        gameInfoTextCountdownTransition = new SequentialTransition();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), label_InfoText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (label_InfoText.getText()) {
                    case "READY":
                        label_InfoText.setText("SET");
                        label_InfoText.setTextFill(Color.YELLOW);
                        break;
                    case "SET":
                        label_InfoText.setText("GO!");
                        label_InfoText.setTextFill(Color.GREEN);
                        game.startGame();
                        break;
                    case "GO!":
                        stopGameInfoTextCountdown();
                        break;
                }
            }
        });
        gameInfoTextCountdownTransition.getChildren().add(fadeOut);
        gameInfoTextCountdownTransition.setCycleCount(SequentialTransition.INDEFINITE);
    }

    private void startGameInfoTextCountdown(){
        if (isGameInfoTextCountdownTransitionRunning == false) {
            resetGameInfoTextCountdown();
            isGameInfoTextCountdownTransitionRunning = true;
            gameInfoTextCountdownTransition.play();
        }
    }

    private void stopGameInfoTextCountdown(){
        if (gameInfoTextCountdownTransition != null) {
            label_InfoText.setVisible(true);
            isGameInfoTextCountdownTransitionRunning = false;
            gameInfoTextCountdownTransition.stop();
        }
    }

    private void resetGameInfoTextCountdown(){
        label_InfoText.setText("READY");
        label_InfoText.setTextFill(Color.RED);
        label_InfoText.setVisible(true);
    }


    private SequentialTransition gameInfoTextGameOverTransition;
    private boolean isGameInfoTextGameOverTransitionRunning = false;
    private int gameInfoTextGameOverStatus = 0;

    private void initGameInfoTextGameOver(){
        gameInfoTextGameOverTransition = new SequentialTransition();
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), label_InfoText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (gameInfoTextGameOverStatus) {
                    case 0:
                        int x = game.whichMinionWon();
                        if ( x == 0 ) {
                            label_InfoText.setText("Minion Won!");
                            label_InfoText.setTextFill(Color.YELLOW);
                        } else if (x == 1) {
                            label_InfoText.setText("Evil Minion Won!");
                            label_InfoText.setTextFill(Color.PURPLE);
                        } else {
                            label_InfoText.setText("DRAW!");
                        }
                        gameInfoTextGameOverStatus++;
                        break;
                    case 1:
                        label_InfoText.setText("Press X or A to Restart");
                        label_InfoText.setTextFill(Color.WHITE);
                        break;
                }
            }
        });
        gameInfoTextGameOverTransition.getChildren().add(fadeOut);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), label_InfoText);
        fadeIn.setFromValue(0.1);
        fadeIn.setToValue(1.0);

        gameInfoTextGameOverTransition.getChildren().add(fadeIn);
        gameInfoTextGameOverTransition.setCycleCount(SequentialTransition.INDEFINITE);
    }

    private void startGameInfoTextGameOver(){
        if (isGameInfoTextGameOverTransitionRunning == false) {
            resetGameInfoTextGameOver();
            isGameInfoTextGameOverTransitionRunning = true;
            gameInfoTextGameOverTransition.play();
        }
    }

    private void stopGameInfoTextGameOver(){
        if (gameInfoTextGameOverTransition != null) {
            label_InfoText.setVisible(true);
            isGameInfoTextGameOverTransitionRunning = false;
            gameInfoTextGameOverTransition.stop();
        }
    }

    private void resetGameInfoTextGameOver(){
        label_InfoText.setText("GAME OVER");
        label_InfoText.setTextFill(Color.WHITE);
        label_InfoText.setVisible(true);
        gameInfoTextGameOverStatus = 0;
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


    public void saveGameboardPreferences(){
        gameboard.saveGameboardPreferences();
    }




}
