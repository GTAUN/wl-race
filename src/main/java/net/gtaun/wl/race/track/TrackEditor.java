package net.gtaun.wl.race.track;

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
import net.gtaun.wl.race.dialog.TrackCheckpointEditDialog;
import net.gtaun.wl.race.dialog.TrackEditDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;

public class TrackEditor extends AbstractPlayerContext
{
	private final RaceServiceImpl raceService;
	private final Track track;

	private long lastHornKeyPressedTime;
	
	
	public TrackEditor(Shoebill shoebill, EventManager rootEventManager, Player player, RaceServiceImpl raceService, Track track)
	{
		super(shoebill, rootEventManager, player);
		this.raceService = raceService;
		this.track = track;
	}
	
	@Override
	protected void onInit()
	{
		eventManager.registerHandler(PlayerKeyStateChangeEvent.class, player, playerEventHandler, HandlerPriority.NORMAL);
		
		player.sendMessage(Color.LIGHTBLUE, "%1$s: 你现在正在编辑 \"%2$s\" 赛道。", "赛车系统", track.getName());
	}
	
	@Override
	protected void onDestroy()
	{
		player.sendMessage(Color.LIGHTBLUE, "%1$s: 已停止编辑 \"%2$s\" 赛道。", "赛车系统", track.getName());
	}
	
	public Track getTrack()
	{
		return track;
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
				if (now <= lastHornKeyPressedTime + 1000)
				{
					new TrackEditDialog(player, shoebill, eventManager, null, raceService, track).show();
				}
				lastHornKeyPressedTime = System.currentTimeMillis();
			}
			
			if (keyState.isKeyPressed(PlayerKey.ANALOG_DOWN))
			{
				TrackCheckpoint checkpoint = new TrackCheckpoint(track, player.getLocation());
				new TrackCheckpointEditDialog(player, shoebill, eventManager, null, checkpoint).show();
			}
		}
	};
}
