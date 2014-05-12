/**
 * WL Race Plugin
 * Copyright (C) 2013 MK124
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.gtaun.wl.race.racing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.RaceCheckpoint;
import net.gtaun.shoebill.event.checkpoint.RaceCheckpointEnterEvent;
import net.gtaun.shoebill.event.checkpoint.RaceCheckpointLeaveEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Timer.TimerCallback;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.script.ScriptException;
import net.gtaun.wl.race.script.ScriptExecutor;
import net.gtaun.wl.race.script.ScriptInstructionCountLimitException;
import net.gtaun.wl.race.script.ScriptTimeoutException;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.TrackCheckpoint.TrackRaceCheckpoint;

import org.apache.commons.lang3.StringUtils;

public class Racing extends AbstractShoebillContext
{
	public enum RacingStatus
	{
		WAITING,
		COUNTING,
		RACING,
		ENDED,
	}
	
	public enum RacingType
	{
		NORMAL,
		KNOCKOUT,
	}
	
	public enum DeathRule
	{
		WAIT_AND_RETURN,
		KNOCKOUT,
	}
	
	
	private final RaceServiceImpl raceService;
	private final RacingManagerImpl manager;
	
	private final Track track;
	private final Player sponsor;
	
	private List<Player> players;
	private List<Player> finishedPlayers;
	
	private Map<Player, RacingPlayerContextImpl> playerContexts;
	
	private String name;
	private RacingStatus status;
	
	private List<RacingPlayerContext> racingRankedList;
	
	private RacingSetting setting;
	
	private Timer timer;
	
	private Timer countTimer;
	private int countdown;
	
	
	Racing(EventManager rootEventManager, RaceServiceImpl raceService, RacingManagerImpl racingManager, Track track, Player sponsor, String name)
	{
		super(rootEventManager);
		this.raceService = raceService;
		this.manager = racingManager;
		this.track = track;
		this.sponsor = sponsor;
		this.name = name;
		
		setting = new RacingSetting(track);
		
		players = new ArrayList<>();
		finishedPlayers = new ArrayList<>();
		
		playerContexts = new HashMap<>();

		status = RacingStatus.WAITING;
	}

	@Override
	protected void onInit()
	{
		eventManagerNode.registerHandler(RaceCheckpointEnterEvent.class, (e) ->
		{
			Player player = e.getPlayer();
			if (!playerContexts.containsKey(player)) return;
			if (status == RacingStatus.WAITING) return;
			if (player.getState() != PlayerState.DRIVER) return;

			final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();

			TrackRaceCheckpoint checkpoint = (TrackRaceCheckpoint) e.getCheckpoint();
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
				catch (ScriptException ex)
				{
					String detail = ex.getMessage();
					int lineNum = ex.getLineNumber();
					int colNum = ex.getColumnNumber();
					String line = ex.getLineSource();
					
					player.sendMessage(Color.RED, stringSet.format(player, "Racing.Script.ErrorMessage", track.getName(), trackCheckpoint.getNumber(), lineNum, detail));
					if (line != null)
					{
						if (colNum != -1) line = line.substring(0, colNum) + "<ERROR>" + line.substring(colNum, line.length());
						player.sendMessage(Color.RED, stringSet.format(player, "Racing.Script.ErrorLineMessage", track.getName(), trackCheckpoint.getNumber(), line));
					}
				}
				catch (ScriptTimeoutException ex)
				{
					player.sendMessage(Color.RED, stringSet.format(player, "Racing.Script.TimeOutErrorMessage", track.getName(), trackCheckpoint.getNumber()));
				}
				catch (ScriptInstructionCountLimitException ex)
				{
					player.sendMessage(Color.RED, stringSet.format(player, "Racing.Script.InstructionCountLimitErrorMessage", track.getName(), trackCheckpoint.getNumber()));
				}
			}
			
			RaceCheckpoint next = checkpoint.getNext();
			player.setRaceCheckpoint(next);
			
			context.onPassCheckpoint(trackCheckpoint);
			player.playSound(1138, player.getLocation());
			
			if (next == null)
			{
				finishedPlayers.add(player);
				leave(player);

				player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Racing.Message.FinishMessage", getName()));
				for (Player otherPlayer : getPlayers())
				{
					if (otherPlayer == player) continue;
					otherPlayer.sendMessage(Color.LIGHTBLUE, stringSet.format(otherPlayer, "Racing.Message.PlayerFinishMessage", player.getName(), getName()));
				}
			}

		});
		
		eventManagerNode.registerHandler(RaceCheckpointLeaveEvent.class, (e) ->
		{
			
		});
		
		timer = Timer.create(1000, (factualInterval) ->
		{
			updateRacingRankedList();
		});
		timer.start();
		addDestroyable(timer);
		
		updateRacingRankedList();
	}

	@Override
	protected void onDestroy()
	{
		for (Iterator<Player> it = players.iterator(); it.hasNext(); )
		{
			Player player = it.next();
			it.remove();
			leave(player);
		}
		players.clear();
		
		for (RacingPlayerContextImpl ctx : playerContexts.values()) ctx.destroy();
		playerContexts.clear();
		
		manager.destroyRacing(this);
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
		List<Player> allPlayers = new ArrayList<>(getPlayerNumber());
		allPlayers.addAll(players);
		allPlayers.addAll(finishedPlayers);
		return allPlayers;
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
	
	public RacingSetting getSetting()
	{
		return setting;
	}
	
	public void setSetting(RacingSetting setting)
	{
		this.setting = setting;
	}
	
	public void join(Player player)
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		if (manager.isPlayerInRacing(player)) return;
		manager.joinRacing(this, player);
		players.add(player);

		TrackCheckpoint first = track.getCheckpoints().get(0);
		player.setRaceCheckpoint(first.getRaceCheckpoint());

		player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Racing.Message.JoinMessage", getName(), track.getName()));
		for (Player otherPlayer : getPlayers())
		{
			if (otherPlayer == player) continue;
			otherPlayer.sendMessage(Color.LIGHTBLUE, stringSet.format(otherPlayer, "Racing.Message.PlayerJoinMessage", player.getName(), getName()));
		}
		
		player.sendMessage(Color.LIGHTBLUE, stringSet.get(player, "Racing.Message.JoinTipMessage"));
	}
	
	public void leave(Player player)
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		manager.leaveRacing(this, player);
		player.disableRaceCheckpoint();
		
		RacingPlayerContextImpl context = playerContexts.get(player);
		if (context != null)
		{
			context.destroy();
			playerContexts.remove(player);
		}
		players.remove(player);

		if (!finishedPlayers.contains(player) && status != RacingStatus.ENDED)
		{
			player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Racing.Message.LeaveMessage", getName()));
			for (Player otherPlayer : getPlayers())
			{
				if (otherPlayer == player) continue;
				otherPlayer.sendMessage(Color.LIGHTBLUE, stringSet.format(otherPlayer, "Racing.Message.PlayerLeaveMessage", player.getName(), getName()));
			}
		}
		
		if (player == sponsor && status == RacingStatus.WAITING) end();
		else if (players.size() == 0) end();
	}

	public void kick(Player player)
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Racing.Message.KickMessage", getName()));
		for (Player otherPlayer : getPlayers())
		{
			if (otherPlayer == player) continue;
			otherPlayer.sendMessage(Color.LIGHTBLUE, stringSet.format(otherPlayer, "Racing.Message.KickPlayerMessage", player.getName(), getName()));
		}
		
		leave(player);
	}
	
	public void cancel()
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		for (Player otherPlayer : getPlayers())
		{
			otherPlayer.sendMessage(Color.LIGHTBLUE, stringSet.format(otherPlayer, "Racing.Message.KickPlayerMessage", getName()));
		}
		
		end();
	}
	
	public void beginCountdown()
	{
		if (status != RacingStatus.WAITING) throw new IllegalStateException();
		
		countTimer = Timer.create(1000, 5, new TimerCallback()
		{
			@Override
			public void onTick(int factualInterval)
			{
				if (countdown == 0) return;
				for (Player player : players)
				{
					player.sendGameText(2000, 6, "- %1$d -", countdown);
					player.playSound(1056, player.getLocation());
				}
				countdown--;
			}
			
			@Override
			public void onStart()
			{
				status = RacingStatus.COUNTING;
				countdown = 5;
				onTick(0);
			}
			
			@Override
			public void onStop()
			{
				countTimer = null;
				begin();
			}
		});
		countTimer.start();
	}
	
	public void begin()
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		if (status != RacingStatus.WAITING && status != RacingStatus.COUNTING) throw new IllegalStateException();
		if (track.getCheckpoints().size() == 0) throw new IllegalStateException();
		
		status = RacingStatus.RACING;
		init();
		
		TrackCheckpoint first = track.getCheckpoints().get(0);
		TrackRaceCheckpoint firstCheckpoint = first.getRaceCheckpoint();
		for (Player player : players) 
		{
			RacingPlayerContextImpl context = new RacingPlayerContextImpl(rootEventManager, player, raceService, this, first);
			context.init();
			context.begin();
			
			playerContexts.put(player, context);
			
			player.playSound(1057, player.getLocation());
			player.setRaceCheckpoint(firstCheckpoint);
			player.sendGameText(1000, 6, stringSet.get(player, "Racing.GameText.GoMessage"));
		}
		
		updateRacingRankedList();
	}
	
	public void end()
	{
		if (status == RacingStatus.ENDED) return;
		status = RacingStatus.ENDED;
		destroy();
	}
	
	public void teleToStartingPoint(Player player)
	{
		Location location = new Location(track.getStartLocation());
		location.setZ(location.getZ() + 2.0f);
		player.setLocation(location);
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
	
	public int getPlayerNumber()
	{
		return players.size() + finishedPlayers.size();
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
}
