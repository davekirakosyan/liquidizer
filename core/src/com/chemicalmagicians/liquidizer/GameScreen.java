package com.chemicalmagicians.liquidizer;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.chemicalmagicians.liquidizer.ui.GameScreenUI;

public abstract class GameScreen extends Table {

    protected final Liquidizer liquidizer;

    public GameScreen (Liquidizer liquidizer) {
        this.liquidizer = liquidizer;

        setFillParent(true);
    }

}
