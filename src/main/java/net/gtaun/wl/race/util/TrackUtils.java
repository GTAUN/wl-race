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

package net.gtaun.wl.race.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import net.gtaun.shoebill.data.Location;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;

import org.apache.commons.lang3.StringUtils;

public final class TrackUtils
{
	public static final int NAME_MIN_LENGTH = 3;
	public static final int NAME_MAX_LENGTH = 40;
	
	
	public static boolean isVaildName(String name)
	{
		if (name.length() < NAME_MIN_LENGTH || name.length() > NAME_MAX_LENGTH) return false;
		if (name.contains("%") || name.contains("\t") || name.contains("\n")) return false;
		if (!StringUtils.trimToEmpty(name).equals(name)) return false;
		return true;
	}
	
	public static String filterName(String name)
	{
		name = StringUtils.trimToEmpty(name);
		name = StringUtils.replace(name, "%", "#");
		name = StringUtils.replace(name, "\t", " ");
		name = StringUtils.replace(name, "\n", " ");
		return name;
	}
	
	public static List<Track> filterTracks(List<Track> tracks, Predicate<Track> filter)
	{
		List<Track> filteredTracks = new ArrayList<>();
		for (Track track : tracks) if (filter.test(track)) filteredTracks.add(track);
		return filteredTracks;
	}
	
	public static final Predicate<Track> FILTER_ALL = (track) -> true;
	public static final Predicate<Track> FILTER_COMPLETED = (track) -> track.getStatus() != TrackStatus.EDITING;
	public static final Predicate<Track> FILTER_RANKING = (track) -> track.getStatus() == TrackStatus.RANKING;
	public static final Predicate<Track> FILTER_EDITING = (track) -> track.getStatus() == TrackStatus.EDITING;
	
	public static Comparator<Track> createNearestComparator(Location loc)
	{
		return (o1, o2) -> (int) (o1.getStartLocation().distance(loc) - o2.getStartLocation().distance(loc));
	}
	
	public static final Comparator<Track> COMPARATOR_LENGTH_LONGTOSHORT = (o1, o2) -> (int) (o2.getLength() - o1.getLength());
	public static final Comparator<Track> COMPARATOR_LENGTH_SHORTTOLONG = (o1, o2) -> (int) (o1.getLength() - o2.getLength());
	
	
	private TrackUtils()
	{
		
	}
}
