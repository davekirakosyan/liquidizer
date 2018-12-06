package com.chemicalmagicians.liquidizer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.chemicalmagicians.liquidizer.Liquidizer;
import com.chemicalmagicians.liquidizer.ParticleActor;
import com.chemicalmagicians.liquidizer.gamescreens.Gameplay;

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
		rightTopAlignedTable.setTouchable(Touchable.enabled);

		for(int i=0; i<Gameplay.currentLvlElixirColors.length; i++) {
			if (Gameplay.currentLvlElixirColors[i]==Color.RED) {
				Image redFlask=flasks().get(0);
				rightTopAlignedTable.add(redFlask);
				redFlask.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						Gameplay.currentUsingColor = Color.RED;
						startTouchParticle();
                    }
				});
				rightTopAlignedTable.row().padTop(-10);

				redFlask.scaleBy(-0.2f);
			} else if(Gameplay.currentLvlElixirColors[i]==Color.GREEN) {
				Image greenFlask=flasks().get(1);
				rightTopAlignedTable.add(greenFlask);
				greenFlask.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						Gameplay.currentUsingColor = Color.GREEN;
						startTouchParticle();
					}
				});
				rightTopAlignedTable.row().padTop(-10);
				greenFlask.scaleBy(-0.2f);
			} else if(Gameplay.currentLvlElixirColors[i]==Color.BLUE) {
				Image blueFlask=flasks().get(2);
				rightTopAlignedTable.add(blueFlask);
				blueFlask.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						Gameplay.currentUsingColor = Color.BLUE;
						startTouchParticle();
					}
				});
				rightTopAlignedTable.row().padTop(-10);
				blueFlask.scaleBy(-0.2f);
			} else if(Gameplay.currentLvlElixirColors[i]==Color.YELLOW) {
				Image yellowFlask=flasks().get(3);
				rightTopAlignedTable.add(yellowFlask);
				yellowFlask.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						Gameplay.currentUsingColor = Color.YELLOW;
						startTouchParticle();
					}
				});
				rightTopAlignedTable.row().padTop(-10);
				yellowFlask.scaleBy(-0.2f);
			} else if(Gameplay.currentLvlElixirColors[i]==Color.PURPLE) {
				Image purpleFlask=flasks().get(4);
				rightTopAlignedTable.add(purpleFlask);
				purpleFlask.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						Gameplay.currentUsingColor = Color.PURPLE;
						startTouchParticle();
					}
				});
				rightTopAlignedTable.row().padTop(-10);
				purpleFlask.scaleBy(-0.2f);
			} else if(Gameplay.currentLvlElixirColors[i]==Color.ORANGE) {
				Image orangeFlask=flasks().get(5);
				rightTopAlignedTable.add(orangeFlask);
				orangeFlask.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						Gameplay.currentUsingColor = Color.ORANGE;
						startTouchParticle();
					}
				});
				rightTopAlignedTable.row().padTop(-10);
				orangeFlask.scaleBy(-0.2f);
			}
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
		Image restartBtn = new Image(atlas.findRegion("restart-button"));
		restartBtn.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
//				Gameplay.startLvl2 = true;
			}
		});
		leftTopAlignedTable.add(restartBtn).size(88);

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

	public void startTouchParticle() {
		ParticleEffect touchParticle = new ParticleEffect();
		touchParticle.load(Gdx.files.internal("particles/touch.p"), Gdx.files.internal("particles/textures"));
		ParticleActor touchParticleActor = new ParticleActor(touchParticle);
		touchParticleActor.startEffect();
		touchParticleActor.setPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
		addActor(touchParticleActor);
	}


}
