package net.gtaun.wl.race.data;

import com.google.code.morphia.annotations.Reference;

import net.gtaun.shoebill.data.Location;

public class TrackCheckpoint
{
	@Reference private Track track;
	private Location location;
	

	public TrackCheckpoint()
	{
		
	}
	
	public TrackCheckpoint(Track track, Location location)
	{
		this.track = track;
		setLocation(location);
	}
	
	public Track getTrack()
	{
		return track;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public void setLocation(Location location)
	{
		this.location = location.immutable();
	}
	
	public int getNumber()
	{
		return track.getCheckpoints().indexOf(this);
	}
}
