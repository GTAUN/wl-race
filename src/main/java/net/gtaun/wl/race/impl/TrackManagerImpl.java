package net.gtaun.wl.race.impl;

import java.util.HashMap;
import java.util.Map;

import net.gtaun.wl.race.TrackManager;
import net.gtaun.wl.race.data.Track;

public class TrackManagerImpl implements TrackManager
{
	private Map<String, Track> tracks;
	
	
	public TrackManagerImpl()
	{
		tracks = new HashMap<>();
	}

	@Override
	public Track createTrack()
	{
		return new Track();
	}
}
