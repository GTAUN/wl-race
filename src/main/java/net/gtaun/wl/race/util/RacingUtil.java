package net.gtaun.wl.race.util;

import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.race.track.Track;

public final class RacingUtil
{
	public static String getDefaultName(Player player, Track track)
	{
		return player.getName() + "'s Racing";
	}
	
	private RacingUtil()
	{
		
	}
}
