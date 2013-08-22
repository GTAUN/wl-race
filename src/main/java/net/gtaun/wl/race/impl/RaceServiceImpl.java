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

package net.gtaun.wl.race.impl;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.gtaun.shoebill.SampObjectFactory;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObjectFactory;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Timer.TimerCallback;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.util.event.ManagedEventManager;
import net.gtaun.wl.race.RacePlugin;
import net.gtaun.wl.race.RaceService;
import net.gtaun.wl.race.dialog.RaceMainDialog;
import net.gtaun.wl.race.importer.SraceImporter;
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
	
	private Timer timer;
	
	
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
		new SraceImporter(trackManager, new File(plugin.getDataDir(), "import/srace")).importAll();
		
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
		
		SampObjectFactory factory = shoebill.getSampObjectFactory();
		timer = factory.createTimer(1000*60*5, new TimerCallback()
		{	
			@Override
			public void onTick(int factualInterval)
			{
				trackManager.save();
			}
		});
		addDestroyable(timer);
		
		racingManager.init();
	}

	protected void onDestroy()
	{
		trackManager.save();
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
	public void stopEditingTrack(Player player)
	{
		PlayerActuator actuator = playerLifecycleHolder.getObject(player, PlayerActuator.class);
		actuator.setEditingTrack(null);
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
				new RaceMainDialog(player, shoebill, rootEventManager, null, RaceServiceImpl.this).show();
				event.setProcessed();
				return;
			}
		}
	};
}
