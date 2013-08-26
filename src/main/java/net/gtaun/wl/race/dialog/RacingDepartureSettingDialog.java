package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.race.racing.RacingSetting;

public class RacingDepartureSettingDialog extends AbstractListDialog
{
	public RacingDepartureSettingDialog(final Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, final RacingSetting setting)
	{
		super(player, shoebill, eventManager, parentDialog);

		dialogListItems.add(new DialogListItem("无")
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				setting.setDepartureInterval(0);
				showParentDialog();
			}
		});
		
		dialogListItems.add(new DialogListItem("1秒钟")
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				setting.setDepartureInterval(1);
				showParentDialog();
			}
		});
		
		dialogListItems.add(new DialogListItem("2秒钟")
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				setting.setDepartureInterval(2);
				showParentDialog();
			}
		});
		
		dialogListItems.add(new DialogListItem("5秒钟")
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				setting.setDepartureInterval(5);
				showParentDialog();
			}
		});
		
		dialogListItems.add(new DialogListItem("10秒钟")
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				setting.setDepartureInterval(10);
				showParentDialog();
			}
		});
		
		dialogListItems.add(new DialogListItem("15秒钟")
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				setting.setDepartureInterval(15);
				showParentDialog();
			}
		});
		
		dialogListItems.add(new DialogListItem("30秒钟")
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				setting.setDepartureInterval(30);
				showParentDialog();
			}
		});
		
		dialogListItems.add(new DialogListItem("1分钟")
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				setting.setDepartureInterval(60);
				showParentDialog();
			}
		});
		
		dialogListItems.add(new DialogListItem("2分钟")
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				setting.setDepartureInterval(120);
				showParentDialog();
			}
		});
	}
	
	
}
