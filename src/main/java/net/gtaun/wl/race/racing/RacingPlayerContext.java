package net.gtaun.wl.race.racing;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

public class RacingPlayerContext extends AbstractPlayerContext
{
	public RacingPlayerContext(Shoebill shoebill, EventManager rootEventManager, Player player)
	{
		super(shoebill, rootEventManager, player);
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
