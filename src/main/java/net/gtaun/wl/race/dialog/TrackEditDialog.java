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
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.TrackManagerImpl;
import net.gtaun.wl.race.util.RacingUtils;

import org.apache.commons.lang3.StringUtils;

public class TrackEditDialog extends AbstractListDialog
{
	private final RaceServiceImpl raceService;
	private final Track track;
	

	public TrackEditDialog
	(final Player player, final Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.raceService = raceService;
		this.track = track;
	}
	
	@Override
	public void show()
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		final RacingManagerImpl racingManager = raceService.getRacingManager();
		
		dialogListItems.clear();
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.TrackEditDialog.Name", track.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = stringSet.get(player, "Dialog.TrackEditNameDialog.Caption");
				String message = stringSet.format(player, "Dialog.TrackEditNameDialog.Text", track.getName());
				new TrackNamingDialog(player, shoebill, rootEventManager, caption, message, TrackEditDialog.this, raceService)
				{
					protected void onNaming(String name)
					{
						try
						{
							TrackManagerImpl trackManager = raceService.getTrackManager();
							trackManager.renameTrack(track, name);
							showParentDialog();
						}
						catch (AlreadyExistException e)
						{
							append = stringSet.format(player, "Dialog.TrackEditNameDialog.AlreadyExistAppendMessage", name);
							show();
						}
						catch (IllegalArgumentException e)
						{
							append = stringSet.format(player, "Dialog.TrackEditNameDialog.IllegalFormatAppendMessage", name);
							show();
						}
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String desc = track.getDesc();
				if (StringUtils.isBlank(desc)) desc = stringSet.get(player, "Common.Empty");
				return stringSet.format(player, "Dialog.TrackEditDialog.Desc", desc);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = stringSet.get(player, "Dialog.TrackEditDescDialog.Caption");
				String message = stringSet.format(player, "Dialog.TrackEditDescDialog.Text", track.getName(), track.getDesc());
				new AbstractInputDialog(player, shoebill, rootEventManager, TrackEditDialog.this, caption, message)
				{
					public void onClickOk(String inputText)
					{
						String desc = StringUtils.trimToEmpty(inputText);
						desc = StringUtils.replace(desc, "%", "#");
						desc = StringUtils.replace(desc, "\t", " ");
						desc = StringUtils.replace(desc, "\n", " ");
						
						track.setDesc(desc);
						showParentDialog();
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.TrackEditDialog.Checkpoints", track.getCheckpoints().size());
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
				return stringSet.format(player, "Dialog.TrackEditDialog.Length", track.getLength()/1000.0f);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackEditDialog.AddCheckpoint"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				TrackCheckpoint checkpoint = track.createCheckpoint(player.getLocation());
				new TrackCheckpointEditDialog(player, shoebill, eventManager, null, raceService, checkpoint).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackEditDialog.Setting"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new TrackSettingDialog(player, shoebill, rootEventManager, TrackEditDialog.this, raceService, track).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackEditDialog.Delete"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = stringSet.get(player, "Dialog.TrackDeleteConfirmDialog.Caption");
				String message = stringSet.format(player, "Dialog.TrackDeleteConfirmDialog.Text", track.getName());
				new AbstractInputDialog(player, shoebill, rootEventManager, TrackEditDialog.this, caption, message)
				{
					public void onClickOk(String inputText)
					{
						player.playSound(1083, player.getLocation());
						if (!track.equals(inputText))
						{
							String caption = stringSet.get(player, "Dialog.TrackDeleteConfirmDialog.Caption");
							String message = stringSet.format(player, "Dialog.TrackDeleteConfirmDialog.Text", track.getName());
							new MsgboxDialog(player, shoebill, rootEventManager, TrackEditDialog.this, caption, message)
							{
								protected void onClickOk()
								{
									onClickCancel();
								}
							}.show();
						}
						
						raceService.stopEditingTrack(player);
						
						TrackManagerImpl trackManager = raceService.getTrackManager();
						trackManager.deleteTrack(track);
						
						String caption = stringSet.get(player, "Dialog.TrackDeleteCompleteDialog.Caption");
						String message = stringSet.format(player, "Dialog.TrackDeleteCompleteDialog.Text", track.getName());
						new MsgboxDialog(player, shoebill, rootEventManager, null, caption, message)
						{
							protected void onClickOk()
							{
								onClickCancel();
							}
						}.show();
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackEditDialog.StopEditing"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				raceService.stopEditingTrack(player);
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackEditDialog.FinishEditing"))
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getCheckpoints().size() < 2) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = stringSet.get(player, "Dialog.TrackFinishEditingConfirmDialog.Caption");
				String message = stringSet.format(player, "Dialog.TrackFinishEditingConfirmDialog.Text", track.getName());
				new MsgboxDialog(player, shoebill, rootEventManager, TrackEditDialog.this, caption, message)
				{
					protected void onClickOk()
					{
						player.playSound(1083, player.getLocation());
						
						raceService.stopEditingTrack(player);
						track.setStatus(TrackStatus.COMPLETED);
						
						TrackEditDialog.this.showParentDialog();
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackEditDialog.Test"))
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getCheckpoints().isEmpty()) return false;
				return true;
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
					new NewRacingConfirmDialog(player, shoebill, rootEventManager, TrackEditDialog.this, raceService, racing)
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
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public boolean isEnabled()
			{
				List<TrackCheckpoint> checkpoints = track.getCheckpoints();
				return !checkpoints.isEmpty();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		List<TrackCheckpoint> checkpoints = track.getCheckpoints();
		for (int i=0; i<checkpoints.size(); i++)
		{
			final TrackCheckpoint checkpoint = checkpoints.get(i);
			float distance = player.getLocation().distance(checkpoint.getLocation());
			String item = stringSet.format(player, "Dialog.TrackEditDialog.Checkpoint", i+1, distance);
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					new TrackCheckpointEditDialog(player, shoebill, eventManager, TrackEditDialog.this, raceService, checkpoint).show();
				}
			});
		}
		
		this.caption = stringSet.format(player, "Dialog.TrackEditDialog.Caption", track.getName());
		super.show();
	}
}
