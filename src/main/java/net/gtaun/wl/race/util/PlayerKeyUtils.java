package net.gtaun.wl.race.util;

import net.gtaun.shoebill.object.Player;

public class PlayerKeyUtils
{
	private static final int DOUBLE_PRESS_KEY_TIMEDIFF = 1000;
	public static int getDoublePressKeyTimeDiff(Player player)
	{
		return DOUBLE_PRESS_KEY_TIMEDIFF + player.getPing()*2;
	}
	
	
	private PlayerKeyUtils()
	{
		
	}
}
