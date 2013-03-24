package com.mobwin.whackem.scenes;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontUtils;

import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.ResourceManager;

public class SplashScene extends Scene {
	
	public static final String SPLASH_STRING = "HELLO SPLASH SCREEN!";
	Text mSplashSceneText;
	
	public SplashScene(Engine engine)
	{
		// Retrieve our font from the resource manager
		Font font = ResourceManager.getInstance().mFont;

		// Set the location of our splash 'image' (text object in this case).
		// We can use FontUtils.measureText to retrieve the width of our text
		// object in order to properly format its position
		float x = MainActivity.WIDTH / 2 - FontUtils.measureText(font, SPLASH_STRING) / 2;
		float y = MainActivity.HEIGHT / 2 - font.getLineHeight() / 2;

		// Create our splash screen text object
		mSplashSceneText = new Text(x, y, font, SPLASH_STRING, SPLASH_STRING.length(), engine.getVertexBufferObjectManager());
		// Attach the text object to our splash scene
		attachChild(mSplashSceneText);
	}
}
