package net.gtaun.wl.race.racing;

import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.race.track.Track;

public class Racing extends AbstractShoebillContext
{
	private Track track;
	private Player sponsor;
	
	private List<Player> players;
	
	
	public Racing(Shoebill shoebill, EventManager rootEventManager, Track track, Player player)
	{
		super(shoebill, rootEventManager);
	}

	@Override
	protected void onInit()
	{
		
	}

	@Override
	protected void onDestroy()
	{
		
	}
}
