package net.gtaun.wl.race.dialog;

import org.apache.commons.lang3.StringUtils;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.ScriptType;

public class TrackScriptEditDialog extends AbstractListDialog
{
	protected TrackScriptEditDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.caption = String.format("%1$s: 编辑赛道事件脚本: %2$s", "赛车系统", track.getName());
		
		for (final ScriptType type : ScriptType.values())
		{
			dialogListItems.add(new DialogListItem()
			{
				@Override
				public String toItemString()
				{
					String code = track.getScript(type);
					int lines = StringUtils.countMatches(code, "\n");
					return String.format("事件 %1$s 脚本: %2$d 行 (%3$d 个字符)", type.name(), lines, code.length());
				}
				
				@Override
				public void onItemSelect()
				{
					final String title = String.format("事件 %1$s", type.name());
					final String code = track.getScript(type);
					new CodeEditorDialog(player, shoebill, eventManager, TrackScriptEditDialog.this, title, code)
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
