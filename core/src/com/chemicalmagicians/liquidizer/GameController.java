package com.chemicalmagicians.liquidizer;

import com.chemicalmagicians.liquidizer.Liquidizer;
import com.chemicalmagicians.liquidizer.gamescreens.MainMenu;
import com.chemicalmagicians.liquidizer.interfaces.IGameController;

public class GameController implements IGameController {

    private final Liquidizer liquidizer;

    public GameController (Liquidizer liquidizer) {
        this.liquidizer = liquidizer;
    }


    @Override
    public void showSettings () {

    }

    @Override
    public void showMainMenu () {

    }

    @Override
    public void showLevelSelect () {

    }

    @Override
    public void showGame () {

    }
}
