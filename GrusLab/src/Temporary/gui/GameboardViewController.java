package Temporary.gui;

import Temporary.controller.GameboardController;
import javafx.fxml.FXML;

/**
 * Created by Mark Mauerhofer on 06.12.2016.
 */
public class GameboardViewController {

    GameboardController _gameboardController;

    @FXML
    public void initialize(){
        _gameboardController = new GameboardController(); // TODO: getInstance from ControllerManager
    }
}
