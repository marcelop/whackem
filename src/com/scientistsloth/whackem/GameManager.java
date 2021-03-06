package com.scientistsloth.whackem;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseElasticOut;
import org.json.JSONException;
import org.json.JSONObject;

import tv.ouya.console.api.CancelIgnoringOuyaResponseListener;
import tv.ouya.console.api.OuyaEncryptionHelper;
import tv.ouya.console.api.OuyaFacade;
import tv.ouya.console.api.OuyaResponseListener;
import tv.ouya.console.api.Product;
import tv.ouya.console.api.Purchasable;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.scientistsloth.whackem.MenuItem.IMenuHandler;
import com.scientistsloth.whackem.scenes.EndLevelMenu;
import com.scientistsloth.whackem.scenes.GameScene;
import com.scientistsloth.whackem.scenes.GameScene.MoleType;

public class GameManager {
	
	/* Since this class is a singleton, we must declare an instance
	 * of this class within itself. The singleton will be instantiated
	 * a single time during the course of an application's full life-cycle
	 */
	private static GameManager INSTANCE;
	
	private static final int INITIAL_SCORE = 0;
	private static final int INITIAL_MOLE_COUNT = 0;
	private static final int INITIAL_HIT_COUNT = 0;
	private static final int INITIAL_LEVEL = 0;

	protected static final int UNLOCK_GAME_LEVEL = 10;
	
	/* The game manager should keep track of certain data involved in
	 * our game. This particular game manager holds data for score, bird
	 * counts and enemy counts.
	 */
	private long mCurrentScore;
	private int mMoleHitCount;
	public int mMoleHitCountThisLevel;
	private int mHitCount;
	public int mHitCountThisLevel;
	private int mCurrentLevel;
	private int mMolesInLevel;
	public int mMissedMoles;
	private int mMaxMissedMoles;
	private int mMaxSimultaneousMoles;
	private GameState mState;
	
	private boolean mMusicEnabled = true;
	
	private Sprite mGameOver = null;
	private MenuBuilder mGameOverMenu = null;
	private Entity mGameOverMenuEntity = null;

	private int mMolesUp;
	
	private PublicKey mPublicKey;
	
	enum GameState
	{
		IN_GAME,
		FINISHING,
		OFF_GAME
	}
	
	// The constructor fills the product list
	GameManager()
	{
		requestProductsList();
	}
	
	public void requestProductsList()
	{
		if (OuyaFacade.getInstance().isRunningOnOUYAHardware() && 
				!UserData.getInstance().isGameUnlocked()){
			OuyaFacade.getInstance().requestProductList(PRODUCT_ID_LIST, productListListener);
			// Create a PublicKey object from the key data downloaded from the developer portal.
			try {
				// Read in the key.der file (downloaded from the developer portal)
				InputStream inputStream = MainActivity.activity.getResources().openRawResource(R.raw.key);
				byte[] applicationKey = new byte[inputStream.available()];
				inputStream.read(applicationKey);
				inputStream.close();
				// Create a public key
				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(applicationKey);
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				mPublicKey = keyFactory.generatePublic(keySpec);
			} catch (Exception e) {
				Log.e("wackem", "Unable to create encryption key", e);
			}
		}
	}
	
	/* For a singleton class, we must have some method which provides
	 * access to the class instance. getInstance is a static method,
	 * which means we can access it globally (within other classes).
	 * If the GameManager has not yet been instantiated, we create a 
	 * new one.
	 */
	public static GameManager getInstance(){
		if(INSTANCE == null){
			INSTANCE = new GameManager();
		}
		return INSTANCE;
	}
	
	// get the current score
	public long getCurrentScore(){
		return this.mCurrentScore;
	}
	
	// increase the current score, most likely when an enemy is destroyed
	public void incrementScore(int pIncrementBy){
		mCurrentScore += pIncrementBy;
	}
	
	public void incrementMoleHitCount()
	{
		mMoleHitCount++;
		mMoleHitCountThisLevel++;
	}
	
	public void incrementMissedMoleCount()
	{
		mMissedMoles++;
		if(mMissedMoles > mMaxMissedMoles)
			mState = GameState.FINISHING;
	}
	
	public int getCurrentLevel() {
		return mCurrentLevel;
	}

