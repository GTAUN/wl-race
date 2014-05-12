/**
 * WL Race Plugin
 * Copyright (C) 2013 MK124
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.gtaun.wl.race.dialog;

import java.util.List;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlInputDialog;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackManagerImpl;

public class TrackListMainDialog
{
	public static WlListDialog create(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		TrackManagerImpl trackManager = service.getTrackManager();
	
		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(stringSet.get("Dialog.TrackListMainDialog.Caption"))
			.item(stringSet.get("Dialog.TrackListMainDialog.Nearby"), (i) ->
			{
				List<Track> tracks = trackManager.getAllTracks();
				new TrackListDialog(player, eventManager, i.getCurrentDialog(), service, tracks).show();
			})
			.item(stringSet.get("Dialog.TrackListMainDialog.MyFavorites"), (i) ->
			{
				List<Track> tracks = trackManager.getAllTracks();
				new TrackListDialog(player, eventManager, i.getCurrentDialog(), service, tracks).show();
			})
			.item(stringSet.get("Dialog.TrackListMainDialog.MyTracks"), (i) ->
			{
				List<Track> tracks = trackManager.searchTrackByAuthor(player.getName());
				new TrackListDialog(player, eventManager, i.getCurrentDialog(), service, tracks).show();
			})
			.item(stringSet.get("Dialog.TrackListMainDialog.SearchByAuthor"), (i) ->
			{

				String caption = stringSet.get("Dialog.TrackSearchByAuthorDialog.Caption");
				String message = stringSet.get("Dialog.TrackSearchByAuthorDialog.Text");
				WlInputDialog.create(player, eventManager)
					.parentDialog(i.getCurrentDialog())
					.caption(caption)
					.message(message)
					.onClickOk((d, text) ->
					{
						player.playSound(1083);
						
						List<Track> tracks = trackManager.searchTrackByAuthor(text);
						new TrackListDialog(player, eventManager, i.getCurrentDialog(), service, tracks).show();
					})
					.build().show();
			})
			.item(stringSet.get("Dialog.TrackListMainDialog.SearchByKeyword"), (i) ->
			{
				String caption = stringSet.get("Dialog.TrackSearchByKeywordDialog.Caption");
				String message = stringSet.get("Dialog.TrackSearchByKeywordDialog.Text");
				WlInputDialog.create(player, eventManager)
					.parentDialog(i.getCurrentDialog())
					.caption(caption)
					.message(message)
					.onClickOk((d, text) ->
					{
						List<Track> tracks = trackManager.searchTrackByName(text);
						new TrackListDialog(player, eventManager, i.getCurrentDialog(), service, tracks).show();
					})
					.build().show();
			})
			.onClickOk((d, i) -> player.playSound(1083))
			.build();
	}
}
