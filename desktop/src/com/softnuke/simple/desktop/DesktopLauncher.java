package com.softnuke.simple.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.softnuke.simple.MyGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//HD
		config.width = 1280;
		config.height = 720;
		
		//config.width = 960;
		//config.height =540;
		
		//FULLHD
		//config.width = 1920;
		//config.height = 1080;
		
		//config.fullscreen = true;
		
		config.title = "Fly!";
		config.addIcon("icon.png", Files.FileType.Internal);
		
		new LwjglApplication(new MyGame(), config);
	}
}
