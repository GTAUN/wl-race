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

package net.gtaun.wl.race.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.common.ColorUtils;
import net.gtaun.shoebill.common.player.PlayerLifecycleObject;
import net.gtaun.shoebill.constant.MapIconStyle;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.player.PlayerUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerMapIcon.MapIcon;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;

public class TrackEditor extends PlayerLifecycleObject
{
	private final RaceServiceImpl raceService;
	private final Track track;
	
	private Map<TrackCheckpoint, MapIcon> mapIcons;
	
	
	public TrackEditor(EventManager rootEventManager, Player player, RaceServiceImpl raceService, Track track)
	{
		super(rootEventManager, player);
		this.raceService = raceService;
		this.track = track;
		this.mapIcons = new HashMap<>();
	}
	
	@Override
	protected void onInit()
	{

		eventManagerNode.registerHandler(PlayerUpdateEvent.class, HandlerPriority.NORMAL, Attentions.create().object(player), (e) ->
		{
			if (player.getUpdateCount() % 40 == 0) updateMapIcons();
		});
		
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "TrackEditor.StartEditingTrackMessage", track.getName()));
	}
	
	@Override
	protected void onDestroy()
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "TrackEditor.EndEditingTrackMessage", track.getName()));
		
		for (MapIcon icon : mapIcons.values()) icon.destroy();
		mapIcons.clear();
	}
	
	public Track getTrack()
	{
		return track;
	}
	
	public void updateMapIcons()
	{
		List<TrackCheckpoint> checkpoints = new ArrayList<>(track.getCheckpoints());
		Collections.sort(checkpoints, new Comparator<TrackCheckpoint>()
		{
			@Override
			public int compare(TrackCheckpoint o1, TrackCheckpoint o2)
			{
				Location playerLoc = player.getLocation();
				return (int) (o1.getLocation().distance(playerLoc) - o2.getLocation().distance(playerLoc));
			}
		});
		
		Map<TrackCheckpoint, MapIcon> lastMapIcons = mapIcons;
		mapIcons = new HashMap<>();

		int size = Math.min(10, checkpoints.size());
		for (int i=0; i<size; i++)
		{
			TrackCheckpoint checkpoint = checkpoints.get(i);
			MapIcon icon = lastMapIcons.get(checkpoint);
			
			if (icon == null) icon = player.getMapIcon().createIcon();
			else lastMapIcons.remove(checkpoint);
			
			final float fadeOutDistance = 1000.0f;
			float distance = checkpoint.getLocation().distance(player.getLocation());
			if (distance > fadeOutDistance) distance = fadeOutDistance;
			int alpha = (int) (255 * distance / fadeOutDistance);
			
			Color subColor = new Color(Color.GREEN);
			subColor.setA(128);
			Color color = ColorUtils.colorBlend(Color.RED, subColor, alpha);
			
			icon.update(checkpoint.getLocation(), 0, color, MapIconStyle.GLOBAL_CHECKPOINT);
			mapIcons.put(checkpoint, icon);
		}

		for (MapIcon icon : lastMapIcons.values()) icon.destroy();
	}
}
