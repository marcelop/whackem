package com.mobwin.whackem;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class ResourceManager {

	// ResourceManager Singleton instance
	private static ResourceManager INSTANCE;

	/*
	 * The variables listed should be kept public, allowing us easy access to
	 * them when creating new Sprites, Text objects and to play sound files
	 */
	public ITextureRegion mO_BUTTON;
	public ITextureRegion mU_BUTTON;
	public ITextureRegion mY_BUTTON;
	public ITextureRegion mA_BUTTON;
	public ITextureRegion mMenuBackgroundTextureRegion;
	
	public ITextureRegion mGameBackgroundTextureRegion;
	public ITextureRegion mGameDirtRegion;
	public ITextureRegion mGameHolesRegion1;
	public ITextureRegion mGameHolesRegion2;
	public ITextureRegion mGameHolesRegion3;
	public ITextureRegion mGameHolesRegion4;
	public ITextureRegion mGameHoleSelector;

	public Sound mSound;

	public Font mFont;

	ResourceManager() {
		// The constructor is of no use to us
	}

	public synchronized static ResourceManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ResourceManager();
		}
		return INSTANCE;
	}

	/*
	 * Each scene within a game should have a loadTextures method as well as an
	 * accompanying unloadTextures method. This way, we can display a loading
	 * image during scene swapping, unload the first scene's textures then load
	 * the next scenes textures.
	 */
	public synchronized void loadGameTextures(Engine pEngine, Context pContext) {
		// Set our game assets folder in "assets/gfx/game/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

//		BuildableBitmapTextureAtlas mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(
//				pEngine.getTextureManager(), 200, 200);

//		mGameBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
//				.createFromAsset(mBitmapTextureAtlas, pContext,
//						"ouya/OUYA_A.png");
//
//		try {
//			mBitmapTextureAtlas
//					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
//							0, 1, 1));
//			mBitmapTextureAtlas.load();
//		} catch (TextureAtlasBuilderException e) {
//			Debug.e(e);
//		}
		
		//Load button textures
		BitmapTextureAtlas button = new BitmapTextureAtlas(pEngine.getTextureManager(), 200, 200, TextureOptions.BILINEAR);
		mO_BUTTON = BitmapTextureAtlasTextureRegionFactory.createFromAsset(button, pContext, "ouya/OUYA_O.png", 0, 0); button.load();
		button = new BitmapTextureAtlas(pEngine.getTextureManager(), 200, 200, TextureOptions.BILINEAR);
		mU_BUTTON = BitmapTextureAtlasTextureRegionFactory.createFromAsset(button, pContext, "ouya/OUYA_U.png", 0, 0); button.load();
		button = new BitmapTextureAtlas(pEngine.getTextureManager(), 200, 200, TextureOptions.BILINEAR);
		mY_BUTTON = BitmapTextureAtlasTextureRegionFactory.createFromAsset(button, pContext, "ouya/OUYA_Y.png", 0, 0); button.load();
		button = new BitmapTextureAtlas(pEngine.getTextureManager(), 200, 200, TextureOptions.BILINEAR);
		mA_BUTTON = BitmapTextureAtlasTextureRegionFactory.createFromAsset(button, pContext, "ouya/OUYA_A.png", 0, 0); button.load();

		//Load Game Textures
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		
		BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1422, 640, TextureOptions.BILINEAR);
		mGameBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/background_1@2x.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 512, 384, TextureOptions.BILINEAR);
		mGameDirtRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/bg_dirt.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		mGameHolesRegion1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/bg_layer1.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		mGameHolesRegion2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/bg_layer2.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		mGameHolesRegion3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/bg_layer3.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		mGameHolesRegion4 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/bg_layer4.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		mGameHoleSelector = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/selection.png", 0, 0); textureAtlas.load();

	}

	/*
	 * All textures should have a method call for unloading once they're no
	 * longer needed; ie. a level transition.
	 */
	public synchronized void unloadGameTextures() {
		// call unload to remove the corresponding texture atlas from memory
		BuildableBitmapTextureAtlas mBitmapTextureAtlas = (BuildableBitmapTextureAtlas) mGameBackgroundTextureRegion
				.getTexture();
		mBitmapTextureAtlas.unload();

		// ... Continue to unload all textures related to the 'Game' scene

		// Once all textures have been unloaded, attempt to invoke the Garbage
		// Collector
		System.gc();
	}

	/*
	 * Similar to the loadGameTextures(...) method, except this method will be
	 * used to load a different scene's textures
	 */
	public synchronized void loadMenuTextures(Engine pEngine, Context pContext) {
		// Set our menu assets folder in "assets/gfx/menu/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");

		BuildableBitmapTextureAtlas mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(
				pEngine.getTextureManager(), 800, 480);

		mMenuBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas, pContext,
						"menu_background.png");

		try {
			mBitmapTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 1));
			mBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}

	}

	// Once again, this method is similar to the 'Game' scene's for unloading
	public synchronized void unloadMenuTextures() {
		// call unload to remove the corresponding texture atlas from memory
		BuildableBitmapTextureAtlas mBitmapTextureAtlas = (BuildableBitmapTextureAtlas) mMenuBackgroundTextureRegion
				.getTexture();
		mBitmapTextureAtlas.unload();

		// ... Continue to unload all textures related to the 'Game' scene

		// Once all textures have been unloaded, attempt to invoke the Garbage
		// Collector
		System.gc();
	}

	/*
	 * As with textures, we can create methods to load sound/music objects for
	 * different scene's within our games.
	 */
	public synchronized void loadSounds(Engine pEngine, Context pContext) {
		// Set the SoundFactory's base path
		SoundFactory.setAssetBasePath("sounds/");
		try {
			// Create mSound object via SoundFactory class
			mSound = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "sound.mp3");
		} catch (final IOException e) {
			Log.v("Sounds Load", "Exception:" + e.getMessage());
		}
	}

	/*
	 * In some cases, we may only load one set of sounds throughout our entire
	 * game's life-cycle. If that's the case, we may not need to include an
	 * unloadSounds() method. Of course, this all depends on how much variance
	 * we have in terms of sound
	 */
	public synchronized void unloadSounds() {
		// we call the release() method on sounds to remove them from memory
		if (!mSound.isReleased())
			mSound.release();
	}

	/*
	 * Lastly, we've got the loadFonts method which, once again, tends to only
	 * need to be loaded once as Font's are generally used across an entire
	 * game, from menu to shop to game-play.
	 */
	public synchronized void loadFonts(Engine pEngine) {
		FontFactory.setAssetBasePath("fonts/");

		// Create mFont object via FontFactory class
		mFont = FontFactory.create(pEngine.getFontManager(),
				pEngine.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 32f, true,
				org.andengine.util.adt.color.Color.WHITE_ABGR_PACKED_INT);

		mFont.load();
	}

	/*
	 * If an unloadFonts() method is necessary, we can provide one
	 */
	public synchronized void unloadFonts() {
		// Similar to textures, we can call unload() to destroy font resources
		mFont.unload();
	}
}