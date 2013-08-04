package net.gtaun.wl.race.dialog;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractPageListDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;

public class TrackListDialog extends AbstractPageListDialog
{
	public TrackListDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final AbstractDialog parentDialog, final RaceServiceImpl raceService, List<Track> tracks)
	{
		super(player, shoebill, eventManager, parentDialog);
		
		for (final Track track : tracks)
		{
			String desc = StringUtils.abbreviate(track.getDesc(), 8);
			String item = String.format("赛道: %1$s	作者: %2$s	描述: %3$s	检查点数: %4$d	状态: %5$s", track.getName(), track.getAuthorUniqueId(), desc, track.getCheckpoints().size(), track.getStatus());
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					new TrackDialog(player, shoebill, eventManager, TrackListDialog.this, raceService, track).show();
				}
			});
		}
	}
}
