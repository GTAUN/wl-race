package net.gtaun.wl.race;

import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.service.Service;
import net.gtaun.wl.race.track.Track;

public interface RaceService extends Service
{
	Plugin getPlugin();

	TrackManager getTrackManager();
	RacingManager getRacingManager();

	void editTrack(Player player, Track track);
	
	boolean isEditingTrack(Player player);
	Track getEditingTrack(Player player);
}
