package net.gtaun.wl.race.dialog;

import org.apache.commons.lang3.StringUtils;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;

public abstract class TrackNamingDialog extends AbstractInputDialog
{
	public TrackNamingDialog(Player player, Shoebill shoebill, EventManager rootEventManager, String caption, String message, AbstractDialog parentDialog)
	{
		super(player, shoebill, rootEventManager, caption, message, parentDialog);
	}
	
	public void onClickOk(String inputText)
	{
		String name = StringUtils.trimToEmpty(inputText);
		name = StringUtils.replace(name, "%", "#");
		name = StringUtils.replace(name, "\t", " ");
		name = StringUtils.replace(name, "\n", " ");
		if (name.length() < 3)
		{
			append = "{FF0000}* 赛道名长度最少为 3 个字，请重新输入。";
			show();
			return;
		}
		if (name.length() > 40)
		{
			append = "{FF0000}* 赛道名长度最长为 40 个字，请重新输入。";
			show();
			return;
		}
		
		onNaming(name);
		showParentDialog();
	}
	
	protected abstract void onNaming(String name);
}
