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
		if (options != null) selector = 0;
		else selector = -1;
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
			if (handler != null) handler.onChange(this,selector);
		}
		return selector;
	}
	
	public int selectionDown() {
		if (options != null && selector > 0 ) {
			selector--;
			if (handler != null) handler.onChange(this,selector);
		}
		return selector;
	}
	
	public int getSelection() {
		return selector;
	}
	
	public String getSelectionText() {
		if (options == null) return "";
		return options[selector];
	}
	
	public String getText() {
		return text;
	}
	
	public boolean hasOptions() {
		return (options != null);
	}
	
	public void Select() {
		if (handler!= null) handler.onClick(this);
	}
	
	void registerHandler(IMenuHandler handler) {
		this.handler = handler;
	}	
	
	public interface IMenuHandler {
		void onChange(MenuItem sender, int selected);
		void onClick(MenuItem sender);
	}
}
