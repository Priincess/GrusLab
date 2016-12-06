package Temporary;/**
 * Created by Mark Mauerhofer on 04.12.2016.
 */

import Temporary.controller.GameSettingsController;
import Temporary.gui.GameSettingsViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SettingsTestApplication extends Application {
    Stage _stage;

    public static void main(String[] args) {
        launch(args);
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
        gotoGameSettingsView();
    }

    public void gotoGameSettingsView(){
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = (Parent) loader.load(getClass().getResource("gui/GameSettingsView.fxml").openStream());
            //GameSettingsViewController controller = loader.getController();
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
