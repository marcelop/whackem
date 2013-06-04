package com.mobwin.whackem;

import org.andengine.entity.sprite.Sprite;

public class MenuItem {
	String text;
	int selector;
	String[] options;
	IMenuHandler handler;
	
	public MenuItem (String text, String[] options) {
		this.text = text;
		this.options = options;
		selector = 0;
		handler = null;
	}
	
	public MenuItem(String text) {
		this.text = text;
		this.options = null;
		selector = -1; //no selection
		handler = null;
	}
	
	public int selectionUp() {
		if (options != null && selector < options.length -1 ) {
			selector++;
			if (handler != null) handler.onChange(selector);
		}
		return selector;
	}
	
	public int selectionDown() {
		if (options != null && selector > 0 ) {
			selector--;
			if (handler != null) handler.onChange(selector);
		}
		return selector;
	}
	
	public int getSelection() {
		return selector;
	}
	
	public String getSelectionText() {
		if (options == null) return null;
		return options[selector];
	}
	
	public String toString() {
		if (options == null) return text;
		return text + "   < " + options[selector] + " >";
	}
	
	void registerHandler(IMenuHandler handler) {
		this.handler = handler;
	}
	
	public interface IMenuHandler {
		void onChange(int selected);
		void onClick();
	}
}
