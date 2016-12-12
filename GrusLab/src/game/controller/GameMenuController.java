package game.controller;


import game.gui.GuiManager;

public class GameMenuController {

    GuiManager _guiManager = new GuiManager();

    public GameMenuController(){
        _guiManager = new GuiManager();
    }

    public void gotoSettingsView(){
        _guiManager.gotoView(GuiManager.GAMESETTING_VIEW);
    }

    public void gotoGameboardView(){
        _guiManager.gotoView(GuiManager.GAMEBOARD_VIEW);
    }



}
