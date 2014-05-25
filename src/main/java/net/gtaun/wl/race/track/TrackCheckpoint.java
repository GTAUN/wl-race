/**
 * WL Race Plugin
 * Copyright (C) 2013 MK124
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.gtaun.wl.race.track;

import java.util.List;

import net.gtaun.shoebill.constant.RaceCheckpointType;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.object.RaceCheckpoint;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Transient;

@Embedded
public class TrackCheckpoint implements RaceCheckpoint
{
	private static final float DEFAULT_SIZE = 15.0f;


	@Transient private Track track;

	private Radius location;
	private RaceCheckpointType type;

	private String script;


	private TrackCheckpoint()
	{

	}

	TrackCheckpoint(Track track, Location location)
	{
		this();
		this.track = track;
		setLocation(location);
		type = RaceCheckpointType.NORMAL;
		this.script = "";
	}

	@Override
	public Radius getLocation()
	{
		return location.clone();
	}

	@Override
	public RaceCheckpointType getType()
	{
		return type;
	}

	public Track getTrack()
	{
		return track;
	}

	void setTrack(Track track)
	{
		this.track = track;
	}

	public void setLocation(Location location)
	{
		this.location = new Radius(location, DEFAULT_SIZE);
	}

	public void setLocation(Radius location)
	{
		this.location = new Radius(location);
	}

	public void setSize(float size)
	{
		location = new Radius(location, size);
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

	@Override
	public TrackCheckpoint getNext()
	{
		int index = getNumber() + 1;
		List<TrackCheckpoint> checkpoints = track.getCheckpoints();
		if (index >= checkpoints.size()) return null;
		return checkpoints.get(index);
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
