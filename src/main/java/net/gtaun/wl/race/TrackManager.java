package net.gtaun.wl.race;

import java.util.List;

import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.race.track.Track;

public interface TrackManager
{
	Track createTrack(Player player, String name) throws AlreadyExistException, IllegalArgumentException;
	Track getTrack(String name);

	List<Track> searchTrackByName(String text);
	List<Track> searchTrackByAuthor(String uniqueId);
	
	List<Track> getAllTracks();
}
