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
import com.chemicalmagicians.liquidizer.interfaces.GameController;

public class Liquidizer extends ApplicationAdapter {

	private Stage stage;
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
		ExtendViewport extendViewport = new ExtendViewport(1920, 1080, 2000, 1920, camera);
		stage = new Stage(extendViewport, batch);

		//Load and init

		//load

		//init
		mainMenu = new MainMenu(this);
		levelSelection = new LevelSelection(this);
		gameplay = new Gameplay(this);

		gameController = new GameController(this);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
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
