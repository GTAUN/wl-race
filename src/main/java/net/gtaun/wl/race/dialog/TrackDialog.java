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

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.event.dialog.DialogCancelEvent.DialogCancelType;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.util.RacingUtils;

import org.apache.commons.lang3.StringUtils;

public class TrackDialog extends AbstractListDialog
{
	public TrackDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, final AbstractDialog parentDialog, final RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		final RacingManagerImpl racingManager = raceService.getRacingManager();

		this.caption = stringSet.format(player, "Dialog.TrackDialog.Caption", track.getName());
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.TrackDialog.Name", track.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.TrackDialog.Author", track.getAuthorUniqueId());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String desc = track.getDesc();
				if (StringUtils.isBlank(desc)) desc = stringSet.get(player, "Common.Empty");
				return stringSet.format(player, "Dialog.TrackDialog.Desc", StringUtils.abbreviate(desc, 60));
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.TrackDialog.Status", track.getStatus());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.TrackDialog.Checkpoints", track.getCheckpoints().size());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.TrackDialog.Length", track.getLength()/1000.0f);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.TrackDialog.Distance", player.getLocation().distance(track.getStartLocation()));
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackDialog.Edit"))
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getStatus() == TrackStatus.RANKING) return false;
				if (player.isAdmin()) return true;
				return player.getName().equals(track.getAuthorUniqueId());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				if (track.getStatus() == TrackStatus.COMPLETED)
				{
					String caption = stringSet.get(player, "Dialog.TrackEditConfirmDialog.Caption");
					String text = stringSet.format(player, "Dialog.TrackEditConfirmDialog.Text", track.getName());
					new MsgboxDialog(player, shoebill, eventManager, TrackDialog.this, caption, text)
					{
						protected void onClickOk()
						{
							player.playSound(1083, player.getLocation());
							raceService.editTrack(player, track);
						}
					}.show();
				}
				else if (track.getStatus() == TrackStatus.RANKING)
				{
					show();
				}
				else
				{
					raceService.editTrack(player, track);
				}
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackDialog.Test"))
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return track.getStatus() == TrackStatus.EDITING;
			}
			
			private void startNewRacing()
			{
				Racing racing = racingManager.createRacing(track, player, RacingUtils.getDefaultName(player, track));
				racing.teleToStartingPoint(player);
				racing.beginCountdown();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				List<TrackCheckpoint> checkpoints = track.getCheckpoints();
				if (checkpoints.isEmpty()) return;
				
				if (racingManager.isPlayerInRacing(player))
				{
					final Racing racing = racingManager.getPlayerRacing(player);
					new NewRacingConfirmDialog(player, shoebill, rootEventManager, TrackDialog.this, raceService, racing)
					{
						@Override
						protected void startRacing()
						{
							startNewRacing();
						}
					}.show();
				}
				else startNewRacing();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackDialog.NewRacing"))
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return track.getStatus() != TrackStatus.EDITING;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new NewRacingDialog(player, shoebill, eventManager, TrackDialog.this, raceService, track).show();
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackDialog.QuickNewRacing"))
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return track.getStatus() != TrackStatus.EDITING;
			}
			
			private void startNewRacing()
			{
				Racing racing = racingManager.createRacing(track, player, RacingUtils.getDefaultName(player, track));
				racing.teleToStartingPoint(player);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				if (racingManager.isPlayerInRacing(player))
				{
					final Racing racing = racingManager.getPlayerRacing(player);
					String caption = stringSet.get(player, "Dialog.TrackNewRacingConfirmDialog.Caption");
					String text = stringSet.format(player, "Dialog.TrackNewRacingConfirmDialog.Text", racing.getName());
					new MsgboxDialog(player, shoebill, eventManager, TrackDialog.this, caption, text)
					{
						@Override
						protected void onClickOk()
						{
							player.playSound(1083, player.getLocation());
							racing.leave(player);
							startNewRacing();
						}
						
						@Override
						protected void onCancel(DialogCancelType type)
						{
							player.playSound(1083, player.getLocation());
							showParentDialog();
						}
					}.show();
				}
				else startNewRacing();
			}
		});
	}
	
	@Override
	public void show()
	{
		super.show();
	}
}
