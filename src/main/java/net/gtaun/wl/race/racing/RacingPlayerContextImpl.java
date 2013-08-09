package net.gtaun.wl.race.racing;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.race.script.ScriptExecutor;
import net.gtaun.wl.race.script.ScriptExecutorFactory;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.vehicle.VehicleManagerService;

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
		
		VehicleManagerService service = shoebill.getServiceStore().getService(VehicleManagerService.class);
		if (service != null)
		{
			service.startRacingStatistic(player);
		}
	}
	
	@Override
	protected void onDestroy()
	{
		scriptExecutor = null;
		
		VehicleManagerService service = shoebill.getServiceStore().getService(VehicleManagerService.class);
		if (service != null)
		{
			service.endRacingStatistic(player);
		}
	}
	
	void onPassCheckpoint(TrackCheckpoint checkpoint)
	{
		currentCheckpoint = checkpoint;
	}

	@Override
	public Racing getRacing()
	{
		return racing;
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
	
	@Override
	public int getRankingNumber()
	{
		return racing.getRacingRankingNumber(this);
	}
	
	@Override
	public String getRankingString()
	{
		int num = getRankingNumber();
		String str;
		
		switch (num)
		{
		case 0:		str = "N/A";		break;
		case 1:		str = "1st";		break;
		case 2:		str = "2nd";		break;
		case 3:		str = "3rd";		break;
		default:	str = num + "th";	break;
		}
		
		return str;
	}
}
