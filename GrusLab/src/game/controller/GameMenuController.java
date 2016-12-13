package game.controller;

import game.player.GamepadState;
import game.player.Player;

public class GameMenuController {
    private Player _yellowPlayer;
    private Player _purplePlayer;   // TODO: Both Players necessary

    public GameMenuController(Player yellowPlayer, Player purplePlayer){
        _yellowPlayer = yellowPlayer;
        _purplePlayer = purplePlayer;
    }

    public GamepadState getYellowCommand() {
        if (_yellowPlayer.hasControllerChanged()) {
            GamepadState state = _yellowPlayer.getControllerState();
            _yellowPlayer.resetControllerChanged();
            return state;
        }
        return GamepadState.None;
    }

}
