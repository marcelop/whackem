package com.mobwin.whackem.scenes;

import org.andengine.engine.Engine;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleAtModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.modifier.IModifier;

import android.content.Context;

import com.mobwin.whackem.MainActivity;
import com.mobwin.whackem.ResourceManager;

public class SplashScene extends Scene {

	private static final float mEachAnimationDuration = 0.25f;
	private static final float mEachAnimationPauseDuration = 1f;
	private static final float mEachScaleToSize = 0.9f;
	private static SequenceEntityModifier mAndEngineLogo_SequenceEntityModifier;
	private static SequenceEntityModifier mCompanyLogo_SequenceEntityModifier;
	public static final String SPLASH_STRING = "powered by AndEngine";
	Text mSplashSceneText;
	private Sprite mAndEngineLogoSprite;
	private Sprite mCompanyLogoSprite;

	public SplashScene(Engine engine, Context pContext) {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		// Retrieve our font from the resource manager
		Font font = ResourceManager.getInstance().mFont;

		// Set the location of our splash 'image' (text object in this case).
		// We can use FontUtils.measureText to retrieve the width of our text
		// object in order to properly format its position
		float x = MainActivity.WIDTH / 2;
		float y = MainActivity.HEIGHT / 4;

		final BitmapTextureAtlas mAndEngineLogoTexture = new BitmapTextureAtlas(
				engine.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		final ITextureRegion mAndEngineLogoTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mAndEngineLogoTexture, pContext,
						"splash/AE_logo.png", 0, 0);
		mAndEngineLogoTexture.load();
		mAndEngineLogoSprite = new Sprite(x,
				MainActivity.HEIGHT / 2, 350, 350, mAndEngineLogoTextureRegion,
				engine.getVertexBufferObjectManager());
		
		final BitmapTextureAtlas mCompanyLogoTexture = new BitmapTextureAtlas(
				engine.getTextureManager(), 600, 401, TextureOptions.BILINEAR);
		final ITextureRegion mCompanyLogoTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mCompanyLogoTexture, pContext,
						"splash/sloth_scientist.jpg", 0, 0); 
		mCompanyLogoTexture.load();
		mCompanyLogoSprite = new Sprite(x,
				MainActivity.HEIGHT / 2, 600, 401, mCompanyLogoTextureRegion,
				engine.getVertexBufferObjectManager());
		

		// Create our splash screen text object
		mSplashSceneText = new Text(x, y, font, SPLASH_STRING,
				SPLASH_STRING.length(), engine.getVertexBufferObjectManager());
		
		mSplashSceneText.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(2f),
				new FadeInModifier(mEachAnimationDuration)));
		// Attach the text object to our splash scene
		attachChild(mSplashSceneText);
		
		
		mCompanyLogo_SequenceEntityModifier = new SequenceEntityModifier(
				new DelayModifier(1f),
				new ParallelEntityModifier(new ScaleAtModifier(
						mEachAnimationDuration, 25f, mEachScaleToSize, 0.5f, 0.5f),
						new FadeInModifier(mEachAnimationDuration)),
				new DelayModifier(mEachAnimationPauseDuration),
				new ParallelEntityModifier(new ScaleAtModifier(
						mEachAnimationDuration, mEachScaleToSize, 0f, 0.5f, 0.5f),
						new FadeOutModifier(mEachAnimationDuration)));
		
		mAndEngineLogo_SequenceEntityModifier = new SequenceEntityModifier(
				new DelayModifier(2f),
				new ParallelEntityModifier(new ScaleAtModifier(
						mEachAnimationDuration, 25f, mEachScaleToSize, 0.5f, 0.5f),
						new FadeInModifier(mEachAnimationDuration)),
				new DelayModifier(mEachAnimationPauseDuration),
				new ParallelEntityModifier(new ScaleAtModifier(
						mEachAnimationDuration, mEachScaleToSize, 0f, 0.5f, 0.5f),
						new FadeOutModifier(mEachAnimationDuration, new IEntityModifierListener() {
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
							}
							
							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								mSplashSceneText.setVisible(false);
								mCompanyLogoSprite.registerEntityModifier(mCompanyLogo_SequenceEntityModifier);
							}
						})));
		mAndEngineLogoSprite.setVisible(true);
		mAndEngineLogoSprite.setAlpha(0);
		mAndEngineLogoSprite
				.registerEntityModifier(mAndEngineLogo_SequenceEntityModifier);
		attachChild(mAndEngineLogoSprite);
		
		
		mCompanyLogoSprite.setVisible(true);
		mCompanyLogoSprite.setAlpha(0);
		attachChild(mCompanyLogoSprite);
	}

	public void unloadRes() {
		mAndEngineLogoSprite.getTextureRegion().getTexture().unload();
	}
}
