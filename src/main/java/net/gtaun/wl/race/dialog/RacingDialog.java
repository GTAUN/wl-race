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
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;

public class RacingDialog extends AbstractListDialog
{
	private final RaceServiceImpl raceService;
	private final Racing racing;
	
	
	public RacingDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService, final Racing racing)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.raceService = raceService;
		this.racing = racing;
		
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		this.caption = stringSet.format(player, "Dialog.RacingDialog.Caption", racing.getName());
	}
	
	@Override
	public void show()
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		final Track track = racing.getTrack();
		final RacingManagerImpl racingManager = raceService.getRacingManager();

		dialogListItems.clear();
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.RacingDialog.Name", racing.getName());
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
				return stringSet.format(player, "Dialog.RacingDialog.Track", track.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new TrackDialog(player, shoebill, eventManager, RacingDialog.this, raceService, track).show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.RacingDialog.Sponsor", racing.getSponsor().getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RacingDialog.Join"))
		{
			@Override
			public boolean isEnabled()
			{
				if (racing.getStatus() != RacingStatus.WAITING) return false;
				if (racingManager.getPlayerRacing(player) == racing) return false;
				return true;
			}
			
			private void joinRacing()
			{
				racing.join(player);
				
				List<TrackCheckpoint> checkpoints = track.getCheckpoints();
				if (checkpoints.isEmpty()) return;
				
				Location startLoc = checkpoints.get(0).getLocation();
				Location location = new Location(startLoc);
				location.setZ(location.getZ() + 2.0f);
				
				player.setLocation(location);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				if (racingManager.isPlayerInRacing(player))
				{
					final Racing nowRacing = racingManager.getPlayerRacing(player);
					String caption = stringSet.get(player, "Dialog.RacingLeaveAndJoinConfirmDialog.Caption");
					String text = stringSet.format(player, "Dialog.RacingLeaveAndJoinConfirmDialog.Text", nowRacing.getName(), racing.getName());
					new MsgboxDialog(player, shoebill, eventManager, RacingDialog.this, caption, text)
					{
						@Override
						protected void onClickOk()
						{
							player.playSound(1083, player.getLocation());
							nowRacing.leave(player);
							joinRacing();
						}
					}.show();
				}
				else joinRacing();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RacingDialog.Leave"))
		{
			@Override
			public boolean isEnabled()
			{
				if (racingManager.getPlayerRacing(player) != racing) return false;
				if (racing.getStatus() == RacingStatus.WAITING && racing.getSponsor() == player) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				if (racingManager.getPlayerRacing(player) != racing) return ;
				
				String caption = stringSet.get(player, "Dialog.RacingLeaveConfirmDialog.Caption");
				String text = stringSet.format(player, "Dialog.RacingLeaveConfirmDialog.Text", racing.getName());
				new MsgboxDialog(player, shoebill, eventManager, RacingDialog.this, caption, text)
				{
					@Override
					protected void onClickOk()
					{
						player.playSound(1083, player.getLocation());
						racing.leave(player);
						RacingDialog.this.showParentDialog();
					}
				}.show();
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RacingDialog.Cancel"))
		{
			@Override
			public boolean isEnabled()
			{
				if (racingManager.getPlayerRacing(player) != racing) return false;
				if (racing.getStatus() != RacingStatus.WAITING || racing.getSponsor() != player) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				if (racingManager.getPlayerRacing(player) != racing) return ;

				String caption = stringSet.get(player, "Dialog.RacingCancelConfirmDialog.Caption");
				String text = stringSet.format(player, "Dialog.RacingCancelConfirmDialog.Text", racing.getName());
				new MsgboxDialog(player, shoebill, eventManager, RacingDialog.this, caption, text)
				{
					@Override
					protected void onClickOk()
					{
						player.playSound(1083, player.getLocation());
						racing.cancel();
						RacingDialog.this.showParentDialog();
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.RacingDialog.Start"))
		{
			@Override
			public boolean isEnabled()
			{
				if (racing.getStatus() != RacingStatus.WAITING) return false;
				if (racingManager.getPlayerRacing(player) != racing) return false;
				if (racing.getSponsor() != player) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				racing.beginCountdown();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});

		List<Player> players = racing.getPlayers();
		for (final Player joinedPlayer : players)
		{
			String item = stringSet.format(player, "Dialog.RacingDialog.Player", joinedPlayer.getName());
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					if (player != racing.getSponsor() || player == joinedPlayer)
					{
						show();
					}
					else
					{
						String caption = stringSet.get(player, "Dialog.RacingKickConfirmDialog.Caption");
						String text = stringSet.format(player, "Dialog.RacingKickConfirmDialog.Text", joinedPlayer.getName());
						new MsgboxDialog(player, shoebill, rootEventManager, RacingDialog.this, caption, text)
						{
							protected void onClickOk()
							{
								player.playSound(1083, player.getLocation());
								racing.kick(joinedPlayer);
								showParentDialog();
							}
						}.show();
					}
				}
			});
		}
		
		super.show();
	}
}
