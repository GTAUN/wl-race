package net.gtaun.wl.race.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import net.gtaun.shoebill.SampObjectFactory;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.object.RaceCheckpoint;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Transient;

@Entity("RaceTrack")
public class Track
{
	public enum TrackStatus
	{
		EDITING,
		COMPLETED,
	}
	
	
	@Transient private TrackManagerImpl trackManager;
	
	@Indexed private String authorUniqueId;
	
	@Indexed private String name;
	private String desc;
	private TrackStatus status;
	
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
		this.status = TrackStatus.EDITING;
		
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
	
	public TrackStatus getStatus()
	{
		return status;
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
	
	public List<RaceCheckpoint> generateRaceCheckpoints()
	{
		SampObjectFactory factory = Shoebill.Instance.get().getSampObjectFactory();

		RaceCheckpoint lastCheckpoint = null;
		List<RaceCheckpoint> list = new ArrayList<>(checkpoints.size());
		for (ListIterator<TrackCheckpoint> it = checkpoints.listIterator(checkpoints.size()); it.hasPrevious();)
		{
			TrackCheckpoint checkpoint = it.previous();
			lastCheckpoint = factory.createRaceCheckpoint(checkpoint.getLocation(), checkpoint.getType(), lastCheckpoint);
			list.add(lastCheckpoint);
		}
		
		Collections.reverse(list);
		return list;
	}
}
