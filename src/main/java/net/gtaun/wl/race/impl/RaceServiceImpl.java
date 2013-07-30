/**
 * Copyright (C) 2013 MK124
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.gtaun.wl.race.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObjectFactory;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.util.event.ManagedEventManager;
import net.gtaun.wl.race.RacePlugin;
import net.gtaun.wl.race.RaceService;
import net.gtaun.wl.race.dialog.RaceDialog;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackManagerImpl;

import com.google.code.morphia.Datastore;

public class RaceServiceImpl extends AbstractShoebillContext implements RaceService
{
	class OwnedVehicleLastPassengers
	{
		final long lastUpdate;
		final List<Player> passengers;
		
		private OwnedVehicleLastPassengers(long lastUpdate, List<Player> passengers)
		{
			this.lastUpdate = lastUpdate;
			this.passengers = passengers;
		}
	}
	
	
	private final RacePlugin plugin;
	
	private final ManagedEventManager eventManager;
	private final PlayerLifecycleHolder playerLifecycleHolder;
	
	private final TrackManagerImpl trackManager;
	private final RacingManagerImpl racingManager;
	
	private boolean isCommandEnabled = true;
	private String commandOperation = "/r";
	
	
	public RaceServiceImpl(Shoebill shoebill, EventManager rootEventManager, RacePlugin plugin, Datastore datastore)
	{
		super(shoebill, rootEventManager);
		this.plugin = plugin;
		
		eventManager = new ManagedEventManager(rootEventManager);
		playerLifecycleHolder = new PlayerLifecycleHolder(shoebill, eventManager);
		
		trackManager = new TrackManagerImpl(datastore);
		racingManager = new RacingManagerImpl(shoebill, eventManager, datastore);
		
		init();
	}
	
	protected void onInit()
	{
		eventManager.registerHandler(PlayerCommandEvent.class, playerEventHandler, HandlerPriority.NORMAL);
		
		PlayerLifecycleObjectFactory<PlayerActuator> objectFactory = new PlayerLifecycleObjectFactory<PlayerActuator>()
		{
			@Override
			public PlayerActuator create(Shoebill shoebill, EventManager eventManager, Player player)
			{
				return new PlayerActuator(shoebill, eventManager, player, RaceServiceImpl.this);
			}
		};
		playerLifecycleHolder.registerClass(PlayerActuator.class, objectFactory);

		addDestroyable(playerLifecycleHolder);
	}

	protected void onDestroy()
	{
		
	}
	
	@Override
	public Plugin getPlugin()
	{
		return plugin;
	}

	@Override
	public TrackManagerImpl getTrackManager()
	{
		return trackManager;
	}
	
	@Override
	public RacingManagerImpl getRacingManager()
	{
		return racingManager;
	}

	@Override
	public void editTrack(Player player, Track track)
	{
		PlayerActuator actuator = playerLifecycleHolder.getObject(player, PlayerActuator.class);
		actuator.setEditingTrack(track);
	}
	
	@Override
	public boolean isEditingTrack(Player player)
	{
		PlayerActuator actuator = playerLifecycleHolder.getObject(player, PlayerActuator.class);
		return actuator.isEditingTrack();
	}
	
	@Override
	public Track getEditingTrack(Player player)
	{
		PlayerActuator actuator = playerLifecycleHolder.getObject(player, PlayerActuator.class);
		return actuator.getEditingTrack();
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerCommand(PlayerCommandEvent event)
		{
			if (isCommandEnabled == false) return;
			
			Player player = event.getPlayer();
			
			String command = event.getCommand();
			String[] splits = command.split(" ", 2);
			
			String operation = splits[0].toLowerCase();
			Queue<String> args = new LinkedList<>();
			
			if (splits.length > 1)
			{
				String[] argsArray = splits[1].split(" ");
				args.addAll(Arrays.asList(argsArray));
			}
			
			if (operation.equals(commandOperation))
			{
				new RaceDialog(player, shoebill, rootEventManager, null, RaceServiceImpl.this).show();
				event.setProcessed();
				return;
			}
		}
	};
}
