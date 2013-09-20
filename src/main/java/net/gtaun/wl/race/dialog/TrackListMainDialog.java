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

import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackManagerImpl;

public class TrackListMainDialog extends AbstractListDialog
{
	public TrackListMainDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		final TrackManagerImpl trackManager = raceService.getTrackManager();
		
		this.caption = stringSet.get(player, "Dialog.TrackListMainDialog.Caption");

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackListMainDialog.Nearby"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				List<Track> tracks = trackManager.getAllTracks();
				new TrackListDialog(player, shoebill, eventManager, TrackListMainDialog.this, raceService, tracks).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackListMainDialog.MyFavorites"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				List<Track> tracks = trackManager.getAllTracks();
				new TrackListDialog(player, shoebill, eventManager, TrackListMainDialog.this, raceService, tracks).show();
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackListMainDialog.MyTracks"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				List<Track> tracks = trackManager.searchTrackByAuthor(player.getName());
				new TrackListDialog(player, shoebill, eventManager, TrackListMainDialog.this, raceService, tracks).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackListMainDialog.SearchByAuthor"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = stringSet.get(player, "Dialog.TrackSearchByAuthorDialog.Caption");
				String message = stringSet.get(player, "Dialog.TrackSearchByAuthorDialog.Text");
				new AbstractInputDialog(player, shoebill, eventManager, TrackListMainDialog.this, caption, message)
				{
					public void onClickOk(String inputText)
					{
						player.playSound(1083, player.getLocation());
						
						List<Track> tracks = trackManager.searchTrackByAuthor(inputText);
						new TrackListDialog(player, shoebill, eventManager, TrackListMainDialog.this, raceService, tracks).show();
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackListMainDialog.SearchByKeyword"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());

				String caption = stringSet.get(player, "Dialog.TrackSearchByKeywordDialog.Caption");
				String message = stringSet.get(player, "Dialog.TrackSearchByKeywordDialog.Text");
				new AbstractInputDialog(player, shoebill, eventManager, TrackListMainDialog.this, caption, message)
				{
					public void onClickOk(String inputText)
					{
						player.playSound(1083, player.getLocation());
						
						List<Track> tracks = trackManager.searchTrackByName(inputText);
						new TrackListDialog(player, shoebill, eventManager, TrackListMainDialog.this, raceService, tracks).show();
					}
				}.show();
			}
		});
	}
}
