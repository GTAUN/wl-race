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
	
	private RacingHudWidget hudWidget;
	
	
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
		
		hudWidget = new RacingHudWidget(shoebill, rootEventManager, player, this);
		hudWidget.init();
		addDestroyable(hudWidget);
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
		TrackCheckpoint next = currentCheckpoint.getNext();
		float distance = 0.0f, cpDistance = currentCheckpoint.getDistance();
		
		if (next != null) distance = next.getLocation().distance(player.getLocation());
		if (distance > cpDistance) distance = cpDistance;
		
		float nextCheckpointCompleted = 1.0f - (distance / cpDistance);
		return ((float)getPassedCheckpoints() + nextCheckpointCompleted) / (getTrackCheckpoints() - 1);
	}
}
