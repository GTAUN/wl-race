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

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.service.Service;
import net.gtaun.wl.race.track.Track;

public interface RaceService extends Service
{
	Plugin getPlugin();

	void showMainDialog(Player player, AbstractDialog parentDialog);
	
	TrackManager getTrackManager();
	RacingManager getRacingManager();

	void editTrack(Player player, Track track);
	void stopEditingTrack(Player player);
	
	boolean isEditingTrack(Player player);
	Track getEditingTrack(Player player);
}
