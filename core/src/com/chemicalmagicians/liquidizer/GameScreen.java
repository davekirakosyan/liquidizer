package com.chemicalmagicians.liquidizer;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class GameScreen extends Table {

    protected final Liquidizer liquidizer;

    public GameScreen (Liquidizer liquidizer) {
        this.liquidizer = liquidizer;

        setFillParent(true);
    }

}
