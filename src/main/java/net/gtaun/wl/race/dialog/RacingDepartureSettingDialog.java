package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.RacingSetting;

public class RacingDepartureSettingDialog
{
	public static WlListDialog create
	(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, RacingSetting setting)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		
		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(() -> stringSet.format("Dialog.RacingDepartureSettingDialog.Caption"))
			.item(() -> stringSet.get("Common.None"), (i) -> setting.setDepartureInterval(0))
			.execute((b) ->
			{
				int[] intervals = {1, 2, 5, 10, 15, 30, 60, 120};
				for (int interval : intervals)
				{
					String item = stringSet.format("Time.Format.S", interval);
					if (interval >= 60 && interval % 60 == 0) item = stringSet.format("Time.Format.M", interval/60);
					b.item(item, (i) -> setting.setDepartureInterval(interval));
				}
			})
			.onClickOk((d, i) ->
			{
				player.playSound(1083);
				d.showParentDialog();
			})
			.build();
	}
}
