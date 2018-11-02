package com.chemicalmagicians.liquidizer.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

	public Table elixirs(){
		Table elixir = new Table();
		elixir.setTouchable(Touchable.enabled);
		debugAll();
		for (Image flask:flasks()) {
			elixir.add(flask).size(100);
//			flask.addListener(new ClickListener(){
//				@Override
//				public void clicked(InputEvent event, float x, float y) {
//					System.out.println("I got clicked!");
//				}
//			});
			elixir.row().padTop(20);
		}
		return elixir;
	}

	public Array<Image> flasks(){
		Array<Image> flaskArray = new Array<Image>();
		flaskArray.add(blueFlask);
		flaskArray.add(redFlask);
		return flaskArray;
	}

}
