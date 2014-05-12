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

import net.gtaun.shoebill.data.Location;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Transient;

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
		return location.clone();
	}
	
	public void setLocation(Location location)
	{
		this.location = location.clone();
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
