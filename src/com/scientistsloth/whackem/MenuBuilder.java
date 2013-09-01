package com.scientistsloth.whackem;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;

import tv.ouya.console.api.OuyaController;
import android.view.KeyEvent;

public class MenuBuilder {

	int selector;
	MenuItem[] options;
	Text[] menuTexts, menuSelections;
	final Sprite imgSelector;
	final Sprite imgLeftArrow, imgRightArrow;
	final float bufferArea = 10;
	private float anchorX;
	private float anchorY;
	
	public MenuBuilder(Entity scene, Engine engine, float x, float y, MenuItem[] options, Font font, ITextureRegion selectorImage, ITextureRegion arrowImage) {
		
		selector = 0;
		this.options = options;

		anchorX = 0.5f;
		anchorY = 0.5f;
		
		// place the selector right before the first option
		this.imgSelector = new Sprite(x,y,selectorImage,engine.getVertexBufferObjectManager());
		imgSelector.setRotationCenter(imgSelector.getRotationCenterX(), imgSelector.getRotationCenterY()*0.5f);
		imgSelector.setScale(font.getLineHeight()/imgSelector.getHeight());
		imgSelector.setAnchorCenterX(1f);

		this.imgLeftArrow = new Sprite(x,y,arrowImage,engine.getVertexBufferObjectManager());
		imgLeftArrow.setScale(font.getLineHeight()/imgLeftArrow.getHeight());
		imgLeftArrow.setAnchorCenterX(0f);
		
		this.imgRightArrow = new Sprite(x,y,arrowImage,engine.getVertexBufferObjectManager());
		imgRightArrow.setFlippedHorizontal(true); // Flip the image for the right side
		imgRightArrow.setScale(font.getLineHeight()/imgRightArrow.getHeight());
		imgRightArrow.setAnchorCenterX(0f);

		/* if the first option in the menu does not contain any extra options, hide the arrows */
		imgLeftArrow.setVisible(options[selector].hasOptions());
		imgRightArrow.setVisible(options[selector].hasOptions());
		
		menuTexts = new Text[options.length];
		menuSelections = new Text[options.length]; 
		
		for(int i = 0; i < menuSelections.length; i++) {
			menuTexts[i] = new Text(x, y, font, options[i].getText(), engine.getVertexBufferObjectManager());
			//menuTexts[i].setAnchorCenterX(0f);
			scene.attachChild(new Sprite(menuTexts[i].getX() , menuTexts[i].getY(), 
					 MainActivity.WIDTH/3 -60, font.getLineHeight()+40, ResourceManager.getInstance().mUIRedButton, MainActivity.activity.getVertexBufferObjectManager()));
			scene.attachChild(menuTexts[i]);

			if (options[i].hasOptions()) {				
				/* place the selection after the menu text and the arrow to the left
				 * menu text _ < _ menu option _ > 
				 */
				menuSelections[i] = new Text(x + 2*bufferArea + 30 + menuTexts[i].getWidth(),
						y, font, options[i].getSelectionText(), 100, engine.getVertexBufferObjectManager());
				//menuSelections[i].setAnchorCenterX(0f);
				scene.attachChild(menuSelections[i]);
				if (i == selector) {
					/* correct placement of the arrows  */
					imgLeftArrow.setX(menuTexts[i].getX() + menuTexts[i].getWidth() + bufferArea);
					imgRightArrow.setX(menuSelections[i].getX() + menuSelections[i].getWidth() + bufferArea);
				}	
			}
			
			if(options[i].hasCheckmark() && options[i].checkmarkValue)
			{
				options[i].imgCheckMark.setX(x + 130);
				options[i].imgCheckMark.setY(y+5);
				scene.attachChild(options[i].imgCheckMark);
			}
			
			// separates the lines according to the font size, adding some space in between
			y -= font.getLineHeight() +40;
		}
		
		imgSelector.setX(MainActivity.WIDTH/2 - menuTexts[0].getWidth()/2);
		scene.attachChild(imgSelector);
		scene.attachChild(imgLeftArrow);
		scene.attachChild(imgRightArrow);
	}
	
	public synchronized void onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case OuyaController.BUTTON_DPAD_DOWN:
			if (selector < options.length - 1 ) {
				selector++;
				// update position of the selection indicator
				imgSelector.setPosition(menuTexts[selector].getX(), menuTexts[selector].getY());
				/* if the new option in the menu does not contain any extra options, hide the arrows */
				imgLeftArrow.setVisible(options[selector].hasOptions());
				imgRightArrow.setVisible(options[selector].hasOptions());
				/* correct placement of the arrows  */
				if (options[selector].hasOptions()) {
					imgLeftArrow.setPosition(menuTexts[selector].getX() + menuTexts[selector].getWidth() + bufferArea, menuTexts[selector].getY());
					imgRightArrow.setPosition(menuSelections[selector].getX() + menuSelections[selector].getWidth() + bufferArea, menuTexts[selector].getY());
				}
				ResourceManager.getInstance().mButtonClickSound.play();
			}
			break;
		case OuyaController.BUTTON_DPAD_UP:
			if (selector > 0) {
				selector--;
				// update position of the selection indicator
				imgSelector.setPosition(menuTexts[selector].getX(), menuTexts[selector].getY());
				/* if the new option in the menu does not contain any extra options, hide the arrows */
				imgLeftArrow.setVisible(options[selector].hasOptions());
				imgRightArrow.setVisible(options[selector].hasOptions());
				/* correct placement of the arrows  */
				if (options[selector].hasOptions()) {
					imgLeftArrow.setPosition(menuTexts[selector].getX() + menuTexts[selector].getWidth() + bufferArea, menuTexts[selector].getY());
					imgRightArrow.setPosition(menuSelections[selector].getX() + menuSelections[selector].getWidth() + bufferArea, menuTexts[selector].getY());
				}
				ResourceManager.getInstance().mButtonClickSound.play();
			}
			break;
		case OuyaController.BUTTON_DPAD_RIGHT:
			if (options[selector].hasOptions()) {
				options[selector].selectionUp();
				menuSelections[selector].setText(options[selector].getSelectionText());
				/* Adjust right arrow to the end of the new text */
				imgRightArrow.setPosition(menuSelections[selector].getX() + menuSelections[selector].getWidth() + bufferArea, menuTexts[selector].getY());
			}
			ResourceManager.getInstance().mButtonClickSound.play();
			break;
		case OuyaController.BUTTON_DPAD_LEFT:
			if (options[selector].hasOptions()) {
				options[selector].selectionDown();
				menuSelections[selector].setText(options[selector].getSelectionText());
				/* Adjust right arrow to the end of the new text */
				imgRightArrow.setPosition(menuSelections[selector].getX() + menuSelections[selector].getWidth() + bufferArea, menuTexts[selector].getY());
			}
			ResourceManager.getInstance().mButtonClickSound.play();
			break;
		case OuyaController.BUTTON_O:
			options[selector].Select();
			ResourceManager.getInstance().mButtonClickSound.play();
		}
		
		imgSelector.setX(MainActivity.WIDTH/2 - menuTexts[selector].getWidth()/2);
	}
	
	void setAnchor(float pX, float pY) {
		anchorX = pX;
		anchorY = pY;
	}
	
	void setAnchorX(float pX) {
		anchorX = pX;
	}
	
	void setAnchorY(float pY) {
		anchorY = pY;
	}
}
