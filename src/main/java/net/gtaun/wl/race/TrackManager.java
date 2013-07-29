package net.gtaun.wl.race;

import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.wl.race.data.Track;

public interface TrackManager
{
	Track createTrack(String name) throws AlreadyExistException;
}
