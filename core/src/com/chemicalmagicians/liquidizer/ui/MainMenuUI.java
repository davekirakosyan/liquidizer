package com.chemicalmagicians.liquidizer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.chemicalmagicians.liquidizer.gamescreens.Gameplay;
import com.chemicalmagicians.liquidizer.gamescreens.MainMenu;

public class MainMenuUI extends Table {

	private TextureAtlas atlas;

	public MainMenuUI(){
		atlas = new TextureAtlas(Gdx.files.internal("atlas.pack"));
		addActor(leftSideTable());
		addActor(rightSideTable());
		addActor(characterTable());
	}

	public Table leftSideTable(){
		Table table = new Table();
		table.setFillParent(true);
		table.left();
		Image paper=new Image(atlas.findRegion("paper"));
		paper.scaleBy(-0.15f);
		Table paperTable = new Table();
		paperTable.add(paper).align(Align.center).padTop(40).padLeft(40);
		table.stack(new Image(atlas.findRegion("board-left")),paperTable).padTop(-150);
		return table;
	}

	public Table rightSideTable(){
		Table table = new Table();
		table.setFillParent(true);
		table.right();
		Sprite boardSprite = atlas.createSprite("board-left");
		boardSprite.flip(true,false);
		Sprite paperSprite = atlas.createSprite("paper");
		paperSprite.flip(true,false);
		Image paper = new Image(new TextureRegionDrawable(paperSprite));
		paper.scaleBy(-0.3f);
		Table paperTable = new Table();
		paperTable.add(paper).align(Align.center).padTop(20).padRight(-100);
		paperTable.row();
		Image playButton = new Image(atlas.findRegion("play-btn"));
		playButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
//				System.out.println("click");
			}
		});
		paperTable.add(playButton);
		table.stack(new Image(new TextureRegionDrawable(boardSprite)),paperTable);
		return table;
	}

	public Table characterTable(){
		Table table= new Table();
		table.setFillParent(true);
		table.bottom();
		table.add(new Image(new TextureRegionDrawable(atlas.findRegion("character"))));
		return table;
	}
}
