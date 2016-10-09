package Game;

import Game.Gameboard.GameboardViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main {

	public static void main(String[] args) {
		while(true){
			CentralControl.getInstance().startGame();
		}
	}

//	public class Main extends Application {
//
//		public static void main(String[] args) {
//			//launch(args);
//		}
//
//
//	// TODO: Move to right place
//	@Override
//	public void start(Stage primaryStage) throws Exception{
//
//		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Game/Gameboard/GameboardView.fxml"));
//		Parent root = (Parent) fxmlLoader.load();
//		primaryStage.setTitle("Grus Lab - Immer Der Banana Nach");
//		Scene scene = new Scene(root, 1280, 720);
//		primaryStage.setScene(scene);
//		primaryStage.show();
//	}

}