	public void incrementHitCount() {
		mHitCount++;
		mHitCountThisLevel++;
	}
	
	public void decrementMoleCount() {
		mMolesInLevel--;
		if(mMolesInLevel <= 0)
			mState = GameState.FINISHING;
	}
	
	// Any time a bird is launched, we decrement our bird count
	public boolean makeMoleClimb(){
		if (mMolesInLevel > 0)
		{
			mMolesInLevel -= 1;
			return true;
		}
		return false;
	}
	
	public float getAccuracy()
	{
		return mHitCount == 0 ? 0 : mMoleHitCount *100/ mHitCount;
	}
	
	public float getAccuracyThisLevel()
	{
		return mHitCountThisLevel == 0 ? 0 : mMoleHitCountThisLevel *100/ mHitCountThisLevel;
	}
	
	// Resetting the game simply means we must revert back to initial values.
	public void resetGame(){
		mCurrentScore = GameManager.INITIAL_SCORE;
		mMoleHitCount = INITIAL_HIT_COUNT;
		mHitCount = INITIAL_HIT_COUNT;
		mCurrentLevel = INITIAL_LEVEL;
		mMolesInLevel = INITIAL_MOLE_COUNT;	
		mMolesUp = INITIAL_MOLE_COUNT;
		mMissedMoles = INITIAL_MOLE_COUNT;
		mMaxMissedMoles = INITIAL_MOLE_COUNT;
		mMaxSimultaneousMoles = INITIAL_MOLE_COUNT;
	}
	
