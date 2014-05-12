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
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.common.dialog.WlMsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;

public class RacingDialog extends WlListDialog
{
	private final RaceServiceImpl raceService;
	private final Racing racing;

	private final PlayerStringSet stringSet;
	
	
	public RacingDialog(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, Racing racing)
	{		super(player, eventManager);
		setParentDialog(parent);
		
		this.raceService = service;
		this.racing = racing;
		this.stringSet = service.getLocalizedStringSet().getStringSet(player);
		
		setCaption(() -> stringSet.format("Dialog.RacingDialog.Caption", racing.getName()));
		setClickOkHandler((d, i) -> player.playSound(1083));
	}
	
	@Override
	public void show()
	{	
		Track track = racing.getTrack();
		RacingManagerImpl racingManager = raceService.getRacingManager();

		items.clear();
		
		addItem(() -> stringSet.format("Dialog.RacingDialog.Name", racing.getName()), (i) -> show());
		addItem(() -> stringSet.format("Dialog.RacingDialog.Track", track.getName()), (i) ->
		{
			TrackDialog.create(player, rootEventManager, RacingDialog.this, raceService, track).show();
		});

		addItem(() -> stringSet.format("Dialog.RacingDialog.Sponsor", racing.getSponsor().getName()), (i) -> show());
		
		addItem(() -> stringSet.get("Dialog.RacingDialog.Join"), () ->
		{
			if (racing.getStatus() != RacingStatus.WAITING) return false;
			if (racingManager.getPlayerRacing(player) == racing) return false;
			return true;
		}, (i) ->
		{
			Runnable joinRacing = () ->
			{
				racing.join(player);
				
				List<TrackCheckpoint> checkpoints = track.getCheckpoints();
				if (checkpoints.isEmpty()) return;
				
				Location startLoc = checkpoints.get(0).getLocation();
				Location location = new Location(startLoc);
				location.setZ(location.getZ() + 2.0f);
				
				player.setLocation(location);
			};

			if (racingManager.isPlayerInRacing(player))
			{
				Racing nowRacing = racingManager.getPlayerRacing(player);
				String caption = stringSet.get("Dialog.RacingLeaveAndJoinConfirmDialog.Caption");
				String text = stringSet.format("Dialog.RacingLeaveAndJoinConfirmDialog.Text", nowRacing.getName(), racing.getName());
				
				WlMsgboxDialog.create(player, rootEventManager)
					.parentDialog(this)
					.caption(caption)
					.message(text)
					.onClickOk((d) ->
					{
						player.playSound(1083);
						nowRacing.leave(player);
						joinRacing.run();
					})
					.build().show();
			}
			else joinRacing.run();;
		});
		
		addItem(() -> stringSet.get("Dialog.RacingDialog.Leave"), () ->
		{
			if (racingManager.getPlayerRacing(player) != racing) return false;
			if (racing.getStatus() == RacingStatus.WAITING && racing.getSponsor() == player) return false;
			return true;
		}, (i) ->
		{
			if (racingManager.getPlayerRacing(player) != racing) return ;
			
			String caption = stringSet.get("Dialog.RacingLeaveConfirmDialog.Caption");
			String text = stringSet.format("Dialog.RacingLeaveConfirmDialog.Text", racing.getName());
			
			WlMsgboxDialog.create(player, rootEventManager)
				.parentDialog(this)
				.caption(caption)
				.message(text)
				.onClickOk((d) ->
				{
					player.playSound(1083);
					racing.leave(player);
					showParentDialog();
				})
				.build().show();
		});
		
		addItem(() -> stringSet.get("Dialog.RacingDialog.Cancel"), () ->
		{
			if (racingManager.getPlayerRacing(player) != racing) return false;
			if (racing.getStatus() != RacingStatus.WAITING || racing.getSponsor() != player) return false;
			return true;
		}, (i) ->
		{
			if (racingManager.getPlayerRacing(player) != racing) return ;

			String caption = stringSet.get("Dialog.RacingCancelConfirmDialog.Caption");
			String text = stringSet.format("Dialog.RacingCancelConfirmDialog.Text", racing.getName());
			
			WlMsgboxDialog.create(player, rootEventManager)
				.parentDialog(this)
				.caption(caption)
				.message(text)
				.onClickOk((d) ->
				{
					player.playSound(1083);
					racing.cancel();
					showParentDialog();
				})
				.build().show();
		});
		
		addItem(() -> stringSet.get("Dialog.RacingDialog.Start"), () ->
		{
			if (racing.getStatus() != RacingStatus.WAITING) return false;
			if (racingManager.getPlayerRacing(player) != racing) return false;
			if (racing.getSponsor() != player) return false;
			return true;
		}, (i) ->
		{
			player.playSound(1083);
			racing.beginCountdown();
		});
		
		addItem("-", (i) -> show());
		
		for (Player joinedPlayer : racing.getPlayers())
		{
			addItem(() -> stringSet.format("Dialog.RacingDialog.Player", joinedPlayer.getName()), (i) ->
			{
				if (player != racing.getSponsor() || player == joinedPlayer) show();
				else
				{
					String caption = stringSet.get("Dialog.RacingKickConfirmDialog.Caption");
					String text = stringSet.format("Dialog.RacingKickConfirmDialog.Text", joinedPlayer.getName());
					WlMsgboxDialog.create(joinedPlayer, rootEventManager)
						.parentDialog(this)
						.caption(caption)
						.message(text)
						.onClickOk((d) ->
						{
							player.playSound(1083);
							racing.kick(joinedPlayer);
							showParentDialog();
						})
						.build().show();
				}
			});
		}
		
		super.show();
	}
}
