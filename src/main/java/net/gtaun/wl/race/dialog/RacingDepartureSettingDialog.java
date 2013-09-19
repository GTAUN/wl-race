package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.RacingSetting;

public class RacingDepartureSettingDialog extends AbstractListDialog
{
	public RacingDepartureSettingDialog(final Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService, final RacingSetting setting)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		this.caption = stringSet.format(player, "Dialog.RacingDepartureSettingDialog.Caption");

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Common.None"))
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				setting.setDepartureInterval(0);
				showParentDialog();
			}
		});
		
		int[] intervals = {1, 2, 5, 10, 15, 30, 60, 120};
		for (final int interval : intervals)
		{
			String item = stringSet.format(player, "Time.Format.S", interval);
			if (interval >= 60 && interval % 60 == 0) item = stringSet.format(player, "Time.Format.M", interval/60);
			dialogListItems.add(new DialogListItem(item)
			{	
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					setting.setDepartureInterval(interval);
					showParentDialog();
				}
			});
		}
	}
	
	
}
