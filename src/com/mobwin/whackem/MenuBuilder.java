package com.mobwin.whackem;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.align.HorizontalAlign;

import tv.ouya.console.api.OuyaController;

import android.view.KeyEvent;

public class MenuBuilder {

	int selector;
	MenuItem[] options;
	final Text[] menuSelections;
	
	public MenuBuilder(Scene scene, Engine engine, float x, float y, MenuItem[] options) {
		
		selector = 0;
		this.options = options;
		// Retrieve our font from the resource manager
		Font font = ResourceManager.getInstance().mFont;
		menuSelections = new Text[options.length]; 

		
		for(int i = 0; i < menuSelections.length; i++) {
			if (i == selector)
				menuSelections[i] = new Text(x, y, font, " >> " + options[i].toString(), engine.getVertexBufferObjectManager());
			else
				menuSelections[i] = new Text(x, y, font, options[i].toString(), engine.getVertexBufferObjectManager());
			//menuSelections[i].setTextOptions(new TextOptions(HorizontalAlign.LEFT));
			scene.attachChild(menuSelections[i]);
			// separates the lines according to the font size, adding some space in between
			y -= font.getLineHeight();			
		}
	}
	
	public synchronized void onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case OuyaController.BUTTON_DPAD_DOWN:
			if (selector < options.length - 1 ) {
				menuSelections[selector].setText(options[selector].toString());
				selector++;
				menuSelections[selector].setText(" >> " + options[selector].toString());
			}
			break;
		case OuyaController.BUTTON_DPAD_UP:
			if (selector > 0) {
				menuSelections[selector].setText(options[selector].toString());
				selector--;
				menuSelections[selector].setText(" >> " + options[selector].toString());
			}
			break;
		case OuyaController.BUTTON_DPAD_RIGHT:
			options[selector].selectionUp();
			menuSelections[selector].setText(" >> " + options[selector].toString());
			break;
		case OuyaController.BUTTON_DPAD_LEFT:
			options[selector].selectionDown();
			menuSelections[selector].setText(" >> " + options[selector].toString());
			break;
		}
		
	}

}
