package net.gtaun.wl.race.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Transient;

@Entity("RaceTrack")
public class Track
{
	@Transient private TrackManagerImpl trackManager;
	
	@Indexed private String authorUniqueId;
	
	@Indexed private String name;
	private String desc;
	
	private List<TrackCheckpoint> checkpoints;
	
	
	protected Track()
	{
		
	}
	
	public Track(TrackManagerImpl trackManager, String name, String uniqueId)
	{
		this.trackManager = trackManager;
		this.name = name;
		this.authorUniqueId = uniqueId;
		this.desc = "";
		
		checkpoints = new ArrayList<>();
	}
	
	public void setTrackManager(TrackManagerImpl trackManager)
	{
		this.trackManager = trackManager;
	}
	
	public String getAuthorUniqueId()
	{
		return authorUniqueId;
	}
	
	public String getName()
	{
		return name;
	}
	
	void setName(String name)
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
