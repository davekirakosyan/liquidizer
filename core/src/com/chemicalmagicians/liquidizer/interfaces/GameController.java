package com.chemicalmagicians.liquidizer.interfaces;

import com.chemicalmagicians.liquidizer.IGameController;
import com.chemicalmagicians.liquidizer.Liquidizer;

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
