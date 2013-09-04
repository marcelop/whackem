package com.scientistsloth.whackem.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SkewModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseElasticOut;

import tv.ouya.console.api.OuyaFacade;
import android.app.AlertDialog;
import android.view.KeyEvent;

import com.scientistsloth.whackem.GameManager;
import com.scientistsloth.whackem.MainActivity;
import com.scientistsloth.whackem.MenuBuilder;
import com.scientistsloth.whackem.MenuItem;
import com.scientistsloth.whackem.MenuItem.IMenuHandler;
import com.scientistsloth.whackem.ResourceManager;
import com.scientistsloth.whackem.UserData;

public class MainMenuScene extends Scene {

	Text mHighScoreText;
	Sprite logoSprite;
	Sprite buttonSprite;
	MenuBuilder menu;
	private Sprite backgroundSprite;
	private Entity menuLayer;
	private boolean isMenuActivated = false;
	

	public MainMenuScene(final Engine engine)
	{
		// Retrieve our font from the resource manager
		Font font = ResourceManager.getInstance().mFont;

		float x = MainActivity.WIDTH / 2;
		float y = font.getLineHeight() / 2 + 100;
				
		
		//Make a temporary menu
		
		int menuSize = 3;
		if (OuyaFacade.getInstance().isRunningOnOUYAHardware() && 
				!UserData.getInstance().isGameUnlocked())
			menuSize = 4;

		MenuItem[] items = new MenuItem[menuSize];
		items[0] = new MenuItem("Start Game");
		
		items[0].registerHandler(new IMenuHandler() {

			@Override
			public void onChange(MenuItem sender, int selected) {
			}

			@Override
			public void onClick(MenuItem sender) {
				if(GameManager.getInstance().isMusicEnabled())
				{
					ResourceManager.getInstance().mIntroMusic.pause();
				}
				engine.setScene(new TutorialScene());
				
			}
		});
		
		
		items[1] = new MenuItem("Music", true);
		items[1].registerHandler(new IMenuHandler() {
			
			@Override
			public void onClick(MenuItem sender) {
				GameManager.getInstance().toggleMusic();
				if(GameManager.getInstance().isMusicEnabled())
				{
					ResourceManager.getInstance().mIntroMusic.play();			
					sender.setCheckMarkVisible(true);
				}
				else
				{
					ResourceManager.getInstance().mIntroMusic.pause();
					sender.setCheckMarkVisible(false);
				}
			}
			
			@Override
			public void onChange(MenuItem sender, int selected) {
			}
		});
		
		//items[2] = new MenuItem("Sound Effects", new String[] {"ON","OFF"});
		
		if (OuyaFacade.getInstance().isRunningOnOUYAHardware() && 
				!UserData.getInstance().isGameUnlocked())
		{ 
			items[2] = new MenuItem("UNLOCK GAME");
			items[2].registerHandler(new IMenuHandler() {
				
				@Override
				public void onClick(MenuItem sender) {
					if(!UserData.getInstance().isGameUnlocked())
					{
						try {
							GameManager.getInstance().requestPurchase();
						} catch (Exception e) {
							e.printStackTrace();
							showPurchaseFailedAlert();
							// If failed because products list was empty, try to get it again
							if(GameManager.mGameUnlockProduct == null)
								GameManager.getInstance().requestProductsList();
						}
					}
				}
				
				@Override
				public void onChange(MenuItem sender, int selected) {}
			});
		}
	
		items[menuSize-1] = new MenuItem("Credits");
		
		items[menuSize-1].registerHandler(new IMenuHandler() {

			@Override
			public void onChange(MenuItem sender, int selected) {
			}

			@Override
			public void onClick(MenuItem sender) {
	    		//ResourceManager.getInstance().mIntroMusic.pause();
	    		//ResourceManager.getInstance().mGameMusic.play();
	    		engine.setScene(MainActivity.activity.mCreditsScene);
	    		MainActivity.activity.mCreditsScene.start();
			}
		});
		
		menuLayer = new Entity( -MainActivity.WIDTH, MainActivity.HEIGHT / 2);
		menu = new MenuBuilder(menuLayer, engine, x, y+90, items, ResourceManager.getInstance().mFont, ResourceManager.getInstance().mGameHammer,ResourceManager.getInstance().mGameHammer);
		//end temp menu

		attachChild(menuLayer);
		
		//Make logo move indefinitely
		logoSprite = new Sprite(MainActivity.WIDTH / 2, MainActivity.HEIGHT / 2, ResourceManager.getInstance().mGameTitle, engine.getVertexBufferObjectManager());
		attachChild(logoSprite);
	
		logoSprite.registerEntityModifier(new DelayModifier(1, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				logoSprite.registerEntityModifier(new SkewModifier(1,0f,1.5f,0f,1.5f)); //, EaseBounceInOut.getInstance()));
				logoSprite.registerEntityModifier(new ScaleModifier(0.7f, 1f, 1.005f));
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				registerSkew(logoSprite);
				registerScale(logoSprite);
			}
		}));
		
		buttonSprite = new Sprite( -MainActivity.WIDTH, MainActivity.HEIGHT / 2, MainActivity.WIDTH / 4, 80, ResourceManager.getInstance().mUIRedButton, engine.getVertexBufferObjectManager());
		attachChild(buttonSprite);
		
		backgroundSprite = new Sprite(MainActivity.WIDTH / 2 , MainActivity.HEIGHT/2,MainActivity.WIDTH, MainActivity.HEIGHT, ResourceManager.getInstance().mMenuBackgroundTextureRegion, engine.getVertexBufferObjectManager());
		
		ParallaxBackground background = new AutoParallaxBackground(0, 0, 0, 5);
		
		background.attachParallaxEntity(new ParallaxEntity(10, backgroundSprite));
		setBackground(background);
		setBackgroundEnabled(true);
		
		mHighScoreText = new Text(-MainActivity.WIDTH, -MainActivity.HEIGHT/6, font, "HIGH SCORE: " + UserData.getInstance().getHighScore(), 300, engine.getVertexBufferObjectManager());
		mHighScoreText.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {}
			@Override
			public void onUpdate(float pSecondsElapsed) {
				mHighScoreText.setText("HIGH SCORE: " + UserData.getInstance().getHighScore());
			}
		});
		attachChild(mHighScoreText);
		
		ResourceManager.getInstance().mIntroMusic.play();
		
	}

	protected void showPurchaseFailedAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.activity);

		// Setting Dialog Title
		alertDialog.setTitle("Purchase Failed");

		// Setting Dialog Message
		alertDialog.setMessage("Sorry, but the purchase failed. Your connection is probably acting weird. Please try again later.");

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

