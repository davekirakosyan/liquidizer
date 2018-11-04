package com.chemicalmagicians.liquidizer.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.chemicalmagicians.liquidizer.Liquidizer;
import javafx.scene.control.Tab;

public class WinLoseUI extends Table {

	Liquidizer liquidizer;

	public WinLoseUI(Liquidizer liquidizer){
		this.liquidizer=liquidizer;
	}

	public Table winTable(){
		Table table=new Table();
		return table;
	}
}
