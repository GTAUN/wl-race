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

import java.util.NoSuchElementException;
import java.util.Scanner;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItemRadio;
import net.gtaun.shoebill.constant.RaceCheckpointType;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlInputDialog;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;

import org.apache.commons.lang3.StringUtils;

public class TrackCheckpointEditDialog
{
	public static WlListDialog create
	(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, TrackCheckpoint checkpoint, boolean isCreateNew)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		Track track = checkpoint.getTrack();

		if (track.getCheckpoints().contains(checkpoint) == false) player.setLocation(checkpoint.getLocation());

		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(() -> stringSet.format("Dialog.TrackCheckpointEditDialog.Caption", track.getName(), checkpoint.getNumber()))

			.item(() -> stringSet.get("Common.OK"), () -> isCreateNew, (i) -> i.getCurrentDialog().showParentDialog())

			.item(() -> stringSet.get("Dialog.TrackCheckpointEditDialog.Teleport"), () ->
			{
				if (player.getLocation().equals(checkpoint.getLocation())) return false;
				return track.getCheckpoints().contains(checkpoint);
			}, (i) ->
			{
				player.setLocation(checkpoint.getLocation());
				i.getCurrentDialog().show();
			})

			.item(() ->
			{
				Radius loc = checkpoint.getLocation();
				String item = stringSet.format("Dialog.TrackCheckpointEditDialog.Position", loc.getX(), loc.getY(), loc.getZ(), loc.getInteriorId());
				return item;
			}, (i) ->
			{
				Radius oldLoc = checkpoint.getLocation();
				String caption = stringSet.get("Dialog.TrackCheckpointEditPositionDialog.Caption");
				String message = stringSet.format("Dialog.TrackCheckpointEditPositionDialog.Text", oldLoc.getX(), oldLoc.getY(), oldLoc.getZ(), oldLoc.getInteriorId());

				WlInputDialog.create(player, eventManager)
					.parentDialog(i.getCurrentDialog())
					.caption(caption)
					.message(message)
					.onClickOk((d, text) ->
					{
						player.playSound(1083);

						try (Scanner scanner = new Scanner(text))
						{
							Radius loc = new Radius(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat(), scanner.nextInt(), oldLoc.getWorldId(), oldLoc.getRadius());
							checkpoint.setLocation(loc);
							d.showParentDialog();
						}
						catch (NoSuchElementException e)
						{
							((WlInputDialog) d).setAppendMessage(stringSet.get("Dialog.TrackCheckpointEditPositionDialog.IllegalFormatAppendMessage"));
							i.getCurrentDialog().show();
						}
					})
					.build().show();
			})

			.item(ListDialogItemRadio.create()
				.selectedIndex(() -> checkpoint.getType() == RaceCheckpointType.NORMAL ? 0 : 1)
				.itemText(() -> stringSet.get("Dialog.TrackCheckpointEditDialog.Type"))
				.item(stringSet.get("Track.Checkpoint.Type.Normal"), Color.RED, () -> checkpoint.setType(RaceCheckpointType.NORMAL))
				.item(stringSet.get("Track.Checkpoint.Type.Air"), Color.BLUE, () -> checkpoint.setType(RaceCheckpointType.AIR))
				.onSelect((i) -> i.getCurrentDialog().show())
				.build())

			.item(() -> stringSet.format("Dialog.TrackCheckpointEditDialog.Size", checkpoint.getSize()), (i) ->
			{
				String caption = stringSet.get("Dialog.TrackCheckpointEditSizeDialog.Caption");
				String message = stringSet.format("Dialog.TrackCheckpointEditSizeDialog.Text", checkpoint.getSize());

				WlInputDialog.create(player, eventManager)
					.parentDialog(i.getCurrentDialog())
					.caption(caption)
					.message(message)
					.onClickOk((d, text) ->
					{
						player.playSound(1083);

						try (Scanner scanner = new Scanner(text))
						{
							checkpoint.setSize(scanner.nextFloat());
							d.showParentDialog();
						}
						catch (NoSuchElementException e)
						{
							((WlInputDialog) d).setAppendMessage(stringSet.get("Dialog.TrackCheckpointEditSizeDialog.IllegalFormatAppendMessage"));
							i.getCurrentDialog().show();
						}
					})
					.build().show();
			})

			.item(() ->
			{
				String code = checkpoint.getScript();
				int lines = StringUtils.countMatches(code, "\n");
				return stringSet.format("Dialog.TrackCheckpointEditDialog.Script", lines, code.length());
			}, (i) ->
			{
				String title = stringSet.format("Dialog.TrackCheckpointEditDialog.CheckpointFormat", checkpoint.getNumber()+1);
				String code = checkpoint.getScript();
				new CodeEditorDialog(player, eventManager, i.getCurrentDialog(), service, title, code, (newCode) ->
				{
					checkpoint.setScript(newCode);
					i.getCurrentDialog().showParentDialog();
				}).show();
			})

			.item(() -> stringSet.get("Dialog.TrackCheckpointEditDialog.UpdatePosition"), () ->
			{
				if (player.getLocation().equals(checkpoint.getLocation())) return false;
				return track.getCheckpoints().contains(checkpoint);
			}, (i) ->
			{
				checkpoint.setLocation(player.getLocation());
				player.sendMessage(Color.LIGHTBLUE, stringSet.get("Dialog.TrackCheckpointEditDialog.UpdatePositionMessage"));
				i.getCurrentDialog().show();
			})

			.item(() -> stringSet.get("Dialog.TrackCheckpointEditDialog.Delete"), () -> track.getCheckpoints().contains(checkpoint), (i) ->
			{
				track.removeChechpoint(checkpoint);
			})

			.onClickOk((d, i) -> player.playSound(1083))
			.build();
	}
}
