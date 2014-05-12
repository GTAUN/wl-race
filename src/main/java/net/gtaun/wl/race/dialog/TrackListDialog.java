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
import java.util.function.Predicate;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.ListDialogItemRadio;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.DialogUtils;
import net.gtaun.wl.common.dialog.WlPageListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.util.TrackUtils;

import org.apache.commons.lang3.StringUtils;

public class TrackListDialog extends WlPageListDialog
{
	private final RaceServiceImpl raceService;
	private final List<Track> tracks;

	private final List<Predicate<Track>> statusFilters;
	private Predicate<Track> statusFilter;

	private final List<Comparator<Track>> trackComparators;
	private Comparator<Track> trackComparator;
	
	private List<Track> filteredTracks;
	
	
	public TrackListDialog
	(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, List<Track> tracks)
	{		super(player, eventManager);
		setParentDialog(parent);
		this.raceService = service;
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
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		filteredTracks = TrackUtils.filterTracks(tracks, statusFilter);
		Collections.sort(filteredTracks, trackComparator);
		
		items.clear();
		items.add(new ListDialogItemRadio(stringSet.get(player, "Dialog.TrackListDialog.StatusFilter"))
		{
			{
				addItem(new RadioItem(stringSet.get(player, "Track.Status.Completed"), Color.LIGHTGREEN));
				addItem(new RadioItem(stringSet.get(player, "Track.Status.Ranking"), Color.LIGHTBLUE));
				addItem(new RadioItem(stringSet.get(player, "Track.Status.Editing"), Color.LIGHTPINK));
			}
			
			@Override
			public int getSelected()
			{
				return statusFilters.indexOf(statusFilter);
			}
			
			@Override
			public void onItemSelect(RadioItem item, int index)
			{
				player.playSound(1083);
				statusFilter = statusFilters.get(index);
				update();
				show();
			}
		});
		
		items.add(new ListDialogItemRadio(stringSet.get(player, "Dialog.TrackListDialog.SortMode"))
		{
			{
				addItem(new RadioItem(stringSet.get(player, "Track.SortMode.Nearest"), Color.RED));
				addItem(new RadioItem(stringSet.get(player, "Track.SortMode.LongToShort"), Color.BLUE));
				addItem(new RadioItem(stringSet.get(player, "Track.SortMode.ShortToLong"), Color.GREEN));
			}
			
			@Override
			public int getSelected()
			{
				return trackComparators.indexOf(trackComparator);
			}
			
			@Override
			public void onItemSelect(RadioItem item, int index)
			{
				player.playSound(1083);
				trackComparator = trackComparators.get(index);
				update();
				show();
			}
		});
		
		for (final Track track : filteredTracks)
		{
			String trackName = track.getName();
			String author = track.getAuthorUniqueId();
			
			String item = stringSet.format(player, "Dialog.TrackListDialog.Item",
					DialogUtils.rightPad(StringUtils.abbreviate(trackName, 20), 16, 8), author, track.getLength()/1000.0f, track.getCheckpoints().size());
			
			items.add(new ListDialogItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083);
					TrackDialog.create(player, eventManagerNode.getParent(), TrackListDialog.this, raceService, track).show();
				}
			});
		}
	}
	
	@Override
	public void show()
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		setCaption(stringSet.format(player, "Dialog.TrackListDialog.Caption", getCurrentPage()+1, getMaxPage()+1));
		super.show();
	}
}
