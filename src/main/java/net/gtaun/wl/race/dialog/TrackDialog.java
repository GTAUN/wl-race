package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;

import org.apache.commons.lang3.StringUtils;

public class TrackDialog extends AbstractListDialog
{
	private final Track track;
	
	
	public TrackDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, final AbstractDialog parentDialog, final RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.caption = String.format("%1$s: 查看赛道 %2$s 的信息", "赛车系统", track.getName());
		this.track = track;
		
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
				return String.format("描述: %1$s", desc);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
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
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new StartNewRacingDialog(player, shoebill, eventManager, TrackDialog.this, raceService, track).show();
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
				new StartNewRacingDialog(player, shoebill, eventManager, TrackDialog.this, raceService, track).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("编辑赛道")
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getStatus() == TrackStatus.RANKING) return false;
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
	}
	
	@Override
	public void show()
	{
		this.caption = String.format("%1$s: 赛道 %2$s (作者: %3$s)", "赛车系统", track.getName(), track.getAuthorUniqueId());
		super.show();
	}
}
