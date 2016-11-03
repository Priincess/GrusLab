package game.gui;

import game.Game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by Mark Mauerhofer on 23.10.2016.
 */
public class GuiManager extends Application{

    private StageManager stageManager;
    private Game _game;

    public GuiManager(Game game){
        stageManager = StageManager.getInstance();
        _game = game;
    }

    public void launchGUI(){
        launch(null);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        stageManager.setPrimaryStage(primaryStage);
        stageManager.getPrimaryStage().setFullScreen(true);
        stageManager.getPrimaryStage().setTitle("Gru´s Lab - Immer Der Banana Nach");
        stageManager.getPrimaryStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        //gotoGameStartView();
        gotoGameboardView();
    }

    public void gotoGameStartView(){

        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent) loader.load(getClass().getResource("GameStartView.fxml").openStream());
            sceneChange(root);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void gotoGameboardView(){
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent) loader.load(getClass().getResource("GameboardView.fxml").openStream());
            GameboardViewController controller = loader.getController();
            controller.initGameboardViewController(_game);
            sceneChange(root);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void sceneChange(Parent root){
        if (stageManager.getPrimaryStage().getScene() == null){
            stageManager.getPrimaryStage().setScene(new Scene(root));
            stageManager.getPrimaryStage().setFullScreen(true);
            stageManager.getPrimaryStage().show();
        } else {
            stageManager.getPrimaryStage().getScene().setRoot(root);
        }
    }

}
