package com.mobwin.whackem;

import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;

import com.mobwin.whackem.scenes.GameScene;

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
	
	/* The game manager should keep track of certain data involved in
	 * our game. This particular game manager holds data for score, bird
	 * counts and enemy counts.
	 */
	private long mCurrentScore;
	private int mMoleHitCount;
	private int mHitCount;
	private int mCurrentLevel;
	private int mMolesInLevel;
	private int mMissedMoles;
	private int mMaxMissedMoles;
	private int mMaxSimultaneousMoles;
	private GameState mState;
	
	private Sprite mGameOver = null;

	private int mMolesUp;
	
	enum GameState
	{
		IN_GAME,
		FINISHING,
		OFF_GAME
	}
	
	// The constructor does not do anything for this singleton
	GameManager(){
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
		if (level == 0)
			resetGame();
		
		mCurrentLevel = level;
		mMolesInLevel = 10 + level*5;	
		mMissedMoles = INITIAL_MOLE_COUNT;
		mMolesUp = INITIAL_MOLE_COUNT;
		mMaxMissedMoles = (int) (mMolesInLevel*0.3f);
		mMaxSimultaneousMoles = (int) (2 + Math.floor(level / 15));
		
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
					scene.moles[Math.abs(scene.mRand.nextInt())%3][Math.abs(scene.mRand.nextInt())%3].makeMoleClimb(); 
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
								displayGameOver(scene);
								UserData.getInstance().setHighScore(getCurrentScore());
								resetGame();
							}
							else
							{
								//Yay! Next Level
								mCurrentLevel++;
								UserData.getInstance().unlockLevel(mCurrentLevel);
								ResourceManager.getInstance().mLevelUpSound.play();
								displayNextLevel(mCurrentLevel, scene);
								scene.registerEntityModifier(new SequenceEntityModifier(new DelayModifier(3, new IEntityModifierListener() {
									@Override
									public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
									
									@Override
									public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
										startLevel(mCurrentLevel, scene);
									}
								})));
							}
							
						}
					}
					
				}
				scene.mGameSceneText.setText("LEVEL: " + GameManager.getInstance().getCurrentLevel() + " SCORE: " + GameManager.getInstance().getCurrentScore() + " ACCURACY: " + GameManager.getInstance().getAccuracy());

			}
		});
	}

	protected void displayGameOver(final GameScene scene) {

		if (mGameOver == null)
		{
			mGameOver = new Sprite(MainActivity.WIDTH/2, MainActivity.HEIGHT/2, ResourceManager.getInstance().mGameOver, scene.getEngine().getVertexBufferObjectManager());
			scene.attachChild(mGameOver);
		}

		mGameOver.registerEntityModifier(new DelayModifier(2, new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				pItem.setVisible(true);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				pItem.setVisible(false);
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
}