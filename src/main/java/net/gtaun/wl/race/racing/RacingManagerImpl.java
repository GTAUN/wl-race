package net.gtaun.wl.race.racing;

import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.Datastore;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.race.RacingManager;
import net.gtaun.wl.race.track.Track;

public class RacingManagerImpl extends AbstractShoebillContext implements RacingManager
{
	private List<Racing> waitingRacings;
	private List<Racing> racings;
	

	public RacingManagerImpl(Shoebill shoebill, EventManager rootEventManager, Datastore datastore)
	{
		super(shoebill, rootEventManager);
		waitingRacings = new ArrayList<>();
		racings = new ArrayList<>();
	}

	@Override
	protected void onInit()
	{
		
	}

	@Override
	protected void onDestroy()
	{
		
	}
	
	public Racing createRacing(Track track, Player sponsor)
	{
		Racing racing = new Racing(shoebill, rootEventManager, track, sponsor);
		return racing;
	}
}
