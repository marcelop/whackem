package com.mobwin.whackem;

import org.andengine.engine.Engine;
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
import org.andengine.entity.text.Text;
import org.andengine.ui.activity.BaseGameActivity;

import tv.ouya.console.api.OuyaController;
import tv.ouya.console.api.OuyaFacade;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.mobwin.whackem.scenes.CreditsScene;
import com.mobwin.whackem.scenes.GameScene;
import com.mobwin.whackem.scenes.MainMenuScene;
import com.mobwin.whackem.scenes.SplashScene;

public class MainActivity extends BaseGameActivity {

	//====================================================
	// CONSTANTS
	//====================================================
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static MainActivity activity;
	// Your developer id can be found in the Developer Portal
    public static final String DEVELOPER_ID = "558982c4-6083-4828-8246-b661697e6711";
	

	//====================================================
	// VARIABLES
	//====================================================
	// We'll be creating 1 scene for our main menu and one for our splash image
	public Scene mMenuScene;
	public SplashScene mSplashScene;
	public GameScene mGameScene;
	public CreditsScene mCreditsScene;
	
	private Camera mCamera;
	
	// These two text objects will represent each of our two scenes to be displayed
	Text mMenuSceneText;
	
	
	//====================================================
	// INITIALIZING OUYA CONTROLLER
	//====================================================	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OuyaController.init(this);
        activity = this;
        
        OuyaFacade.getInstance().init(this, DEVELOPER_ID);
        // Initializing User Data
        UserData.getInstance().init(this);
    }
    
	//====================================================
	// EMULATING THE OUYA CONTROLLER WITH THE KEYBOARD
	//====================================================	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) {
		case 29:
			return this.onKeyDown(OuyaController.BUTTON_O, event);
		case 45:
			return this.onKeyDown(OuyaController.BUTTON_U, event);
		case 51:
			return this.onKeyDown(OuyaController.BUTTON_Y, event);
		case 47:
			return this.onKeyDown(OuyaController.BUTTON_A, event);
		default:
	    	break;
		}
    	
    	if(mEngine.getScene().getClass().equals(GameScene.class))
    	{
    		((GameScene) mEngine.getScene()).onKeyDown(keyCode, event);
    		return true;
    	}
    	
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	switch (keyCode) {
		case 29:
			return this.onKeyUp(OuyaController.BUTTON_O, event);
		case 45:
			return this.onKeyUp(OuyaController.BUTTON_U, event);
		case 51:
			return this.onKeyUp(OuyaController.BUTTON_Y, event);
		case 47:
			return this.onKeyUp(OuyaController.BUTTON_A, event);
		default:
			break;
		}
    	if(mEngine.getScene().getClass().equals(MainMenuScene.class))
    	{
    		((MainMenuScene) mEngine.getScene()).onKeyUp(keyCode, event);
    		return true;
    	}
    	else if(mEngine.getScene().getClass().equals(CreditsScene.class))
    	{
    		((CreditsScene) mEngine.getScene()).onKeyUp(keyCode, event);
    		return true;
    	}
    	else if(mEngine.getScene().getClass().equals(GameScene.class))
    	{
    		((GameScene) mEngine.getScene()).onKeyUp(keyCode, event);
    		return true;
    	}
    	
    	return super.onKeyUp(keyCode, event);
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
		
		ResourceManager.getInstance().loadMenuTextures(mEngine, getApplicationContext());
		ResourceManager.getInstance().loadSounds(mEngine, getApplicationContext());

		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}


	//====================================================
	// CREATE SCENE
	//====================================================
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		
		mSplashScene = new SplashScene(mEngine, getApplicationContext());
		
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
//		mEngine.registerUpdateHandler(new TimerHandler(4, new ITimerCallback(){
//		
//		// Override ITimerCallback's 'onTimePassed' method to allow
//		// us to control what happens after the timer duration ends
//		@Override
//		public void onTimePassed(TimerHandler pTimerHandler) {
//			// When 4 seconds is up, switch to our menu scene
//			mEngine.setScene(mMenuScene);
//			}
//		}));
		
		
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
		
		mEngine.registerUpdateHandler(new TimerHandler(7f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mMenuScene =  new MainMenuScene(mEngine);
				
				mGameScene = new GameScene(mEngine);
				
				mGameScene.populate(mEngine);
				
				mEngine.setScene(mMenuScene);
				
				mCreditsScene = new CreditsScene(mEngine);
				
				mSplashScene.unloadRes();
				
			}
		}));

		// Initialize the game manager
		GameManager.getInstance();
		
		pOnPopulateSceneCallback.onPopulateSceneFinished();	
	}
	
	@Override
	protected void onPause() {
		if(ResourceManager.getInstance().mIntroMusic != null)
			ResourceManager.getInstance().mIntroMusic.pause();
		if(ResourceManager.getInstance().mGameMusic != null)
			ResourceManager.getInstance().mGameMusic.pause();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		ResourceManager.getInstance().unloadMenuTextures();
		ResourceManager.getInstance().unloadGameTextures();
		finish();
		super.onStop();
	}

	public Scene getMenuScene() {
		return mMenuScene;
	}

	public SplashScene getSplashScene() {
		return mSplashScene;
	}

	public GameScene getGameScene() {
		return mGameScene;
	}
	
	public Engine getEngine()
	{
		return mEngine;
	}
	
	@Override
	protected void onDestroy() {
		OuyaFacade.getInstance().shutdown();
		super.onDestroy();
	}
}
