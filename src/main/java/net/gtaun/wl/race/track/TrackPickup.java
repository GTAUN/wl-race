package net.gtaun.wl.race.track;

import net.gtaun.shoebill.data.Location;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Transient;

@Embedded
public class TrackPickup
{
	@Transient private Track track;
	
	private Location location;
	private int modelId;
	private int type;

	private String script;
	

	public TrackPickup()
	{
		
	}
	
	public TrackPickup(Track track, Location location, int modelId, int type)
	{
		this.track = track;
		setLocation(location);
		this.modelId = modelId;
		this.type = type;
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
	
	public Location getLocation()
	{
		return location.immutable();
	}
	
	public void setLocation(Location location)
	{
		this.location = location.immutable();
	}
	
	public int getModelId()
	{
		return modelId;
	}
	
	public void setModelId(int modelId)
	{
		this.modelId = modelId;
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public String getScript()
	{
		return script;
	}
	
	public void setScript(String script)
	{
		this.script = script;
	}
}
