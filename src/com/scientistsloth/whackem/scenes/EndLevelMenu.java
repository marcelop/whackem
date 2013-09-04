package com.scientistsloth.whackem.scenes;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseElasticOut;

import tv.ouya.console.api.OuyaController;
import android.view.KeyEvent;

import com.scientistsloth.whackem.GameManager;
import com.scientistsloth.whackem.MainActivity;
import com.scientistsloth.whackem.MenuBuilder;
import com.scientistsloth.whackem.MenuItem;
import com.scientistsloth.whackem.UserData;
import com.scientistsloth.whackem.MenuItem.IMenuHandler;
import com.scientistsloth.whackem.ResourceManager;

public class EndLevelMenu extends Entity {

	Text mLevelText;
	Text mLevelStatsText;
	Sprite mGameOverText;
	
	Sprite mGoldStar1;
	Sprite mGoldStar2;
	Sprite mGoldStar3;
	Sprite mVacantStar1;
	Sprite mVacantStar2;
	Sprite mVacantStar3;
	
	MenuBuilder mLevelMenu;
	MenuBuilder mGameOverMenu;
	Entity mLevelMenuEntity;
	
	boolean readyForInput = false;
	private int mNextLevel;
	private Sprite mBackground;
	private Sprite mGoodJobText;
	
	public static final int GAMEOVER = -1;
	
