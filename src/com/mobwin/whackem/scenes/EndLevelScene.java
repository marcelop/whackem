package com.mobwin.whackem.scenes;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseElasticOut;

import tv.ouya.console.api.OuyaController;

import android.view.KeyEvent;

import com.mobwin.whackem.GameManager;
import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.MenuBuilder;
import com.mobwin.whackem.MenuItem;
import com.mobwin.whackem.ResourceManager;
import com.mobwin.whackem.MenuItem.IMenuHandler;

public class EndLevelScene extends Scene {

	Sprite mLevelText;
	Sprite mGameOverText;
	
	MenuBuilder mLevelMenu;
	MenuBuilder mGameOverMenu;
	Entity mLevelMenuEntity;
	
	public static final int GAMEOVER = -1;
	
	public EndLevelScene(Engine engine, int nextLevel, int score) {
		
		this.setBackgroundEnabled(false);
		mLevelText = new Sprite(MainActivity.WIDTH/2, MainActivity.HEIGHT/2, ResourceManager.getInstance().mGameOver, engine.getVertexBufferObjectManager());
		mGameOverText = new Sprite(MainActivity.WIDTH/2, MainActivity.HEIGHT/2, ResourceManager.getInstance().mGameOver, engine.getVertexBufferObjectManager());
		
		
		
		if (nextLevel == GAMEOVER)
		{
			float x = MainActivity.WIDTH / 2;
			float y = ResourceManager.getInstance().mFont.getLineHeight() / 2 + 190;
			MenuItem[] items = new MenuItem[3];
			items[0] = new MenuItem("Share!");			
			items[0].registerHandler(new IMenuHandler() {

				@Override
				public void onChange(MenuItem sender, int selected) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onClick(MenuItem sender) {
					// TODO Auto-generated method stub
				}
			});
			items[1] = new MenuItem("Play again");
			items[1].registerHandler(new IMenuHandler() {

				@Override
				public void onChange(MenuItem sender, int selected) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onClick(MenuItem sender) {
					GameManager.getInstance().resetGame();
					MainActivity.activity.mGameScene.clearChildScene();
					GameManager.getInstance().startLevel(0, MainActivity.activity.mGameScene);
					
				}
			});
			items[2] = new MenuItem("Return to main menu");
			items[2].registerHandler(new IMenuHandler() {

				@Override
				public void onChange(MenuItem sender, int selected) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onClick(MenuItem sender) {
					ResourceManager.getInstance().mGameMusic.pause();
					ResourceManager.getInstance().mIntroMusic.play();
					MainActivity.activity.mGameScene.clearChildScene();
					MainActivity.activity.getEngine().setScene(MainActivity.activity.mMenuScene);
				}
			});			
			mLevelMenuEntity = new Entity( -MainActivity.WIDTH, MainActivity.HEIGHT / 2);
			mLevelMenu = new MenuBuilder(mLevelMenuEntity, engine, x, y, items, ResourceManager.getInstance().mFont, ResourceManager.getInstance().mGameHammer,ResourceManager.getInstance().mGameHammer);
			
			mLevelMenuEntity.registerEntityModifier(new DelayModifier(2, new IEntityModifierListener() {

				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

				}

				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					pItem.setVisible(true);
					pItem.registerEntityModifier(new MoveModifier(1f, pItem.getX(),  MainActivity.HEIGHT/2-100, 0, MainActivity.HEIGHT/2-100, EaseBackOut.getInstance()));
				}
			}));
		
			mGameOverText.registerEntityModifier(new DelayModifier(2, new IEntityModifierListener() {

				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					pItem.setVisible(true);
				}

				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					//pItem.setVisible(false);
					pItem.registerEntityModifier(new MoveModifier(1f, pItem.getX(), pItem.getY(), pItem.getX(), MainActivity.HEIGHT - (MainActivity.HEIGHT / 2)/4, EaseElasticOut.getInstance()));
				}
			}));
			
			this.attachChild(mGameOverText);
			this.attachChild(mLevelMenuEntity);
		}

		
	}
	
	public synchronized void onKeyUp(int keyCode, KeyEvent event) {
		mLevelMenu.onKeyUp(keyCode, event);
	}
	
}
