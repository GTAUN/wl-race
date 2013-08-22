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
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.util.RacingUtils;

import org.apache.commons.lang3.StringUtils;

public class NewRacingDialog extends AbstractListDialog
{
	private String racingName;
	
	
	public NewRacingDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);

		final RacingManagerImpl racingManager = raceService.getRacingManager();
		final String racingTypeStr = (track.getStatus() == TrackStatus.EDITING) ? "测试编辑中的赛道" : "发起新比赛";
		
		this.caption = String.format("%1$s: %2$s", "赛车系统", racingTypeStr);
		this.racingName = RacingUtils.getDefaultName(player, track);

		dialogListItems.add(new DialogListItem()
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getStatus() == TrackStatus.EDITING) return false;
				return true;
			}
			
			@Override
			public String toItemString()
			{
				return String.format("比赛名称: %1$s", racingName);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = String.format("%1$s: %2$s: 编辑比赛名称", "赛车系统", racingTypeStr);
				String message = "请输入新的比赛名称:";
				new AbstractInputDialog(player, shoebill, eventManager, NewRacingDialog.this, caption, message)
				{
					public void onClickOk(String inputText)
					{
						String name = StringUtils.trimToEmpty(inputText);
						if (name.length()<3 || name.length()>40)
						{
							append = "比赛名称长度要求为 3 ~ 40 之间，请重新输入。";
							show();
							return;
						}
						
						racingName = name;
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("赛道: %1$s", track.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new TrackDialog(player, shoebill, eventManager, NewRacingDialog.this, raceService, track).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("发起比赛")
		{
			private void startNewRacing()
			{
				Racing racing = racingManager.createRacing(track, player, racingName);
				racing.teleToStartingPoint(player);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				if (track.getCheckpoints().isEmpty()) return;
				if (racingManager.isPlayerInRacing(player))
				{
					final Racing racing = racingManager.getPlayerRacing(player);
					new NewRacingConfirmDialog(player, shoebill, rootEventManager, NewRacingDialog.this, raceService, racing)
					{
						@Override
						protected void startRacing()
						{
							startNewRacing();
						}
					}.show();
				}
				else startNewRacing();
			}
		});
	}
}
