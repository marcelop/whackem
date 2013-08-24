package com.mobwin.whackem.scenes;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierMatcher;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.RotationAtModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.IModifier;

import tv.ouya.console.api.OuyaController;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.Toast;

import com.mobwin.whackem.GameManager;
import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.ResourceManager;
import com.scientistsloth.whackem.R;

public class GameScene extends Scene {

	public static final String SPLASH_STRING = "HELLO GAME SCREEN!";
	private Engine mEngine;
	public Text mGameSceneText;
	public Text mGameSceneLevel;
	public Random mRand = new Random();

	Sprite mHoleSelector;
	Sprite mHoleSelectorAlpha;
	Sprite mFlowers1;
	Sprite mFlowers2;
	Sprite mMoon;
	Sprite mTree;
	Sprite mCloud1;
	Sprite mCloud2;
	
	Sprite mScoreBox;

	int selectorX = 1;
	int selectorY = 1;

	public MoleInstance[][] moles;
	public float curTimeElapsed = 0;
	private Sprite mHammer;

	public enum MoleState
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

		moles[0][0] = new MoleInstance(394,394,0);
		moles[0][1] = new MoleInstance(632,394,0);
		moles[0][2] = new MoleInstance(872,394,0);

		moles[1][0] = new MoleInstance(394,248,0);
		moles[1][1] = new MoleInstance(632,248,0);
		moles[1][2] = new MoleInstance(867,248,0);