//		// Setting Positive "Yes" Btn
//		alertDialog.setPositiveButton("OK",
//				new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				// Write your code here to execute after dialog
//				startLevel(mCurrentLevel, MainActivity.activity.mGameScene);
//			}
//		});

		// Showing Alert Dialog
		alertDialog.show();
	}

	protected void registerSkew(final Sprite logoSprite) {

		logoSprite.registerEntityModifier(new DelayModifier(1, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				logoSprite.registerEntityModifier(new SkewModifier(1,logoSprite.getSkewX(),-logoSprite.getSkewX(),logoSprite.getSkewY(),-logoSprite.getSkewY()));//  , EaseBounceInOut.getInstance()));
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				registerSkew(logoSprite);
			}
	}));
	}
	
	protected void registerScale(final Sprite logoSprite) {

		logoSprite.registerEntityModifier(new DelayModifier(0.7f, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				if(logoSprite.getScaleX() < 1)
					logoSprite.registerEntityModifier(new ScaleModifier(0.7f, logoSprite.getScaleX(), 1.005f));
				else
					logoSprite.registerEntityModifier(new ScaleModifier(0.7f, logoSprite.getScaleX(), 0.99f));
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				registerScale(logoSprite);
			}
	}));
	}
    
	@Override
	public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
		activateMenu();
		super.onSceneTouchEvent(pSceneTouchEvent);
		return true;
	}
	
	public void activateMenu()
	{
		isMenuActivated = true;
		logoSprite.registerEntityModifier(new MoveModifier(1f, logoSprite.getX(), logoSprite.getY(), logoSprite.getX(), MainActivity.HEIGHT - (MainActivity.HEIGHT / 2)/4, EaseElasticOut.getInstance()));
		menuLayer.registerEntityModifier(new MoveModifier(1f, menuLayer.getX(),  MainActivity.HEIGHT/2-100, 0, MainActivity.HEIGHT/2-100, EaseBackOut.getInstance()));
		mHighScoreText.registerEntityModifier(new MoveModifier(1f, mHighScoreText.getX(),  MainActivity.HEIGHT/8, MainActivity.WIDTH/2, MainActivity.HEIGHT/8, EaseBackOut.getInstance()));
	}
	
	public synchronized void onKeyUp(int keyCode, KeyEvent event) {
		if(isMenuActivated)
			menu.onKeyUp(keyCode, event);
		else
			activateMenu();
	}
	
	
	
}
