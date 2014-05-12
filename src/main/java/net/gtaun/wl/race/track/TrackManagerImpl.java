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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.common.Saveable;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.race.TrackManager;
import net.gtaun.wl.race.util.RaceUtils;
import net.gtaun.wl.race.util.TrackUtils;

import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.Datastore;

public class TrackManagerImpl implements TrackManager, Saveable
{
	private final Datastore datastore;
	
	private Map<String, Track> tracks;
	
	
	public TrackManagerImpl(Datastore datastore)
	{
		this.datastore = datastore;
		tracks = new HashMap<>();
		
		load();
	}

	public void load()
	{
		List<Track> list = datastore.createQuery(Track.class).asList();
		for (Track track : list)
		{
			track.setTrackManager(this);
			tracks.put(track.getName(), track);
		}
	}
	
	@Override
	public void save()
	{
		datastore.save(tracks.values());
	}
	
	public void save(Track track)
	{
		datastore.save(track);
	}

	@Override
	public void renameTrack(Track track, String name) throws AlreadyExistException, IllegalArgumentException
	{
		if (!TrackUtils.isVaildName(name)) throw new IllegalArgumentException();
		if (tracks.containsKey(name)) throw new AlreadyExistException();
		
		tracks.remove(track.getName());
		track.setName(name);
		tracks.put(name, track);
	}

	@Override
	public Track createTrack(Player player, String name) throws AlreadyExistException, IllegalArgumentException
	{
		if (!TrackUtils.isVaildName(name)) throw new IllegalArgumentException();
		if (tracks.containsKey(name)) throw new AlreadyExistException();
		
		Track track = new Track(this, name, RaceUtils.getPlayerUniqueId(player));
		tracks.put(track.getName(), track);
		return track;
	}

	@Override
	public Track createTrack(String author, String name) throws AlreadyExistException, IllegalArgumentException
	{
		if (!TrackUtils.isVaildName(name)) throw new IllegalArgumentException();
		if (tracks.containsKey(name)) throw new AlreadyExistException();
		
		Track track = new Track(this, name, author);
		tracks.put(track.getName(), track);
		return track;
	}
	
	@Override
	public Track getTrack(String name)
	{
		return tracks.get(name);
	}

	@Override
	public void deleteTrack(Track track)
	{
		if (track == getTrack(track.getName()))
		{
			datastore.delete(track);
			tracks.remove(track.getName());
		}
	}
	
	@Override
	public List<Track> searchTrackByName(String text)
	{
		String[] keywords = text.split(" ");
		
		List<Track> list = new ArrayList<>();
		for (Track track : tracks.values())
		{
			String name = track.getName();
			for (String word : keywords) if (!StringUtils.containsIgnoreCase(name, word)) continue;
			list.add(track);
		}
		
		return list;
	}

	@Override
	public List<Track> searchTrackByAuthor(String uniqueId)
	{
		List<Track> list = new ArrayList<>();
		for (Track track : tracks.values())
		{
			if (track.getAuthorUniqueId().equalsIgnoreCase(uniqueId)) list.add(track);
		}
		return list;
	}
	
	@Override
	public List<Track> getAllTracks()
	{
		return new ArrayList<>(tracks.values());
	}
}
