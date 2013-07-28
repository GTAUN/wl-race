package net.gtaun.wl.race.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.race.data.Track;

import org.apache.commons.lang3.StringUtils;

public class TrackEditDialog extends AbstractListDialog
{
	private final Track track;
	

	public TrackEditDialog
	(final Player player, final Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.track = track;
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("赛道名: %1$s", track.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				String caption = String.format("%1$s: 编辑赛道名: %2$s", "赛车系统", track.getName());
				String message = String.format("原始赛道名 \"%1$s\" ，请输入新的赛道名: ", track.getName());
				new AbstractInputDialog(player, shoebill, rootEventManager, caption, message, TrackEditDialog.this)
				{
					private String append;
					
					public void onClickOk(String inputText)
					{
						String name = StringUtils.trimToEmpty(inputText);
						name = StringUtils.replace(name, "%", "#");
						name = StringUtils.replace(name, "\t", " ");
						name = StringUtils.replace(name, "\n", " ");
						if (name.length() < 3)
						{
							append = "{7F0000}* 赛道名长度最少为 3 个字，请重新输入。";
							show();
						}
						if (name.length() > 40)
						{
							append = "{7F0000}* 赛道名长度最长为 40 个字，请重新输入。";
							show();
						}
						
						track.setName(name);
						showParentDialog();
					}
					
					protected void show(String text)
					{
						if (append != null) super.show(this.message + "\n\n" + append);
						super.show(text);
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String desc = track.getDesc();
				if (StringUtils.isBlank(desc)) desc = "空";
				return String.format("描述: %1$s", desc);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String messageFormat =
						"赛道 %1$s 原描述信息为:\n\n" +
						"%2$s\n\n" +
						"请输入新的描述信息:";
				
				String caption = String.format("%1$s: 编辑赛道描述: %2$s", "赛车系统", track.getName());
				String message = String.format(messageFormat, track.getName(), track.getDesc());
				new AbstractInputDialog(player, shoebill, rootEventManager, caption, message, TrackEditDialog.this)
				{
					public void onClickOk(String inputText)
					{
						String desc = StringUtils.trimToEmpty(inputText);
						desc = StringUtils.replace(desc, "%", "#");
						desc = StringUtils.replace(desc, "\t", " ");
						desc = StringUtils.replace(desc, "\n", " ");
						
						track.setDesc(desc);
						showParentDialog();
					}
				}.show();
			}
		});
	}
	
	@Override
	public void show()
	{
		this.caption = String.format("%1$s: 编辑赛道: %2$s", "赛车系统", track.getName());
		super.show();
	}
}
