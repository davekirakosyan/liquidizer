package com.chemicalmagicians.liquidizer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.chemicalmagicians.liquidizer.Liquidizer;
import com.chemicalmagicians.liquidizer.ParticleActor;
import com.chemicalmagicians.liquidizer.gamescreens.Gameplay;

public class MainMenuUI extends Table {

	private TextureAtlas atlas;
	public Gameplay lvl1Gameplay;
	public Gameplay lvl2Gameplay;
	Liquidizer liquidizer;

	public MainMenuUI(Liquidizer liquidizer){
		lvl1Gameplay = new Gameplay(liquidizer, 1, "No Mixing", new Color[] {Color.RED, Color.BLUE}, Color.PURPLE, false, 20);
		lvl2Gameplay = new Gameplay(liquidizer, 2, "Mix red, yellow. No green", new Color[] {Color.RED, Color.YELLOW, Color.GREEN}, Color.ORANGE, true, 20);
		this.liquidizer = liquidizer;
		atlas = new TextureAtlas(Gdx.files.internal("atlas.pack"));
		addActor(characterTable());
		addActor(leftSideTable());
		addActor(rightSideTable());

		ParticleEffect blueFogParticle = new ParticleEffect();
		blueFogParticle.load(Gdx.files.internal("particles/blue-fog.p"), Gdx.files.internal("particles/textures"));
		ParticleActor gasParticleActor = new ParticleActor(blueFogParticle);
		gasParticleActor.startEffect();
		gasParticleActor.setPosition(0, Gdx.graphics.getHeight());
		addActor(gasParticleActor);

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
				lvl1Gameplay.start();
				getStage().addActor(lvl1Gameplay);
				liquidizer.isGameOn = true;
			}
		});
		paperTable.add(playButton);
		table.stack(new Image(new TextureRegionDrawable(boardSprite)),paperTable);

		ParticleEffect violetGasParticle = new ParticleEffect();
		violetGasParticle.load(Gdx.files.internal("particles/violet-gas.p"), Gdx.files.internal("particles/textures"));
		ParticleActor gasParticleActor = new ParticleActor(violetGasParticle);
		gasParticleActor.startEffect();
		table.add(gasParticleActor).right().bottom().padBottom(-100);

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
