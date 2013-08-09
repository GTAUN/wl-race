package net.gtaun.wl.race.dialog;

import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.dialog.DialogCancelEvent.DialogCancelType;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.track.TrackCheckpoint;

import org.apache.commons.lang3.StringUtils;

public class StartNewRacingDialog extends AbstractListDialog
{
	private String racingName;
	
	
	public StartNewRacingDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);

		final RacingManagerImpl racingManager = raceService.getRacingManager();
		final String racingTypeStr = (track.getStatus() == TrackStatus.EDITING) ? "测试编辑中的赛道" : "发起新比赛";
		
		this.caption = String.format("%1$s: %2$s", "赛车系统", racingTypeStr);
		this.racingName = player.getName() + "'s Racing";

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
				new AbstractInputDialog(player, shoebill, eventManager, StartNewRacingDialog.this, caption, message)
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
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("开始测试赛道")
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return track.getStatus() == TrackStatus.EDITING;
			}
			
			private void startNewRacing(Location location)
			{
				Racing racing = racingManager.createRacing(track, player);
				racing.setName(racingName);
				
				player.setLocation(location);
				racing.begin();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				List<TrackCheckpoint> checkpoints = track.getCheckpoints();
				if (checkpoints.isEmpty()) return;
				
				final Location startLoc = checkpoints.get(0).getLocation();
				
				if (racingManager.isPlayerInRacing(player))
				{
					final Racing racing = racingManager.getPlayerRacing(player);
					String text = String.format("当前正在参加 %1$s 比赛，您确定要退出并举行新比赛吗？", racing.getName());
					new MsgboxDialog(player, shoebill, eventManager, StartNewRacingDialog.this, "开始新比赛", text)
					{
						@Override
						protected void onClickOk()
						{
							player.playSound(1083, player.getLocation());
							racing.leave(player);
							startNewRacing(startLoc);
						}
						
						@Override
						protected void onCancel(DialogCancelType type)
						{
							player.playSound(1083, player.getLocation());
							showParentDialog();
						}
					}.show();
				}
				else startNewRacing(startLoc);
			}
		});
		
		dialogListItems.add(new DialogListItem("发起比赛")
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getStatus() != TrackStatus.COMPLETED) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				Racing racing = racingManager.createRacing(track, player);
				racing.setName(racingName);
			}
		});
	}
}
