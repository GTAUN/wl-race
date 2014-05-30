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

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.resource.Plugin;
import net.gtaun.shoebill.resource.ResourceDescription;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.common.dialog.WlMsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackManagerImpl;

public class RaceMainDialog
{
	public static WlListDialog create(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		TrackManagerImpl trackManager = service.getTrackManager();
		RacingManagerImpl racingManager = service.getRacingManager();

		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(stringSet.get("Dialog.RaceMainDialog.Caption"))

			.item(() ->
			{
				Racing racing = racingManager.getPlayerRacing(player);
				return stringSet.format("Dialog.RaceMainDialog.Racing", racing.getName());
			}, () ->
			{
				return racingManager.isPlayerInRacing(player);
			}, (i) ->
			{
				Racing racing = racingManager.getPlayerRacing(player);
				new RacingDialog(player, eventManager, i.getCurrentDialog(), service, racing).show();
			})

			.item(() ->
			{
				Track track = service.getEditingTrack(player);
				return stringSet.format("Dialog.RaceMainDialog.Editing", track.getName());
			}, () ->
			{
				return service.getEditingTrack(player) != null;
			}, (i) ->
			{
				Track track = service.getEditingTrack(player);
				new TrackEditDialog(player, eventManager, i.getCurrentDialog(), service, track).show();
			})

			.item(() -> stringSet.get("Dialog.RaceMainDialog.TrackList"), (i) ->
			{
				TrackListMainDialog.create(player, eventManager, i.getCurrentDialog(), service).show();
			})

			.item(() -> stringSet.get("Dialog.RaceMainDialog.TrackFavorites"), (i) ->
			{

			})

			.item(() -> stringSet.get("Dialog.RaceMainDialog.RacingList"), (i) ->
			{
				new RacingListDialog(player, eventManager, i.getCurrentDialog(), service).show();
			})

			.item(() -> stringSet.get("Dialog.RaceMainDialog.CreateTrack"), () ->
			{
				if (service.isEditingTrack(player)) return false;
				return true;
			}, (i) ->
			{
				String caption = stringSet.get("Dialog.CreateNewTrackNamingDialog.Caption");
				String message = stringSet.get("Dialog.CreateNewTrackNamingDialog.Text");

				TrackNamingDialog.create(player, eventManager, i.getCurrentDialog(), caption, message, service, (d, name) ->
				{
					try
					{
						Track track = trackManager.createTrack(player, name);
						service.editTrack(player, track);
						new TrackEditDialog(player, eventManager, i.getCurrentDialog(), service, track).show();
					}
					catch (AlreadyExistException e)
					{
						d.setAppendMessage(stringSet.format("Dialog.CreateNewTrackNamingDialog.AlreadyExistAppendMessage", name));
						d.show();
					}
					catch (IllegalArgumentException e)
					{
						d.setAppendMessage(stringSet.format("Dialog.CreateNewTrackNamingDialog.IllegalNameAppendMessage", name));
						d.show();
					}
				}).show();
			})

			.item(() -> stringSet.get("Dialog.RaceMainDialog.MyRacerInfo"), (i) ->
			{

			})

			.item(() -> stringSet.get("Dialog.RaceMainDialog.MyRaceRecord"), (i) ->
			{

			})

			.item(() -> stringSet.get("Dialog.RaceMainDialog.WorldRanking"), (i) ->
			{

			})

			.item(() -> stringSet.get("Dialog.RaceMainDialog.PersonalPreferences"), (i) ->
			{

			})

			.item(() -> stringSet.get("Dialog.RaceMainDialog.Help"), (i) ->
			{
				String caption = stringSet.get("Dialog.HelpDialog.Caption");
				String text = stringSet.get("Dialog.HelpDialog.Text");

				WlMsgboxDialog.create(player, eventManager)
					.parentDialog(i.getCurrentDialog())
					.caption(caption)
					.message(text)
					.build().show();
			})

			.item(() -> stringSet.get("Dialog.RaceMainDialog.About"), (i) ->
			{
				Plugin plugin = service.getPlugin();
				ResourceDescription desc = plugin.getDescription();

				String caption = stringSet.get("Dialog.AboutDialog.Caption");
				String format = stringSet.get("Dialog.AboutDialog.Text");
				String message = String.format(format, desc.getVersion(), desc.getBuildNumber(), desc.getBuildDate());

				WlMsgboxDialog.create(player, eventManager)
				.parentDialog(i.getCurrentDialog())
				.caption(caption)
				.message(message)
				.build().show();
			})

			.onClickOk((d, i) -> player.playSound(1083))
			.build();
	}
}
