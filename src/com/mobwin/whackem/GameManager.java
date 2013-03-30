package com.mobwin.whackem;

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
	private int mCurrentScore;
	private int mMoleHitCount;
	private int mHitCount;
	private int mCurrentLevel;
	private int mMolesInLevel;
	
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
	public int getCurrentScore(){
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
	}

	public int getCurrentLevel() {
		return mCurrentLevel;
	}

	public void incrementHitCount() {
		mHitCount++;
	}
	
	public void startLevel(int level, GameScene scene)
	{
		
	}
}