package net.gtaun.wl.race.racing;

import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.race.script.ScriptExecutor;
import net.gtaun.wl.race.script.ScriptExecutorFactory;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;

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
	public float getRemainingDistance()
	{
		TrackCheckpoint next = currentCheckpoint.getNext();
		float distance = 0.0f, cpDistance = currentCheckpoint.getNextDistance();
		
		float nextTotalDistance = (next != null) ? next.getTotalDistance() : 0.0f;
		if (next != null) distance = next.getLocation().distance(player.getLocation());
		if (distance > cpDistance) distance = cpDistance;
		
		return nextTotalDistance + distance;
	}
	
	@Override
	public float getCompletionPercent()
	{
		TrackCheckpoint first = racing.getTrack().getCheckpoints().get(0);
		return 1.0f - (getRemainingDistance() / first.getTotalDistance());
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
	
	@Override
	public float getTimeDiff()
	{
		List<RacingPlayerContext> rankedList = racing.getRacingRankedList();
		int index = rankedList.indexOf(this);
		if (index <= 0) return 0.0f;
		
		float speed;
		VehicleManagerService service = shoebill.getServiceStore().getService(VehicleManagerService.class);
		if (service == null) speed = player.getVelocity().speed3d() * 50;
		else
		{
			OncePlayerVehicleStatistic stat = service.getPlayerCurrentOnceStatistic(player);
			speed = (float) (stat.getDriveOdometer() / stat.getDriveSecondCount());
		}
		
		RacingPlayerContext prev = rankedList.get(index-1);
		float distanceDiff = getRemainingDistance() - prev.getRemainingDistance();
		
		float diff = distanceDiff / speed;
		return diff >= 0.0f ? diff : 0.0f;
	}
}
