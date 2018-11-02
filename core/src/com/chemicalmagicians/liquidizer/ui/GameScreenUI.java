package com.chemicalmagicians.liquidizer.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class GameScreenUI extends Table {
	private Image blueFlask;
	private Image redFlask;
	public GameScreenUI(){
		blueFlask = new Image(new Texture("flask1.png"));
		redFlask = new Image(new Texture("red-flask-md.png"));
		add(elixirs());
	}

	private Table elixirs(){
		Table elixir = new Table();
		debugAll();
		for (Image flask:flasks()) {
			elixir.add(flask).size(100);
			elixir.row().padTop(20);
		}
		return elixir;
	}

	private Array<Image> flasks(){
		Array<Image> flaskArray = new Array<Image>();
		flaskArray.add(blueFlask);
		flaskArray.add(redFlask);
		return flaskArray;
	}

}
