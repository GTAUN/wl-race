package net.gtaun.wl.race;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.constant.PlayerKey;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerKeyStateChangeEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerKeyState;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.wl.race.data.Track;
import net.gtaun.wl.race.dialog.TrackEditDialog;

public class PlayerActuator extends AbstractPlayerContext
{
	private Track editingTrack;
	private long lastHornKeyPressedTime;
	
	
	public PlayerActuator(Shoebill shoebill, EventManager rootEventManager, Player player)
	{
		super(shoebill, rootEventManager, player);
	}

	@Override
	protected void onInit()
	{
		eventManager.registerHandler(PlayerKeyStateChangeEvent.class, player, playerEventHandler, HandlerPriority.NORMAL);
	}

	@Override
	protected void onDestroy()
	{
		
	}
	
	public void setEditingTrack(Track track)
	{
		if (track == null)
		{
			if (editingTrack == null) return;
			player.sendMessage(Color.LIGHTBLUE, "%1$s: 已取消编辑 \"%2$s\" 赛道。", "赛车系统", editingTrack.getName());
		}
		else
		{
			if (editingTrack != null)
			{
				player.sendMessage(Color.LIGHTBLUE, "%1$s: 您现在正在编辑 \"%2$s\" 赛道，请取消编辑后重试。", "赛车系统", editingTrack.getName());				
				return;
			}
			
			editingTrack = track;
			player.sendMessage(Color.LIGHTBLUE, "%1$s: 你现在正在编辑 \"%2$s\" 赛道。", "赛车系统", editingTrack.getName());
		}
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerKeyStateChange(PlayerKeyStateChangeEvent event)
		{
			PlayerKeyState keyState = player.getKeyState();
			if (player.isAdmin()) player.sendMessage(Color.WHITE, "OLD " + event.getOldkeys() + ", NOW " + keyState.getKeys());
			
			if (keyState.isKeyPressed(PlayerKey.CROUCH))
			{
				long now = System.currentTimeMillis();
				if (now <= lastHornKeyPressedTime + 1000 && editingTrack != null)
				{
					new TrackEditDialog(player, shoebill, eventManager, null, editingTrack).show();
				}
				lastHornKeyPressedTime = System.currentTimeMillis();
			}
		}
	};
}
