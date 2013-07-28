package net.gtaun.wl.race.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.code.morphia.annotations.Entity;

@Entity("RaceTrack")
public class Track
{
	private String name;
	private String desc;
	
	private List<TrackCheckpoint> checkpoints;
	
	
	public Track()
	{
		this("Unnamed");
	}
	
	public Track(String name)
	{
		checkpoints = new ArrayList<>();
		
		this.name = name;
		this.desc = "";
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDesc()
	{
		return desc;
	}
	
	public void setDesc(String desc)
	{
		this.desc = desc;
	}
	
	public List<TrackCheckpoint> getCheckpoints()
	{
		return Collections.unmodifiableList(checkpoints);
	}
	
	public void addCheckpoint(TrackCheckpoint checkpoint)
	{
		checkpoints.add(checkpoint);
	}
	
	public void removeChechpoint(TrackCheckpoint checkpoint)
	{
		checkpoints.remove(checkpoint);
	}
}
