package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackSetting;

public class TrackSettingDialog
{
	public static WlListDialog create(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, Track track)
	{
		LocalizedStringSet stringSet = service.getLocalizedStringSet();
		TrackSetting setting = track.getSetting();
	
		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption((d) -> stringSet.format(player, "Dialog.TrackSettingDialog.Caption", track.getName()))
			.item(() ->
			{
				String msg = stringSet.get(player, "Common.NoLimit");
				int min = setting.getMinPlayers();
				if (min != 0) msg = stringSet.format(player, "Dialog.TrackSettingDialog.PlayersFormat", min);
				return stringSet.format(player, "Dialog.TrackSettingDialog.MinPlayersFormat", msg);
			}, (i) ->
			{
				
			})
			
			.item(() ->
			{
				String msg = stringSet.get(player, "Common.NoLimit");
				int min = setting.getMinPlayers();
				if (min != 0) msg = stringSet.format(player, "Dialog.TrackSettingDialog.PlayersFormat", min);
				return stringSet.format(player, "Dialog.TrackSettingDialog.MaxPlayersFormat", msg);
			}, (i) ->
			{
				
			})
			.build();
	}
}
