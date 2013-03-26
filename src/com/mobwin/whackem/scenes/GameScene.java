package com.mobwin.whackem.scenes;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontUtils;
import org.apache.http.HeaderElementIterator;

import tv.ouya.console.api.OuyaController;

import android.view.View.OnKeyListener;

import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.ResourceManager;

public class GameScene extends Scene {
	
	public static final String SPLASH_STRING = "HELLO GAME SCREEN!";
	Text mGameSceneText;
	
	public GameScene(Engine engine)
	{

	}
	
	public void populate(Engine mEngine)
	{
		// Retrieve our font from the resource manager
		Font font = ResourceManager.getInstance().mFont;

		// Set the location of our splash 'image' (text object in this case).
		// We can use FontUtils.measureText to retrieve the width of our text
		// object in order to properly format its position
		float x = MainActivity.WIDTH / 2;
		float y = MainActivity.HEIGHT - font.getLineHeight() / 2 - 20;
		
		
		//Build the background
		
		//dirt 
		attachChild(new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT/4, 2*MainActivity.WIDTH/3, 2*MainActivity.HEIGHT/2, ResourceManager.getInstance().mGameDirtRegion, mEngine.getVertexBufferObjectManager()));

		//Layer 4
		attachChild(new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT/2, MainActivity.WIDTH, MainActivity.HEIGHT, ResourceManager.getInstance().mGameHolesRegion4, mEngine.getVertexBufferObjectManager()));
		
		final Sprite background = new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT - MainActivity.HEIGHT/8, MainActivity.WIDTH, MainActivity.HEIGHT/4, ResourceManager.getInstance().mGameBackgroundTextureRegion, mEngine.getVertexBufferObjectManager());
		attachChild(background);

		//Layer 3
		attachChild(new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT/2, MainActivity.WIDTH, MainActivity.HEIGHT, ResourceManager.getInstance().mGameHolesRegion3, mEngine.getVertexBufferObjectManager()));

		//Layer 2
		attachChild(new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT/2, MainActivity.WIDTH, MainActivity.HEIGHT, ResourceManager.getInstance().mGameHolesRegion2, mEngine.getVertexBufferObjectManager()));

		//Layer 1
		attachChild(new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT/2, MainActivity.WIDTH, MainActivity.HEIGHT, ResourceManager.getInstance().mGameHolesRegion1, mEngine.getVertexBufferObjectManager()));
		
		
//		attachChild(new Sprite(MainActivity.WIDTH / 2, 384/2, 2*MainActivity.WIDTH/3, 384/2, ResourceManager.getInstance().mGameHolesUpRegion, mEngine.getVertexBufferObjectManager()));
//		attachChild(new Sprite(MainActivity.WIDTH / 2, 0, 2*MainActivity.WIDTH/3, 384/2, ResourceManager.getInstance().mGameHolesDownRegion, mEngine.getVertexBufferObjectManager()));
		
		// Create our splash screen text object
		mGameSceneText = new Text(x, y, font, SPLASH_STRING, SPLASH_STRING.length(), mEngine.getVertexBufferObjectManager());
		// Attach the text object to our splash scene
		attachChild(mGameSceneText);
		
	}
	
}
