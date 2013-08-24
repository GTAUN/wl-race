package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.race.racing.RacingLimit;
import net.gtaun.wl.race.track.Track;

public class RacingLimitDialog extends AbstractListDialog
{
	public RacingLimitDialog(Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, Track track, RacingLimit limit)
	{
		super(player, shoebill, eventManager, parentDialog);
	}
	
}
