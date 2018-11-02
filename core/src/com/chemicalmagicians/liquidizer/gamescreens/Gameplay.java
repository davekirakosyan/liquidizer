package com.chemicalmagicians.liquidizer.gamescreens;

import com.chemicalmagicians.liquidizer.GameScreen;
import com.chemicalmagicians.liquidizer.Liquidizer;
import com.chemicalmagicians.liquidizer.data.LevelData;
import com.chemicalmagicians.liquidizer.interfaces.IGameplay;

public class Gameplay extends GameScreen implements IGameplay {

    public Gameplay (Liquidizer liquidizer) {
        super(liquidizer);
    }

    @Override
    public void configureForData (LevelData data) {

    }

    @Override
    public void start () {

    }
}
