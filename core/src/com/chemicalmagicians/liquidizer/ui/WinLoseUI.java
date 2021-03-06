package com.chemicalmagicians.liquidizer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.chemicalmagicians.liquidizer.Liquidizer;
import com.chemicalmagicians.liquidizer.gamescreens.Gameplay;

public class WinLoseUI extends Table {

	Liquidizer liquidizer;
	private TextureAtlas atlas;

	public WinLoseUI(Liquidizer liquidizer){
		this.liquidizer=liquidizer;
		atlas = new TextureAtlas(Gdx.files.internal("atlas.pack"));
	}

	public Table winTable(){
		Table table=new Table();
		table.setFillParent(true);
		Image boardImage = new Image(atlas.findRegion("board-lvl-menu"));
		Image character = new Image(atlas.findRegion("half-character"));
		Image winPaper = new Image(atlas.findRegion("paper-win"));
		Image refreshButton = new Image(atlas.findRegion("restart-button"));
		Image nextLevel = new Image(atlas.findRegion("left-right-arrow"));
		nextLevel.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(Liquidizer.currLvl == 1) {
					liquidizer.mainMenu.mainMenuUI.lvl1Gameplay.remove();
					liquidizer.mainMenu.mainMenuUI.lvl2Gameplay.start();
					liquidizer.stage.addActor(liquidizer.mainMenu.mainMenuUI.lvl2Gameplay);
					liquidizer.isGameOn = true;
				}
				else if(Liquidizer.currLvl == 2) {
					liquidizer.mainMenu.mainMenuUI.lvl2Gameplay.remove();
					liquidizer.mainMenu.mainMenuUI.lvl3Gameplay.start();
					liquidizer.stage.addActor(liquidizer.mainMenu.mainMenuUI.lvl3Gameplay);
					liquidizer.isGameOn = true;
				}
			}
		});
		refreshButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				restartLevel();
			}
		});
		Table boardTable = new Table();
		Table winPaperTable =new Table();
		boardTable.setFillParent(true);
		boardTable.right().top();
		winPaper.setScaling(Scaling.none);
		winPaperTable.add(winPaper).padBottom(-50);
		boardTable.stack(boardImage,winPaperTable);
		Table bottomUIPanel = new Table();
		bottomUIPanel.setFillParent(true);
		bottomUIPanel.add(refreshButton).padBottom(-350).padLeft(500);
		bottomUIPanel.add().width(20);
		bottomUIPanel.add(nextLevel).padBottom(-350);
		table.addActor(boardTable);
		table.addActor(character);
		table.addActor(bottomUIPanel);
		return table;
	}

	public Table failTable(){
		Table table=new Table();
		table.setFillParent(true);
		Image boardImage = new Image(atlas.findRegion("board-lvl-menu"));
		Image character = new Image(atlas.findRegion("half-character"));
		Image winPaper = new Image(atlas.findRegion("paper-fail"));
		Image refreshButton = new Image(atlas.findRegion("restart-button"));
		refreshButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				restartLevel();
			}
		});
		Table boardTable = new Table();
		Table winPaperTable =new Table();
		boardTable.setFillParent(true);
		boardTable.right().top();
		winPaper.setScaling(Scaling.none);
		winPaperTable.add(winPaper).padBottom(-50);
		boardTable.stack(boardImage,winPaperTable);
		Table bottomUIPanel = new Table();
		bottomUIPanel.setFillParent(true);
		bottomUIPanel.add(refreshButton).padBottom(-350).padLeft(500);
		bottomUIPanel.add().width(20);
		table.addActor(boardTable);
		table.addActor(character);
		table.addActor(bottomUIPanel);
		return table;
	}

	public void restartLevel() {
		if(Liquidizer.currLvl == 1) {
			System.out.println("restart lvl 1");
			liquidizer.mainMenu.mainMenuUI.lvl1Gameplay = new Gameplay(liquidizer, 1, "No Mixing", new Color[] {Color.RED, Color.BLUE}, Color.PURPLE, false, 20);
			liquidizer.mainMenu.mainMenuUI.lvl1Gameplay.start();
			liquidizer.stage.addActor(liquidizer.mainMenu.mainMenuUI.lvl1Gameplay);
			liquidizer.isGameOn = true;
		} else if(Liquidizer.currLvl == 2) {
			System.out.println("restart lvl 2");
			liquidizer.mainMenu.mainMenuUI.lvl2Gameplay = new Gameplay(liquidizer, 2, "Mix red, yellow. No green", new Color[] {Color.RED, Color.YELLOW, Color.GREEN}, Color.ORANGE, true, 20);
			liquidizer.mainMenu.mainMenuUI.lvl2Gameplay.start();
			liquidizer.stage.addActor(liquidizer.mainMenu.mainMenuUI.lvl2Gameplay);
			liquidizer.isGameOn = true;
		}  else if(Liquidizer.currLvl == 3) {
			System.out.println("restart lvl 3");
			liquidizer.mainMenu.mainMenuUI.lvl3Gameplay = new Gameplay(liquidizer, 3, "Mix red, blue. No yellow, green", new Color[] {Color.RED, Color.YELLOW, Color.GREEN, Color.ORANGE, Color.BLUE}, Color.PURPLE, true, 20);
			liquidizer.mainMenu.mainMenuUI.lvl3Gameplay.start();
			liquidizer.stage.addActor(liquidizer.mainMenu.mainMenuUI.lvl3Gameplay);
			liquidizer.isGameOn = true;
		}
	}
}
