package com.mobwin.whackem.scenes;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontUtils;
import org.apache.http.HeaderElementIterator;

import tv.ouya.console.api.OuyaController;

import android.view.KeyEvent;
import android.view.View.OnKeyListener;

import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.ResourceManager;

public class GameScene extends Scene {
	
	public static final String SPLASH_STRING = "HELLO GAME SCREEN!";
	Text mGameSceneText;
	Sprite mHoleSelector;
	
	boolean downPressed = false;
	boolean upPressed = false;
	boolean rightPressed = false;
	boolean leftPressed = false;
	
	int selectorX = 1;
	int selectorY = 1;
	
	MoleData[][] moles;
	
	public GameScene(Engine engine)
	{
		moles = new MoleData[3][];
		for (int i = 0; i < moles.length; i++) {
			moles[i] = new MoleData[3];
		}
		
		moles[0][0] = new MoleData(389,394);
		moles[0][1] = new MoleData(627,394);
		moles[0][2] = new MoleData(867,394);
		
		moles[1][0] = new MoleData(389,248);
		moles[1][1] = new MoleData(627,248);
		moles[1][2] = new MoleData(867,248);
		
		moles[2][0] = new MoleData(389,105);
		moles[2][1] = new MoleData(627,105);
		moles[2][2] = new MoleData(867,105);
		
		
	}
	
	class MoleData
	{
		public MoleData(float i, float j) {
			x = i;
			y = j;
		}
		
		float x;
		float y;
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
		

		//Hole Selector
		mHoleSelector = new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT/2, 260, 148, ResourceManager.getInstance().mGameHoleSelector, mEngine.getVertexBufferObjectManager());
		attachChild(mHoleSelector);
		
//		attachChild(new Sprite(MainActivity.WIDTH / 2, 384/2, 2*MainActivity.WIDTH/3, 384/2, ResourceManager.getInstance().mGameHolesUpRegion, mEngine.getVertexBufferObjectManager()));
//		attachChild(new Sprite(MainActivity.WIDTH / 2, 0, 2*MainActivity.WIDTH/3, 384/2, ResourceManager.getInstance().mGameHolesDownRegion, mEngine.getVertexBufferObjectManager()));
		
		// Create our splash screen text object
		mGameSceneText = new Text(x, y, font, SPLASH_STRING, SPLASH_STRING.length(), mEngine.getVertexBufferObjectManager());
		// Attach the text object to our splash scene
		attachChild(mGameSceneText);
		
	}
	
//	@Override
//	public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
//		if(pSceneTouchEvent.isActionDown())
//		{
//			float x = pSceneTouchEvent.getX();
//			float y = pSceneTouchEvent.getY();
//
//			mHoleSelector.setPosition(x, y);
//			mGameSceneText.setText(x + " x " + y);
//		}
//		return true;
//	}

	public synchronized void onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case OuyaController.BUTTON_DPAD_DOWN:
			if(selectorY <= 1)
				selectorY++;
			break;
		case OuyaController.BUTTON_DPAD_UP:
			if(selectorY >= 1)
				selectorY--;
			break;
		case OuyaController.BUTTON_DPAD_RIGHT:
			if(selectorX <= 1)
				selectorX++;
			break;
		case OuyaController.BUTTON_DPAD_LEFT:
			if(selectorX >= 1)
				selectorX--;
			break;

		default:
			break;
		}

		moveSelectorToNewPosition();

	}

	public synchronized void onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case OuyaController.BUTTON_DPAD_DOWN:
			if(selectorY >= 1)
				selectorY--;
			break;
		case OuyaController.BUTTON_DPAD_UP:
			if(selectorY <= 1)
				selectorY++;
			break;
		case OuyaController.BUTTON_DPAD_RIGHT:
			if(selectorX >= 1)
				selectorX--;
			break;
		case OuyaController.BUTTON_DPAD_LEFT:
			if(selectorX <= 1)
				selectorX++;
			break;

		default:
			break;
		}
		
		moveSelectorToNewPosition();
		
	}

	private void moveSelectorToNewPosition() {
		float newX = moles[selectorY][selectorX].x;
		float newY = moles[selectorY][selectorX].y;
		
		mHoleSelector.clearEntityModifiers();
		mHoleSelector.registerEntityModifier(new MoveModifier(0.1f ,mHoleSelector.getX(), mHoleSelector.getY(), newX, newY ));
		}
	
	
}
