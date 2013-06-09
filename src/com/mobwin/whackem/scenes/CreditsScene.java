package com.mobwin.whackem.scenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleAtModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;

import tv.ouya.console.api.OuyaController;

import android.annotation.SuppressLint;
import android.view.KeyEvent;

import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.ResourceManager;

public class CreditsScene extends Scene {

	final Engine engine;
	// titles identify the number of different categories you have. Such as producer, artists, designers, programmers, etc
	String[] titles;
	// names contain the name of the each person on each category (that have the same title)
	String[][] names;
	// define the amount of space in between lines
	final float lineSpacing = 10;
	int numberOfTitles;	
	private Sprite backgroundSprite;
	public static final String MENU_BACK_STRING = "PRESS         TO RETURN";
	
	public CreditsScene(final Engine engine) {
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		// Retrieve our font from the resource manager
		this.engine = engine;
		
		numberOfTitles = 1;
		
		titles = new String[numberOfTitles];
		names = new String[numberOfTitles][];
		
		// Add return option at the bottom right corner of the screen
		Font font = ResourceManager.getInstance().mFont;
		
		float x = MainActivity.WIDTH - 150;
		float y = font.getLineHeight();
		
		final Text returnOption = new Text(x, y, font, MENU_BACK_STRING, engine.getVertexBufferObjectManager());
		final Sprite a_button = new Sprite(x-30, y, 65, 80, ResourceManager.getInstance().mO_BUTTON, engine.getVertexBufferObjectManager());
		attachChild(a_button);
		
		engine.registerUpdateHandler(new TimerHandler(1, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler arg0) {
				returnOption.setVisible(!returnOption.isVisible());
				a_button.setVisible(!a_button.isVisible());
			}
		}));	
		this.attachChild(returnOption);
		
		
		// Place credits centered in the screen, starting from the bottom
		x = MainActivity.WIDTH / 2;
		y = -20; //start offscreen		
		
		
		// Initialize all the titles and names you want in the credits
		titles[0] = "People";
		names[0] = new String[]{"Marcelo Perez","Andre Macedo"};
		
		
		// include them in the screen
		ArrayList<Entity> allLines = new ArrayList<Entity>();
		
		for (int i = 0; i < numberOfTitles; i++) {
			y = addToList(titles[i],names[i], allLines, x, y);
		}
		
		// add all lines to the screen, and apply movement modifier
		final float duration = 3;
		
		for (Entity t : allLines) {
			t.registerEntityModifier(new MoveModifier(duration, x, t.getY(), x, t.getY() - y + MainActivity.HEIGHT));
			this.attachChild(t);
		}
		
		
		// include background
		backgroundSprite = new Sprite(MainActivity.WIDTH / 2 , MainActivity.HEIGHT/2,MainActivity.WIDTH, MainActivity.HEIGHT, ResourceManager.getInstance().mMenuBackgroundTextureRegion, engine.getVertexBufferObjectManager());
		
		ParallaxBackground background = new AutoParallaxBackground(0, 0, 0, 5);
		
		background.attachParallaxEntity(new ParallaxEntity(10, backgroundSprite));
		setBackground(background);
		setBackgroundEnabled(true);
	}
	
	
	//adds the title followed by the list of names to a text list and return the y position of the last added entry, after spacing
	@SuppressLint("DefaultLocale")
	private float addToList(String title, String[] names, List<Entity> list, float x, float y) {
		Font font = ResourceManager.getInstance().mFont;
			
		Text txtTitle = new Text(x,y,font,title.toUpperCase(),engine.getVertexBufferObjectManager());
		list.add(txtTitle);
		y -= font.getLineHeight() + lineSpacing*2;
		
		Arrays.sort(names);
		
		Text txtName;
		for (String name : names) {
			txtName = new Text(x,y,font,name,engine.getVertexBufferObjectManager());
			list.add(txtName);
			y -= font.getLineHeight() + lineSpacing;
		}
		
		return y;
	}
	
	public void unloadRes() {
	}
	
	public synchronized void onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case OuyaController.BUTTON_O:
    		engine.setScene(MainActivity.activity.mMenuScene);
			ResourceManager.getInstance().mButtonClickSound.play();
		}
	}
}
