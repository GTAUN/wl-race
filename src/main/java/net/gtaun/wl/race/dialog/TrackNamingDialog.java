package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.race.util.TrackUtil;

public abstract class TrackNamingDialog extends AbstractInputDialog
{
	public TrackNamingDialog(Player player, Shoebill shoebill, EventManager rootEventManager, String caption, String message, AbstractDialog parentDialog)
	{
		super(player, shoebill, rootEventManager, caption, message, parentDialog);
	}
	
	public void onClickOk(String inputText)
	{
		player.playSound(1083, player.getLocation());
		
		String name = TrackUtil.filterName(inputText);
		if (TrackUtil.isVaildName(name) == false)
		{
			append = String.format("{FF0000}* 赛道名长度要求为 %1$d ~ %2$d 个字，请重新输入。", TrackUtil.NAME_MIN_LENGTH, TrackUtil.NAME_MAX_LENGTH);
			show();
			return;
		}
		
		onNaming(name);
	}
	
	protected abstract void onNaming(String name);
}
