package net.gtaun.wl.race.dialog;

import java.util.List;

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
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.Track.TrackStatus;

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
				new AbstractInputDialog(player, shoebill, eventManager, caption, message, StartNewRacingDialog.this)
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
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				List<TrackCheckpoint> checkpoints = track.getCheckpoints();
				if (checkpoints.isEmpty()) return;
				player.setLocation(checkpoints.get(0).getLocation());
				
				Racing racing = racingManager.createRacing(track, player);
				racing.setName(racingName);
				racing.begin();
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
