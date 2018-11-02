package com.chemicalmagicians.liquidizer.gamescreens;

import com.badlogic.gdx.utils.Array;
import com.chemicalmagicians.liquidizer.GameScreen;
import com.chemicalmagicians.liquidizer.Liquidizer;
import com.chemicalmagicians.liquidizer.data.LevelData;
import com.chemicalmagicians.liquidizer.data.WorldData;
import com.chemicalmagicians.liquidizer.interfaces.ILevelSelection;

public class LevelSelection extends GameScreen implements ILevelSelection {

    public LevelSelection (Liquidizer liquidizer) {
        super(liquidizer);
    }

    @Override
    public Array<WorldData> getAllWorlds () {
        return null;
    }

    @Override
    public Array<LevelData> getAllLevels (WorldData worldData) {
        return null;
    }

    @Override
    public void changeWorld (WorldData worldData) {

    }

    @Override
    public boolean isUnlocked (LevelData levelData) {
        return false;
    }

    @Override
    public void selectLevel (LevelData levelData) {

    }
}
