package com.mobwin.whackem.scenes;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.util.modifier.IModifier;

import tv.ouya.console.api.OuyaController;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.ResourceManager;

public class GameScene extends Scene {
	
	public static final String SPLASH_STRING = "HELLO GAME SCREEN!";
	Engine mEngine;
	Text mGameSceneText;
	Sprite mHoleSelector;
	Random mRand = new Random();
	
	boolean downPressed = false;
	boolean upPressed = false;
	boolean rightPressed = false;
	boolean leftPressed = false;
	
	int selectorX = 1;
	int selectorY = 1;
	
	MoleInstance[][] moles;
	protected float curTimeElapsed = 0;
	
	enum MoleState
	{
		HIDDEN,
		CLIMBING,
		VULNERABLE,
		HIT,
		HIDING
	}
	
	
	
	public GameScene(Engine engine)
	{
		mEngine = engine;
		moles = new MoleInstance[3][];
		for (int i = 0; i < moles.length; i++) {
			moles[i] = new MoleInstance[3];
		}
		
		moles[0][0] = new MoleInstance(394,394);
		moles[0][1] = new MoleInstance(632,394);
		moles[0][2] = new MoleInstance(872,394);
		
		moles[1][0] = new MoleInstance(394,248);
		moles[1][1] = new MoleInstance(632,248);
		moles[1][2] = new MoleInstance(867,248);
		
		moles[2][0] = new MoleInstance(394,105);
		moles[2][1] = new MoleInstance(632,105);
		moles[2][2] = new MoleInstance(872,105);
		
		
	}
	
	class MoleInstance
	{
		public MoleInstance(float i, float j) {
			x = i;
			y = j;
			hiddenPos = y-90;
			showingPos = y+40;
			
			moleSprite = new AnimatedSprite(x, hiddenPos, 135, 152, ResourceManager.getInstance().mGameMole, mEngine.getVertexBufferObjectManager());
		}
		
		void animMoleLaugh()
		{
			int[] frames = {0,1,2,3,2,3,2,1,0};
			long[] durations = new long[frames.length];
			for (int i = 0; i < durations.length; i++) 
				durations[i] = 100;
			moleSprite.animate(durations, frames, false);
		}
		
		void animMoleDie()
		{
			int[] frames = {4,5,6,7,6,7};
			long[] durations = new long[frames.length];
			for (int i = 0; i < durations.length; i++) 
				durations[i] = 50;
			moleSprite.animate(durations, frames, false);
		}
		
