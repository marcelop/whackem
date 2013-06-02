package com.mobwin.whackem;

import org.andengine.entity.sprite.Sprite;

public class MenuItem {
	String text;
	int selector = 0;
	String[] options;
	IMenuHandler handler;
	
	public MenuItem (String text, String[] options) {
		this.text = text;
		this.options = options;
		handler = null;
	}
	
	public int selectionUp() {
		if (selector < options.length -1 ) {
			selector++;
			if (handler != null) handler.onChange(selector);
		}
		return selector;
	}
	
	public int selectionDown() {
		if (selector > 0 ) {
			selector--;
			if (handler != null) handler.onChange(selector);
		}
		return selector;
	}
	
	public int getSelection() {
		return selector;
	}
	
	public String getSelectionText() {
		return options[selector];
	}
	
	public String toString() {
		return(text + "   < " + options[selector] + " >");
	}
	
	void registerHandler(IMenuHandler handler) {
		this.handler = handler;
	}
	
	public interface IMenuHandler {
		void onChange(int selected);
	}
}
