package com.mobwin.whackem.scenes;

import java.lang.reflect.Modifier;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SkewModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontUtils;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseBounceIn;
import org.andengine.util.modifier.ease.EaseBounceInOut;
import org.andengine.util.modifier.ease.EaseElasticIn;
import org.andengine.util.modifier.ease.EaseElasticOut;

import com.mobwin.whackem.GameManager;
import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.ResourceManager;

public class MainMenuScene extends Scene {

	public static final String MENU_STRING = "HELLO MENU SCREEN!";
	public static final String MENU_SELECT_STRING = "PRESS         TO SELECT";
	Text mMenuSceneText;
	Sprite logoSprite;
	Sprite buttonSprite;

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
		//attachChild(mMenuSceneText);
		
		x = MainActivity.WIDTH / 2;
		y = font.getLineHeight() / 2 + 100;
		
		final Text menuSelectText = new Text(x, y, font, MENU_SELECT_STRING, MENU_SELECT_STRING.length(), engine.getVertexBufferObjectManager());
		attachChild(menuSelectText);
		
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
		GameManager.getInstance().startLevel(0, MainActivity.activity.getGameScene());
		MainActivity.activity.getEngine().setScene(
				MainActivity.activity.getGameScene());
		logoSprite.registerEntityModifier(new MoveModifier(1f, logoSprite.getX(), logoSprite.getY(), logoSprite.getX(), MainActivity.HEIGHT - (MainActivity.HEIGHT / 2)/4, EaseElasticOut.getInstance()));
		buttonSprite.registerEntityModifier(new MoveModifier(1f, buttonSprite.getX(), buttonSprite.getY(), MainActivity.WIDTH/2, MainActivity.HEIGHT/2, EaseBackOut.getInstance()));
		
		return true;
	}
}
