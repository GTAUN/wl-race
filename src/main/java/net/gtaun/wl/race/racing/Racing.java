package net.gtaun.wl.race.racing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.script.ScriptException;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.RaceCheckpoint;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.checkpoint.RaceCheckpointEnterEvent;
import net.gtaun.shoebill.event.checkpoint.RaceCheckpointLeaveEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.wl.race.script.ScriptExecutor;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackRaceCheckpoint;
import net.gtaun.wl.race.track.TrackCheckpoint;

import org.apache.commons.lang3.StringUtils;

public class Racing extends AbstractShoebillContext
{
	public enum RacingStatus
	{
		WAITING,
		RACING,
		ENDED,
	}
	
	
	private final RacingManagerImpl manager;
	
	private final Track track;
	private final Player sponsor;
	
	private final SortedSet<TrackRaceCheckpoint> checkpoints;
	
	private List<Player> players;
	private List<Player> finishedPlayers;
	
	private Map<Player, RacingPlayerContext> playerContexts;
	
	private String name;
	private RacingStatus status;
	
	
	Racing(Shoebill shoebill, EventManager rootEventManager, RacingManagerImpl racingManager, Track track, Player sponsor)
	{
		super(shoebill, rootEventManager);
		this.manager = racingManager;
		this.track = track;
		this.sponsor = sponsor;
		
		checkpoints = track.generateRaceCheckpoints();
		
		players = new ArrayList<>();
		finishedPlayers = new ArrayList<>();
		
		playerContexts = new HashMap<>();

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
		manager.destroyRacing(this);
		for (RacingPlayerContext ctx : playerContexts.values()) ctx.destroy();
		playerContexts.clear();
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
	
	public void join(Player player)
	{
		if (manager.isPlayerInRacing(player)) return;
		manager.joinRacing(this, player);
		players.add(player);
	}
	
	public void leave(Player player)
	{
		manager.leaveRacing(this, player);
		players.remove(player);
	}
	
	public void begin()
	{
		if (status != RacingStatus.WAITING) return;
		if (checkpoints.size() == 0) return;
		
		status = RacingStatus.RACING;
		init();
		
		RaceCheckpoint first = checkpoints.first();
		for (Player player : players) 
		{
			RacingPlayerContext context = new RacingPlayerContext(shoebill, rootEventManager, player);
			context.init();
			
			playerContexts.put(player, context);
			
			player.playSound(1057, player.getLocation());
			player.setRaceCheckpoint(first);
		}
	}
	
	public void end()
	{
		if (status != RacingStatus.RACING) return;
		status = RacingStatus.ENDED;
		
		destroy();
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerEnterRaceCheckpoint(RaceCheckpointEnterEvent event)
		{
			Player player = event.getPlayer();
			if (!playerContexts.containsKey(player)) return;

			TrackRaceCheckpoint checkpoint = (TrackRaceCheckpoint) event.getCheckpoint();
			player.playSound(1038, player.getLocation());
			
			TrackCheckpoint trackCheckpoint = checkpoint.trackCheckpoint;
			String script = trackCheckpoint.getScript();
			if (!StringUtils.isBlank(script))
			{
				ScriptExecutor executor = playerContexts.get(player).getScriptExecutor();
				
				try
				{
					executor.execute(script);
				}
				catch (ScriptException e)
				{
					int lineNum = e.getLineNumber();
					int colNum = e.getColumnNumber();
					String line = StringUtils.split(script, '\n') [lineNum-1];
					if (colNum != -1) line = line.substring(0, colNum) + "<ERROR>" + line.substring(colNum, line.length());
					player.sendMessage(Color.RED, "%1$s: 赛道 %2$s (检查点 %3$d): 脚本运行到第 %4$d 行时候发生错误。", "赛车系统", track.getName(), trackCheckpoint.getNumber(), lineNum);
					player.sendMessage(Color.RED, "%1$s: 赛道 %2$s (检查点 %3$d): 错误代码: %4$s 。", "赛车系统", track.getName(), trackCheckpoint.getNumber(), line);
				}
			}
			
			RaceCheckpoint next = checkpoint.getNext();
			player.setRaceCheckpoint(next);
			
			if (next == null)
			{
				players.remove(player);
				finishedPlayers.add(player);
			}
		}
		
		protected void onPlayerLeaveRaceCheckpoint(RaceCheckpointLeaveEvent event)
		{
			
		}
	};
}
