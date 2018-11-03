package com.chemicalmagicians.liquidizer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class GameScreenUI extends Table {
	private TextureAtlas atlas;
	private InputMultiplexer inputMultiplexer = new InputMultiplexer();
	int indexOfImageArray;


	public GameScreenUI(){
		atlas = new TextureAtlas(Gdx.files.internal("atlas.pack"));
		addActor(elixirs());
		addActor(controlBar());
//		inputMultiplexer.addProcessor(getStage());
//		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	private Table elixirs(){
		Table elixir = new Table();
		elixir.setFillParent(true);


		Table rightTopAlignedTable = new Table();

		rightTopAlignedTable.setTouchable(Touchable.enabled);
		for (indexOfImageArray=0;indexOfImageArray<flasks().size;indexOfImageArray++) {
			final Image flask=flasks().get(indexOfImageArray);
			flask.scaleBy(-0.2f);
			rightTopAlignedTable.add(flask);
			flask.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println("asaa");
				}
			});
			rightTopAlignedTable.row().padTop(-10);
		}
		elixir.top().right();
		elixir.add(rightTopAlignedTable);

		return elixir;
	}

	private Table controlBar(){
		Table controlBarTable = new Table();
		controlBarTable.setFillParent(true);

		Table leftTopAlignedTable = new Table();
		leftTopAlignedTable.setTouchable(Touchable.enabled);
		leftTopAlignedTable.add(new Image(atlas.findRegion("settings-button"))).size(112);
		leftTopAlignedTable.row().padTop(20);
		leftTopAlignedTable.add(new Image(atlas.findRegion("restart-button"))).size(88);

		controlBarTable.top().left();
		controlBarTable.add(leftTopAlignedTable).pad(20);
		return controlBarTable;
	}

	private Array<Image> flasks(){
		Array<Image> flaskArray = new Array<Image>();
		flaskArray.add(new Image(atlas.findRegion("red")));
		flaskArray.add(new Image(atlas.findRegion("green")));
		flaskArray.add(new Image(atlas.findRegion("blue")));
		flaskArray.add(new Image(atlas.findRegion("yellow")));
		flaskArray.add(new Image(atlas.findRegion("purple")));
		flaskArray.add(new Image(atlas.findRegion("orange")));
		return flaskArray;
	}

}
