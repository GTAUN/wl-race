package net.gtaun.wl.race.track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.Datastore;

import net.gtaun.shoebill.common.Saveable;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.race.TrackManager;
import net.gtaun.wl.race.util.RaceUtil;
import net.gtaun.wl.race.util.TrackUtil;

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
		for (Track track : list) tracks.put(track.getName(), track);
	}
	
	@Override
	public void save()
	{
		datastore.save(tracks.values());
	}

	@Override
	public Track createTrack(Player player, String name) throws AlreadyExistException, IllegalArgumentException
	{
		if (tracks.containsKey(name)) throw new AlreadyExistException();
		if (!TrackUtil.isVaildName(name)) throw new IllegalArgumentException();
		
		Track track = new Track(name, RaceUtil.getPlayerUniqueId(player));
		tracks.put(track.getName(), track);
		return track;
	}
	
	@Override
	public Track getTrack(String name)
	{
		return tracks.get(name);
	}
	
	@Override
	public List<Track> searchTrackByName(String text)
	{
		String[] keywords = text.split(" ");
		
		List<Track> list = new ArrayList<>();
		for (Track track : tracks.values())
		{
			for (String word : keywords) if (!track.getName().contains(word)) continue;
			list.add(track);
		}
		
		return list;
	}

	@Override
	public List<Track> searchTrackByAuthor(String uniqueId)
	{
		List<Track> list = new ArrayList<>();
		for (Track track : tracks.values()) if (track.getAuthorUniqueId().equals(uniqueId)) list.add(track);
		return list;
	}
	
	@Override
	public List<Track> getAllTracks()
	{
		return new ArrayList<>(tracks.values());
	}
}
