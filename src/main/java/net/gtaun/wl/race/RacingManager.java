package net.gtaun.wl.race;

import java.util.List;

import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.race.exception.AlreadyJoinedException;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.track.Track;

public interface RacingManager
{
	public enum PlayerRacingStatus
	{
		NONE,
		WAITING,
		RACING,
	}
	
	
	List<Racing> getRacings();
	List<Racing> getRacings(RacingStatus status);

	Racing createRacing(Track track, Player sponsor) throws AlreadyJoinedException;
	
	boolean isPlayerInRacing(Player player);
	Racing getPlayerRacing(Player player);
	PlayerRacingStatus getPlayerRacingStatus(Player player);
}
