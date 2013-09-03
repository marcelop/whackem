package com.scientistsloth.whackem;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.color.Color;

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
	public ITextureRegion mGameHoleSelectorAlpha;
	public ITiledTextureRegion mGameMole;
	public ITiledTextureRegion mGameMoleGlasses;
	public ITextureRegion mGameFlowers;
	public ITextureRegion mGameTree;
	public ITextureRegion mGameCloud1;
	public ITextureRegion mGameCloud2;
	public ITextureRegion mGameMoon;
	public ITextureRegion mGameOver;
	public ITextureRegion mFakeBackground;
	public ITextureRegion mTutorial;
	
	public ITextureRegion mEndLevel;
	public ITextureRegion mGoodJob;
	public ITextureRegion mStar;
	public ITextureRegion mStarFrame;
	
	public ITextureRegion mGameTitle;
	public ITextureRegion mGameHammer;
	
	public ITextureRegion mUIRedButton;
	public ITextureRegion mCheckMark;
	public ITextureRegion mScoreBox;
	public ITextureRegion mUIOrangeButton;

	public Sound mHitSound;
	public Sound mAllyHitSound;
	public Sound mHammerSound;
	public Sound mButtonClickSound;
	public Sound mLevelUpSound;
	public Sound mGameOverSound;
	public Music mIntroMusic;
	public Music mGameMusic;

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


		//Load button textures
		BitmapTextureAtlas button = new BitmapTextureAtlas(pEngine.getTextureManager(), 200, 200, TextureOptions.DEFAULT);
		mO_BUTTON = BitmapTextureAtlasTextureRegionFactory.createFromAsset(button, pContext, "ouya/OUYA_O.png", 0, 0); button.load();
		button = new BitmapTextureAtlas(pEngine.getTextureManager(), 200, 200, TextureOptions.DEFAULT);
		mU_BUTTON = BitmapTextureAtlasTextureRegionFactory.createFromAsset(button, pContext, "ouya/OUYA_U.png", 0, 0); button.load();
		button = new BitmapTextureAtlas(pEngine.getTextureManager(), 200, 200, TextureOptions.DEFAULT);
		mY_BUTTON = BitmapTextureAtlasTextureRegionFactory.createFromAsset(button, pContext, "ouya/OUYA_Y.png", 0, 0); button.load();
		button = new BitmapTextureAtlas(pEngine.getTextureManager(), 200, 200, TextureOptions.DEFAULT);
		mA_BUTTON = BitmapTextureAtlasTextureRegionFactory.createFromAsset(button, pContext, "ouya/OUYA_A.png", 0, 0); button.load();

		//Load Game Textures
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		
		BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1422, 640, TextureOptions.BILINEAR);
		mGameBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/background_1@2x.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 512, 384, TextureOptions.BILINEAR); 
		mGameDirtRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/bg_dirt.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.DEFAULT);
		mGameHolesRegion1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/bg_layer1.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.DEFAULT);
		mGameHolesRegion2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/bg_layer2.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.DEFAULT);
		mGameHolesRegion3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/bg_layer3.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.DEFAULT);
		mGameHolesRegion4 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/bg_layer4.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		mGameHoleSelector = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/selection.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		mGameHoleSelectorAlpha = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/selection_alpha.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.DEFAULT);
		mFakeBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/falseBackground.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		mTutorial = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/WhackTutorial.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 833, 508, TextureOptions.DEFAULT);
		mEndLevel = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/end_level_box.png", 0, 0); textureAtlas.load();