		moles[2][0] = new MoleInstance(394,105,0);
		moles[2][1] = new MoleInstance(632,105,0);
		moles[2][2] = new MoleInstance(872,105,0);

	}

	public class MoleInstance
	{
		public MoleInstance(float i, float j, int moleType) {
			x = i;
			y = j;
			hiddenPos = y-90;
			showingPos = y+40;
			this.moleType = moleType;

			enemyMoleSprite = new AnimatedSprite(x, hiddenPos, 135, 152, ResourceManager.getInstance().mGameMole, getEngine().getVertexBufferObjectManager());
			allyMoleSprite = new AnimatedSprite(x, hiddenPos, 135, 152, ResourceManager.getInstance().mGameMoleGlasses, getEngine().getVertexBufferObjectManager());
			
			if (moleType == 0) {//regular mole
				moleSprite = enemyMoleSprite;
			}
			else {//mole with glasses
				moleSprite = allyMoleSprite;
			}
		}

		public int getMoleType() {
			return moleType;
		}
		
		public void setMoleType(int moleType) {
			this.moleType = moleType;
			if (moleType == 0) //regular mole
			{
				moleSprite = enemyMoleSprite;
			}
			else { //mole with glasses
				moleSprite = allyMoleSprite;
			}
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

		public void makeMoleClimb()
		{
			if(state == MoleState.HIDDEN && GameManager.getInstance().isInGame() && GameManager.getInstance().canMoleClimb())
			{
				float delay = 2 - (float)Math.log(GameManager.getInstance().getCurrentLevel()*10)/10;
				GameManager.getInstance().decrementMoleCount();
				moleSprite.animate(new long[1], new int[1], false);
				moleSprite.registerEntityModifier(new SequenceEntityModifier(
						new MoveModifier(0.3f, moleSprite.getX(), moleSprite.getY(), moleSprite.getX(), showingPos, new IEntityModifierListener() {

							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
								state = MoleState.CLIMBING;
								registerEntityModifier(new DelayModifier(0.25f, new IEntityModifierListener() {
									@Override
									public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
									@Override
									public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
										forceUpdateSelectorAlpha();	
									}
								}));
							}

							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								state = MoleState.VULNERABLE;
								if(mRand.nextInt() > 0)
									animMoleLaugh();
							}
						}), 
						new DelayModifier(delay, new IEntityModifierListener() {
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
						GameManager.getInstance().decrementMolesUp();
						if (moleType == 0)
							GameManager.getInstance().incrementMissedMoleCount();

						registerEntityModifier(new DelayModifier(0.05f, new IEntityModifierListener() {
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								forceUpdateSelectorAlpha();	
								setMoleType(0); //once done hiding, turn into enemy
							}
						}));
					}

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						state = MoleState.HIDDEN;
						updateSelectorAlpha();
					}
				}));
		}

		void makeMoleDie()
		{
			if(state == MoleState.VULNERABLE)
			{
				ResourceManager.getInstance().mHitSound.play();
				moleSprite.registerEntityModifier(new SequenceEntityModifier(
						new DelayModifier(0.2f, new IEntityModifierListener() {
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
								state = MoleState.HIT;
								GameManager.getInstance().decrementMolesUp();
								animMoleDie();
								if (moleType == 0) {
									GameManager.getInstance().incrementMoleHitCount();
									GameManager.getInstance().incrementScore(10);
								}
								else
									GameManager.getInstance().incrementMissedMoleCount();
								registerEntityModifier(new DelayModifier(0.25f, new IEntityModifierListener() {
									@Override
									public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
									@Override
									public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
										setMoleType(0); //once done hiding, turn into enemy
										forceUpdateSelectorAlpha();	
									}
								}));
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
								updateSelectorAlpha();
							}
						})));
			}
		}
		
		public void moveToHiddenPosition()
		{
			moleSprite.registerEntityModifier(new MoveModifier(0.2f, moleSprite.getX(), moleSprite.getY(), moleSprite.getX(), hiddenPos));
			state = MoleState.HIDDEN;
		}

		float x;
		float y;
		float hiddenPos;
		float showingPos;
		int moleType;

		public AnimatedSprite moleSprite, allyMoleSprite, enemyMoleSprite;
		public MoleState state = MoleState.HIDDEN;

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

		//Landscape
		final Sprite background = new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT - MainActivity.HEIGHT/6, MainActivity.WIDTH, MainActivity.HEIGHT/3, ResourceManager.getInstance().mGameBackgroundTextureRegion, mEngine.getVertexBufferObjectManager());
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


		//Hole Selectors
		mHoleSelector = new Sprite(627, 248, 260, 148, ResourceManager.getInstance().mGameHoleSelector, mEngine.getVertexBufferObjectManager());
		attachChild(mHoleSelector);
		mHoleSelectorAlpha = new Sprite(627, 248, 260, 148, ResourceManager.getInstance().mGameHoleSelectorAlpha, mEngine.getVertexBufferObjectManager());
		mHoleSelectorAlpha.setAlpha(0);
		attachChild(mHoleSelectorAlpha);


		//Add background details
		mMoon = new Sprite(1200, 660, ResourceManager.getInstance().mGameMoon.getWidth()/2,ResourceManager.getInstance().mGameMoon.getHeight()/2,ResourceManager.getInstance().mGameMoon, mEngine.getVertexBufferObjectManager());
		mCloud1 = new Sprite(300, 680, ResourceManager.getInstance().mGameCloud1.getWidth()/3, ResourceManager.getInstance().mGameCloud1.getHeight()/3, ResourceManager.getInstance().mGameCloud1, mEngine.getVertexBufferObjectManager());
		mCloud2 = new Sprite(800, 660, ResourceManager.getInstance().mGameCloud2.getWidth()/3, ResourceManager.getInstance().mGameCloud2.getHeight()/3, ResourceManager.getInstance().mGameCloud2, mEngine.getVertexBufferObjectManager());
		mTree = new Sprite(1130, 720-270, ResourceManager.getInstance().mGameTree, mEngine.getVertexBufferObjectManager());
		mFlowers1 = new Sprite(200, 300, ResourceManager.getInstance().mGameFlowers, mEngine.getVertexBufferObjectManager());
		mFlowers2 = new Sprite(1130, 150, ResourceManager.getInstance().mGameFlowers, mEngine.getVertexBufferObjectManager());
		attachChild(mMoon);
		attachChild(mCloud1);
		attachChild(mCloud2);
		attachChild(mTree);
		attachChild(mFlowers1);
		attachChild(mFlowers2);

		placeBackgroundDetails();

		
		//Create our Hammer
		mHammer = new Sprite(1130, 150, ResourceManager.getInstance().mGameHammer, mEngine.getVertexBufferObjectManager());
		mHammer.setRotationCenter(mHammer.getRotationCenterX(), mHammer.getRotationCenterY()*0.5f);
		mHammer.setScale(0.75f);
		attachChild(mHammer);
		
		Entity scoreGroup = new Entity();
		mScoreBox = new Sprite(70, 180, ResourceManager.getInstance().mScoreBox, mEngine.getVertexBufferObjectManager());
		mScoreBox.setAlpha(0.6f);
		scoreGroup.attachChild(mScoreBox);
		
		// Create our score text object
		mGameSceneText = new Text(70, 180, font, SPLASH_STRING, 200, mEngine.getVertexBufferObjectManager());
		// Attach the score text object to our scene
		scoreGroup.attachChild(mGameSceneText);
		
		attachChild(scoreGroup);
		scoreGroup.registerEntityModifier(new MoveModifier(.5f, -140, 0, 70, 0));
		
		mGameSceneLevel = new Text(x, MainActivity.HEIGHT/2, font, "LEVEL 1", 200, mEngine.getVertexBufferObjectManager());
		mGameSceneLevel.setColor(Color.BLUE);
		mGameSceneLevel.setVisible(false);
		attachChild(mGameSceneLevel);

	}

	private void placeBackgroundDetails() {

		mCloud1.clearEntityModifiers();
		mCloud2.clearEntityModifiers();

		mCloud1.registerEntityModifier(new MoveByModifier(180, 2000, 0));
		mCloud2.registerEntityModifier(new MoveByModifier(180, 1800, 0));
		mCloud1.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(mCloud1.getX() > 1500)
				{
					mCloud1.clearEntityModifiers();
					mCloud1.setX(-mCloud1.getWidth());
					mCloud1.registerEntityModifier(new MoveByModifier(180, 2000, 0));
				}
					
				if(mCloud2.getX() > 1500)
				{
					mCloud2.clearEntityModifiers();
					mCloud2.setX(-mCloud2.getWidth());
					mCloud2.registerEntityModifier(new MoveByModifier(180, 1800, 0));
				}
			}
		});
		

	}

	public synchronized void onKeyDown(int keyCode, KeyEvent event) {
		boolean updated = false;
		switch (keyCode) {
		case OuyaController.BUTTON_DPAD_DOWN:
			if(selectorY <= 1)
			{
				selectorY++;
				updated = true;
			}
			break;
		case OuyaController.BUTTON_DPAD_UP:
			if(selectorY >= 1)
			{
				selectorY--;
				updated = true;
			}
			break;
		case OuyaController.BUTTON_DPAD_RIGHT:
			if(selectorX <= 1)
			{
				selectorX++;
				updated = true;
			}
			break;
		case OuyaController.BUTTON_DPAD_LEFT:
			if(selectorX >= 1)
			{
				selectorX--;
				updated = true;
			}
			break;

		case OuyaController.BUTTON_A:
			//moles[Math.abs(mRand.nextInt())%3][Math.abs(mRand.nextInt())%3].makeMoleClimb(); 
			break;

		case OuyaController.BUTTON_O:
			moles[selectorY][selectorX].makeMoleDie();
			GameManager.getInstance().incrementHitCount();
			ResourceManager.getInstance().mHammerSound.play();
			animateHammerHit();
			break;

		default:
			break;
		}

		if(updated)
			moveSelectorToNewPosition();

	}

	public synchronized void onKeyUp(int keyCode, KeyEvent event) {
		boolean updated = false;
		switch (keyCode) {
		case OuyaController.BUTTON_DPAD_DOWN:
			if(selectorY >= 1)
			{
				selectorY--;
				updated = true;
			}
			break;
		case OuyaController.BUTTON_DPAD_UP:
			if(selectorY <= 1)
			{
				selectorY++;
				updated = true;
			}
			break;
		case OuyaController.BUTTON_DPAD_RIGHT:
			if(selectorX >= 1)
			{
				selectorX--;
				updated = true;
			}
			break;
		case OuyaController.BUTTON_DPAD_LEFT:
			if(selectorX <= 1)
			{
				selectorX++;
				updated = true;
			}
			break;

		default:
			break;
		}

		if(updated)
			moveSelectorToNewPosition();

	}

	private void moveSelectorToNewPosition() {
		float newX = moles[selectorY][selectorX].x-5;
		float newY = moles[selectorY][selectorX].y;

		mHoleSelector.clearEntityModifiers();
		mHoleSelectorAlpha.clearEntityModifiers();
		mHoleSelector.registerEntityModifier(new MoveModifier(0.1f ,mHoleSelector.getX(), mHoleSelector.getY(), newX, newY ));
		mHoleSelectorAlpha.registerEntityModifier(new MoveModifier(0.1f ,mHoleSelector.getX(), mHoleSelector.getY(), newX, newY ));

		updateSelectorAlpha(); 
		
		//Move the hammer with the selector
		mHammer.unregisterEntityModifiers(new IEntityModifierMatcher() {
			@Override
			public boolean matches(IModifier<IEntity> pObject) {
				if (pObject.getClass() == MoveModifier.class)
					return true;
				return false;
			}
		});
		mHammer.registerEntityModifier(new MoveModifier(0.1f ,mHammer.getX(), mHammer.getY(), newX + mHammer.getWidth()*0.4f, newY + mHammer.getHeight()*0.2f));
	}


	private void forceUpdateSelectorAlpha() {
		if(moles[selectorY][selectorX].state == MoleState.CLIMBING)
		{
			if(mHoleSelector.getAlpha() != 0 || mHoleSelectorAlpha.getAlpha() != 1)
			{
				mHoleSelector.registerEntityModifier(new AlphaModifier(0.1f, mHoleSelector.getAlpha(), 0));
				mHoleSelectorAlpha.registerEntityModifier(new AlphaModifier(0.1f, mHoleSelector.getAlpha(), 1));
			}
		}
		else if(moles[selectorY][selectorX].state == MoleState.HIT || moles[selectorY][selectorX].state == MoleState.HIDING)
		{
			if(mHoleSelector.getAlpha() != 1 || mHoleSelectorAlpha.getAlpha() != 0)
			{
				mHoleSelector.registerEntityModifier(new AlphaModifier(0.1f, mHoleSelector.getAlpha(), 1));
				mHoleSelectorAlpha.registerEntityModifier(new AlphaModifier(0.1f, mHoleSelector.getAlpha(), 0));
			}
		}
	}

	private void updateSelectorAlpha() {
		if(moles[selectorY][selectorX].state != MoleState.HIDDEN)
		{
			if(mHoleSelector.getAlpha() != 0 || mHoleSelectorAlpha.getAlpha() != 1)
			{
				mHoleSelector.registerEntityModifier(new AlphaModifier(0.1f, mHoleSelector.getAlpha(), 0));
				mHoleSelectorAlpha.registerEntityModifier(new AlphaModifier(0.1f, mHoleSelector.getAlpha(), 1));
			}
		}
		else
		{
			if(mHoleSelector.getAlpha() != 1 || mHoleSelectorAlpha.getAlpha() != 0)
			{
				mHoleSelector.registerEntityModifier(new AlphaModifier(0.1f, mHoleSelector.getAlpha(), 1));
				mHoleSelectorAlpha.registerEntityModifier(new AlphaModifier(0.1f, mHoleSelector.getAlpha(), 0));
			}
		}
	}

	private void animateHammerHit()
	{
		final float hitDuration = 0.05f;
		final float backDuration = 0.1f;
		mHammer.registerEntityModifier(new DelayModifier(hitDuration+backDuration, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				mHammer.registerEntityModifier(new RotationAtModifier(hitDuration, mHammer.getRotation(), -50, mHammer.getRotationCenterX(), mHammer.getRotationCenterY()));
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				mHammer.registerEntityModifier(new RotationAtModifier(backDuration, mHammer.getRotation(), 0, mHammer.getRotationCenterX(), mHammer.getRotationCenterY()));				
			}
		}));
	}
	
	@Override
	public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
		animateHammerHit();
		return true;
	}

	public Engine getEngine() {
		return mEngine;
	}

	
	void purchaseGame(){
		
	}
	
	public void showPurchaseDialog(){ 
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.activity);

		// Setting Dialog Title
		alertDialog.setTitle("Funding the Fun");

		// Setting Dialog Message
		alertDialog.setMessage("Hope you liked whacking around! To continue playing, please unlock the game.");

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("Unlock",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog
				Toast.makeText(MainActivity.activity.getApplicationContext(),
						"You clicked on Unlock", Toast.LENGTH_SHORT)
						.show();
			}
		});
		// Setting Negative "NO" Btn
		alertDialog.setNegativeButton("Back",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog
				Toast.makeText(MainActivity.activity.getApplicationContext(),
						"You clicked on Back", Toast.LENGTH_SHORT)
						.show();
				dialog.cancel();
			}
		});

		// Showing Alert Dialog
		alertDialog.show();
	}
}
