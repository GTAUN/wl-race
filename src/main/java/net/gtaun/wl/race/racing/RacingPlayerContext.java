package net.gtaun.wl.race.racing;

import net.gtaun.wl.race.script.ScriptExecutor;

public interface RacingPlayerContext
{
	ScriptExecutor getScriptExecutor();
	
	boolean isCompleted();

	int getPassedCheckpoints();
	int getTrackCheckpoints();
	
	float getCompletionPercent();
}
