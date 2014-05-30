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
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlInputDialog;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.common.dialog.WlMsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.TrackManagerImpl;
import net.gtaun.wl.race.util.RacingUtils;

import org.apache.commons.lang3.StringUtils;



public class TrackEditDialog extends WlListDialog
{
	private final PlayerStringSet stringSet;

	private final RaceServiceImpl raceService;
	private final Track track;


	public TrackEditDialog(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, Track track)
	{		super(player, eventManager);
		setParentDialog(parent);

		this.raceService = service;
		this.track = track;

		stringSet = service.getLocalizedStringSet().getStringSet(player);
		setCaption(() -> stringSet.format("Dialog.TrackEditDialog.Caption", track.getName()));

		setClickOkHandler((d, i) -> player.playSound(1083));
	}

	@Override
	public void show()
	{
		RacingManagerImpl racingManager = raceService.getRacingManager();

		items.clear();
		addItem(stringSet.format("Dialog.TrackEditDialog.Name", track.getName()), (i) ->
		{
			String caption = stringSet.get("Dialog.TrackEditNameDialog.Caption");
			String message = stringSet.format("Dialog.TrackEditNameDialog.Text", track.getName());
			TrackNamingDialog.create(player, rootEventManager, this, caption, message, raceService, (d, name) ->
			{
				try
				{
					TrackManagerImpl trackManager = raceService.getTrackManager();
					trackManager.renameTrack(track, name);
					showParentDialog();
				}
				catch (AlreadyExistException ex)
				{
					d.setAppendMessage(stringSet.format("Dialog.TrackEditNameDialog.AlreadyExistAppendMessage", name));
					show();
				}
				catch (IllegalArgumentException ex)
				{
					d.setAppendMessage(stringSet.format("Dialog.TrackEditNameDialog.IllegalFormatAppendMessage", name));
					show();
				}
			}).show();
		});

		addItem(() ->
		{
			String desc = track.getDesc();
			if (StringUtils.isBlank(desc)) desc = stringSet.get("Common.Empty");
			return stringSet.format("Dialog.TrackEditDialog.Desc", desc);
		}, (i) ->
		{
			String caption = stringSet.get("Dialog.TrackEditDescDialog.Caption");
			String message = stringSet.format("Dialog.TrackEditDescDialog.Text", track.getName(), track.getDesc());
			WlInputDialog.create(player, rootEventManager)
				.parentDialog(this)
				.caption(caption)
				.message(message)
				.onClickOk((d, text) ->
				{
					String desc = StringUtils.trimToEmpty(text);
					desc = StringUtils.replace(desc, "%", "#");
					desc = StringUtils.replace(desc, "\t", " ");
					desc = StringUtils.replace(desc, "\n", " ");

					track.setDesc(desc);
					d.showParentDialog();
				})
				.build().show();
		});

		addItem(() -> stringSet.format("Dialog.TrackEditDialog.Checkpoints", track.getCheckpoints().size()), (i) -> show());
		addItem(() -> stringSet.format("Dialog.TrackEditDialog.Length", track.getLength()/1000.0f), (i) -> show());

		addItem(() -> stringSet.get("Dialog.TrackEditDialog.AddCheckpoint"), (i) ->
		{
			TrackCheckpoint checkpoint = track.createCheckpoint(player.getLocation());
			TrackCheckpointEditDialog.create(player, eventManagerNode, null, raceService, checkpoint, true).show();
		});

		addItem(() -> stringSet.get("Dialog.TrackEditDialog.Setting"), (i) ->
		{
			TrackSettingDialog.create(player, rootEventManager, this, raceService, track).show();
		});

		addItem(() -> stringSet.get("Dialog.TrackEditDialog.Delete"), (i) ->
		{
			String caption = stringSet.get("Dialog.TrackDeleteConfirmDialog.Caption");
			String message = stringSet.format("Dialog.TrackDeleteConfirmDialog.Text", track.getName());
			WlInputDialog.create(player, rootEventManager)
				.parentDialog(this)
				.caption(caption)
				.message(message)
				.onClickOk((d, text) ->
				{
					if (!track.getName().equals(text))
					{
						d.showParentDialog();
						return;
					}

					raceService.stopEditingTrack(player);

					TrackManagerImpl trackManager = raceService.getTrackManager();
					trackManager.deleteTrack(track);

					String msgboxCaption = stringSet.get("Dialog.TrackDeleteCompleteDialog.Caption");
					String msgboxMessage = stringSet.format("Dialog.TrackDeleteCompleteDialog.Text", track.getName());
					WlMsgboxDialog.create(player, rootEventManager)
						.caption(msgboxCaption)
						.message(msgboxMessage)
						.onClickOk((dialog) -> player.playSound(1083))
						.build().show();
				})
				.build().show();
		});

		addItem(stringSet.get("Dialog.TrackEditDialog.StopEditing"), (i) ->
		{
			raceService.stopEditingTrack(player);
		});

		addItem(stringSet.get("Dialog.TrackEditDialog.FinishEditing"), () ->
		{
			if (track.getCheckpoints().size() < 2) return false;
			return true;
		}, (i) ->
		{
			String caption = stringSet.get("Dialog.TrackFinishEditingConfirmDialog.Caption");
			String message = stringSet.format("Dialog.TrackFinishEditingConfirmDialog.Text", track.getName());

			MsgboxDialog.create(player, rootEventManager)
				.caption(caption)
				.message(message)
				.onClickOk((d) ->
				{
					player.playSound(1083);

					raceService.stopEditingTrack(player);
					track.setStatus(TrackStatus.COMPLETED);

					showParentDialog();
				})
				.build().show();;
		});

		addItem(stringSet.get("Dialog.TrackEditDialog.Test"), () ->
		{
			if (track.getCheckpoints().isEmpty()) return false;
			return true;
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
				NewRacingConfirmDialog.create(player, rootEventManager, this, raceService, racing, () ->
				{
					startNewRacing.run();
				}).show();
			}
			else startNewRacing.run();
		});

		addItem("-", (i) -> show());

		List<TrackCheckpoint> checkpoints = track.getCheckpoints();
		for (int i=0; i<checkpoints.size(); i++)
		{
			TrackCheckpoint checkpoint = checkpoints.get(i);
			float distance = player.getLocation().distance(checkpoint.getLocation());
			String item = stringSet.format("Dialog.TrackEditDialog.Checkpoint", i+1, distance);
			addItem(item, (listItem) ->
			{
				TrackCheckpointEditDialog.create(player, eventManagerNode, this, raceService, checkpoint, false).show();
			});
		}

		super.show();
	}
}
