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
import java.util.Arrays;
import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.common.dialog.AbstractPageListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;

import org.apache.commons.lang3.StringUtils;

/**
 * JavaScript 代码编辑器。
 * 
 * @author MK124
 */
public abstract class CodeEditorDialog extends AbstractPageListDialog
{
	private final RaceServiceImpl raceService;
	private final String title;
	private List<String> codeLines;
	
	
	protected CodeEditorDialog(Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, RaceServiceImpl raceService, String title, String code)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.raceService = raceService;
		this.title = title;
		
		if (code == null) code = "";
		codeLines = new ArrayList<>(Arrays.asList(StringUtils.split(code, '\n')));
	}
	
	@Override
	public void show()
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		dialogListItems.clear();
		
		for (int i=0; i<codeLines.size(); i++)
		{
			final String line = codeLines.get(i);
			final int lineNum = i+1;
			
			dialogListItems.add(new DialogListItem(stringSet.format(player, "Dialog.CodeEditorDialog.Item", lineNum, line))
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					
					final String caption = stringSet.format(player, "Dialog.CodeEditorEditLineDialog.Caption", lineNum);
					final String message = stringSet.format(player, "Dialog.CodeEditorEditLineDialog.Text", lineNum, line);
					new AbstractInputDialog(player, shoebill, rootEventManager, CodeEditorDialog.this, caption, message)
					{
						@Override
						public void onClickOk(String inputText)
						{
							player.playSound(1083, player.getLocation());
							
							if (inputText.equalsIgnoreCase("INSERT"))
							{
								addNewLine(lineNum, parentDialog);
								return;
							}
							else if (inputText.equalsIgnoreCase("DELETE"))
							{
								codeLines.remove(lineNum-1);
								showParentDialog();
								return;
							}
							
							codeLines.set(lineNum-1, inputText);
							showParentDialog();
						}
					}.show();
				}
			});
		}
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.CodeEditorDialog.Add"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				final int lineNum = codeLines.size() + 1;
				addNewLine(lineNum, CodeEditorDialog.this);
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.CodeEditorDialog.Save"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				onComplete(toCode());
			}
		});
		
		this.caption = stringSet.format(player, "Dialog.CodeEditorDialog.Caption", title, codeLines.size());
		super.show();
	}
	
	private void addNewLine(final int line, AbstractDialog dialog)
	{
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		final String caption = stringSet.format(player, "Dialog.CodeEditorNewLineDialog.Caption", line);
		final String message = stringSet.format(player, "Dialog.CodeEditorNewLineDialog.Text", line);
		new AbstractInputDialog(player, shoebill, rootEventManager, dialog, caption, message)
		{
			@Override
			public void onClickOk(String inputText)
			{
				player.playSound(1083, player.getLocation());
				
				codeLines.add(line-1, inputText);
				showParentDialog();
			}
		}.show();
	}
	
	public String toCode()
	{
		StringBuilder builder = new StringBuilder();
		for (String line : codeLines)
		{
			builder.append(line);
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	protected abstract void onComplete(String code);
}
