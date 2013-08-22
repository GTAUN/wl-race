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
import net.gtaun.shoebill.event.dialog.DialogCancelEvent.DialogCancelType;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;

public abstract class NewRacingConfirmDialog extends MsgboxDialog
{
	private final Racing racing;
	
	
	public NewRacingConfirmDialog(Player player, Shoebill shoebill, EventManager rootEventManager, AbstractDialog parentDialog, RaceServiceImpl raceService, Racing racing)
	{
		super(player, shoebill, rootEventManager, parentDialog, "开始新比赛", String.format("当前正在参加 %1$s 比赛，您确定要退出并举行新比赛吗？", racing.getName()));
		this.racing = racing;
	}
	
	protected abstract void startRacing();
	
	@Override
	protected void onClickOk()
	{
		player.playSound(1083, player.getLocation());
		racing.leave(player);
		startRacing();
	}
	
	@Override
	protected void onCancel(DialogCancelType type)
	{
		player.playSound(1083, player.getLocation());
		showParentDialog();
	}
}
