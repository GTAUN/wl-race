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

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.DeathRule;
import net.gtaun.wl.race.racing.Racing.RacingType;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.racing.RacingSetting;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.track.Track.TrackType;
import net.gtaun.wl.race.util.RacingUtils;

import org.apache.commons.lang3.StringUtils;

public class NewRacingDialog extends AbstractListDialog
{
	private String racingName;
	private RacingSetting setting;
	
	
	public NewRacingDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();

		final RacingManagerImpl racingManager = raceService.getRacingManager();
		final String racingMode = (track.getStatus() == TrackStatus.EDITING) ? stringSet.get(player, "Dialog.NewRacingDialog.RacingTestCaption") : stringSet.get(player, "Dialog.NewRacingDialog.RacingNormalCaption");
		
		this.caption = stringSet.format(player, "Dialog.NewRacingDialog.Caption", racingMode);
		this.racingName = RacingUtils.getDefaultName(player, track);
		
		setting = new RacingSetting(track);
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public boolean isEnabled()
			{
				if (track.getStatus() == TrackStatus.EDITING) return false;
				return true;
			}
			
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.NewRacingDialog.Name", racingName);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = stringSet.format(player, "Dialog.NewRacingEditNameDialog.Caption", racingMode);
				String message = stringSet.get(player, "Dialog.NewRacingEditNameDialog.Text");
				new AbstractInputDialog(player, shoebill, eventManager, NewRacingDialog.this, caption, message)
				{
					public void onClickOk(String inputText)
					{
						String name = StringUtils.trimToEmpty(inputText);
						if (name.length()<3 || name.length()>40)
						{
							append = stringSet.get(player, "Dialog.NewRacingEditNameDialog.LimitAppendMessage");
							show();
							return;
						}
						
						racingName = name;
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return stringSet.format(player, "Dialog.NewRacingDialog.Track", track.getName(), track.getCheckpoints().size(), track.getLength()/1000.0f);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new TrackDialog(player, shoebill, eventManager, NewRacingDialog.this, raceService, track).show();
			}
		});
		
		String trackType = stringSet.get(player, "Track.Type.Normal");
		if (track.getType() == TrackType.CIRCUIT) trackType = stringSet.format(player, "Dialog.NewRacingDialog.CircultFormat", track.getCircultLaps());
		dialogListItems.add(new DialogListItem(stringSet.format(player, "Dialog.NewRacingDialog.TrackType", trackType))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItemRadio(stringSet.get(player, "Dialog.NewRacingDialog.RacingType"))
		{
			{
				addItem(new RadioItem(stringSet.get(player, "Racing.Type.Normal"), Color.CORNFLOWERBLUE)
				{
					@Override public void onSelected()	{ setting.setRacingType(RacingType.NORMAL); }
				});
				addItem(new RadioItem(stringSet.get(player, "Racing.Type.Knockout"), Color.MAGENTA)
				{
					@Override public void onSelected()	{ setting.setRacingType(RacingType.KNOCKOUT); }
				});
			}
			
			@Override
			public void onItemSelect(RadioItem item, int itemIndex)
			{
				player.playSound(1083, player.getLocation());
				show();
			}
			
			@Override
			public int getSelected()
			{
				return setting.getRacingType().ordinal();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String format = stringSet.get(player, "Dialog.NewRacingDialog.DepartureInterval");
				int interval = setting.getDepartureInterval();
				if (interval == 0) return String.format(format, stringSet.get(player, "Common.None"));
				return String.format(format, stringSet.format(player, "Time.Format.S", interval));
			}

			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new RacingDepartureSettingDialog(player, shoebill, eventManager, NewRacingDialog.this, raceService, setting).show();
			}
		});
		
		dialogListItems.add(new DialogListItemRadio(stringSet.get(player, "Dialog.NewRacingDialog.DeathRule"))
		{
			{
				addItem(new RadioItem(stringSet.get(player, "Racing.DeathRule.WaitAndReturn"), Color.AQUA)
				{
					@Override public void onSelected()	{ setting.setDeathRule(DeathRule.WAIT_AND_RETURN); }
				});
				addItem(new RadioItem(stringSet.get(player, "Racing.DeathRule.Knockout"), Color.FUCHSIA)
				{
					@Override public void onSelected()	{ setting.setDeathRule(DeathRule.KNOCKOUT); }
				});
			}
			
			@Override
			public void onItemSelect(RadioItem item, int itemIndex)
			{
				player.playSound(1083, player.getLocation());
				show();
			}
			
			@Override
			public int getSelected()
			{
				return setting.getDeathRule().ordinal();
			}
		});
		
		dialogListItems.add(new DialogListItemCheck(stringSet.get(player, "Dialog.NewRacingDialog.Limit"))
		{
			{
				addItem(new CheckItem(stringSet.get(player, "Racing.Limit.AutoRepair"), Color.LIME)
				{
					@Override public boolean isChecked()	{ return setting.getLimit().isAllowAutoRepair(); }
				});
				addItem(new CheckItem(stringSet.get(player, "Racing.Limit.InfiniteNitrous"), Color.RED)
				{
					@Override public boolean isChecked()	{ return setting.getLimit().isAllowInfiniteNitrous(); }
				});
				addItem(new CheckItem(stringSet.get(player, "Racing.Limit.AutoFlip"), Color.BLUE)
				{
					@Override public boolean isChecked()	{ return setting.getLimit().isAllowAutoFlip(); }
				});
				addItem(new CheckItem(stringSet.get(player, "Racing.Limit.ChangeVehicle"), Color.GOLD)
				{
					@Override public boolean isChecked()	{ return setting.getLimit().isAllowChangeVehicle(); }
				});
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new RacingLimitDialog(player, shoebill, eventManager, NewRacingDialog.this, raceService, track, setting.getLimit()).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.NewRacingDialog.PlayersLimit"))
		{
			@Override
			public String toItemString()
			{
				int min = track.getSetting().getMinPlayers();
				int max = setting.getMaxPlayers();
				if (min != 0 && max != 0) return stringSet.format(player, "Dialog.NewRacingDialog.PlayersLimitFormat", min, max);
				return itemString + " " + stringSet.get(player, "Dialog.NewRacingDialog.PlayersLimitNone");
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.NewRacingDialog.Password"))
		{
			@Override
			public String toItemString()
			{
				if (StringUtils.isBlank(setting.getPassword())) return itemString + stringSet.get(player, "Common.None");
				return itemString + " " + setting.getPassword();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.NewRacingDialog.Create"))
		{
			private void startNewRacing()
			{
				Racing racing = racingManager.createRacing(track, player, racingName);
				racing.teleToStartingPoint(player);
				racing.setSetting(setting);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				if (track.getCheckpoints().isEmpty()) return;
				if (racingManager.isPlayerInRacing(player))
				{
					final Racing racing = racingManager.getPlayerRacing(player);
					new NewRacingConfirmDialog(player, shoebill, rootEventManager, NewRacingDialog.this, raceService, racing)
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
	}
}
