package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.race.racing.RacingLimit;
import net.gtaun.wl.race.track.Track;

public class RacingLimitDialog extends AbstractListDialog
{
	public RacingLimitDialog(final Player player, Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, Track track, final RacingLimit limit)
	{
		super(player, shoebill, eventManager, parentDialog);
		
		dialogListItems.add(new DialogListItemRadio("自动修车:")
		{
			{
				addItem(new RadioItem("允许", Color.GREEN)
				{
					@Override public void onSelected()	{ limit.setAllowAutoRepair(true); }
				});
				addItem(new RadioItem("禁止", Color.RED)
				{
					@Override public void onSelected()	{ limit.setAllowAutoRepair(false); }
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
				return limit.isAllowAutoRepair() ? 0 : 1;
			}
		});
		
		dialogListItems.add(new DialogListItemRadio("无限氮气:")
		{
			{
				addItem(new RadioItem("允许", Color.GREEN)
				{
					@Override public void onSelected()	{ limit.setAllowInfiniteNitrous(true); }
				});
				addItem(new RadioItem("禁止", Color.RED)
				{
					@Override public void onSelected()	{ limit.setAllowInfiniteNitrous(false); }
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
				return limit.isAllowInfiniteNitrous() ? 0 : 1;
			}
		});
		
		dialogListItems.add(new DialogListItemRadio("自动翻车:")
		{
			{
				addItem(new RadioItem("允许", Color.GREEN)
				{
					@Override public void onSelected()	{ limit.setAllowAutoFlip(true); }
				});
				addItem(new RadioItem("禁止", Color.RED)
				{
					@Override public void onSelected()	{ limit.setAllowAutoFlip(false); }
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
				return limit.isAllowAutoFlip() ? 0 : 1;
			}
		});
		
		dialogListItems.add(new DialogListItemRadio("更换车辆:")
		{
			{
				addItem(new RadioItem("允许", Color.GREEN)
				{
					@Override public void onSelected()	{ limit.setAllowChangeVehicle(true); }
				});
				addItem(new RadioItem("禁止", Color.RED)
				{
					@Override public void onSelected()	{ limit.setAllowChangeVehicle(false); }
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
				return limit.isAllowChangeVehicle() ? 0 : 1;
			}
		});
		
		dialogListItems.add(new DialogListItem("完成")
		{	
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				showParentDialog();
			}
		});
	}
}
