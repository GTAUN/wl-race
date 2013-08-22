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
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackManagerImpl;

public class TrackListMainDialog extends AbstractListDialog
{
	public TrackListMainDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService)
	{
		super(player, shoebill, eventManager, parentDialog);
		final TrackManagerImpl trackManager = raceService.getTrackManager();

		dialogListItems.add(new DialogListItem("搜索附近的赛道 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				List<Track> tracks = trackManager.getAllTracks();
				new TrackListDialog(player, shoebill, eventManager, TrackListMainDialog.this, raceService, tracks).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("列出所有赛道 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				List<Track> tracks = trackManager.getAllTracks();
				new TrackListDialog(player, shoebill, eventManager, TrackListMainDialog.this, raceService, tracks).show();
			}
		});

		dialogListItems.add(new DialogListItem("我的赛道 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				List<Track> tracks = trackManager.searchTrackByAuthor(player.getName());
				new TrackListDialog(player, shoebill, eventManager, TrackListMainDialog.this, raceService, tracks).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("按作者搜索赛道")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				new AbstractInputDialog(player, shoebill, eventManager, TrackListMainDialog.this, "赛车系统: 按作者搜索赛道", "请输入作者名字:")
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
		
		dialogListItems.add(new DialogListItem("按名字搜索赛道")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				new AbstractInputDialog(player, shoebill, eventManager, TrackListMainDialog.this, "赛车系统: 按名字搜索赛道", "请输入搜索关键字，多个关键字请用空格隔开:")
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
