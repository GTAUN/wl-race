package net.gtaun.wl.race.racing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import net.gtaun.shoebill.SampObjectFactory;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.RaceCheckpoint;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.checkpoint.RaceCheckpointEnterEvent;
import net.gtaun.shoebill.event.checkpoint.RaceCheckpointLeaveEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Timer.TimerCallback;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.wl.race.script.ScriptExecutor;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.TrackCheckpoint.TrackRaceCheckpoint;

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
	
	private List<Player> players;
	private List<Player> finishedPlayers;
	
	private Map<Player, RacingPlayerContextImpl> playerContexts;
	
	private String name;
	private RacingStatus status;
	
	private Date startTime;
	private List<RacingPlayerContext> racingRankedList;
	
	private Timer timer;
	
	
	Racing(Shoebill shoebill, EventManager rootEventManager, RacingManagerImpl racingManager, Track track, Player sponsor)
	{
		super(shoebill, rootEventManager);
		this.manager = racingManager;
		this.track = track;
		this.sponsor = sponsor;
		
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
		
		SampObjectFactory factory = shoebill.getSampObjectFactory();
		timer = factory.createTimer(1000, new TimerCallback()
		{
			@Override
			public void onTick(int factualInterval)
			{
				updateRacingRankedList();
			}
		});
		timer.start();
		addDestroyable(timer);
		
		updateRacingRankedList();
	}

	@Override
	protected void onDestroy()
	{
		manager.destroyRacing(this);
		for (RacingPlayerContextImpl ctx : playerContexts.values()) ctx.destroy();
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
		List<Player> players = new ArrayList<>(getRankingPlayers());
		players.addAll(players);
		players.addAll(finishedPlayers);
		return players;
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
	
	public Date getStartTime()
	{
		return startTime;
	}
	
	public void join(Player player)
	{
		if (manager.isPlayerInRacing(player)) return;
		manager.joinRacing(this, player);
		players.add(player);

		player.sendMessage(Color.LIGHTBLUE, "%1$s: 您已参与 %1$s 比赛，赛道为 %2$s 。", "赛车系统", getName(), track.getName());
		for (Player otherPlayer : players)
		{
			if (otherPlayer == player) continue;
			otherPlayer.sendMessage(Color.LIGHTBLUE, "%1$s: %2$s 已参与 %3$s 比赛。", "赛车系统", player.getName(), getName());
		}
	}
	
	public void leave(Player player)
	{
		manager.leaveRacing(this, player);
		
		RacingPlayerContextImpl context = playerContexts.get(player);
		if (context != null)
		{
			context.destroy();
			playerContexts.remove(player);
		}
		players.remove(player);

		if (finishedPlayers.contains(player))
		{
			player.sendMessage(Color.LIGHTBLUE, "%1$s: 您已完成 %2$s 比赛。", "赛车系统", getName());
			for (Player otherPlayer : players)
			{
				if (otherPlayer == player) continue;
				otherPlayer.sendMessage(Color.LIGHTBLUE, "%1$s: %2$s 已完成 %3$s 比赛。", "赛车系统", player.getName(), getName());
			}
		}
		else
		{
			player.sendMessage(Color.LIGHTBLUE, "%1$s: 您已离开 %2$s 比赛。", "赛车系统", getName());
			for (Player otherPlayer : players)
			{
				if (otherPlayer == player) continue;
				otherPlayer.sendMessage(Color.LIGHTBLUE, "%1$s: %2$s 已退出 %3$s 比赛。", "赛车系统", player.getName(), getName());
			}
		}
	}

	public void kick(Player player)
	{
		player.sendMessage(Color.LIGHTBLUE, "%1$s: 您已被踢出 %2$s 比赛。", "赛车系统", getName());
		for (Player otherPlayer : players)
		{
			if (otherPlayer == player) continue;
			otherPlayer.sendMessage(Color.LIGHTBLUE, "%1$s: %2$s 已被踢出 %3$s 比赛。", "赛车系统", player.getName(), getName());
		}
		
		leave(player);
	}
	
	public void begin()
	{
		if (status != RacingStatus.WAITING) return;
		if (track.getCheckpoints().size() == 0) return;
		
		startTime = new Date();
		
		status = RacingStatus.RACING;
		init();
		
		TrackCheckpoint first = track.getCheckpoints().get(0);
		TrackRaceCheckpoint firstRaceCheckpoint = first.getRaceCheckpoint();
		for (Player player : players) 
		{
			RacingPlayerContextImpl context = new RacingPlayerContextImpl(shoebill, rootEventManager, player, this, first);
			context.init();
			
			playerContexts.put(player, context);
			
			player.playSound(1057, player.getLocation());
			player.setRaceCheckpoint(firstRaceCheckpoint);
		}
		
		updateRacingRankedList();
	}
	
	public void end()
	{
		if (status != RacingStatus.RACING) return;
		status = RacingStatus.ENDED;
		
		destroy();
	}
	
	public List<RacingPlayerContext> getRacingRankedList()
	{
		return racingRankedList;
	}

	public int getRacingRankingNumber(RacingPlayerContext context)
	{
		int index = racingRankedList.indexOf(context);
		if (index == -1) return finishedPlayers.indexOf(context.getPlayer()) + 1;
		return index + 1 + finishedPlayers.size();
	}
	
	public int getRankingPlayers()
	{
		return playerContexts.size() + finishedPlayers.size();
	}
	
	private void updateRacingRankedList()
	{
		List<RacingPlayerContext> contexts = new ArrayList<>(playerContexts.size());
		contexts.addAll(playerContexts.values());
		Collections.sort(contexts, new Comparator<RacingPlayerContext>()
		{
			@Override
			public int compare(RacingPlayerContext o1, RacingPlayerContext o2)
			{
				return (int)((o2.getCompletionPercent() - o1.getCompletionPercent())*1000.0f);
			}
		});
		
		racingRankedList = Collections.unmodifiableList(contexts);
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerEnterRaceCheckpoint(RaceCheckpointEnterEvent event)
		{
			Player player = event.getPlayer();
			if (!playerContexts.containsKey(player)) return;

			TrackRaceCheckpoint checkpoint = (TrackRaceCheckpoint) event.getCheckpoint();
			TrackCheckpoint trackCheckpoint = checkpoint.getTrackCheckpoint();
			RacingPlayerContextImpl context = playerContexts.get(player);
			ScriptExecutor executor = context.getScriptExecutor();
			
			String script = trackCheckpoint.getScript();
			if (!StringUtils.isBlank(script))
			{
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
			
			context.onPassCheckpoint(trackCheckpoint);
			player.playSound(1038, player.getLocation());
			
			if (next == null)
			{
				finishedPlayers.add(player);
				leave(player);
			}
		}
		
		protected void onPlayerLeaveRaceCheckpoint(RaceCheckpointLeaveEvent event)
		{
			
		}
	};
}
