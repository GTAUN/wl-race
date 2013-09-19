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

package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.ResourceDescription;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackManagerImpl;

public class RaceMainDialog extends AbstractListDialog
{
	public RaceMainDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		final TrackManagerImpl trackManager = raceService.getTrackManager();
		final RacingManagerImpl racingManager = raceService.getRacingManager();
		
		this.caption = stringSet.get(player, "Dialog.RaceMainDialog.Caption");
		
		dialogListItems.add(new DialogListItem()
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
				return stringSet.format(player, "Dialog.RaceMainDialog.Racing", racing.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Racing racing = racingManager.getPlayerRacing(player);
				new RacingDialog(player, shoebill, eventManager, RaceMainDialog.this, raceService, racing).show();
			}
		});

		dialogListItems.add(new DialogListItem()
		{
			@Override
			public boolean isEnabled()
			{
				return raceService.getEditingTrack(player) != null;
			}
			
			@Override
			public String toItemString()
			{
				Track track = raceService.getEditingTrack(player);
				return stringSet.format(player, "Dialog.RaceMainDialog.Editing", track.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Track track = raceService.getEditingTrack(player);
				new TrackEditDialog(player, shoebill, eventManager, RaceMainDialog.this, raceService, track).show();
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RaceMainDialog.TrackList"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new TrackListMainDialog(player, shoebill, eventManager, RaceMainDialog.this, raceService).show();
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RaceMainDialog.TrackFavorites"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RaceMainDialog.RacingList"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new RacingListDialog(player, shoebill, eventManager, RaceMainDialog.this, raceService).show();
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RaceMainDialog.CreateTrack"))
		{
			@Override
			public boolean isEnabled()
			{
				if (raceService.isEditingTrack(player)) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = stringSet.get(player, "Dialog.CreateNewTrackNamingDialog.Caption");
				String message = stringSet.get(player, "Dialog.CreateNewTrackNamingDialog.Text");
				new TrackNamingDialog(player, shoebill, rootEventManager, caption, message, RaceMainDialog.this, raceService)
				{
					@Override
					protected void onNaming(String name)
					{
						try
						{
							Track track = trackManager.createTrack(player, name);
							raceService.editTrack(player, track);
							new TrackEditDialog(player, shoebill, eventManager, RaceMainDialog.this, raceService, track).show();
						}
						catch (AlreadyExistException e)
						{
							append = stringSet.format(player, "Dialog.CreateNewTrackNamingDialog.AlreadyExistAppendMessage", name);
							show();
						}
						catch (IllegalArgumentException e)
						{
							append = stringSet.format(player, "Dialog.CreateNewTrackNamingDialog.IllegalNameAppendMessage", name);
							show();
						}
					}
				}.show();
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RaceMainDialog.MyRacerInfo"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RaceMainDialog.MyRaceRecord"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RaceMainDialog.WorldRanking"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RaceMainDialog.PersonalPreferences"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RaceMainDialog.Help"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				String caption = stringSet.get(player, "Dialog.HelpDialog.Caption");
				String text = stringSet.get(player, "Dialog.HelpDialog.Text");
				new MsgboxDialog(player, shoebill, eventManager, RaceMainDialog.this, caption, text).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RaceMainDialog.About"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Plugin plugin = raceService.getPlugin();
				ResourceDescription desc = plugin.getDescription();
				
				String caption = stringSet.get(player, "Dialog.AboutDialog.Caption");
				String format = stringSet.get(player, "Dialog.AboutDialog.Text");
				String message = String.format(format, desc.getVersion(), desc.getBuildNumber(), desc.getBuildDate());
				
				new MsgboxDialog(player, shoebill, eventManager, RaceMainDialog.this, caption, message).show();
			}
		});
	}
}
