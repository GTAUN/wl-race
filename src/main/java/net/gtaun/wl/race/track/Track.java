package net.gtaun.wl.race.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.data.Location;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.PostLoad;
import com.google.code.morphia.annotations.Transient;

@Entity("RaceTrack")
public class Track
{
	public enum TrackStatus
	{
		EDITING,
		COMPLETED,
		RANKING,
	}
	
	public enum ScriptType
	{
		JOIN,
		BEGIN,
		RANKING,
		COMPLETE,
		QUIT,
		END,
	}
	
	
	@Transient private TrackManagerImpl trackManager;
	
	@Id private ObjectId id;
	
	@Indexed private String authorUniqueId;
	
	@Indexed private String name;
	private String desc;
	private TrackStatus status;

	private List<TrackCheckpoint> checkpoints;
	private Map<ScriptType, String> scripts;
	
	
	protected Track()
	{
		
	}
	
	public Track(TrackManagerImpl trackManager, String name, String uniqueId)
	{
		this();
		this.trackManager = trackManager;
		this.name = name;
		this.authorUniqueId = uniqueId;
		this.desc = "";
		this.status = TrackStatus.EDITING;
		this.checkpoints = new ArrayList<>();
		this.scripts = new HashMap<>();
	}
	
	@PostLoad
	private void postLoad()
	{
		if (checkpoints == null) checkpoints = new ArrayList<>();
		if (scripts == null) scripts = new HashMap<>();
		
		for (TrackCheckpoint checkpoint : checkpoints) checkpoint.setTrack(this);
	}
	
	void setTrackManager(TrackManagerImpl trackManager)
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
	
	public TrackStatus getStatus()
	{
		return status;
	}
	
	public void setStatus(TrackStatus status)
	{
		this.status = status;
	}
	
	public String getScript(ScriptType type)
	{
		return scripts.get(type);
	}
	
	public void setScript(ScriptType type, String script)
	{
		scripts.put(type, script);
	}
	
	public List<TrackCheckpoint> getCheckpoints()
	{
		return Collections.unmodifiableList(checkpoints);
	}
	
	public float getLength()
	{
		if (checkpoints.isEmpty()) return 0.0f;
		return checkpoints.get(0).getTotalDistance();
	}
	
	public Location getStartLocation()
	{
		if (checkpoints.isEmpty()) return new Location();
		return checkpoints.get(0).getLocation();
	}
	
	public TrackCheckpoint createCheckpoint(Location location)
	{
		TrackCheckpoint checkpoint = new TrackCheckpoint(this, location);
		checkpoints.add(checkpoint);
		return checkpoint;
	}
	
	public void removeChechpoint(TrackCheckpoint checkpoint)
	{
		checkpoints.remove(checkpoint);
	}
}
