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

import java.util.function.BiConsumer;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlInputDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.util.TrackUtils;

public abstract class TrackNamingDialog
{
	public static WlInputDialog create(Player player, EventManager eventManager, AbstractDialog parent, String caption, String message, RaceServiceImpl raceService, BiConsumer<WlInputDialog, String> namingHandler)
	{
		PlayerStringSet stringSet = raceService.getLocalizedStringSet().getStringSet(player);
		return WlInputDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(caption)
			.message(message)
			.onClickOk((d, text) ->
			{
				player.playSound(1083);
				
				String name = TrackUtils.filterName(text);
				if (TrackUtils.isVaildName(name) == false)
				{
					((WlInputDialog) d).setAppendMessage(stringSet.format("Dialog.TrackNamingDialog.IllegalLengthAppendMessage", TrackUtils.NAME_MIN_LENGTH, TrackUtils.NAME_MAX_LENGTH));
					d.show();
					return;
				}
				
				namingHandler.accept((WlInputDialog) d, name);
			})
			.build();
	}
}
