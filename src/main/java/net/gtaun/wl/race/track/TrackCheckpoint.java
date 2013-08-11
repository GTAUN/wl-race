package net.gtaun.wl.race.track;

import java.util.List;

import net.gtaun.shoebill.constant.RaceCheckpointType;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.RaceCheckpoint;
import net.gtaun.shoebill.data.Radius;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Transient;

@Embedded
public class TrackCheckpoint
{
	public class TrackRaceCheckpoint extends RaceCheckpoint
	{
		private TrackRaceCheckpoint()
		{
			
		}
		
		public TrackCheckpoint getTrackCheckpoint()
		{
			return TrackCheckpoint.this;
		}
		
		@Override
		public Radius getLocation()
		{
			return location;
		}
		
		@Override
		public RaceCheckpointType getType()
		{
			return type;
		}
		
		@Override
		public RaceCheckpoint getNext()
		{
			TrackCheckpoint checkpoint = TrackCheckpoint.this.getNext();
			return checkpoint == null ? null : checkpoint.getRaceCheckpoint();
		}
	}
	
	
	private static final float DEFAULT_SIZE = 15.0f;
	
	
	@Transient private Track track;
	@Transient private TrackRaceCheckpoint raceCheckpoint;
	
	private Radius location;
	private RaceCheckpointType type;

	private String script;
	

	private TrackCheckpoint()
	{
		raceCheckpoint = new TrackRaceCheckpoint();
	}

	TrackCheckpoint(Track track, Location location)
	{
		this();
		this.track = track;
		setLocation(location);
		type = RaceCheckpointType.NORMAL;
		this.script = "";
	}
	
	public Track getTrack()
	{
		return track;
	}
	
	void setTrack(Track track)
	{
		this.track = track;
	}
	
	public Radius getLocation()
	{
		return location;
	}
	
	public void setLocation(Location location)
	{
		this.location = new Radius(location, DEFAULT_SIZE).immutable();
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
	
	public void setType(RaceCheckpointType type)
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
	
	public int getNumber()
	{
		return track.getCheckpoints().indexOf(this);
	}
	
	public TrackCheckpoint getPrev()
	{
		int index = getNumber() - 1;
		if (index < 0) return null;
		List<TrackCheckpoint> checkpoints = track.getCheckpoints();
		return checkpoints.get(index);
	}
	
	public TrackCheckpoint getNext()
	{
		int index = getNumber() + 1;
		List<TrackCheckpoint> checkpoints = track.getCheckpoints();
		if (index >= checkpoints.size()) return null;
		return checkpoints.get(index);
	}
	
	public TrackRaceCheckpoint getRaceCheckpoint()
	{
		return raceCheckpoint;
	}
	
	public float getNextDistance()
	{
		TrackCheckpoint next = getNext();
		if (next == null) return 0.0f;
		return location.distance(next.getLocation());
	}
	
	public float getTotalDistance()
	{
		TrackCheckpoint next = getNext();
		if (next == null) return 0.0f;
		return location.distance(next.getLocation()) + next.getTotalDistance();
	}
	
	public float getDistance(TrackCheckpoint checkpoint)
	{
		float distance = 0.0f;
		for (TrackCheckpoint cp = this; checkpoint != cp && cp != null; cp = cp.getNext())
		{
			distance += cp.getNextDistance();
		}
		return distance;
	}
}
