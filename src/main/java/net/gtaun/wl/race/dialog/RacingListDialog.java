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

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractPageListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;

public class RacingListDialog extends AbstractPageListDialog
{
	private final RaceServiceImpl raceService;
	
	
	public RacingListDialog(Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, RaceServiceImpl raceService)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.raceService = raceService;
	}
	
	@Override
	public void show()
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		RacingManagerImpl racingManager = raceService.getRacingManager();
		List<Racing> allRacings = racingManager.getRacings();
		List<Racing> racings = racingManager.getRacings(RacingStatus.WAITING);
		
		dialogListItems.clear();
		for (final Racing racing : racings)
		{
			Track track = racing.getTrack();
			String item = stringSet.format(player, "Dialog.RacingListDialog.Text", racing.getName(), track.getName(), racing.getSponsor().getName());
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					new RacingDialog(player, shoebill, eventManager, RacingListDialog.this, raceService, racing).show();
				}
			});
		}
		
		this.caption = stringSet.format(player, "Dialog.RacingListDialog.Caption", racings.size(), allRacings.size());
		super.show();
	}
}
