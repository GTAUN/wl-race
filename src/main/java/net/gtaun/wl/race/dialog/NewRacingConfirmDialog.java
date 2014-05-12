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

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlMsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;

public class NewRacingConfirmDialog
{
	public static WlMsgboxDialog create
	(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, Racing racing, Runnable startRacingCallback)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		return WlMsgboxDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(stringSet.get("Dialog.NewRacingConfirmDialog.Caption"))
			.message(stringSet.format("Dialog.NewRacingConfirmDialog.Text", racing.getName()))
			.onClickOk((d) ->
			{
				player.playSound(1083);
				racing.leave(player);
				startRacingCallback.run();
			})
			.build();
	}
}