	public void startLevel(int level, final GameScene scene)
	{
		if (level == 0) {
			resetGame();
		}
		scene.resetHammerPosition();
		mCurrentLevel = level;
		mMolesInLevel = 10 + (int)(level/5)*3;	
		mMissedMoles = INITIAL_MOLE_COUNT;
		mMolesUp = INITIAL_MOLE_COUNT;
		mMaxMissedMoles = (int) (mMolesInLevel*0.3f);
		mMaxSimultaneousMoles = 2 + (int)(level / 3);
		mHitCountThisLevel = 0;
		mMoleHitCountThisLevel = 0;
		
		
		for (int i = 0; i < scene.moles.length; i++) {
			for (int j = 0; j < scene.moles[i].length; j++) {
				GameScene.MoleInstance mole = scene.moles[i][j];
				mole.moleSprite.clearEntityModifiers();
				mole.moveToHiddenPosition();
			}
		}
		
		mState = GameState.IN_GAME;
		
		scene.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				scene.curTimeElapsed  += pSecondsElapsed;
				if (scene.curTimeElapsed >= 0.3f)
				{
					int m = Math.abs(scene.mRand.nextInt())%3, 
					n = Math.abs(scene.mRand.nextInt())%3;
					if (mCurrentLevel > 3)
					{
						//add 3% chance of finding an ally mole every 2 levels. start with 10% at level 3
						if (scene.mRand.nextInt(100) < 10)
							scene.moles[m][n].setMoleType(MoleType.ALLY);
					}
					scene.moles[m][n].makeMoleClimb();
					scene.curTimeElapsed = 0;

					//Verify if we can finish the game
					if(mState == GameState.FINISHING)
					{
						int total_finished = 0;
						for (int i = 0; i < scene.moles.length; i++) {
							for (int j = 0; j < scene.moles[i].length; j++) {
								GameScene.MoleInstance mole = scene.moles[i][j];
								if(mole.state == GameScene.MoleState.HIDDEN || mole.state == GameScene.MoleState.HIT || mole.state == GameScene.MoleState.HIDING)
									total_finished++;
							}
						}
						if(total_finished == 9)
						{
							mState = GameState.OFF_GAME;
							scene.clearUpdateHandlers();

							// Verify if the player won the level or not
							if(mMissedMoles > mMaxMissedMoles)
							{
								//Game Over

								displayEndLevel(scene, EndLevelMenu.GAMEOVER);
								UserData.getInstance().setHighScore(getCurrentScore());
							}
							else
							{
								//Yay! Next Level
								mCurrentLevel++;
								displayEndLevel(scene, mCurrentLevel);
								UserData.getInstance().unlockLevel(mCurrentLevel);
								ResourceManager.getInstance().mLevelUpSound.play();
								//displayNextLevel(mCurrentLevel, scene);

								scene.registerEntityModifier(new DelayModifier(1f, new IEntityModifierListener() {
									@Override
									public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
									@Override
									public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
										if(mCurrentLevel == UNLOCK_GAME_LEVEL && !UserData.getInstance().isGameUnlocked())
										{
											try {
												requestPurchase();
											} catch (Exception e) {
												if(mGameUnlockProduct == null)
													requestProductsList();

												MainActivity.activity.runOnUiThread(new Runnable() {
													@Override
													public void run() {
														showPurchaseFailedTryAgain();	
													}
												});


											}
										}
									}
								}));							
							}
						}
					}
				}
				scene.mGameSceneText.setText("LEVEL:   " + GameManager.getInstance().getCurrentLevel() + "\nSCORE: " + GameManager.getInstance().getCurrentScore());// + " ACCURACY: " + GameManager.getInstance().getAccuracy());

			}
		});
	}
	
	void displayEndLevel(final GameScene scene, int level) {

		((GameScene) scene).showEndLevelMenu(new EndLevelMenu(scene.getEngine(), level, mCurrentLevel));		
	}
	
	//Display gameover text, as well as score, if it's a new highscore, post on facebook option and return to start
	protected void displayGameOver(final GameScene scene) {

		if (mGameOver == null)
		{
			mGameOver = new Sprite(MainActivity.WIDTH/2, MainActivity.HEIGHT/2, ResourceManager.getInstance().mGameOver, scene.getEngine().getVertexBufferObjectManager());

			float x = MainActivity.WIDTH / 2;
			float y = ResourceManager.getInstance().mFont.getLineHeight() / 2 + 190;
			MenuItem[] items = new MenuItem[3];
			items[0] = new MenuItem("Share!");			
			items[0].registerHandler(new IMenuHandler() {

				@Override
				public void onChange(MenuItem sender, int selected) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onClick(MenuItem sender) {
					// TODO Auto-generated method stub
				}
			});
			items[1] = new MenuItem("Restart game");
			items[1].registerHandler(new IMenuHandler() {

				@Override
				public void onChange(MenuItem sender, int selected) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onClick(MenuItem sender) {
					// TODO Auto-generated method stub
					resetGame();
				}
			});
			items[2] = new MenuItem("Back to main menu");
			items[2].registerHandler(new IMenuHandler() {

				@Override
				public void onChange(MenuItem sender, int selected) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void onClick(MenuItem sender) {
					// TODO Auto-generated method stub
				}
			});			
			mGameOverMenuEntity = new Entity( -MainActivity.WIDTH, MainActivity.HEIGHT / 2);
			mGameOverMenu = new MenuBuilder(mGameOverMenuEntity, scene.getEngine(), x, y, items, ResourceManager.getInstance().mFont, ResourceManager.getInstance().mGameHammer,ResourceManager.getInstance().mGameHammer);
			
			scene.attachChild(mGameOver);
			scene.attachChild(mGameOverMenuEntity);
		}

		mGameOver.registerEntityModifier(new DelayModifier(2, new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				pItem.setVisible(true);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				//pItem.setVisible(false);
				pItem.registerEntityModifier(new MoveModifier(1f, pItem.getX(), pItem.getY(), pItem.getX(), MainActivity.HEIGHT - (MainActivity.HEIGHT / 2)/4, EaseElasticOut.getInstance()));
			}
		}));
		mGameOverMenuEntity.registerEntityModifier(new DelayModifier(2, new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				pItem.setVisible(true);
				pItem.registerEntityModifier(new MoveModifier(1f, pItem.getX(),  MainActivity.HEIGHT/2-100, 0, MainActivity.HEIGHT/2-100, EaseBackOut.getInstance()));
			}
		}));

	}

	protected void displayNextLevel(int level, final GameScene scene) {
		scene.mGameSceneLevel.setText("LEVEL " + level);
		scene.mGameSceneLevel.registerEntityModifier(new DelayModifier(2, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				scene.mGameSceneLevel.setVisible(true);
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				scene.mGameSceneLevel.setVisible(false);
			}
		}));
		
	}

	public boolean isInGame() {
		return mState == GameState.IN_GAME;
	}

	public boolean canMoleClimb() {
		if(mMolesUp >= mMaxSimultaneousMoles && mMissedMoles <= mMaxMissedMoles)
			return false;
		else
		{
			mMolesUp++;
			return true;
		}
	}
	
	public void decrementMolesUp()
	{
		mMolesUp--;
	}
	
	public boolean isMusicEnabled()
	{
		return mMusicEnabled;
	}
	
	public void toggleMusic()
	{
		mMusicEnabled = !mMusicEnabled;
	}
	
	
	/* ====================================
	 * ======= OUYA STORE STUFF ===========
	 * ====================================
	 */
	
	  // This is the set of product IDs which our app knows about
	public static final List<Purchasable> PRODUCT_ID_LIST =
			Arrays.asList(new Purchasable("WHACK_EM_GAME"));
	
	public static Product mGameUnlockProduct = null;

	public OuyaResponseListener<ArrayList<Product>> productListListener =
			new CancelIgnoringOuyaResponseListener<ArrayList<Product>>() {
		@Override
		public void onSuccess(ArrayList<Product> products) {
			for(Product p : products) {
				mGameUnlockProduct = p;
				Log.d("Product", p.getName() + " costs " + p.getPriceInCents());
			}
		}

		@Override
		public void onFailure(int errorCode, String errorMessage, Bundle errorBundle) {
			Log.d("Error", errorMessage);
			mGameUnlockProduct = null;
		}
	};
	
