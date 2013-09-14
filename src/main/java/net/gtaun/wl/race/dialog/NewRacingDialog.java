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
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.DeathRule;
import net.gtaun.wl.race.racing.Racing.RacingType;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.racing.RacingSetting;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.track.Track.TrackType;
import net.gtaun.wl.race.util.RacingUtils;

import org.apache.commons.lang3.StringUtils;

public class NewRacingDialog extends AbstractListDialog
{
	private String racingName;
	private RacingSetting setting;
	
	
	public NewRacingDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);

		final RacingManagerImpl racingManager = raceService.getRacingManager();
		final String racingTypeStr = (track.getStatus() == TrackStatus.EDITING) ? "测试编辑中的赛道" : "发起新比赛";
		
		this.caption = String.format("%1$s: %2$s", "赛车系统", racingTypeStr);
		this.racingName = RacingUtils.getDefaultName(player, track);
		
		setting = new RacingSetting(track);
		
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
				return String.format("赛道: %1$s (检查点数%2$d, 长度%3$1.1fKM)",
						track.getName(), track.getCheckpoints().size(), track.getLength()/1000.0f);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new TrackDialog(player, shoebill, eventManager, NewRacingDialog.this, raceService, track).show();
			}
		});
		
		String trackType = "普通赛道";
		if (track.getType() == TrackType.CIRCUIT) trackType = String.format("绕圈赛道 (%1$d 圈)", track.getCircultLaps());
		dialogListItems.add(new DialogListItem(String.format("赛道类型: %1$s", trackType))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItemRadio("比赛类型:")
		{
			{
				addItem(new RadioItem("普通", Color.CORNFLOWERBLUE)
				{
					@Override public void onSelected()	{ setting.setRacingType(RacingType.NORMAL); }
				});
				addItem(new RadioItem("淘汰赛", Color.MAGENTA)
				{
					@Override public void onSelected()	{ setting.setRacingType(RacingType.KNOCKOUT); }
				});
			}
			
			@Override
			public void onItemSelect(RadioItem item, int itemIndex)
			{
				player.playSound(1083, player.getLocation());
				show();
			}
			
			@Override
			public int getSelected()
			{
				return setting.getRacingType().ordinal();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String format = "发车间隔: %1$s";
				int interval = setting.getDepartureInterval();
				if (interval == 0) return String.format(format, "无");
				return String.format(format, interval);
			}

			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new RacingDepartureSettingDialog(player, shoebill, eventManager, NewRacingDialog.this, setting).show();
			}
		});
		
		dialogListItems.add(new DialogListItemRadio("死亡处理:")
		{
			{
				addItem(new RadioItem("等待并回到检查点", Color.AQUA)
				{
					@Override public void onSelected()	{ setting.setDeathRule(DeathRule.WAIT_AND_RETURN); }
				});
				addItem(new RadioItem("淘汰", Color.FUCHSIA)
				{
					@Override public void onSelected()	{ setting.setDeathRule(DeathRule.KNOCKOUT); }
				});
			}
			
			@Override
			public void onItemSelect(RadioItem item, int itemIndex)
			{
				player.playSound(1083, player.getLocation());
				show();
			}
			
			@Override
			public int getSelected()
			{
				return setting.getDeathRule().ordinal();
			}
		});
		
		dialogListItems.add(new DialogListItemCheck("限制:")
		{
			{
				addItem(new CheckItem("自动修车", Color.LIME)
				{
					@Override public boolean isChecked()	{ return setting.getLimit().isAllowAutoRepair(); }
				});
				addItem(new CheckItem("无限氮气", Color.RED)
				{
					@Override public boolean isChecked()	{ return setting.getLimit().isAllowInfiniteNitrous(); }
				});
				addItem(new CheckItem("自动翻车", Color.BLUE)
				{
					@Override public boolean isChecked()	{ return setting.getLimit().isAllowAutoFlip(); }
				});
				addItem(new CheckItem("更换载具", Color.GOLD)
				{
					@Override public boolean isChecked()	{ return setting.getLimit().isAllowChangeVehicle(); }
				});
			}
			
			@Override
			public void onItemSelect()
			{
				new RacingLimitDialog(player, shoebill, eventManager, NewRacingDialog.this, track, setting.getLimit()).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("人数限制: ")
		{
			@Override
			public String toItemString()
			{
				int min = track.getSetting().getMinPlayers();
				int max = setting.getMaxPlayers();
				if (min != 0 && max != 0) return String.format("%1$d ~ %2$d 人", min, max);
				return itemString + "不限制";
			}
			
			@Override
			public void onItemSelect()
			{
				
			}
		});
		
		dialogListItems.add(new DialogListItem("比赛密码: ")
		{
			@Override
			public String toItemString()
			{
				if (StringUtils.isBlank(setting.getPassword())) return itemString + "无";
				return itemString + setting.getPassword();
			}
			
			@Override
			public void onItemSelect()
			{
				
			}
		});
		
		dialogListItems.add(new DialogListItem("发起比赛")
		{
			private void startNewRacing()
			{
				Racing racing = racingManager.createRacing(track, player, racingName);
				racing.teleToStartingPoint(player);
				racing.setSetting(setting);
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
