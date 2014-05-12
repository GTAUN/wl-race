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

import java.util.ArrayList;
import java.util.List;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlPageListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.ScriptType;

import org.apache.commons.lang3.StringUtils;

public class TrackScriptEditDialog
{
	protected WlPageListDialog create
	(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, Track track)
	{
		LocalizedStringSet stringSet = service.getLocalizedStringSet();
		
		List<ListDialogItem> items = new ArrayList<>();
		for (ScriptType type : ScriptType.values())
		{
			items.add(new ListDialogItem(() ->
			{
				String code = track.getScript(type);
				int lines = StringUtils.countMatches(code, "\n");
				return stringSet.format(player, "Dialog.TrackScriptEditDialog.Item", type.name(), lines, code.length());
			}, (i) ->
			{
				player.playSound(1083);
				
				String title = stringSet.format(player, "Dialog.TrackScriptEditDialog.EventFormat", type.name());
				String code = track.getScript(type);
				new CodeEditorDialog(player, eventManager, i.getCurrentDialog(), service, title, code, (newCode) ->
				{
					player.playSound(1083);
					track.setScript(type, newCode);
				}).show();
			}));
		}
	
		return WlPageListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption((d) -> stringSet.format(player, "Dialog.TrackScriptEditDialog.Caption", track.getName()))
			.items(items)
			.build();
	}	
}
