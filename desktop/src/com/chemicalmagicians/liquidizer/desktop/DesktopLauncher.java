package com.chemicalmagicians.liquidizer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.chemicalmagicians.liquidizer.Liquidizer;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int)((float)2048/2f);
		config.height = (int)((float)1536/2f);
		new LwjglApplication(new Liquidizer(), config);
	}
}
