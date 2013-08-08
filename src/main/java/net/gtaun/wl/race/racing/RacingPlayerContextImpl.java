package net.gtaun.wl.race.racing;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.race.script.ScriptExecutor;
import net.gtaun.wl.race.script.ScriptExecutorFactory;
import net.gtaun.wl.race.track.TrackCheckpoint;

public class RacingPlayerContextImpl extends AbstractPlayerContext implements RacingPlayerContext
{
	private final Racing racing;
	
	private ScriptExecutor scriptExecutor;
	private TrackCheckpoint currentCheckpoint;
	
	
	public RacingPlayerContextImpl(Shoebill shoebill, EventManager rootEventManager, Player player, Racing racing, TrackCheckpoint startCheckpoint)
	{
		super(shoebill, rootEventManager, player);
		this.racing = racing;
		this.currentCheckpoint = startCheckpoint;
	}
	
	@Override
	protected void onInit()
	{
		scriptExecutor = ScriptExecutorFactory.createCheckpointScriptExecutor(player);
		
	}
	
	@Override
	protected void onDestroy()
	{
		scriptExecutor = null;
	}
	
	void onPassCheckpoint(TrackCheckpoint checkpoint)
	{
		currentCheckpoint = checkpoint;
	}

	@Override
	public ScriptExecutor getScriptExecutor()
	{
		return scriptExecutor;
	}
	
	@Override
	public boolean isCompleted()
	{
		return false;
	}
	
	@Override
	public int getPassedCheckpoints()
	{
		return currentCheckpoint.getNumber();
	}
	
	@Override
	public int getTrackCheckpoints()
	{
		return racing.getTrack().getCheckpoints().size();
	}
	
	@Override
	public float getCompletionPercent()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
