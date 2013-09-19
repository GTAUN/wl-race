package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackSetting;

public class TrackSettingDialog extends AbstractListDialog
{
	public TrackSettingDialog(final Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, RaceServiceImpl raceService, Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		final TrackSetting setting = track.getSetting();
		
		this.caption = stringSet.format(player, "Dialog.TrackSettingDialog.Caption", track.getName());
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String msg = stringSet.get(player, "Common.NoLimit");
				int min = setting.getMinPlayers();
				if (min != 0) msg = stringSet.format(player, "Dialog.TrackSettingDialog.PlayersFormat", min);
				return stringSet.format(player, "Dialog.TrackSettingDialog.MinPlayersFormat", msg);
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
				String msg = stringSet.get(player, "Common.NoLimit");
				int min = setting.getMinPlayers();
				if (min != 0) msg = stringSet.format(player, "Dialog.TrackSettingDialog.PlayersFormat", min);
				return stringSet.format(player, "Dialog.TrackSettingDialog.MaxPlayersFormat", msg);
			}
			
			@Override
			public void onItemSelect()
			{
				
			}
		});
	}
}