	public EndLevelMenu(Engine engine, int nextLevel, int score) {

		UserData.getInstance().setHighScore(GameManager.getInstance().getCurrentScore());
		
		mNextLevel = nextLevel;
		mGameOverText = new Sprite(MainActivity.WIDTH/2, MainActivity.HEIGHT/2, ResourceManager.getInstance().mGameOver, engine.getVertexBufferObjectManager());
		mGoodJobText = new Sprite(MainActivity.WIDTH/2, MainActivity.HEIGHT/2, ResourceManager.getInstance().mGoodJob, engine.getVertexBufferObjectManager());
		
		mGoldStar1 = new Sprite(MainActivity.WIDTH/2 - 100, -50, 75, 75, ResourceManager.getInstance().mStar, engine.getVertexBufferObjectManager());
		mGoldStar2 = new Sprite(MainActivity.WIDTH/2, -50, 75, 75, ResourceManager.getInstance().mStar, engine.getVertexBufferObjectManager());
		mGoldStar3 = new Sprite(MainActivity.WIDTH/2 + 100, -50, 75, 75, ResourceManager.getInstance().mStar, engine.getVertexBufferObjectManager());
		mVacantStar1 = new Sprite(MainActivity.WIDTH/2 -100, -50, 70, 70, ResourceManager.getInstance().mStarFrame, engine.getVertexBufferObjectManager());
		mVacantStar2 = new Sprite(MainActivity.WIDTH/2, -50, 70, 70, ResourceManager.getInstance().mStarFrame, engine.getVertexBufferObjectManager());
		mVacantStar3 = new Sprite(MainActivity.WIDTH/2 + 100, -50, 70, 70, ResourceManager.getInstance().mStarFrame, engine.getVertexBufferObjectManager());
		
		if(MainActivity.activity.mGameScene.mEndLevelMenu != null)
		{
			MainActivity.activity.mGameScene.detachChild(MainActivity.activity.mGameScene.mEndLevelMenu);
			MainActivity.activity.mGameScene.mEndLevelMenu = null;
		}
		
		ResourceManager.getInstance().mGameMusic.setVolume(0.3f);
		
		if (nextLevel == GAMEOVER)
		{
			ResourceManager.getInstance().mGameOverSound.play();
			ResourceManager.getInstance().mGameMusic.setVolume(0.3f);
			
			float x = MainActivity.WIDTH / 2;
			float y = ResourceManager.getInstance().mFont.getLineHeight() / 2 + 190;
			MenuItem[] items = new MenuItem[2];
			/*items[0] = new MenuItem("Share!");			
			items[0].registerHandler(new IMenuHandler() {

				@Override
				public void onChange(MenuItem sender, int selected) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onClick(MenuItem sender) {
					// TODO Auto-generated method stub
				}
			});*/
			items[0] = new MenuItem("Play again");
			items[0].registerHandler(new IMenuHandler() {

				@Override
				public void onChange(MenuItem sender, int selected) {
				}
				@Override
				public void onClick(MenuItem sender) {
					GameManager.getInstance().resetGame();
					MainActivity.activity.mGameScene.mEndLevelMenu.registerEntityModifier(new FadeOutModifier(0.25f, new IEntityModifierListener() {
						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {						}
						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
							MainActivity.activity.mGameScene.detachChild(MainActivity.activity.mGameScene.mEndLevelMenu);
							MainActivity.activity.mGameScene.mEndLevelMenu = null;
							ResourceManager.getInstance().mGameMusic.setVolume(0.7f);
						}
					}));
					GameManager.getInstance().startLevel(0, MainActivity.activity.mGameScene);
				}
			});
			items[1] = new MenuItem("Back to main menu");
			items[1].registerHandler(new IMenuHandler() {

				@Override
				public void onChange(MenuItem sender, int selected) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onClick(MenuItem sender) {
					if(GameManager.getInstance().isMusicEnabled())
					{
						ResourceManager.getInstance().mGameMusic.setVolume(0.7f);
						ResourceManager.getInstance().mGameMusic.pause();
						ResourceManager.getInstance().mIntroMusic.play();
					}
					MainActivity.activity.mGameScene.detachChild(MainActivity.activity.mGameScene.mEndLevelMenu);
					MainActivity.activity.mGameScene.mEndLevelMenu = null;
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
					pItem.registerEntityModifier(new DelayModifier(1f, new IEntityModifierListener() {
						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}

						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
							// Accept input when finished moving
							readyForInput = true;
						}
					}));
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
		else
		{
			// Show End Level Stats
			
			mLevelText = new Text(MainActivity.WIDTH/2, MainActivity.HEIGHT / 3, ResourceManager.getInstance().mFont, "LEVEL " + (mNextLevel-1) + " RECAP", 20, engine.getVertexBufferObjectManager());
			mLevelStatsText = new Text(MainActivity.WIDTH/2, MainActivity.HEIGHT / 8, ResourceManager.getInstance().mFont, 
					"Missed Moles: " + GameManager.getInstance().mMissedMoles + "\n" +
					"Moles Hit: " + GameManager.getInstance().mMoleHitCountThisLevel + "\n" +
					"Total Accuracy: " + GameManager.getInstance().getAccuracy() + "\n" +
					"Level Accuracy: " + GameManager.getInstance().getAccuracyThisLevel() + "\n"
					, 1000, engine.getVertexBufferObjectManager());
			
			mBackground = new Sprite(MainActivity.WIDTH/2, MainActivity.HEIGHT / 10, 450, 508, ResourceManager.getInstance().mEndLevel, engine.getVertexBufferObjectManager());
			mLevelMenuEntity = new Entity( -MainActivity.WIDTH, MainActivity.HEIGHT / 2);
			mLevelMenuEntity.attachChild(mBackground);
			mLevelMenuEntity.attachChild(mLevelText);
			mLevelMenuEntity.attachChild(mLevelStatsText);
			mLevelMenuEntity.registerEntityModifier(new DelayModifier(1, new IEntityModifierListener() {

				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

				}

				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					pItem.setVisible(true);
					pItem.registerEntityModifier(new MoveModifier(1f, pItem.getX(),  MainActivity.HEIGHT/2-100, 0, MainActivity.HEIGHT/2-100, EaseBackOut.getInstance()));
					pItem.registerEntityModifier(new DelayModifier(1f, new IEntityModifierListener() {
						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}

						@Override
						public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
							// Accept input when finished moving
							readyForInput = true;
						}
					}));
				}
			}));
			
			//attachChild(mBackground);
			
			mLevelMenuEntity.attachChild(mVacantStar1);
			mLevelMenuEntity.attachChild(mVacantStar2);
			mLevelMenuEntity.attachChild(mVacantStar3);
			
			mLevelMenuEntity.attachChild(mGoldStar1);
			if(GameManager.getInstance().getAccuracyThisLevel() > 60)
				mLevelMenuEntity.attachChild(mGoldStar2);
			if(GameManager.getInstance().getAccuracyThisLevel() > 80)
				mLevelMenuEntity.attachChild(mGoldStar3);
			
			
			mGoodJobText.registerEntityModifier(new DelayModifier(1, new IEntityModifierListener() {

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
			
			this.attachChild(mGoodJobText);
			this.attachChild(mLevelMenuEntity);
		}

		
	}
	
	public synchronized void onKeyUp(int keyCode, KeyEvent event) {
		if(readyForInput)
		{
			if(mNextLevel == GAMEOVER)
				mLevelMenu.onKeyUp(keyCode, event);
			else if(keyCode == OuyaController.BUTTON_O)
			{
				MainActivity.activity.mGameScene.mEndLevelMenu.registerEntityModifier(new FadeOutModifier(0.25f, new IEntityModifierListener() {
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {						}
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						MainActivity.activity.mGameScene.detachChild(MainActivity.activity.mGameScene.mEndLevelMenu);
						MainActivity.activity.mGameScene.mEndLevelMenu = null;
						ResourceManager.getInstance().mGameMusic.setVolume(0.7f);
						GameManager.getInstance().startLevel(mNextLevel, MainActivity.activity.mGameScene);
					}
				}));
				
			}
		}
	}
	
}
