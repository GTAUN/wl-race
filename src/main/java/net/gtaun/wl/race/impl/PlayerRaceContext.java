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

package net.gtaun.wl.race.impl;

import net.gtaun.shoebill.common.player.PlayerLifecycleObject;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerKeyState;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import net.gtaun.wl.race.dialog.RacingDialog;
import net.gtaun.wl.race.dialog.RacingListDialog;
import net.gtaun.wl.race.dialog.TrackCheckpointEditDialog;
import net.gtaun.wl.race.dialog.TrackEditDialog;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.TrackEditor;
import net.gtaun.wl.race.util.PlayerKeyUtils;

public class PlayerRaceContext extends PlayerLifecycleObject
{
	private final RaceServiceImpl raceService;

	private TrackEditor trackEditor;
	private long lastHornKeyPressedTime;
	private long lastAnalogDownKeyPressedTime;


	public PlayerRaceContext(EventManager rootEventManager, Player player, RaceServiceImpl raceService)
	{
		super(rootEventManager, player);
		this.raceService = raceService;
	}

	@Override
	protected void onInit()
	{
		eventManagerNode.registerHandler(PlayerKeyStateChangeEvent.class, HandlerPriority.NORMAL, Attentions.create().object(player), (e) ->
		{
			PlayerKeyState keyState = player.getKeyState();

			Track editingTrack = getEditingTrack();
			if (editingTrack != null)
			{
				if (keyState.isKeyPressed(PlayerKey.CROUCH))
				{
					long now = System.currentTimeMillis();
					if (now <= lastHornKeyPressedTime + 1000)
					{
						new TrackEditDialog(player, eventManagerNode, null, raceService, editingTrack).show();
					}
					lastHornKeyPressedTime = System.currentTimeMillis();
				}
				else if (keyState.isKeyPressed(PlayerKey.ANALOG_DOWN))
				{
					TrackCheckpoint checkpoint = editingTrack.createCheckpoint(player.getLocation());
					trackEditor.updateMapIcons();

					TrackCheckpointEditDialog.create(player, eventManagerNode, null, raceService, checkpoint, true).show();
				}
			}
			else
			{
				if (keyState.isAccurateKeyPressed(PlayerKey.CROUCH))
				{
					long now = System.currentTimeMillis();
					if (now <= lastHornKeyPressedTime + PlayerKeyUtils.getDoublePressKeyTimeDiff(player))
					{
						RacingManagerImpl racingManager = raceService.getRacingManager();
						Racing racing = racingManager.getPlayerRacing(player);
						if (racing != null)
						{
							new RacingDialog(player, eventManagerNode, null, raceService, racing).show();
						}
					}
					lastHornKeyPressedTime = System.currentTimeMillis();
				}
				else if (keyState.isAccurateKeyPressed(PlayerKey.ANALOG_DOWN))
				{
					long now = System.currentTimeMillis();
					if (now <= lastAnalogDownKeyPressedTime + PlayerKeyUtils.getDoublePressKeyTimeDiff(player))
					{
						new RacingListDialog(player, eventManagerNode, null, raceService).show();
					}
					lastAnalogDownKeyPressedTime = System.currentTimeMillis();
				}
			}
		});
	}

	@Override
	protected void onDestroy()
	{

	}

	public boolean isEditingTrack()
	{
		return trackEditor != null;
	}

	public void setEditingTrack(Track track)
	{
		if (track == null)
		{
			if (trackEditor == null) return;

			Track lastTrack = trackEditor.getTrack();
			trackEditor.destroy();
			trackEditor = null;

			raceService.getTrackManager().save(lastTrack);
		}
		else
		{
			if (trackEditor != null) return;

			if (track.getStatus() == TrackStatus.RANKING) throw new UnsupportedOperationException();
			track.setStatus(TrackStatus.EDITING);

			trackEditor = new TrackEditor(rootEventManager, player, raceService, track);
			trackEditor.init();

			new TrackEditDialog(player, eventManagerNode, null, raceService, track).show();
		}
	}

	public Track getEditingTrack()
	{
		if (trackEditor == null) return null;
		return trackEditor.getTrack();
	}
}
