package com.mobwin.whackem;

import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontUtils;
import org.andengine.ui.activity.BaseGameActivity;

import tv.ouya.console.api.OuyaController;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends BaseGameActivity {

	//====================================================
	// CONSTANTS
	//====================================================
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	
	// Instead of graphics, we'll be using these strings which will
	// represent our splash scene and menu scene
	public static final String SPLASH_STRING = "HELLO SPLASH SCREEN!";
	public static final String MENU_STRING = "HELLO MENU SCREEN!";
	public static final String MENU_SELECT_STRING = "PRESS         TO SELECT";
	
	//====================================================
	// VARIABLES
	//====================================================
	// We'll be creating 1 scene for our main menu and one for our splash image
	private Scene mMenuScene;
	private Scene mSplashScene;
	
	private Camera mCamera;
	
	// These two text objects will represent each of our two scenes to be displayed
	Text mSplashSceneText;
	Text mMenuSceneText;
	
	
	//====================================================
	// INITIALIZING OUYA CONTROLLER
	//====================================================	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("whacka", "Initializing Controller");
        OuyaController.init(this);
        Log.d("whacka", "Finished Initializing Controller");
    }
	
	//====================================================
	// CREATE ENGINE OPTIONS
	//====================================================
	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, WIDTH, HEIGHT);
		
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);
		
		// Enable sounds.
		engineOptions.getAudioOptions().setNeedsSound(true);
		// Enable music.
		engineOptions.getAudioOptions().setNeedsMusic(true);
		// Turn on Dithering to smooth texture gradients.
		engineOptions.getRenderOptions().setDithering(true);
		// Turn on MultiSampling to smooth the alias of hard-edge elements.
		engineOptions.getRenderOptions().getConfigChooserOptions().setRequestedMultiSampling(true);
		// Set the Wake Lock options to prevent the engine from dumping textures when focus changes.
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		
		mEngine = new FixedStepEngine(engineOptions, 60);
		
		return engineOptions;
	}

	//====================================================
	// CREATE RESOURCES
	//====================================================
	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {
		
		// Load our fonts.
		ResourceManager.getInstance().loadFonts(mEngine);
		ResourceManager.getInstance().loadGameTextures(mEngine, getApplicationContext());

		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}


	//====================================================
	// CREATE SCENE
	//====================================================
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		
		// Retrieve our font from the resource manager
		Font font = ResourceManager.getInstance().mFont;
		
		// Set the location of our splash 'image' (text object in this case).
		// We can use FontUtils.measureText to retrieve the width of our text
		// object in order to properly format its position
		float x = WIDTH / 2 - FontUtils.measureText(font, SPLASH_STRING) / 2;
		float y = HEIGHT / 2 - font.getLineHeight() / 2;
		
		// Create our splash scene object
		mSplashScene = new Scene();
		// Create our splash screen text object
		mSplashSceneText = new Text(x, y, font, SPLASH_STRING, SPLASH_STRING.length(), mEngine.getVertexBufferObjectManager());
		// Attach the text object to our splash scene
		mSplashScene.attachChild(mSplashSceneText);

		// We must change the value of x depending on the length of our menu
		// string now in order to keep the text centered on-screen
		x = WIDTH / 2 - FontUtils.measureText(font, MENU_STRING) / 2;
		
		// Create our main menu scene
		mMenuScene = new Scene();
		// Create our menu screen text object
		mMenuSceneText = new Text(x, y, font, MENU_STRING, MENU_STRING.length(), mEngine.getVertexBufferObjectManager());
		// Attach the text object to our menu scene
		mMenuScene.attachChild(mMenuSceneText);
		
		x = WIDTH / 2;
		y = font.getLineHeight() / 2 + 100;
		
		final Text menuSelectText = new Text(x, y, font, MENU_SELECT_STRING, MENU_SELECT_STRING.length(), mEngine.getVertexBufferObjectManager());
		mMenuScene.attachChild(menuSelectText);
		
		final Sprite a_button = new Sprite(x-30, y, 65, 80, ResourceManager.getInstance().mA_BUTTON, getVertexBufferObjectManager());
		mMenuScene.attachChild(a_button);
		
		mEngine.registerUpdateHandler(new TimerHandler(1, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler arg0) {
				menuSelectText.setVisible(!menuSelectText.isVisible());
				a_button.setVisible(!a_button.isVisible());
			}
		}));
		
		
		
		// Finally, we must callback the initial scene to be loaded (splash scene)
		pOnCreateSceneCallback.onCreateSceneFinished(mSplashScene);
	}

	//====================================================
	// POPULATE SCENE
	//====================================================
	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {
		
		// We will create a timer-handler to handle the duration
		// in which the splash screen is shown
		mEngine.registerUpdateHandler(new TimerHandler(1, new ITimerCallback(){
		
		// Override ITimerCallback's 'onTimePassed' method to allow
		// us to control what happens after the timer duration ends
		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {
			// When 4 seconds is up, switch to our menu scene
			mEngine.setScene(mMenuScene);
			}
		}));
		
		
		//Tell the controller that a new frame has started
		mEngine.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {}
			@Override
			public void onUpdate(float arg0) {
//				OuyaController.startOfFrame();
				
//				if (OuyaController.getControllerByDeviceId(1)!= null && OuyaController.getControllerByDeviceId(1).buttonChangedThisFrame(OuyaController.BUTTON_DPAD_DOWN))
//				{
//					mMenuSceneText.setVisible(false);
//				}
//				else
//					mMenuSceneText.setVisible(true);
			}
		});

		pOnPopulateSceneCallback.onPopulateSceneFinished();	
	}
}
