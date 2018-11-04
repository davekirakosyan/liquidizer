package com.chemicalmagicians.liquidizer.gamescreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.chemicalmagicians.liquidizer.GameScreen;
import com.chemicalmagicians.liquidizer.Liquidizer;
import com.chemicalmagicians.liquidizer.interfaces.IMainMenu;
import com.chemicalmagicians.liquidizer.ui.MainMenuUI;

public class MainMenu extends GameScreen implements IMainMenu {
    private MainMenuUI mainMenuUI;
    private TextureAtlas atlas;

    public MainMenu (Liquidizer liquidizer) {
        super(liquidizer);
        atlas=new TextureAtlas(Gdx.files.internal("atlas.pack"));
        mainMenuUI=new MainMenuUI();
        setBackground(new TextureRegionDrawable(atlas.findRegion("backgraund-game-start-menu")));
        add(mainMenuUI).grow();
    }


    public void start() {

        System.out.println("started");
    }
}
