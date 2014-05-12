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
import net.gtaun.shoebill.common.dialog.ListDialogItemCheck;
import net.gtaun.shoebill.common.dialog.ListDialogItemRadio;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlInputDialog;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
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

public class NewRacingDialog
{
	public static WlListDialog create
	(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, Track track)
	{
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		
		RacingManagerImpl racingManager = service.getRacingManager();
		String racingMode = stringSet.get((track.getStatus() == TrackStatus.EDITING) ? "Dialog.NewRacingDialog.RacingTestCaption" : "Dialog.NewRacingDialog.RacingNormalCaption");
		
		String[] racingName = new String[1];
		racingName[0] = RacingUtils.getDefaultName(player, track);
		
		RacingSetting setting = new RacingSetting(track);
		
		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption(() -> stringSet.format("Dialog.NewRacingDialog.Caption", racingMode))
			
			.item(() -> stringSet.format("Dialog.NewRacingDialog.Name", racingName[0]), () ->
			{
				if (track.getStatus() == TrackStatus.EDITING) return false;
				return true;
			}, (i) ->
			{
				player.playSound(1083);
				
				String caption = stringSet.format("Dialog.NewRacingEditNameDialog.Caption", racingMode);
				String message = stringSet.get("Dialog.NewRacingEditNameDialog.Text");
				
				WlInputDialog.create(player, eventManager)
					.parentDialog(i.getCurrentDialog())
					.caption(caption)
					.message(message)
					.onClickOk((d, text) ->
					{
						String name = StringUtils.trimToEmpty(text);
						if (name.length()<3 || name.length()>40)
						{
							((WlInputDialog) d).setAppendMessage(stringSet.get("Dialog.NewRacingEditNameDialog.LimitAppendMessage"));
							d.show();
							return;
						}
						
						racingName[0] = name;
					})
					.build().show();
			})
			
			.item(() -> stringSet.format("Dialog.NewRacingDialog.Track", track.getName(), track.getCheckpoints().size(), track.getLength()/1000.0f), (i) ->
			{
				player.playSound(1083);
				TrackDialog.create(player, eventManager, i.getCurrentDialog(), service, track).show();	
			})
			
			.item(() ->
			{
				String trackType = stringSet.get("Track.Type.Normal");
				if (track.getType() == TrackType.CIRCUIT) trackType = stringSet.format("Dialog.NewRacingDialog.CircultFormat", track.getCircultLaps());
				return stringSet.format("Dialog.NewRacingDialog.TrackType", trackType);
			}, (i) ->
			{
				player.playSound(1083);
				i.getCurrentDialog().show();
			})
			
			.item(ListDialogItemRadio.create()
				.itemText(stringSet.get("Dialog.NewRacingDialog.RacingType"))
				.selectedIndex(() -> setting.getRacingType().ordinal())
				.item(stringSet.get("Racing.Type.Normal"), Color.CORNFLOWERBLUE, () -> setting.setRacingType(RacingType.NORMAL))
				.item(stringSet.get("Racing.Type.Knockout"), Color.MAGENTA, () -> setting.setRacingType(RacingType.KNOCKOUT))
				.onSelect((i) ->
				{
					player.playSound(1083);
					i.getCurrentDialog().show();
				})
				.build())
				
			.item(() ->
			{
				String format = stringSet.get("Dialog.NewRacingDialog.DepartureInterval");
				int interval = setting.getDepartureInterval();
				if (interval == 0) return String.format(format, stringSet.get("Common.None"));
				return String.format(format, stringSet.format("Time.Format.S", interval));	
			}, (i) ->
			{
				player.playSound(1083);
				RacingDepartureSettingDialog.create(player, eventManager, i.getCurrentDialog(), service, setting).show();
			})
			
			.item(ListDialogItemRadio.create()
				.selectedIndex(() -> setting.getDeathRule().ordinal())
				.itemText(stringSet.get("Dialog.NewRacingDialog.DeathRule"))
				.item(stringSet.get("Racing.DeathRule.WaitAndReturn"), Color.AQUA, () -> setting.setDeathRule(DeathRule.WAIT_AND_RETURN))
				.item(stringSet.get("Racing.DeathRule.Knockout"), Color.FUCHSIA, () -> setting.setDeathRule(DeathRule.KNOCKOUT))
				.onSelect((i) ->
				{
					player.playSound(1083);
					i.getCurrentDialog().show();
				})
				.build())
				
			.item(ListDialogItemCheck.create()
				.itemText(stringSet.get("Dialog.NewRacingDialog.Limit"))
				.item(stringSet.get("Racing.Limit.AutoRepair"), Color.LIME, () -> setting.getLimit().isAllowAutoRepair())
				.item(stringSet.get("Racing.Limit.InfiniteNitrous"), Color.RED, () -> setting.getLimit().isAllowInfiniteNitrous())
				.item(stringSet.get("Racing.Limit.AutoFlip"), Color.BLUE, () -> setting.getLimit().isAllowAutoFlip())
				.item(stringSet.get("Racing.Limit.ChangeVehicle"), Color.GOLD, () -> setting.getLimit().isAllowChangeVehicle())
				.onSelect((i) ->
				{
					player.playSound(1083);
					RacingLimitDialog.create(player, eventManager, i.getCurrentDialog(), service, track, setting.getLimit()).show();
				})
				.build())
				
			.item(() ->
			{
				int min = track.getSetting().getMinPlayers();
				int max = setting.getMaxPlayers();
				if (min != 0 && max != 0) return stringSet.format("Dialog.NewRacingDialog.PlayersLimitFormat", min, max);
				return stringSet.get("Dialog.NewRacingDialog.PlayersLimit") + " " + stringSet.get("Dialog.NewRacingDialog.PlayersLimitNone");	
			}, (i) ->
			{
				player.playSound(1083);
				i.getCurrentDialog().show();
			})
			
			.item(() ->
			{
				String itemText = stringSet.get("Dialog.NewRacingDialog.Password");
				if (StringUtils.isBlank(setting.getPassword())) return itemText + stringSet.get("Common.None");
				return itemText + " " + setting.getPassword();
			}, (i) ->
			{
				player.playSound(1083);
				i.getCurrentDialog().show();
			})
			
			.item(stringSet.get("Dialog.NewRacingDialog.Create"), (i) ->
			{
				Runnable startNewRacing = () ->
				{
					Racing racing = racingManager.createRacing(track, player, racingName[0]);
					racing.teleToStartingPoint(player);
					racing.setSetting(setting);
				};
				
				player.playSound(1083);
				
				if (track.getCheckpoints().isEmpty()) return;
				if (racingManager.isPlayerInRacing(player))
				{
					Racing racing = racingManager.getPlayerRacing(player);
					NewRacingConfirmDialog.create(player, eventManager, i.getCurrentDialog(), service, racing, () -> startNewRacing.run());
				}
				else startNewRacing.run();
			})
			
			.build();
	}
}
