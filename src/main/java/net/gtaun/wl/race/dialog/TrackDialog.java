package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;

import org.apache.commons.lang3.StringUtils;

public class TrackDialog extends AbstractListDialog
{
	private final Track track;
	
	
	public TrackDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, final AbstractDialog parentDialog, final RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
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
		
		dialogListItems.add(new DialogListItem("发起新比赛")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new StartNewRacingDialog(player, shoebill, eventManager, parentDialog, raceService, track).show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public boolean isEnabled()
			{
				return player.getName().equals(track.getAuthorUniqueId());
			}
			
			@Override
			public String toItemString()
			{
				return "编辑赛道";
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				raceService.editTrack(player, track);
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
