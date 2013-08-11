package net.gtaun.wl.race.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.gtaun.shoebill.common.Filter;
import net.gtaun.shoebill.data.Location;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;

import org.apache.commons.lang3.StringUtils;

public final class TrackUtil
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
	
	public static List<Track> filterTracks(List<Track> tracks, Filter<Track> filter)
	{
		List<Track> filteredTracks = new ArrayList<>();
		for (Track track : tracks) if (filter.isAcceptable(track)) filteredTracks.add(track);
		return filteredTracks;
	}
	
	public static final Filter<Track> FILTER_ALL = new Filter<Track>()
	{
		@Override
		public boolean isAcceptable(Track track)
		{
			return true;
		}
	};
	
	public static final Filter<Track> FILTER_COMPLETED = new Filter<Track>()
	{
		@Override
		public boolean isAcceptable(Track track)
		{
			return track.getStatus() != TrackStatus.EDITING;
		}
	};
	
	public static final Filter<Track> FILTER_RANKING = new Filter<Track>()
	{
		@Override
		public boolean isAcceptable(Track track)
		{
			return track.getStatus() == TrackStatus.RANKING;
		}
	};
	
	public static final Filter<Track> FILTER_EDITING = new Filter<Track>()
	{
		@Override
		public boolean isAcceptable(Track track)
		{
			return track.getStatus() == TrackStatus.EDITING;
		}
	};
	
	public static Comparator<Track> createNearestComparator(final Location loc)
	{
		return new Comparator<Track>()
		{
			@Override
			public int compare(Track o1, Track o2)
			{
				return (int) (o1.getStartLocation().distance(loc) - o2.getStartLocation().distance(loc));
			}
		};
	}
	
	public static final Comparator<Track> COMPARATOR_LENGTH_LONGTOSHORT = new Comparator<Track>()
	{
		@Override
		public int compare(Track o1, Track o2)
		{
			return (int) (o2.getLength() - o1.getLength());
		}
	};
	
	public static final Comparator<Track> COMPARATOR_LENGTH_SHORTTOLONG = new Comparator<Track>()
	{
		@Override
		public int compare(Track o1, Track o2)
		{
			return (int) (o1.getLength() - o2.getLength());
		}
	};
	
	private TrackUtil()
	{
		
	}
}
