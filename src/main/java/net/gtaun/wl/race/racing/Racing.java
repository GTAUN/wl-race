package net.gtaun.wl.race.racing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.race.track.Track;

public class Racing extends AbstractShoebillContext
{
	private final Track track;
	private final Player sponsor;
	
	private List<Player> players;
	private String name;
	
	
	Racing(Shoebill shoebill, EventManager rootEventManager, Track track, Player sponsor)
	{
		super(shoebill, rootEventManager);
		this.track = track;
		this.sponsor = sponsor;
		
		players = new ArrayList<>();
		players.add(sponsor);
	}

	@Override
	protected void onInit()
	{
		
	}

	@Override
	protected void onDestroy()
	{
		
	}
	
	public Track getTrack()
	{
		return track;
	}
	
	public Player getSponsor()
	{
		return sponsor;
	}
	
	public List<Player> getPlayers()
	{
		return Collections.unmodifiableList(players);
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
}
