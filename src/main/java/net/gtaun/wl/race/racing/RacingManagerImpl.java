package net.gtaun.wl.race.racing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.code.morphia.Datastore;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.race.RacingManager;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.track.Track;

public class RacingManagerImpl extends AbstractShoebillContext implements RacingManager
{
	private List<Racing> racings;
	

	public RacingManagerImpl(Shoebill shoebill, EventManager rootEventManager, Datastore datastore)
	{
		super(shoebill, rootEventManager);
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

	@Override
	public List<Racing> getRacings()
	{
		return Collections.unmodifiableList(racings);
	}

	@Override
	public List<Racing> getRacings(RacingStatus status)
	{
		List<Racing> list = new ArrayList<>();
		for (Racing racing : racings) if (racing.getStatus() == status) list.add(racing);
		return list;
	}

	@Override
	public Racing createRacing(Track track, Player sponsor)
	{
		Racing racing = new Racing(shoebill, rootEventManager, track, sponsor);
		racings.add(racing);
		return racing;
	}
}
