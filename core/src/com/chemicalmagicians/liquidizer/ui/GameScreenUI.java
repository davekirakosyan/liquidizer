package com.chemicalmagicians.liquidizer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class GameScreenUI extends Table {
	private TextureAtlas atlas;
	public GameScreenUI(){
		atlas = new TextureAtlas(Gdx.files.internal("atlas.pack"));
		addActor(elixirs());
		addActor(controlBar());
	}

	private Table elixirs(){
		Table elixir = new Table();
		elixir.setFillParent(true);

		Table rightTopAlignedTable = new Table();

		for (Image flask:flasks()) {
			rightTopAlignedTable.add(flask).size(70);
			rightTopAlignedTable.row().padTop(20);
		}

		elixir.top().right();
		elixir.add(rightTopAlignedTable).pad(20);

		return elixir;
	}

	private Table controlBar(){
		Table controlBarTable = new Table();
		controlBarTable.setFillParent(true);

		Table leftTopAlignedTable = new Table();
		leftTopAlignedTable.add(new Image(atlas.findRegion("settings-button"))).size(112);
		leftTopAlignedTable.row().padTop(20);
		leftTopAlignedTable.add(new Image(atlas.findRegion("restart-button"))).size(88);

		controlBarTable.top().left();
		controlBarTable.add(leftTopAlignedTable).pad(20);

		return controlBarTable;
	}

	private Array<Image> flasks(){
		Array<Image> flaskArray = new Array<Image>();
		flaskArray.add(new Image(atlas.findRegion("flask1")));
		flaskArray.add(new Image(atlas.findRegion("red-flask-md")));
		return flaskArray;
	}

}
