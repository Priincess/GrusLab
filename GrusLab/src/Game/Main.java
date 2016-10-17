package Game;

import Game.Gameboard.Gameboard;
import Game.Gameboard.GameboardViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main {

	public static void main(String[] args) {
		while(true){
			CentralControl.getInstance().startGame();
		}
	}

//	public class Main extends Application {
//
//		public static void main(String[] args) {
//			launch(args);
//		}
//
//
//	// TODO: Move to right place
//	@Override
//	public void start(Stage primaryStage) throws Exception{
//
//        Gameboard gameboard = new Gameboard();
//
//		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Game/Gameboard/GameboardView.fxml"));
//		Parent root = (Parent) fxmlLoader.load();
//        GameboardViewController controller = fxmlLoader.getController();
//
//        controller.initGameboardViewController(gameboard);
//
//		primaryStage.setTitle("Grus Lab - Immer Der Banana Nach");
//		Scene scene = new Scene(root, 1280, 720);
//		primaryStage.setScene(scene);
//		primaryStage.setMaximized(true);
//		primaryStage.show();
//
//        // TODO: close all threads...
//        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent t) {
//                Platform.exit();
//                System.exit(0);
//            }
//        });
//	}


}
