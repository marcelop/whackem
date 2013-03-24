package com.mobwin.whackem.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontUtils;

import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.ResourceManager;

public class MainMenuScene extends Scene {
	
	public static final String MENU_STRING = "HELLO MENU SCREEN!";
	public static final String MENU_SELECT_STRING = "PRESS         TO SELECT";
	Text mMenuSceneText;
	
	public MainMenuScene(Engine engine)
	{
		// Retrieve our font from the resource manager
		Font font = ResourceManager.getInstance().mFont;

		// We must change the value of x depending on the length of our menu
		// string now in order to keep the text centered on-screen
		float x = MainActivity.WIDTH / 2 - FontUtils.measureText(font, MENU_STRING) / 2;
		float y = MainActivity.HEIGHT / 2 - font.getLineHeight() / 2;
		
		// Create our menu screen text object
		mMenuSceneText = new Text(x, y, font, MENU_STRING, MENU_STRING.length(), engine.getVertexBufferObjectManager());
		// Attach the text object to our menu scene
		attachChild(mMenuSceneText);
		
		x = MainActivity.WIDTH / 2;
		y = font.getLineHeight() / 2 + 100;
		
		final Text menuSelectText = new Text(x, y, font, MENU_SELECT_STRING, MENU_SELECT_STRING.length(), engine.getVertexBufferObjectManager());
		attachChild(menuSelectText);
		
		final Sprite a_button = new Sprite(x-30, y, 65, 80, ResourceManager.getInstance().mA_BUTTON, engine.getVertexBufferObjectManager());
		attachChild(a_button);
		
		engine.registerUpdateHandler(new TimerHandler(1, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler arg0) {
				menuSelectText.setVisible(!menuSelectText.isVisible());
				a_button.setVisible(!a_button.isVisible());
			}
		}));
	}
}
