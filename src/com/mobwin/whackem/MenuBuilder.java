package com.mobwin.whackem;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;

import tv.ouya.console.api.OuyaController;

import android.view.KeyEvent;

public class MenuBuilder {

	int selector;
	MenuItem[] options;
	final Text[] menuSelections;
	final Sprite imgSelector;
	
	public MenuBuilder(Scene scene, Engine engine, float x, float y, MenuItem[] options, ITextureRegion selectorImage) {
		
		selector = 0;
		this.options = options;
		Font font = ResourceManager.getInstance().mFont;

		this.imgSelector = new Sprite(selectorImage.getWidth(),
				selectorImage.getHeight(), 
				selectorImage, 
				engine.getVertexBufferObjectManager());
		imgSelector.setRotationCenter(imgSelector.getRotationCenterX(), imgSelector.getRotationCenterY()*0.5f);
		imgSelector.setScale(font.getLineHeight()/imgSelector.getHeight());
		imgSelector.setAnchorCenterX(1f);
		scene.attachChild(imgSelector);
		
		menuSelections = new Text[options.length]; 
		
		for(int i = 0; i < menuSelections.length; i++) {
			if (i == selector)
				imgSelector.setPosition(x, y);
			menuSelections[i] = new Text(x, y, font, options[i].toString(), engine.getVertexBufferObjectManager());
			menuSelections[i].setAnchorCenterX(0f);
			scene.attachChild(menuSelections[i]);
			// separates the lines according to the font size, adding some space in between
			y -= font.getLineHeight();			
		}
	}
	
	public synchronized void onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case OuyaController.BUTTON_DPAD_DOWN:
			if (selector < options.length - 1 ) {
				selector++;
				// update position of the selection indicator
				imgSelector.setPosition(menuSelections[selector].getX(), menuSelections[selector].getY());
				ResourceManager.getInstance().mButtonClickSound.play();
			}
			break;
		case OuyaController.BUTTON_DPAD_UP:
			if (selector > 0) {
				selector--;
				// update position of the selection indicator
				imgSelector.setPosition(menuSelections[selector].getX(), menuSelections[selector].getY());
				ResourceManager.getInstance().mButtonClickSound.play();
			}
			break;
		case OuyaController.BUTTON_DPAD_RIGHT:
			options[selector].selectionUp();
			menuSelections[selector].setText(options[selector].toString());
			ResourceManager.getInstance().mButtonClickSound.play();
			break;
		case OuyaController.BUTTON_DPAD_LEFT:
			options[selector].selectionDown();
			menuSelections[selector].setText(options[selector].toString());
			ResourceManager.getInstance().mButtonClickSound.play();
			break;
		}
		
	}

}
