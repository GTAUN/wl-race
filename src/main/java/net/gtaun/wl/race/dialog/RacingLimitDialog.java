package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItemRadio;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.RacingLimit;
import net.gtaun.wl.race.track.Track;

public class RacingLimitDialog
{
	public static WlListDialog create
	(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, Track track, RacingLimit limit)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);

		String allow = stringSet.get("Common.Allow");
		String disallow = stringSet.get("Common.Disallow");

		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(() -> stringSet.get("Dialog.RacingLimitDialog.Caption"))

			.item(ListDialogItemRadio.create()
				.itemText(() -> stringSet.get("Racing.Limit.AutoRepair") + ":")
				.selectedIndex(() -> limit.isAllowAutoRepair() ? 0 : 1)
				.item(allow, Color.GREEN, (i) -> limit.setAllowAutoRepair(true))
				.item(disallow, Color.RED, (i) -> limit.setAllowAutoRepair(false))
				.onSelect((i) -> i.getCurrentDialog().show())
				.build())

			.item(ListDialogItemRadio.create()
				.itemText(() -> stringSet.get("Racing.Limit.InfiniteNitrous") + ":")
				.selectedIndex(() -> limit.isAllowInfiniteNitrous() ? 0 : 1)
				.item(allow, Color.GREEN, (i) -> limit.setAllowInfiniteNitrous(true))
				.item(disallow, Color.RED, (i) -> limit.setAllowInfiniteNitrous(false))
				.onSelect((i) -> i.getCurrentDialog().show())
				.build())

			.item(ListDialogItemRadio.create()
				.itemText(() -> stringSet.get("Racing.Limit.AutoFlip") + ":")
				.selectedIndex(() -> limit.isAllowAutoFlip() ? 0 : 1)
				.item(allow, Color.GREEN, (i) -> limit.setAllowAutoFlip(true))
				.item(disallow, Color.RED, (i) -> limit.setAllowAutoFlip(false))
				.onSelect((i) -> i.getCurrentDialog().show())
				.build())

			.item(ListDialogItemRadio.create()
				.itemText(() -> stringSet.get("Racing.Limit.ChangeVehicle") + ":")
				.selectedIndex(() -> limit.isAllowChangeVehicle() ? 0 : 1)
				.item(allow, Color.GREEN, (i) -> limit.setAllowChangeVehicle(true))
				.item(disallow, Color.RED, (i) -> limit.setAllowChangeVehicle(false))
				.onSelect((i) -> i.getCurrentDialog().show())
				.build())

			.item(() -> stringSet.get("Common.OK"), (i) -> i.getCurrentDialog().showParentDialog())

			.onClickOk((d, i) -> player.playSound(1083))
			.build();
	}
}
