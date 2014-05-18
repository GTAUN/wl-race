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

import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder;
import net.gtaun.shoebill.common.player.PlayerLifecycleHolder.PlayerLifecycleObjectFactory;
import net.gtaun.shoebill.event.player.PlayerCommandEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.service.Service;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.gamemode.event.GameListDialogExtendEvent;
import net.gtaun.wl.gamemode.event.MainMenuDialogExtendEvent;
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

import org.mongodb.morphia.Datastore;

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
	
	private final PlayerLifecycleHolder playerLifecycleHolder;

	private final LocalizedStringSet localizedStringSet;
	
	private final TrackManagerImpl trackManager;
	private final RacingManagerImpl racingManager;

	
	private boolean isCommandEnabled = true;
	private String commandOperation = "/r";
	
	private Timer timer;
	
	
	public RaceServiceImpl(EventManager rootEventManager, RacePlugin plugin, Datastore datastore)
	{
		super(rootEventManager);
		this.plugin = plugin;
		
		playerLifecycleHolder = new PlayerLifecycleHolder(eventManagerNode);

		LanguageService languageService = Service.get(LanguageService.class);
		localizedStringSet = languageService.createStringSet(new File(plugin.getDataDir(), "text"));
		
		trackManager = new TrackManagerImpl(datastore);
		racingManager = new RacingManagerImpl(eventManagerNode, this, datastore);
		
		init();
	}
	
	@Override
	protected void onInit()
	{
		new SraceImporter(trackManager, new File(plugin.getDataDir(), "import/srace")).importAll();

		eventManagerNode.registerHandler(PlayerCommandEvent.class, (e) ->
		{
			if (isCommandEnabled == false) return;
			
			Player player = e.getPlayer();
			
			String command = e.getCommand();
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
				e.setProcessed();
				return;
			}
		});

		eventManagerNode.registerHandler(MainMenuDialogExtendEvent.class, (e) ->
		{
			Player player = e.getPlayer();
			WlListDialog dialog = e.getDialog();

			dialog.addItem(ListDialogItem.create()
				.enabled(() -> racingManager.isPlayerInRacing(player))
				.itemText(() ->
				{
					Racing racing = racingManager.getPlayerRacing(player);
					return localizedStringSet.format(player, "Gamemode.MainMenuDialog.Racing", racing.getName());	
				})
				.onSelect((i) ->
				{
					player.playSound(1083);
					
					Racing racing = racingManager.getPlayerRacing(player);
					new RacingDialog(player, eventManagerNode, i.getCurrentDialog(), RaceServiceImpl.this, racing).show();
				})
				.build());
		
			dialog.addItem(ListDialogItem.create()
				.enabled(() -> getEditingTrack(player) != null)
				.itemText(() ->
				{
					Track track = getEditingTrack(player);
					return localizedStringSet.format(player, "Gamemode.MainMenuDialog.Editing", track.getName());
				})
				.onSelect((i) ->
				{
					player.playSound(1083);
					
					Track track = getEditingTrack(player);
					new TrackEditDialog(player, eventManagerNode, i.getCurrentDialog(), RaceServiceImpl.this, track).show();
				})
				.build());
			
			dialog.addItem(localizedStringSet.get(player, "Gamemode.MainMenuDialog.Item"), (i) ->
			{
				player.playSound(1083);
				showMainDialog(player, i.getCurrentDialog());
			});
		});
		
		eventManagerNode.registerHandler(GameListDialogExtendEvent.class, (e) ->
		{
			Player player = e.getPlayer();
			WlListDialog dialog = e.getDialog();

			dialog.addItem(localizedStringSet.get(player, "Gamemode.GameListDialog.NewGame"), (i) ->
			{
				player.playSound(1083);
				TrackListMainDialog.create(player, rootEventManager, i.getCurrentDialog(), RaceServiceImpl.this).show();
			});
			
			List<Racing> racings = racingManager.getRacings(RacingStatus.WAITING);
			for (Racing racing : racings)
			{
				Track track = racing.getTrack();
				String item = localizedStringSet.format(player, "Gamemode.GameListDialog.Racing", racing.getName(), track.getName(), racing.getSponsor().getName());
				dialog.addItem(item, (i) ->
				{
					player.playSound(1083);
					new RacingDialog(player, eventManagerNode, i.getCurrentDialog(), RaceServiceImpl.this, racing).show();
				});
			}
		});
		
		PlayerLifecycleObjectFactory<PlayerRaceContext> objectFactory = new PlayerLifecycleObjectFactory<PlayerRaceContext>()
		{
			@Override
			public PlayerRaceContext create(EventManager eventManager, Player player)
			{
				return new PlayerRaceContext(eventManager, player, RaceServiceImpl.this);
			}
		};
		playerLifecycleHolder.registerClass(PlayerRaceContext.class, objectFactory);
		addDestroyable(playerLifecycleHolder);
		
		timer = Timer.create(1000*60*5, (factualInterval) -> trackManager.save());
		addDestroyable(timer);
		
		racingManager.init();
	}

	@Override
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
		RaceMainDialog.create(player, rootEventManager, parentDialog, RaceServiceImpl.this).show();
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
}
