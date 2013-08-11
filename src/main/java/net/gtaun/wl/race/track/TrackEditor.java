package net.gtaun.wl.race.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.ColorUtils;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.constant.MapIconStyle;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerMapIcon.MapIcon;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.wl.race.impl.RaceServiceImpl;

public class TrackEditor extends AbstractPlayerContext
{
	private final Track track;
	
	private Map<TrackCheckpoint, MapIcon> mapIcons;
	
	
	public TrackEditor(Shoebill shoebill, EventManager rootEventManager, Player player, RaceServiceImpl raceService, Track track)
	{
		super(shoebill, rootEventManager, player);
		this.track = track;
		this.mapIcons = new HashMap<>();
	}
	
	@Override
	protected void onInit()
	{
		eventManager.registerHandler(PlayerUpdateEvent.class, player, playerEventHandler, HandlerPriority.NORMAL);
		
		player.sendMessage(Color.LIGHTBLUE, "%1$s: 你现在正在编辑 %2$s 赛道。", "赛车系统", track.getName());
	}
	
	@Override
	protected void onDestroy()
	{
		player.sendMessage(Color.LIGHTBLUE, "%1$s: 已停止编辑 %2$s 赛道。", "赛车系统", track.getName());
		
		for (MapIcon icon : mapIcons.values()) icon.destroy();
		mapIcons.clear();
	}
	
	public Track getTrack()
	{
		return track;
	}

	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{	
		protected void onPlayerUpdate(PlayerUpdateEvent event)
		{
			if (player.getUpdateFrameCount() % 40 != 0) return;
			
			List<TrackCheckpoint> checkpoints = new ArrayList<>(track.getCheckpoints());
			Collections.sort(checkpoints, new Comparator<TrackCheckpoint>()
			{
				@Override
				public int compare(TrackCheckpoint o1, TrackCheckpoint o2)
				{
					Location playerLoc = player.getLocation();
					return (int) (o1.getLocation().distance(playerLoc) - o2.getLocation().distance(playerLoc));
				}
			});
			
			Map<TrackCheckpoint, MapIcon> lastMapIcons = mapIcons;
			mapIcons = new HashMap<>();

			int size = Math.min(10, checkpoints.size());
			for (int i=0; i<size; i++)
			{
				TrackCheckpoint checkpoint = checkpoints.get(i);
				MapIcon icon = lastMapIcons.get(checkpoint);
				
				if (icon == null) icon = player.getMapIcon().createIcon();
				else lastMapIcons.remove(checkpoint);
				
				final float fadeOutDistance = 1000.0f;
				float distance = checkpoint.getLocation().distance(player.getLocation());
				if (distance > fadeOutDistance) distance = fadeOutDistance;
				int alpha = (int) (255 * distance / fadeOutDistance);
				
				Color subColor = new Color(Color.GREEN);
				subColor.setA(128);
				Color color = ColorUtils.colorBlend(Color.RED, subColor, alpha);
				
				icon.update(checkpoint.getLocation(), 0, color, MapIconStyle.GLOBAL_CHECKPOINT);
				mapIcons.put(checkpoint, icon);
			}

			for (MapIcon icon : lastMapIcons.values()) icon.destroy();
		}
	};
}
