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

    private static Stage _stage;
    private static Game _game;

    public void launchGUI(){
        launch(null);
    }

    public void setGame(Game game){
        _game = game;
    }

    public Game getGame(){
        return _game;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        _stage = primaryStage;
        //_stage.setFullScreen(true);
        _stage.setMaximized(true);
        _stage.setTitle("Gru´s Lab - Immer Der Banana Nach");
        _stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        gotoGameStartView();
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
        if (_stage.getScene() == null){
            _stage.setScene(new Scene(root));
            //_stage.setFullScreen(true);
            _stage.setMaximized(true);
            _stage.show();
        } else {
            _stage.getScene().setRoot(root);
        }
    }

}
