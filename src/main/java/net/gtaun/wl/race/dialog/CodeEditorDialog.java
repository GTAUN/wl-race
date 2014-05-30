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
import java.util.function.Consumer;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlInputDialog;
import net.gtaun.wl.common.dialog.WlPageListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;

import org.apache.commons.lang3.StringUtils;

/**
 * JavaScript 代码编辑器。
 *
 * @author MK124
 */
public class CodeEditorDialog extends WlPageListDialog
{
	private final PlayerStringSet stringSet;

	private List<String> codeLines;
	private Consumer<String> onCompleteHandler;


	public CodeEditorDialog(Player player, EventManager eventManager, AbstractDialog parent, RaceServiceImpl service, String title, String code, Consumer<String> onCompleteHandler)
	{		super(player, eventManager);
		setParentDialog(parent);

		this.onCompleteHandler = onCompleteHandler;

		if (code == null) code = "";
		codeLines = new ArrayList<>(Arrays.asList(StringUtils.split(code, '\n')));

		stringSet = service.getLocalizedStringSet().getStringSet(player);
		setCaption(() -> stringSet.format("Dialog.CodeEditorDialog.Caption", title, codeLines.size()));
	}

	@Override
	public void show()
	{
		items.clear();

		for (int i=0; i<codeLines.size(); i++)
		{
			String line = codeLines.get(i);
			int lineNum = i+1;

			addItem(stringSet.format("Dialog.CodeEditorDialog.Item", lineNum, line), (item) ->
			{
				player.playSound(1083);

				String caption = stringSet.format("Dialog.CodeEditorEditLineDialog.Caption", lineNum);
				String message = stringSet.format("Dialog.CodeEditorEditLineDialog.Text", lineNum, line);

				WlInputDialog.create(player, rootEventManager)
					.parentDialog(this)
					.caption(caption)
					.message(message)
					.onClickOk((d, text) ->
					{
						player.playSound(1083);

						if (text.equalsIgnoreCase("INSERT"))
						{
							showInsertLineDialog(lineNum, d.getParentDialog());
							return;
						}
						else if (text.equalsIgnoreCase("DELETE"))
						{
							codeLines.remove(lineNum-1);
							d.showParentDialog();
							return;
						}

						codeLines.set(lineNum-1, text);
						d.showParentDialog();
					})
					.build().show();
			});
		}

		addItem(stringSet.get("Dialog.CodeEditorDialog.Add"), (i) ->
		{
			player.playSound(1083);

			int lineNum = codeLines.size() + 1;
			showInsertLineDialog(lineNum, CodeEditorDialog.this);
		});

		addItem(stringSet.get("Dialog.CodeEditorDialog.Save"), (i) ->
		{
			player.playSound(1083);
			onCompleteHandler.accept(toCode());
		});

		super.show();
	}

	private void showInsertLineDialog(int line, AbstractDialog dialog)
	{
		String caption = stringSet.format("Dialog.CodeEditorNewLineDialog.Caption", line);
		String message = stringSet.format("Dialog.CodeEditorNewLineDialog.Text", line);

		WlInputDialog.create(player, rootEventManager)
			.parentDialog(dialog)
			.caption(caption)
			.message(message)
			.onClickOk((d, text) ->
			{
				player.playSound(1083);

				codeLines.add(line-1, text);
				d.showParentDialog();
			})
			.build().show();
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
}
