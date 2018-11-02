package com.chemicalmagicians.liquidizer.interfaces;

import com.badlogic.gdx.utils.Array;
import com.chemicalmagicians.liquidizer.data.LevelData;
import com.chemicalmagicians.liquidizer.data.WorldData;

public interface ILevelSelection {

    Array<WorldData> getAllWorlds ();
    Array<LevelData> getAllLevels (WorldData worldData);

    void changeWorld (WorldData worldData);


    boolean isUnlocked (LevelData levelData);
    void selectLevel (LevelData levelData);


}
