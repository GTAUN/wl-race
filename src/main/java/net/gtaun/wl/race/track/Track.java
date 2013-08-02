package net.gtaun.wl.race.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.gtaun.shoebill.data.RaceCheckpoint;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Transient;

@Entity("RaceTrack")
public class Track
{
	public static class TrackRaceCheckpoint extends RaceCheckpoint
	{
		public final TrackCheckpoint trackCheckpoint;
		
		private TrackRaceCheckpoint(TrackCheckpoint trackCheckpoint, RaceCheckpoint next)
		{
			super(trackCheckpoint.getLocation(), trackCheckpoint.getType(), next);
			this.trackCheckpoint = trackCheckpoint;
		}
	}
	
	public enum TrackStatus
	{
		EDITING,
		COMPLETED,
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
		this.trackManager = trackManager;
		this.name = name;
		this.authorUniqueId = uniqueId;
		this.desc = "";
		this.status = TrackStatus.EDITING;
		this.checkpoints = new ArrayList<>();
		this.scripts = new HashMap<>();
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
	
	public void addCheckpoint(TrackCheckpoint checkpoint)
	{
		checkpoints.add(checkpoint);
	}
	
	public void removeChechpoint(TrackCheckpoint checkpoint)
	{
		checkpoints.remove(checkpoint);
	}
	
	public SortedSet<TrackRaceCheckpoint> generateRaceCheckpoints()
	{
		SortedSet<TrackRaceCheckpoint> set = new TreeSet<>(new Comparator<TrackRaceCheckpoint>()
		{
			@Override
			public int compare(TrackRaceCheckpoint o1, TrackRaceCheckpoint o2)
			{
				return o1.trackCheckpoint.getNumber() - o2.trackCheckpoint.getNumber();
			}
		});

		TrackRaceCheckpoint lastCheckpoint = null;
		for (ListIterator<TrackCheckpoint> it = checkpoints.listIterator(checkpoints.size()); it.hasPrevious();)
		{
			TrackCheckpoint checkpoint = it.previous();
			lastCheckpoint = new TrackRaceCheckpoint(checkpoint, lastCheckpoint);
			set.add(lastCheckpoint);
		}
		
		return set;
	}
}
