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

package net.gtaun.wl.race;

import java.util.List;

import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.race.track.Track;

public interface TrackManager
{
	void renameTrack(Track track, String name) throws AlreadyExistException, IllegalArgumentException;
	
	Track createTrack(Player player, String name) throws AlreadyExistException, IllegalArgumentException;
	Track createTrack(String author, String name) throws AlreadyExistException, IllegalArgumentException;
	Track getTrack(String name);
	
	void deleteTrack(Track track);

	List<Track> searchTrackByName(String text);
	List<Track> searchTrackByAuthor(String uniqueId);
	
	List<Track> getAllTracks();
}
