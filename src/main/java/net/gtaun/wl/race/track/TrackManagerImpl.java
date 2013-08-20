package net.gtaun.wl.race.track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.gtaun.shoebill.common.Saveable;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.race.TrackManager;
import net.gtaun.wl.race.util.RaceUtil;
import net.gtaun.wl.race.util.TrackUtil;

import com.google.code.morphia.Datastore;

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
		if (!TrackUtil.isVaildName(name)) throw new IllegalArgumentException();
		if (tracks.containsKey(name)) throw new AlreadyExistException();
		
		tracks.remove(track.getName());
		track.setName(name);
		tracks.put(name, track);
	}

	@Override
	public Track createTrack(Player player, String name) throws AlreadyExistException, IllegalArgumentException
	{
		if (!TrackUtil.isVaildName(name)) throw new IllegalArgumentException();
		if (tracks.containsKey(name)) throw new AlreadyExistException();
		
		Track track = new Track(this, name, RaceUtil.getPlayerUniqueId(player));
		tracks.put(track.getName(), track);
		return track;
	}

	@Override
	public Track createTrack(String author, String name) throws AlreadyExistException, IllegalArgumentException
	{
		if (!TrackUtil.isVaildName(name)) throw new IllegalArgumentException();
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
