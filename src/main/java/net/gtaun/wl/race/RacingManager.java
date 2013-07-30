package net.gtaun.wl.race;

import java.util.List;

import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.track.Track;

public interface RacingManager
{
	List<Racing> getRacings();
	List<Racing> getRacings(RacingStatus status);

	Racing createRacing(Track track, Player sponsor);
}
