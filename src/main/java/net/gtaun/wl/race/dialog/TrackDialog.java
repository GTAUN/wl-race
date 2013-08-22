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
import net.gtaun.shoebill.event.dialog.DialogCancelEvent.DialogCancelType;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.util.RacingUtil;

import org.apache.commons.lang3.StringUtils;

public class TrackDialog extends AbstractListDialog
{
	private final Track track;
	
	
	public TrackDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, final AbstractDialog parentDialog, final RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.caption = String.format("%1$s: 查看赛道 %2$s 的信息", "赛车系统", track.getName());
		this.track = track;
		
		final RacingManagerImpl racingManager = raceService.getRacingManager();
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("赛道名: %1$s", track.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("作者: %1$s", track.getAuthorUniqueId());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String desc = track.getDesc();
				if (StringUtils.isBlank(desc)) desc = "空";
				return String.format("描述: %1$s", StringUtils.abbreviate(desc, 60));
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("状态: %1$s", track.getStatus());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("检查点数: %1$d", track.getCheckpoints().size());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("总长度: %1$1.2f公里", track.getLength()/1000.0f);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("当前距离: %1$1.1f米", player.getLocation().distance(track.getStartLocation()));
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("编辑赛道")
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getStatus() == TrackStatus.RANKING) return false;
				if (player.isAdmin()) return true;
				return player.getName().equals(track.getAuthorUniqueId());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				if (track.getStatus() == TrackStatus.COMPLETED)
				{
					String format =
							"尝试再次编辑赛道将会清空当前所有的比赛成绩信息。\n" +
							"这将会影响到其他玩家的感受，请您务必谨慎操作。\n\n" +
							"您确定要再次编辑赛道 %1$s 吗？";
					String message = String.format(format, track.getName());
					new MsgboxDialog(player, shoebill, eventManager, TrackDialog.this, "再次编辑赛道确认", message)
					{
						protected void onClickOk()
						{
							player.playSound(1083, player.getLocation());
							raceService.editTrack(player, track);
						}
					}.show();
				}
				else if (track.getStatus() == TrackStatus.RANKING)
				{
					show();
				}
				else
				{
					raceService.editTrack(player, track);
				}
			}
		});
		
		dialogListItems.add(new DialogListItem("测试本赛道")
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return track.getStatus() == TrackStatus.EDITING;
			}
			
			private void startNewRacing()
			{
				Racing racing = racingManager.createRacing(track, player, RacingUtil.getDefaultName(player, track));
				racing.teleToStartingPoint(player);
				racing.beginCountdown();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				List<TrackCheckpoint> checkpoints = track.getCheckpoints();
				if (checkpoints.isEmpty()) return;
				
				if (racingManager.isPlayerInRacing(player))
				{
					final Racing racing = racingManager.getPlayerRacing(player);
					new NewRacingConfirmDialog(player, shoebill, rootEventManager, TrackDialog.this, raceService, racing)
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
		
		dialogListItems.add(new DialogListItem("发起新比赛")
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return track.getStatus() != TrackStatus.EDITING;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new NewRacingDialog(player, shoebill, eventManager, TrackDialog.this, raceService, track).show();
			}
		});

		dialogListItems.add(new DialogListItem("快速发起比赛")
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return track.getStatus() != TrackStatus.EDITING;
			}
			
			private void startNewRacing()
			{
				Racing racing = racingManager.createRacing(track, player, RacingUtil.getDefaultName(player, track));
				racing.teleToStartingPoint(player);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				if (racingManager.isPlayerInRacing(player))
				{
					final Racing racing = racingManager.getPlayerRacing(player);
					String text = String.format("当前正在参加 %1$s 比赛，您确定要退出并举行新比赛吗？", racing.getName());
					new MsgboxDialog(player, shoebill, eventManager, TrackDialog.this, "开始新比赛", text)
					{
						@Override
						protected void onClickOk()
						{
							player.playSound(1083, player.getLocation());
							racing.leave(player);
							startNewRacing();
						}
						
						@Override
						protected void onCancel(DialogCancelType type)
						{
							player.playSound(1083, player.getLocation());
							showParentDialog();
						}
					}.show();
				}
				else startNewRacing();
			}
		});
	}
	
	@Override
	public void show()
	{
		this.caption = String.format("%1$s: 赛道 %2$s (作者: %3$s)", "赛车系统", track.getName(), track.getAuthorUniqueId());
		super.show();
	}
}