//	public CancelIgnoringOuyaResponseListener<String> purchaseListener =
//			new CancelIgnoringOuyaResponseListener<String>() {
//		@Override
//		public void onSuccess(String response) {
//			try {
//				OuyaEncryptionHelper helper = new OuyaEncryptionHelper();
//
//				JSONObject result = new JSONObject(response);
//
//				String id = helper.decryptPurchaseResponse(result, mPublicKey);
//				
//				/*** UNLOCK THE GAME ****/
//				
//				UserData.getInstance().unlockGame();
//				
//
//				Log.d("Purchase", "Congrats you bought: " + mGameUnlockProduct.getName());
//			} catch (Exception e) {
//				Log.e("Purchase", "Your purchase failed.", e);
//			}
//		}
//	@Override
//	public void onFailure(int errorCode, String errorMessage, Bundle errorBundle) {
//		Log.d("Error", errorMessage);
//	}
//};
	
	public OuyaResponseListener<String> purchaseListener = new OuyaResponseListener<String>() {
		
		@Override
		public void onSuccess(String response) {
			try {
			OuyaEncryptionHelper helper = new OuyaEncryptionHelper();

			JSONObject result = new JSONObject(response);

			String id = helper.decryptPurchaseResponse(result, mPublicKey);
			
			/*** UNLOCK THE GAME ****/
			
			UserData.getInstance().unlockGame();
			
			showThankYouDialog();
			
			Log.d("Purchase", "Congrats you bought: " + mGameUnlockProduct.getName());
			} catch (Exception e) {
				Log.e("Purchase", "Your purchase failed.", e);
			}
		}
		
		@Override
		public void onFailure(int arg0, String arg1, Bundle arg2) {
			Log.d("Error", arg1);
		}
		
		@Override
		public void onCancel() {
			if(MainActivity.activity.getEngine().getScene().getClass().equals(GameScene.class) && !UserData.getInstance().isGameUnlocked())
				showSuggestBuyAlert();
		}
	};

	public void requestPurchase()
	        throws GeneralSecurityException, UnsupportedEncodingException, JSONException {
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");

	        // This is an ID that allows you to associate a successful purchase with
	        // it's original request. The server does nothing with this string except
	        // pass it back to you, so it only needs to be unique within this instance
	        // of your app to allow you to pair responses with requests.
	        String uniqueId = Long.toHexString(sr.nextLong());

	        JSONObject purchaseRequest = new JSONObject();
	        purchaseRequest.put("uuid", uniqueId);
	        purchaseRequest.put("identifier", mGameUnlockProduct.getIdentifier());
	        // This value is only needed for testing, not setting it results in a live purchase
//	        purchaseRequest.put("testing", "true"); 
	        String purchaseRequestJson = purchaseRequest.toString();

	        byte[] keyBytes = new byte[16];
	        sr.nextBytes(keyBytes);
	        SecretKey key = new SecretKeySpec(keyBytes, "AES");

	        byte[] ivBytes = new byte[16];
	        sr.nextBytes(ivBytes);
	        IvParameterSpec iv = new IvParameterSpec(ivBytes);

	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
	        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
	        byte[] payload = cipher.doFinal(purchaseRequestJson.getBytes("UTF-8"));

	        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
	        cipher.init(Cipher.ENCRYPT_MODE, mPublicKey);
	        byte[] encryptedKey = cipher.doFinal(keyBytes);

	        Purchasable purchasable =
	                new Purchasable(
	                		mGameUnlockProduct.getIdentifier(),
	                        Base64.encodeToString(encryptedKey, Base64.NO_WRAP),
	                        Base64.encodeToString(ivBytes, Base64.NO_WRAP),
	                        Base64.encodeToString(payload, Base64.NO_WRAP) );

//	        synchronized (mOutstandingPurchaseRequests) {
//	            mOutstandingPurchaseRequests.put(uniqueId, product);
//	        }
	        Log.d("whackem", "Requesting purchase..."); 
	        OuyaFacade.getInstance().requestPurchase(purchasable, purchaseListener);
	    }

	protected void showSuggestBuyAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.activity);

		// Setting Dialog Title
		alertDialog.setTitle("Funding the Fun");

		// Setting Dialog Message
		alertDialog.setMessage("Hope you liked whacking around! Please unlock the game for unlimited play.");

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("Unlock",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog
//				Toast.makeText(MainActivity.activity.getApplicationContext(),
//						"You clicked on Unlock", Toast.LENGTH_SHORT)
//						.show();
				try {
					requestPurchase();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		});
		// Setting Negative "NO" Btn
		alertDialog.setNegativeButton("Back",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog
//				Toast.makeText(MainActivity.activity.getApplicationContext(),
//						"You clicked on Back", Toast.LENGTH_SHORT)
//						.show();
				dialog.cancel();
				displayEndLevel(MainActivity.activity.mGameScene, EndLevelMenu.GAMEOVER);
				UserData.getInstance().setHighScore(getCurrentScore());
			}
		});

		// Showing Alert Dialog
		alertDialog.show();
	}
	
	protected void showThankYouDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.activity);

		// Setting Dialog Title
		alertDialog.setTitle("Funding the Fun");

		// Setting Dialog Message
		alertDialog.setMessage("Thanks a lot!! We love you!! <3\nNow don't stop whackin'!");

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog
//				MainActivity.activity.runOnUpdateThread(new Runnable() {
//					@Override
//					public void run() {
//						startLevel(mCurrentLevel, MainActivity.activity.mGameScene);		
//					}
//				});
			}
		});

		// Showing Alert Dialog
		alertDialog.show();
	}
	

	protected void showPurchaseFailedTryAgain() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.activity);

		// Setting Dialog Title
		alertDialog.setTitle("Funding the Fun");

		// Setting Dialog Message
		alertDialog.setMessage("To continue you must unlock the game, but it looks like your connection isn't playing nice. Please try again.");

		// Setting Icon to Dialog
		//alertDialog2.setIcon(R.drawable.delete);

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("Unlock",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog
//				Toast.makeText(MainActivity.activity.getApplicationContext(),
//						"You clicked on Unlock", Toast.LENGTH_SHORT)
//						.show();
				try {
					requestPurchase();
				} catch (Exception e) {
					dialog.cancel();
					showPurchaseFailedTryAgain();
				} 
			}
		});
		// Setting Negative "NO" Btn
		alertDialog.setNegativeButton("End Game",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog
//				Toast.makeText(MainActivity.activity.getApplicationContext(),
//						"You clicked on Back", Toast.LENGTH_SHORT)
//						.show();
				dialog.cancel();
				MainActivity.activity.runOnUpdateThread(new Runnable() {
					@Override
					public void run() {
						if(MainActivity.activity.mGameScene.mEndLevelMenu != null)
						{
							MainActivity.activity.mGameScene.detachChild(MainActivity.activity.mGameScene.mEndLevelMenu);
							MainActivity.activity.mGameScene.mEndLevelMenu = null;
						}
						displayEndLevel(MainActivity.activity.mGameScene, EndLevelMenu.GAMEOVER);
						UserData.getInstance().setHighScore(getCurrentScore());
					}
				});
				
			}
		});

		// Showing Alert Dialog
		alertDialog.show();
	}

}
