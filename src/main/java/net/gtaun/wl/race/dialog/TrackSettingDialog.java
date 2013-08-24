package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackSetting;

public class TrackSettingDialog extends AbstractListDialog
{
	public TrackSettingDialog(Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		final TrackSetting setting = track.getSetting();
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String msg = "不限制";
				int min = setting.getMinPlayers();
				if (min != 0) msg = String.format("%1$d 人", min);
				return String.format("最少参赛人数: %1$s", msg);
			}
			
			@Override
			public void onItemSelect()
			{
				
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String msg = "不限制";
				int min = setting.getMinPlayers();
				if (min != 0) msg = String.format("%1$d 人", min);
				return String.format("最多参赛人数: %1$s", msg);
			}
			
			@Override
			public void onItemSelect()
			{
				
			}
		});
		
		
	}
}
