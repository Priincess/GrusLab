package game.controller;

import game.player.GamepadState;
import game.player.Player;

public class GameMenuController {
    private Player _yellowPlayer;
    private Player _purplePlayer;

    public GameMenuController(Player yellowPlayer, Player purplePlayer){
        _yellowPlayer = yellowPlayer;
        _purplePlayer = purplePlayer;
    }

    public GamepadState getYellowCommand() {
        return _yellowPlayer.getControllerState();
    }

    public GamepadState getPurpleCommand() {
        return _purplePlayer.getControllerState();
    }

}
