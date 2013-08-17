/**
 * Copyright (C) 2013 MK124
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.ResourceDescription;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackManagerImpl;

public class RaceMainDialog extends AbstractListDialog
{
	public RaceMainDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.caption = "赛车系统";
		
		final TrackManagerImpl trackManager = raceService.getTrackManager();
		final RacingManagerImpl racingManager = raceService.getRacingManager();

		dialogListItems.add(new DialogListItem()
		{
			@Override
			public boolean isEnabled()
			{
				return racingManager.isPlayerInRacing(player);
			}
			
			@Override
			public String toItemString()
			{
				Racing racing = racingManager.getPlayerRacing(player);
				return String.format("参与中的比赛: %1$s", racing.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Racing racing = racingManager.getPlayerRacing(player);
				new RacingDialog(player, shoebill, eventManager, RaceMainDialog.this, raceService, racing).show();
			}
		});

		dialogListItems.add(new DialogListItem()
		{
			@Override
			public boolean isEnabled()
			{
				return raceService.getEditingTrack(player) != null;
			}
			
			@Override
			public String toItemString()
			{
				Track track = raceService.getEditingTrack(player);
				return String.format("编辑中的赛道: %1$s", track.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Track track = raceService.getEditingTrack(player);
				new TrackEditDialog(player, shoebill, eventManager, RaceMainDialog.this, raceService, track).show();
			}
		});

		dialogListItems.add(new DialogListItem("赛道列表 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new TrackListMainDialog(player, shoebill, eventManager, RaceMainDialog.this, raceService).show();
			}
		});

		dialogListItems.add(new DialogListItem("赛道收藏夹 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});

		dialogListItems.add(new DialogListItem("当前比赛列表 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new RacingListDialog(player, shoebill, eventManager, RaceMainDialog.this, raceService).show();
			}
		});

		dialogListItems.add(new DialogListItem("创建新赛道")
		{
			@Override
			public boolean isEnabled()
			{
				if (raceService.isEditingTrack(player)) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = String.format("%1$s: 创建新赛道", "赛车系统");
				String message = "请您输入预想的赛道名:";
				new TrackNamingDialog(player, shoebill, rootEventManager, caption, message, RaceMainDialog.this)
				{
					@Override
					protected void onNaming(String name)
					{
						try
						{
							Track track = trackManager.createTrack(player, name);
							raceService.editTrack(player, track);
							new TrackEditDialog(player, shoebill, eventManager, RaceMainDialog.this, raceService, track).show();
						}
						catch (AlreadyExistException e)
						{
							append = String.format("{FF0000}* 赛道名 {FFFFFF}\"%1$s\" {FF0000}已被使用，请重新命名。", name);
							show();
						}
						catch (IllegalArgumentException e)
						{
							append = String.format("{FF0000}* 赛道名 {FFFFFF}\"%1$s\" {FF0000}不合法，请重新命名。", name);
							show();
						}
					}
				}.show();
			}
		});

		dialogListItems.add(new DialogListItem("我的车手信息")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});

		dialogListItems.add(new DialogListItem("我的比赛记录 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});

		dialogListItems.add(new DialogListItem("综合实力排名 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});
		
		dialogListItems.add(new DialogListItem("个人偏好设置 ...")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});
		
		dialogListItems.add(new DialogListItem("帮助信息")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				String caption = String.format("%1$s: %2$s", "车管", "帮助信息");
				new MsgboxDialog(player, shoebill, eventManager, RaceMainDialog.this, caption, "偷懒中，暂无帮助信息……").show();
			}
		});
		
		dialogListItems.add(new DialogListItem("关于赛车系统")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Plugin plugin = raceService.getPlugin();
				ResourceDescription desc = plugin.getDescription();
				
				String caption = String.format("%1$s: %2$s", "赛车", "关于赛车系统");
				String format =
					"--- 新未来世界 赛车系统组件 ---\n" +
					"版本: %1$s (Build %2$d)\n" +
					"编译时间: %3$s\n\n" +
					"开发: mk124\n" +
					"功能设计: mk124\n" +
					"设计顾问: 52_PLA(aka. Yin.J), [ITC]1314, [ITC]KTS, snwang1996\n" +
					"数据采集: mk124, 52_PLA\n" +
					"测试: 52_PLA, [ITC]1314, [ITC]KTS, SMALL_KR, snwang1996\n" +
					"感谢: 原未来世界制作团队成员(yezhizhu, vvg, fangye), Luck, Waunny\n\n" +
					"本组件是新未来世界项目的一部分。\n" +
					"本组件使用 GPL v2 许可证开放源代码。\n" +
					"本组件禁止在任何商业或盈利性服务器上使用。\n";
				String message = String.format(format, desc.getVersion(), desc.getBuildNumber(), desc.getBuildDate());
				
				new MsgboxDialog(player, shoebill, eventManager, RaceMainDialog.this, caption, message).show();
			}
		});
	}
}
