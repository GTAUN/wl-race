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
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.AbstractListDialog.DialogListItem;
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
import net.gtaun.wl.gamemode.event.GameListDialogShowEvent;
import net.gtaun.wl.gamemode.event.GamemodeDialogEventHandler;
import net.gtaun.wl.gamemode.event.MainMenuDialogShowEvent;
import net.gtaun.wl.lang.LanguageService;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.RacePlugin;
import net.gtaun.wl.race.RaceService;
import net.gtaun.wl.race.dialog.RaceMainDialog;
import net.gtaun.wl.race.dialog.RacingDialog;
import net.gtaun.wl.race.dialog.TrackEditDialog;
import net.gtaun.wl.race.dialog.TrackListMainDialog;
import net.gtaun.wl.race.importer.SraceImporter;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
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

	private final LocalizedStringSet localizedStringSet;
	
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

		LanguageService languageService = shoebill.getServiceStore().getService(LanguageService.class);
		localizedStringSet = languageService.createStringSet(new File(plugin.getDataDir(), "text"));
		
		trackManager = new TrackManagerImpl(datastore);
		racingManager = new RacingManagerImpl(shoebill, eventManager, this, datastore);
		
		init();
	}
	
	protected void onInit()
	{
		new SraceImporter(trackManager, new File(plugin.getDataDir(), "import/srace")).importAll();
		
		eventManager.registerHandler(PlayerCommandEvent.class, playerEventHandler, HandlerPriority.NORMAL);

		eventManager.registerHandler(MainMenuDialogShowEvent.class, gamemodeDialogEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(GameListDialogShowEvent.class, gamemodeDialogEventHandler, HandlerPriority.NORMAL);
		
		PlayerLifecycleObjectFactory<PlayerRaceContext> objectFactory = new PlayerLifecycleObjectFactory<PlayerRaceContext>()
		{
			@Override
			public PlayerRaceContext create(Shoebill shoebill, EventManager eventManager, Player player)
			{
				return new PlayerRaceContext(shoebill, eventManager, player, RaceServiceImpl.this);
			}
		};
		playerLifecycleHolder.registerClass(PlayerRaceContext.class, objectFactory);
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

	public LocalizedStringSet getLocalizedStringSet()
	{
		return localizedStringSet;
	}
	
	@Override
	public Plugin getPlugin()
	{
		return plugin;
	}
	
	@Override
	public void showMainDialog(Player player, AbstractDialog parentDialog)
	{
		new RaceMainDialog(player, shoebill, rootEventManager, parentDialog, RaceServiceImpl.this).show();
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
		PlayerRaceContext context = playerLifecycleHolder.getObject(player, PlayerRaceContext.class);
		context.setEditingTrack(track);
	}
	
	@Override
	public void stopEditingTrack(Player player)
	{
		PlayerRaceContext context = playerLifecycleHolder.getObject(player, PlayerRaceContext.class);
		context.setEditingTrack(null);
	}
	
	@Override
	public boolean isEditingTrack(Player player)
	{
		PlayerRaceContext context = playerLifecycleHolder.getObject(player, PlayerRaceContext.class);
		return context.isEditingTrack();
	}
	
	@Override
	public Track getEditingTrack(Player player)
	{
		PlayerRaceContext context = playerLifecycleHolder.getObject(player, PlayerRaceContext.class);
		return context.getEditingTrack();
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
				showMainDialog(player, null);
				event.setProcessed();
				return;
			}
		}
	};
	
	private GamemodeDialogEventHandler gamemodeDialogEventHandler = new GamemodeDialogEventHandler()
	{
		@Override
		protected void onMainMenuDialogShow(final MainMenuDialogShowEvent event)
		{
			final Player player = event.getPlayer();

			event.addItem(new DialogListItem()
			{
				@Override
				public boolean isEnabled()
				{
					return racingManager.isPlayerInRacing(player);
				}
				
				@Override
				public String toItemString()
				{
					Racing racing = racingManager.getPlayerRacing(player);
					return localizedStringSet.format(player, "Gamemode.MainMenuDialog.Racing", racing.getName());
				}
				
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					
					Racing racing = racingManager.getPlayerRacing(player);
					new RacingDialog(player, shoebill, eventManager, getCurrentDialog(), RaceServiceImpl.this, racing).show();
				}
			});

			event.addItem(new DialogListItem()
			{
				@Override
				public boolean isEnabled()
				{
					return getEditingTrack(player) != null;
				}
				
				@Override
				public String toItemString()
				{
					Track track = getEditingTrack(player);
					return localizedStringSet.format(player, "Gamemode.MainMenuDialog.Editing", track.getName());
				}
				
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					
					Track track = getEditingTrack(player);
					new TrackEditDialog(player, shoebill, eventManager, getCurrentDialog(), RaceServiceImpl.this, track).show();
				}
			});

			event.addItem(new DialogListItem(localizedStringSet.get(player, "Gamemode.MainMenuDialog.Item"))
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					showMainDialog(player, getCurrentDialog());
				}
			});
		}

		@Override
		protected void onGameListDialogShow(GameListDialogShowEvent event)
		{
			final Player player = event.getPlayer();

			event.addItem(new DialogListItem(localizedStringSet.get(player, "Gamemode.GameListDialog.NewGame"))
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					new TrackListMainDialog(player, shoebill, rootEventManager, getCurrentDialog(), RaceServiceImpl.this).show();
				}
			});
			
			List<Racing> racings = racingManager.getRacings(RacingStatus.WAITING);
			for (final Racing racing : racings)
			{
				Track track = racing.getTrack();
				String item = localizedStringSet.format(player, "Gamemode.GameListDialog.Racing", racing.getName(), track.getName(), racing.getSponsor().getName());
				event.addItem(new DialogListItem(item)
				{
					@Override
					public void onItemSelect()
					{
						player.playSound(1083, player.getLocation());
						new RacingDialog(player, shoebill, eventManager, getCurrentDialog(), RaceServiceImpl.this, racing).show();
					}
				});
			}
		}
	};
}
