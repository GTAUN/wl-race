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

import org.apache.commons.lang3.StringUtils;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.ScriptType;

public class TrackScriptEditDialog extends AbstractListDialog
{
	protected TrackScriptEditDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		this.caption = stringSet.format(player, "Dialog.TrackScriptEditDialog.Caption", track.getName());
		
		for (final ScriptType type : ScriptType.values())
		{
			dialogListItems.add(new DialogListItem()
			{
				@Override
				public String toItemString()
				{
					String code = track.getScript(type);
					int lines = StringUtils.countMatches(code, "\n");
					return stringSet.format(player, "Dialog.TrackScriptEditDialog.Item", type.name(), lines, code.length());
				}
				
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					
					final String title = stringSet.format(player, "Dialog.TrackScriptEditDialog.EventFormat", type.name());
					final String code = track.getScript(type);
					new CodeEditorDialog(player, shoebill, eventManager, TrackScriptEditDialog.this, raceService, title, code)
					{
						@Override
						protected void onComplete(String code)
						{
							player.playSound(1083, player.getLocation());
							track.setScript(type, code);
						}
					}.show();
				}
			});
		}
	}	
}
