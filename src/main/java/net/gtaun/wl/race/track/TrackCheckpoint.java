package net.gtaun.wl.race.track;

import net.gtaun.shoebill.constant.RaceCheckpointType;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Radius;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Transient;

@Embedded
public class TrackCheckpoint
{
	private static final float DEFAULT_SIZE = 15.0f;
	
	
	@Transient private Track track;
	
	private Radius location;
	private RaceCheckpointType type;

	private String script;
	

	public TrackCheckpoint()
	{
		
	}
	
	public TrackCheckpoint(Track track, Location location)
	{
		this.track = track;
		setLocation(location);
		type = RaceCheckpointType.NORMAL;
		this.script = "";
	}
	
	public Track getTrack()
	{
		return track;
	}
	
	public void setTrack(Track track)
	{
		this.track = track;
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
	
	public RaceCheckpointType getType()
	{
		return type;
	}
	
	public String getScript()
	{
		return script;
	}
	
	public void setScript(String script)
	{
		this.script = script;
	}
	
	public int getNumber()
	{
		return track.getCheckpoints().indexOf(this);
	}
}
