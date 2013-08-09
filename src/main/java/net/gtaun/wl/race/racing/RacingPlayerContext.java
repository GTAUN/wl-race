package net.gtaun.wl.race.racing;

import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.race.script.ScriptExecutor;

public interface RacingPlayerContext
{
	Player getPlayer();
	Racing getRacing();
	ScriptExecutor getScriptExecutor();
	
	boolean isCompleted();

	int getPassedCheckpoints();
	int getTrackCheckpoints();
	
	float getRemainingDistance();
	float getCompletionPercent();
	
	int getRankingNumber();
	String getRankingString();

	float getTimeDiff();
}
