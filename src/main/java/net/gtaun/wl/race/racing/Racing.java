package net.gtaun.wl.race.racing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.checkpoint.RaceCheckpointEnterEvent;
import net.gtaun.shoebill.event.checkpoint.RaceCheckpointLeaveEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.RaceCheckpoint;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.wl.race.track.Track;

public class Racing extends AbstractShoebillContext
{
	public enum RacingStatus
	{
		WAITING,
		RACING,
		ENDED,
	}
	
	
	private final Track track;
	private final Player sponsor;
	
	private final List<RaceCheckpoint> checkpoints;
	
	private List<Player> players;
	private String name;
	
	private RacingStatus status;
	
	
	Racing(Shoebill shoebill, EventManager rootEventManager, Track track, Player sponsor)
	{
		super(shoebill, rootEventManager);
		this.track = track;
		this.sponsor = sponsor;
		
		checkpoints = track.generateRaceCheckpoints();
		
		players = new ArrayList<>();
		players.add(sponsor);
		
		status = RacingStatus.WAITING;
	}

	@Override
	protected void onInit()
	{
		eventManager.registerHandler(RaceCheckpointEnterEvent.class, playerEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(RaceCheckpointLeaveEvent.class, playerEventHandler, HandlerPriority.NORMAL);
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
	
	public RacingStatus getStatus()
	{
		return status;
	}
	
	public void start()
	{
		if (status != RacingStatus.WAITING) return;
		if (checkpoints.size() == 0) return;
		
		status = RacingStatus.RACING;
		init();
		
		RaceCheckpoint first = checkpoints.get(0);
		for (Player player : players) 
		{
			player.playSound(1057, player.getLocation());
			player.setRaceCheckpoint(first);
		}
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerEnterRaceCheckpoint(RaceCheckpointEnterEvent event)
		{
			Player player = event.getPlayer();
			if (!players.contains(player)) return;

			RaceCheckpoint checkpoint = event.getCheckpoint();
			player.playSound(1039, player.getLocation());
			
			RaceCheckpoint next = checkpoint.getNext();
			player.setRaceCheckpoint(next);
		}
		
		protected void onPlayerLeaveRaceCheckpoint(RaceCheckpointLeaveEvent event)
		{
			
		}
	};
}
