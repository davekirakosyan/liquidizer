package com.chemicalmagicians.liquidizer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.chemicalmagicians.liquidizer.gamescreens.Gameplay;
import com.chemicalmagicians.liquidizer.gamescreens.LevelSelection;
import com.chemicalmagicians.liquidizer.gamescreens.MainMenu;
import com.chemicalmagicians.liquidizer.interfaces.IGameController;
import com.chemicalmagicians.liquidizer.ui.WinLoseUI;
import com.sun.scenario.Settings;

public class Liquidizer extends ApplicationAdapter {

	public Stage stage;
	private PolygonSpriteBatch batch;

	private IGameController gameController;

	//Menus
	public MainMenu mainMenu;
	private WinLoseUI winLoseUI;
	private LevelSelection levelSelection;
	public Gameplay gameplay;
	public static int currLvl;

	@Override
	public void create () {
		OrthographicCamera camera = new OrthographicCamera();
		batch = new PolygonSpriteBatch();
		ExtendViewport extendViewport = new ExtendViewport(1280,720, 4000, 1920, camera);
		stage = new Stage(extendViewport, batch);
		Gdx.input.setInputProcessor(stage);

		winLoseUI=new WinLoseUI(this);
		stage.addActor(winLoseUI.failTable());
		mainMenu = new MainMenu(this);
		stage.addActor(mainMenu);
		levelSelection = new LevelSelection(this);

		gameController = new GameController(this);
	}

	public boolean isGameOn = false;
	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(isGameOn) {
			if(currLvl == 1) {
				mainMenu.mainMenuUI.lvl1Gameplay.render();
			}
			else if(currLvl == 2) {
				mainMenu.mainMenuUI.lvl2Gameplay.render();
			}
		}

		stage.act();
		stage.draw();
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose () {
		stage.dispose();
		batch.dispose();
	}

	public IGameController getGameController () {
		return gameController;
	}

}
