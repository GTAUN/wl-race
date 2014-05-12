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

package net.gtaun.wl.race.dialog;

import java.util.List;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlPageListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;

public class RacingListDialog extends WlPageListDialog
{
	private final RaceServiceImpl raceService;
	
	
	public RacingListDialog(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service)
	{		super(player, eventManager);
		setParentDialog(parent);
		
		this.raceService = service;
	}
	
	@Override
	public void show()
	{
		PlayerStringSet stringSet = raceService.getLocalizedStringSet().getStringSet(player);
		
		RacingManagerImpl racingManager = raceService.getRacingManager();
		List<Racing> allRacings = racingManager.getRacings();
		List<Racing> racings = racingManager.getRacings(RacingStatus.WAITING);
		
		items.clear();
		for (Racing racing : racings)
		{
			Track track = racing.getTrack();
			String item = stringSet.format("Dialog.RacingListDialog.Item", racing.getName(), track.getName(), racing.getSponsor().getName());
			addItem(item, (i) ->
			{
				player.playSound(1083);
				new RacingDialog(player, rootEventManager, this, raceService, racing).show();
			});
		}
		
		setCaption(stringSet.format("Dialog.RacingListDialog.Caption", racings.size(), allRacings.size()));
		super.show();
	}
}
