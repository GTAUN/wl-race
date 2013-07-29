package net.gtaun.wl.race.track;

import com.google.code.morphia.annotations.Reference;

import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Radius;

public class TrackCheckpoint
{
	private static final float DEFAULT_SIZE = 15.0f;
	
	
	@Reference private Track track;
	private Radius location;
	

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
	
	public Radius getLocation()
	{
		return location;
	}
	
	public void setLocation(Location location)
	{
		this.location = new Radius(location, DEFAULT_SIZE);
	}
	
	public void setLocation(Radius location)
	{
		this.location = location.immutable();
	}

	public float getSize()
	{
		return location.getRadius();
	}
	
	public void setSize(float size)
	{
		location = new Radius(location, size).immutable();
	}
	
	public int getNumber()
	{
		return track.getCheckpoints().indexOf(this);
	}
}
