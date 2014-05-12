package net.gtaun.wl.race.track;

import net.gtaun.wl.race.racing.RacingLimit;

public class TrackSetting
{
	private RacingLimit limit;
	
	private int minPlayers;
	private int maxPlayers;
	
	
	public TrackSetting()
	{
		limit = new RacingLimit();
	}
	
	public RacingLimit getLimit()
	{
		return limit;
	}
	
	public int getMinPlayers()
	{
		return minPlayers;
	}
	
	public void setMinPlayers(int minPlayers)
	{
		this.minPlayers = minPlayers;
	}
	
	public int getMaxPlayers()
	{
		return maxPlayers;
	}
	
	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}
}
