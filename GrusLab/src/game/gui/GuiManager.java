package game.gui;

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

    public static final String GAMEMENU_VIEW = "GameMenuView.fxml";
    public static final String GAMESETTING_VIEW = "GameSettingsView.fxml";
    public static final String GAMEBOARD_VIEW = "GameboardView.fxml";

    private static Stage _stage;

    public void launchGUI(){
        launch(null);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        _stage = primaryStage;
        //_stage.setFullScreen(true);   // TODO: Uncomment
        _stage.setMaximized(true);  // TODO: Remove
        _stage.setTitle("Gru´s Lab - Immer Der Banana Nach");
        _stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        gotoView(GAMEMENU_VIEW);
    }

    public void gotoView(String viewName){
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent) loader.load(getClass().getResource(viewName).openStream());
            sceneChange(root);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void sceneChange(Parent root){
        if (_stage.getScene() == null){
            _stage.setScene(new Scene(root));
            //_stage.setFullScreen(true);   // TODO: uncomment
            _stage.setMaximized(true);      // TODO: Remove
            _stage.show();
        } else {
            _stage.getScene().setRoot(root);
        }
    }

}
