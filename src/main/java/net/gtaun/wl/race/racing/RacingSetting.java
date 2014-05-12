package net.gtaun.wl.race.racing;

import net.gtaun.wl.race.racing.Racing.DeathRule;
import net.gtaun.wl.race.racing.Racing.RacingType;
import net.gtaun.wl.race.track.Track;

public class RacingSetting
{
	private final Track track;
	
	private RacingType racingType;
	private int departureInterval;
	private RacingLimit limit;
	private DeathRule deathRule;
	
	private int maxPlayers;
	private String password;
	
	
	public RacingSetting(Track track)
	{
		this.track = track;
		racingType = RacingType.NORMAL;
		limit = new RacingLimit();
		deathRule = DeathRule.WAIT_AND_RETURN;
		password = "";
	}
	
	public Track getTrack()
	{
		return track;
	}
	
	public RacingType getRacingType()
	{
		return racingType;
	}
	
	public void setRacingType(RacingType normal)
	{
		this.racingType = normal;
	}
	
	public int getDepartureInterval()
	{
		return departureInterval;
	}
	
	public void setDepartureInterval(int interval)
	{
		this.departureInterval = interval;
	}
	
	public RacingLimit getLimit()
	{
		return limit;
	}
	
	public DeathRule getDeathRule()
	{
		return deathRule;
	}
	
	public void setDeathRule(DeathRule deathRule)
	{
		this.deathRule = deathRule;
	}
	
	public int getMaxPlayers()
	{
		return maxPlayers;
	}
	
	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
}
