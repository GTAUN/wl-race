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

import org.apache.commons.lang3.StringUtils;

/**
 * JavaScript 代码编辑器。
 * 
 * @author MK124
 */
public abstract class CodeEditorDialog extends AbstractPageListDialog
{
	private final String title;
	private List<String> codeLines;
	
	
	protected CodeEditorDialog(Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, String title, String code)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.title = title;
		
		if (code == null) code = "";
		codeLines = new ArrayList<>(Arrays.asList(StringUtils.split(code, '\n')));
	}
	
	@Override
	public void show()
	{
		dialogListItems.clear();
		
		for (int i=0; i<codeLines.size(); i++)
		{
			final String line = codeLines.get(i);
			final int lineNum = i+1;
			
			dialogListItems.add(new DialogListItem(String.format("[%1$02d] %2$s", lineNum, line))
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					
					final String caption = String.format("代码编辑器: 编辑第 %1$d 行代码", lineNum);
					final String message = String.format("您正在编辑第 %1$d 行代码，原始代码为:\n\n%2$s\n\n输入 {0000FF}INSERT{A9C4E4} 可以在本行上面插入代码；\n输入 {FF0000}DELETE{A9C4E4} 可以删除本行代码。\n\n请输入新的代码，每行最长 144 字节:", lineNum, line);
					new AbstractInputDialog(player, shoebill, rootEventManager, caption, message, CodeEditorDialog.this)
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
		
		dialogListItems.add(new DialogListItem("[+] 添加新一行代码")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				final int lineNum = codeLines.size() + 1;
				addNewLine(lineNum, CodeEditorDialog.this);
			}
		});
		
		dialogListItems.add(new DialogListItem("[S] 保存代码")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				onComplete(toCode());
			}
		});
		
		this.caption = String.format("代码编辑器: 编辑 %1$s 的代码 (共 %2$d 行)", title, codeLines.size());
		super.show();
	}
	
	private void addNewLine(final int line, AbstractDialog dialog)
	{
		final String caption = String.format("代码编辑器: 添加第 %1$d 行代码", line);
		final String message = String.format("请您输入第 %1$s 行的新代码，每行最长 144 字节:", line);
		new AbstractInputDialog(player, shoebill, rootEventManager, caption, message, dialog)
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
