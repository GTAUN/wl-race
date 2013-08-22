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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.Filter;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractPageListDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.util.TrackUtils;

public class TrackListDialog extends AbstractPageListDialog
{
	private final RaceServiceImpl raceService;
	private final List<Track> tracks;

	private final List<Filter<Track>> statusFilters;
	private Filter<Track> statusFilter;

	private final List<Comparator<Track>> trackComparators;
	private Comparator<Track> trackComparator;
	
	private List<Track> filteredTracks;
	
	
	public TrackListDialog
	(Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, RaceServiceImpl raceService, List<Track> tracks)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.raceService = raceService;
		this.tracks = tracks;
		
		statusFilters = new ArrayList<>();
		statusFilters.add(TrackUtils.FILTER_COMPLETED);
		statusFilters.add(TrackUtils.FILTER_RANKING);
		statusFilters.add(TrackUtils.FILTER_EDITING);
		statusFilter = statusFilters.get(0);
		
		trackComparators = new ArrayList<>();
		trackComparators.add(TrackUtils.createNearestComparator(player.getLocation()));
		trackComparators.add(TrackUtils.COMPARATOR_LENGTH_LONGTOSHORT);
		trackComparators.add(TrackUtils.COMPARATOR_LENGTH_SHORTTOLONG);
		trackComparator = trackComparators.get(0);

		update();
	}
	
	private void update()
	{
		filteredTracks = TrackUtils.filterTracks(tracks, statusFilter);
		Collections.sort(filteredTracks, trackComparator);
		
		dialogListItems.clear();
		dialogListItems.add(new DialogListItemRadio("过滤状态: ")
		{
			{
				addItem(new RadioItem("已完成", Color.LIGHTGREEN));
				addItem(new RadioItem("已认证", Color.LIGHTBLUE));
				addItem(new RadioItem("编辑中", Color.LIGHTPINK));
			}
			
			@Override
			public int getSelected()
			{
				return statusFilters.indexOf(statusFilter);
			}
			
			@Override
			public void onItemSelect(RadioItem item, int index)
			{
				player.playSound(1083, player.getLocation());
				statusFilter = statusFilters.get(index);
				update();
				show();
			}
		});
		
		dialogListItems.add(new DialogListItemRadio("排序方式: ")
		{
			{
				addItem(new RadioItem("距离最近", Color.RED));
				addItem(new RadioItem("从长到短", Color.BLUE));
				addItem(new RadioItem("从短到长", Color.GREEN));
			}
			
			@Override
			public int getSelected()
			{
				return trackComparators.indexOf(trackComparator);
			}
			
			@Override
			public void onItemSelect(RadioItem item, int index)
			{
				player.playSound(1083, player.getLocation());
				trackComparator = trackComparators.get(index);
				update();
				show();
			}
		});
		
		for (final Track track : filteredTracks)
		{
			String trackName = track.getName();
			String author = track.getAuthorUniqueId();
			
			String item = String.format("赛道: %1$s	{7F7F7F}by %2$s - 长度 %3$1.1f公里, 点数 %4$d",
				StringUtils.abbreviate(trackName, 23) + StringUtils.repeat('\t', (23-trackName.length())/8),
				author, track.getLength()/1000.0f, track.getCheckpoints().size());
			
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					new TrackDialog(player, shoebill, eventManager, TrackListDialog.this, raceService, track).show();
				}
			});
		}
	}
	
	@Override
	public void show()
	{
		super.show();
	}
}
