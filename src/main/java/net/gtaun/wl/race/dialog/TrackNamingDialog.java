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

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.util.TrackUtils;

public abstract class TrackNamingDialog extends AbstractInputDialog
{
	private RaceServiceImpl raceService;
	
	
	public TrackNamingDialog(Player player, Shoebill shoebill, EventManager rootEventManager, String caption, String message, AbstractDialog parentDialog, RaceServiceImpl raceService)
	{
		super(player, shoebill, rootEventManager, parentDialog, caption, message);
		this.raceService = raceService;
	}
	
	public void onClickOk(String inputText)
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		player.playSound(1083, player.getLocation());
		
		String name = TrackUtils.filterName(inputText);
		if (TrackUtils.isVaildName(name) == false)
		{
			append = stringSet.format(player, "Dialog.TrackNamingDialog.IllegalLengthAppendMessage", TrackUtils.NAME_MIN_LENGTH, TrackUtils.NAME_MAX_LENGTH);
			show();
			return;
		}
		
		onNaming(name);
	}
	
	protected abstract void onNaming(String name);
}
