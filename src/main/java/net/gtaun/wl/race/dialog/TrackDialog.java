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
import net.gtaun.shoebill.common.dialog.MsgboxDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.common.dialog.WlMsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.util.RacingUtils;

import org.apache.commons.lang3.StringUtils;

public class TrackDialog
{
	public static WlListDialog create(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, Track track)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		RacingManagerImpl racingManager = service.getRacingManager();

		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(() -> stringSet.format("Dialog.TrackDialog.Caption", track.getName()))

			.item(() -> stringSet.format("Dialog.TrackDialog.Name", track.getName()), (i) -> i.getCurrentDialog().show())
			.item(() -> stringSet.format("Dialog.TrackDialog.Author", track.getAuthorUniqueId()), (i) -> i.getCurrentDialog().show())

			.item(() ->
			{
				String desc = track.getDesc();
				if (StringUtils.isBlank(desc)) desc = stringSet.get("Common.Empty");
				return stringSet.format("Dialog.TrackDialog.Desc", StringUtils.abbreviate(desc, 60));
			}, (i) ->
			{
				i.getCurrentDialog().show();
			})

			.item(() -> stringSet.format("Dialog.TrackDialog.Status", track.getStatus()), (i) -> i.getCurrentDialog().show())
			.item(() -> stringSet.format("Dialog.TrackDialog.Checkpoints", track.getCheckpoints().size()), (i) -> i.getCurrentDialog().show())
			.item(() -> stringSet.format("Dialog.TrackDialog.Length", track.getLength()/1000.0f), (i) -> i.getCurrentDialog().show())
			.item(() -> stringSet.format("Dialog.TrackDialog.Distance", player.getLocation().distance(track.getStartLocation())), (i) -> i.getCurrentDialog().show())

			.item(() -> stringSet.get("Dialog.TrackDialog.Edit"), () ->
			{
				if (track.getStatus() == TrackStatus.RANKING) return false;
				if (player.isAdmin()) return true;
				return player.getName().equalsIgnoreCase(track.getAuthorUniqueId());
			}, (i) ->
			{
				if (track.getStatus() == TrackStatus.COMPLETED)
				{
					String caption = stringSet.get("Dialog.TrackEditConfirmDialog.Caption");
					String text = stringSet.format("Dialog.TrackEditConfirmDialog.Text", track.getName());

					MsgboxDialog.create(player, eventManager)
						.parentDialog(i.getCurrentDialog())
						.caption(caption)
						.message(text)
						.onClickOk((d) ->
						{
							player.playSound(1083);
							service.editTrack(player, track);
						})
						.build();
				}
				else if (track.getStatus() == TrackStatus.RANKING)
				{
					i.getCurrentDialog().show();
				}
				else
				{
					service.editTrack(player, track);
				}
			})

			.item(() -> stringSet.get("Dialog.TrackDialog.Test"), () ->
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return track.getStatus() == TrackStatus.EDITING;
			}, (i) ->
			{
				Runnable startNewRacing = () ->
				{
					Racing racing = racingManager.createRacing(track, player, RacingUtils.getDefaultName(player, track));
					racing.teleToStartingPoint(player);
					racing.beginCountdown();
				};

				List<TrackCheckpoint> checkpoints = track.getCheckpoints();
				if (checkpoints.isEmpty()) return;

				if (racingManager.isPlayerInRacing(player))
				{
					Racing racing = racingManager.getPlayerRacing(player);
					NewRacingConfirmDialog.create(player, eventManager, i.getCurrentDialog(), service, racing, () ->
					{
						startNewRacing.run();
					}).show();
				}
				else startNewRacing.run();
			})

			.item(() -> stringSet.get("Dialog.TrackDialog.NewRacing"), () ->
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return track.getStatus() != TrackStatus.EDITING;
			}, (i) ->
			{
				NewRacingDialog.create(player, eventManager, i.getCurrentDialog(), service, track).show();
			})

			.item(() -> stringSet.get("Dialog.TrackDialog.QuickNewRacing"), () ->
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return track.getStatus() != TrackStatus.EDITING;
			}, (i) ->
			{
				Runnable startNewRacing = () ->
				{
					Racing racing = racingManager.createRacing(track, player, RacingUtils.getDefaultName(player, track));
					racing.teleToStartingPoint(player);
				};

				if (racingManager.isPlayerInRacing(player))
				{
					Racing racing = racingManager.getPlayerRacing(player);
					String caption = stringSet.get("Dialog.TrackNewRacingConfirmDialog.Caption");
					String text = stringSet.format("Dialog.TrackNewRacingConfirmDialog.Text", racing.getName());

					WlMsgboxDialog.create(player, eventManager)
						.parentDialog(parent)
						.caption(caption)
						.message(text)
						.onClickOk((d) ->
						{
							player.playSound(1083);
							racing.leave(player);
							startNewRacing.run();
						})
						.build().show();
				}
				else startNewRacing.run();
			})

			.onClickOk((d, i) -> player.playSound(1083))
			.build();
	}
}
