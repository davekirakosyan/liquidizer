package com.chemicalmagicians.liquidizer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.chemicalmagicians.liquidizer.gamescreens.Gameplay;
import com.chemicalmagicians.liquidizer.gamescreens.LevelSelection;
import com.chemicalmagicians.liquidizer.gamescreens.MainMenu;
import com.chemicalmagicians.liquidizer.interfaces.GameController;

public class Liquidizer extends ApplicationAdapter {

	public Stage stage;
	private PolygonSpriteBatch batch;


	private IGameController gameController;

	//Menus

	private MainMenu mainMenu;
	private LevelSelection levelSelection;
	private Gameplay gameplay;

	@Override
	public void create () {
		OrthographicCamera camera = new OrthographicCamera();
		batch = new PolygonSpriteBatch();
		ExtendViewport extendViewport = new ExtendViewport(1280,720, 4000, 1920, camera);
		stage = new Stage(extendViewport, batch);
		Gdx.input.setInputProcessor(stage);	// todo:  KAREVOR TOX
		//Load and init

		//load

		//init
		mainMenu = new MainMenu(this);
		levelSelection = new LevelSelection(this);

		gameController = new GameController(this);

		gameplay = new Gameplay(this);
		gameplay.start();
		stage.addActor(gameplay);

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameplay.render();

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
