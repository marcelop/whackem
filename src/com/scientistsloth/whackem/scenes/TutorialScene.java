package com.scientistsloth.whackem.scenes;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;

import tv.ouya.console.api.OuyaController;
import android.view.KeyEvent;

import com.scientistsloth.whackem.GameManager;
import com.scientistsloth.whackem.MainActivity;
import com.scientistsloth.whackem.ResourceManager;

public class TutorialScene extends Scene {
	
	Sprite mTutorialBg = new Sprite(MainActivity.WIDTH/2, MainActivity.HEIGHT/2, ResourceManager.getInstance().mTutorial, MainActivity.activity.getVertexBufferObjectManager());
	
public TutorialScene() {
	
	mTutorialBg.setAlpha(0);
	mTutorialBg.registerEntityModifier(new FadeInModifier(1f));
	attachChild(mTutorialBg);

}

public void onKeyUp(int keyCode, KeyEvent event) {
	if(keyCode == OuyaController.BUTTON_O)
	{
		
		mTutorialBg.registerEntityModifier(new FadeOutModifier(1f, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				
				if(GameManager.getInstance().isMusicEnabled())
				{
					ResourceManager.getInstance().mGameMusic.play();
					ResourceManager.getInstance().mGameMusic.setVolume(0.7f);
				}

				MainActivity.activity.getEngine().setScene(MainActivity.activity.mGameScene);
				GameManager.getInstance().startLevel(0, MainActivity.activity.mGameScene);
				
			}
		}));

	}
}

}