/*
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 833, 508, TextureOptions.DEFAULT);
		mStar = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/end_level_box.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 833, 508, TextureOptions.DEFAULT);
		mStarFrame = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/end_level_box.png", 0, 0); textureAtlas.load();
*/
		
		BuildableBitmapTextureAtlas bmpTextureAtlas = new BuildableBitmapTextureAtlas(pEngine.getTextureManager(), 1424, 400, TextureOptions.BILINEAR);
		mGameMole = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bmpTextureAtlas, pContext, "mole/double_mole.png", 8, 2);
		try {
			bmpTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,0,0));
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		bmpTextureAtlas.load();
		
		bmpTextureAtlas = new BuildableBitmapTextureAtlas(pEngine.getTextureManager(), 1424, 200, TextureOptions.BILINEAR);
		mGameMoleGlasses = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bmpTextureAtlas, pContext, "mole/molewithglasses.png", 8, 1);
		try {
			bmpTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,0,0));
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		bmpTextureAtlas.load();
		

		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 301, 152, TextureOptions.BILINEAR);
		mGameFlowers = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/flowers@2x.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 226, 380, TextureOptions.BILINEAR);
		mGameTree = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/tree@2x.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 382, 220, TextureOptions.BILINEAR);
		mGameCloud1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/cloud_1@2x.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 268, 196, TextureOptions.BILINEAR);
		mGameCloud2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/cloud_2@2x.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 112, 146, TextureOptions.BILINEAR);
		mGameMoon = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/moon@2x.png", 0, 0); textureAtlas.load();
		
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 486, 123, TextureOptions.BILINEAR);
		mGameOver = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/gameover.png", 0, 0); textureAtlas.load();
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 359, 105, TextureOptions.BILINEAR);
		mGoodJob = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/good_job.png", 0, 0); textureAtlas.load();
		
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 496, 133, TextureOptions.BILINEAR);
		mGameTitle = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/logo.png", 0, 0); textureAtlas.load();
		
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 500, 500, TextureOptions.BILINEAR);
		mGameHammer = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/hammer.png", 0, 0); textureAtlas.load();
		
		
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 877, 270, TextureOptions.BILINEAR);
		mUIRedButton = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/red_button.png", 0, 0); textureAtlas.load();
		//mUIOrangeButton = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/orange_button.png", 0, 0); textureAtlas.load();
				
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 150, 150, TextureOptions.DEFAULT);
		mCheckMark = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/checkmark.png", 0, 0); textureAtlas.load();
		
		textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 314, 140, TextureOptions.DEFAULT);
		mScoreBox = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "foreground/score_box.png", 0, 0); textureAtlas.load();
	}

	/*
	 * All textures should have a method call for unloading once they're no
	 * longer needed; ie. a level transition.
	 */
	public synchronized void unloadGameTextures() {
		// call unload to remove the corresponding texture atlas from memory
		mGameBackgroundTextureRegion.getTexture().unload();
		mGameDirtRegion.getTexture().unload();
		mGameHoleSelector.getTexture().unload();
		mGameHolesRegion1.getTexture().unload();
		mGameHolesRegion2.getTexture().unload();
		mGameHolesRegion3.getTexture().unload();
		mGameHolesRegion4.getTexture().unload();
		mGameMole.getTexture().unload();
		mGameMoleGlasses.getTexture().unload();
		mGameFlowers.getTexture().unload();
		mGameTree.getTexture().unload();
		mGameCloud1.getTexture().unload();
		mGameCloud2.getTexture().unload();
		mGameMoon.getTexture().unload();
		
		

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

		BitmapTextureAtlas textureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 1422, 640, TextureOptions.BILINEAR);
		mMenuBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, pContext, "background/background_2@2x.png", 0, 0); textureAtlas.load();
		
//		BuildableBitmapTextureAtlas textureAtlas = new BuildableBitmapTextureAtlas(pEngine.getTextureManager(), 1422, 640, TextureOptions.BILINEAR);
//		mGameBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(textureAtlas, pContext, "background/background_2@2x.png", 1, 1);
//		/* Build and load the mBitmapTextureAtlas object */
//		try {
//			textureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
//		} catch (TextureAtlasBuilderException e) {
//		e.printStackTrace();
//		}
//		textureAtlas.load();

	}

	// Once again, this method is similar to the 'Game' scene's for unloading
	public synchronized void unloadMenuTextures() {
		// call unload to remove the corresponding texture atlas from memory
		mGameBackgroundTextureRegion.getTexture().unload();
		System.gc();
	}

	/*
	 * As with textures, we can create methods to load sound/music objects for
	 * different scene's within our games.
	 */
	public synchronized void loadSounds(Engine pEngine, Context pContext) {
		// Set the SoundFactory's base path
		SoundFactory.setAssetBasePath("sfx/sound/");
		MusicFactory.setAssetBasePath("sfx/music/");
		try {
			// Create mSound object via SoundFactory class
			mHitSound = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "hit.wav");
			mAllyHitSound = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "allyhit.wav");
			mHammerSound = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "hammer.wav");
			mButtonClickSound = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "menuchange.wav");
			mLevelUpSound = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "level-up.mp3");
			mGameOverSound = SoundFactory.createSoundFromAsset(
					pEngine.getSoundManager(), pContext, "gameover.wav");
			mIntroMusic = MusicFactory.createMusicFromAsset(
					pEngine.getMusicManager(), pContext, "intro.mp3");
			mGameMusic = MusicFactory.createMusicFromAsset(
					pEngine.getMusicManager(), pContext, "gameplay.mp3");
			
			mIntroMusic.setLooping(true);
			mGameMusic.setLooping(true);
			mIntroMusic.setVolume(0.7f);
			mGameMusic.setVolume(0.7f);
			
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
//		if (!mSound.isReleased())
//			mSound.release();
	}

	/*
	 * Lastly, we've got the loadFonts method which, once again, tends to only
	 * need to be loaded once as Font's are generally used across an entire
	 * game, from menu to shop to game-play.
	 */
	public synchronized void loadFonts(Engine pEngine) {
		FontFactory.setAssetBasePath("fonts/");

		final ITexture strokeFontTexture = new BitmapTextureAtlas(pEngine.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		
		// Create mFont object via FontFactory class
	/*	mFont = FontFactory.create(pEngine.getFontManager(),
				pEngine.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32f, true,
				org.andengine.util.adt.color.Color.WHITE_ABGR_PACKED_INT);*/
				
		mFont = new StrokeFont(pEngine.getFontManager(), strokeFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32f, true, Color.WHITE, 1, Color.BLACK);

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