		void makeMoleClimb()
		{
			if(state == MoleState.HIDDEN)
			{
				moleSprite.animate(new long[1], new int[1], false);
				moleSprite.registerEntityModifier(new SequenceEntityModifier(
						new MoveModifier(0.3f, moleSprite.getX(), moleSprite.getY(), moleSprite.getX(), showingPos, new IEntityModifierListener() {

							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
								state = MoleState.CLIMBING;
							}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								state = MoleState.VULNERABLE;
								if(mRand.nextInt() > 0)
									animMoleLaugh();
							}
						}), 
						new DelayModifier(2, new IEntityModifierListener() {
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
							}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								makeMoleHide();					
							}
						})
						));
			}
		}
		
		void makeMoleHide()
		{
			if(state == MoleState.VULNERABLE)
				moleSprite.registerEntityModifier(new MoveModifier(0.3f, moleSprite.getX(), moleSprite.getY(), moleSprite.getX(), hiddenPos, new IEntityModifierListener() {

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
						state = MoleState.HIDING;
					}

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						state = MoleState.HIDDEN;
					}
				}));
		}
		
		void makeMoleDie()
		{
			if(state == MoleState.VULNERABLE)
				moleSprite.registerEntityModifier(new SequenceEntityModifier(
						new DelayModifier(0.2f, new IEntityModifierListener() {
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
								state = MoleState.HIT;
								animMoleDie();
							}
							
							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
							}
						}),
						new MoveModifier(0.3f, moleSprite.getX(), moleSprite.getY(), moleSprite.getX(), hiddenPos, new IEntityModifierListener() {

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					}

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						state = MoleState.HIDDEN;
					}
				})));
		}
		
		float x;
		float y;
		float hiddenPos;
		float showingPos;
		
		AnimatedSprite moleSprite;
		MoleState state = MoleState.HIDDEN;
		
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
		attachChild(new Sprite((MainActivity.WIDTH / 2)-7, MainActivity.HEIGHT/4, 642, 2*MainActivity.HEIGHT/2, ResourceManager.getInstance().mGameDirtRegion, mEngine.getVertexBufferObjectManager()));

		//Layer 4
		attachChild(new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT/2, MainActivity.WIDTH, MainActivity.HEIGHT, ResourceManager.getInstance().mGameHolesRegion4, mEngine.getVertexBufferObjectManager()));
		
		final Sprite background = new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT - MainActivity.HEIGHT/8, MainActivity.WIDTH, MainActivity.HEIGHT/4, ResourceManager.getInstance().mGameBackgroundTextureRegion, mEngine.getVertexBufferObjectManager());
		attachChild(background);

		for (MoleInstance data : moles[0])
			attachChild(data.moleSprite);
		
		//dirt 
		attachChild(new Sprite((MainActivity.WIDTH / 2)-7, (MainActivity.HEIGHT/4)-180, 642, 2*MainActivity.HEIGHT/2, ResourceManager.getInstance().mGameDirtRegion, mEngine.getVertexBufferObjectManager()));
		
		//Layer 3
		attachChild(new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT/2, MainActivity.WIDTH, MainActivity.HEIGHT, ResourceManager.getInstance().mGameHolesRegion3, mEngine.getVertexBufferObjectManager()));
		
		for (MoleInstance data : moles[1])
			attachChild(data.moleSprite);
		
		//dirt 
		attachChild(new Sprite((MainActivity.WIDTH / 2)-7, (MainActivity.HEIGHT/4)-380, 642, 2*MainActivity.HEIGHT/2, ResourceManager.getInstance().mGameDirtRegion, mEngine.getVertexBufferObjectManager()));

		//Layer 2
		attachChild(new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT/2, MainActivity.WIDTH, MainActivity.HEIGHT, ResourceManager.getInstance().mGameHolesRegion2, mEngine.getVertexBufferObjectManager()));
		
		for (MoleInstance data : moles[2])
			attachChild(data.moleSprite);

		//Layer 1
		attachChild(new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT/2, MainActivity.WIDTH, MainActivity.HEIGHT, ResourceManager.getInstance().mGameHolesRegion1, mEngine.getVertexBufferObjectManager()));
		

		//Hole Selector
		mHoleSelector = new Sprite(627, 248, 260, 148, ResourceManager.getInstance().mGameHoleSelector, mEngine.getVertexBufferObjectManager());
		attachChild(mHoleSelector);
		
		// Create our splash screen text object
		mGameSceneText = new Text(x, y, font, SPLASH_STRING, SPLASH_STRING.length(), mEngine.getVertexBufferObjectManager());
		// Attach the text object to our splash scene
		attachChild(mGameSceneText);
		
		mEngine.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				curTimeElapsed  += pSecondsElapsed;
				if (curTimeElapsed >= 3)
				{
					moles[Math.abs(mRand.nextInt())%3][Math.abs(mRand.nextInt())%3].makeMoleClimb(); 
					curTimeElapsed = 0;
				}
				
			}
		});
		
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

		case OuyaController.BUTTON_A:
			moles[Math.abs(mRand.nextInt())%3][Math.abs(mRand.nextInt())%3].makeMoleClimb(); 
			break;
			
		case OuyaController.BUTTON_O:
			moles[selectorY][selectorX].makeMoleDie();
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
		float newX = moles[selectorY][selectorX].x-5;
		float newY = moles[selectorY][selectorX].y;
		
		mHoleSelector.clearEntityModifiers();
		mHoleSelector.registerEntityModifier(new MoveModifier(0.1f ,mHoleSelector.getX(), mHoleSelector.getY(), newX, newY ));
		}
	
	
}
