package com.mobwin.whackem.scenes;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
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
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontUtils;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseElasticOut;

import tv.ouya.console.api.OuyaController;

import android.view.KeyEvent;

import com.mobwin.whackem.GameManager;
import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.MenuBuilder;
import com.mobwin.whackem.MenuItem;
import com.mobwin.whackem.MenuItem.IMenuHandler;
import com.mobwin.whackem.ResourceManager;

public class MainMenuScene extends Scene {

	public static final String MENU_STRING = "HELLO MENU SCREEN!";
	public static final String MENU_SELECT_STRING = "PRESS         TO SELECT";
	Text mMenuSceneText;
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

		// We must change the value of x depending on the length of our menu
		// string now in order to keep the text centered on-screen
		float x = MainActivity.WIDTH / 2 - FontUtils.measureText(font, MENU_STRING) / 2;
		float y = MainActivity.HEIGHT / 2 - font.getLineHeight() / 2;
		
		// Create our menu screen text object
		mMenuSceneText = new Text(x, y, font, MENU_STRING, MENU_STRING.length(), engine.getVertexBufferObjectManager());
		// Attach the text object to our menu scene
		//attachChild(mMenuSceneText);
		
		x = MainActivity.WIDTH / 2;
		y = font.getLineHeight() / 2 + 100;
		
		final Text menuSelectText = new Text(x, y, font, MENU_SELECT_STRING, MENU_SELECT_STRING.length(), engine.getVertexBufferObjectManager());
		attachChild(menuSelectText);
		
		
		//Make a temporary menu
		
		String[] o = new String[2];
		o[0] = "ON";
		o[1] = "OFF";
		MenuItem[] items = new MenuItem[4];
		items[0] = new MenuItem("Start Game");
		
		items[0].registerHandler(new IMenuHandler() {

			@Override
			public void onChange(MenuItem sender, int selected) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onClick(MenuItem sender) {
				// TODO Auto-generated method stub
	    		ResourceManager.getInstance().mIntroMusic.pause();
	    		ResourceManager.getInstance().mGameMusic.play();
	    		engine.setScene(MainActivity.activity.mGameScene);
	    		GameManager.getInstance().startLevel(0, MainActivity.activity.mGameScene);				
			}
		});
		
		
		items[1] = new MenuItem("Music", o);
		items[2] = new MenuItem("Sound Effects", new String[] {"ON","OFF"});
	
		items[3] = new MenuItem("Credits");
		
		items[3].registerHandler(new IMenuHandler() {

			@Override
			public void onChange(MenuItem sender, int selected) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onClick(MenuItem sender) {
				// TODO Auto-generated method stub
	    		ResourceManager.getInstance().mIntroMusic.pause();
	    		//ResourceManager.getInstance().mGameMusic.play();
	    		engine.setScene(MainActivity.activity.mCreditsScene);
			}
		});
		
		menuLayer = new Entity( -MainActivity.WIDTH, MainActivity.HEIGHT / 2);
		menu = new MenuBuilder(menuLayer, engine, x, y+90, items, ResourceManager.getInstance().mFont, ResourceManager.getInstance().mGameHammer,ResourceManager.getInstance().mGameHammer);
		//end temp menu
		attachChild(menuLayer);
		
		
		final Sprite a_button = new Sprite(x-30, y, 65, 80, ResourceManager.getInstance().mO_BUTTON, engine.getVertexBufferObjectManager());
		attachChild(a_button);
		
		engine.registerUpdateHandler(new TimerHandler(1, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler arg0) {
				menuSelectText.setVisible(!menuSelectText.isVisible());
				a_button.setVisible(!a_button.isVisible());
			}
		}));		
		
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
		
		ResourceManager.getInstance().mIntroMusic.play();
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
//		GameManager.getInstance().startLevel(0, MainActivity.activity.getGameScene());
//		MainActivity.activity.getEngine().setScene(
//				MainActivity.activity.getGameScene());
		activateMenu();
		super.onSceneTouchEvent(pSceneTouchEvent);
		return true;
	}
	
	public void activateMenu()
	{
		isMenuActivated = true;
		logoSprite.registerEntityModifier(new MoveModifier(1f, logoSprite.getX(), logoSprite.getY(), logoSprite.getX(), MainActivity.HEIGHT - (MainActivity.HEIGHT / 2)/4, EaseElasticOut.getInstance()));
		menuLayer.registerEntityModifier(new MoveModifier(1f, menuLayer.getX(),  MainActivity.HEIGHT/2-100, 0, MainActivity.HEIGHT/2-100, EaseBackOut.getInstance()));		
	}
	
	public synchronized void onKeyUp(int keyCode, KeyEvent event) {
		if(isMenuActivated)
			menu.onKeyUp(keyCode, event);
		else
			activateMenu();
	}
}
