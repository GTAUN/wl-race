package net.gtaun.wl.race.impl;

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
import net.gtaun.wl.race.dialog.RacingDialog;
import net.gtaun.wl.race.dialog.RacingListDialog;
import net.gtaun.wl.race.dialog.TrackCheckpointEditDialog;
import net.gtaun.wl.race.dialog.TrackEditDialog;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.track.TrackEditor;

import org.apache.commons.lang3.StringUtils;

public class PlayerActuator extends AbstractPlayerContext
{
	private final RaceServiceImpl raceService;
	
	private TrackEditor trackEditor;
	private long lastHornKeyPressedTime;
	private long lastAnalogDownKeyPressedTime;
	
	
	public PlayerActuator(Shoebill shoebill, EventManager rootEventManager, Player player, RaceServiceImpl raceService)
	{
		super(shoebill, rootEventManager, player);
		this.raceService = raceService;
	}

	@Override
	protected void onInit()
	{
		eventManager.registerHandler(PlayerKeyStateChangeEvent.class, player, playerEventHandler, HandlerPriority.NORMAL);
		
		String original = "吾等封印已经全数解除 统治世界之日即将到来";
		String dest = "";
		int len = original.length();
		for (int i=0; i<len; i++)
		{
			int color = java.awt.Color.HSBtoRGB((float)i/len, 1.0f, 1.0f) << 8 | 0xFF;
			dest += new Color(color).toEmbeddingString() + original.charAt(i);
		}
		
		for (String s : StringUtils.split(dest, ' ')) player.sendMessage(Color.WHITE, "* " + s + " {FFFFFF}*");
	}

	@Override
	protected void onDestroy()
	{
		
	}

	public boolean isEditingTrack()
	{
		return trackEditor != null;
	}

	public void setEditingTrack(Track track)
	{
		if (track == null)
		{
			if (trackEditor == null) return;
			
			Track lastTrack = trackEditor.getTrack();
			trackEditor.destroy();
			trackEditor = null;
			
			raceService.getTrackManager().save(lastTrack);
		}
		else
		{
			if (trackEditor != null) return;

			if (track.getStatus() == TrackStatus.RANKING) throw new UnsupportedOperationException();
			track.setStatus(TrackStatus.EDITING);
			
			trackEditor = new TrackEditor(shoebill, rootEventManager, player, raceService, track);
			trackEditor.init();
			
			new TrackEditDialog(player, shoebill, eventManager, null, raceService, track).show();
		}
	}

	public Track getEditingTrack()
	{
		if (trackEditor == null) return null;
		return trackEditor.getTrack();
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerKeyStateChange(PlayerKeyStateChangeEvent event)
		{
			PlayerKeyState keyState = player.getKeyState();
			if (player.isAdmin()) player.sendMessage(Color.WHITE, "OLD " + event.getOldkeys() + ", NOW " + keyState.getKeys());
			
			Track editingTrack = getEditingTrack();
			if (editingTrack != null)
			{
				if (keyState.isKeyPressed(PlayerKey.CROUCH))
				{
					long now = System.currentTimeMillis();
					if (now <= lastHornKeyPressedTime + 1000)
					{
						new TrackEditDialog(player, shoebill, eventManager, null, raceService, editingTrack).show();
					}
					lastHornKeyPressedTime = System.currentTimeMillis();
				}
				else if (keyState.isKeyPressed(PlayerKey.ANALOG_DOWN))
				{
					TrackCheckpoint checkpoint = editingTrack.createCheckpoint(player.getLocation());
					trackEditor.updateMapIcons();
					
					new TrackCheckpointEditDialog(player, shoebill, eventManager, null, checkpoint).show();
				}
			}
			else
			{
				if (keyState.getKeys() == PlayerKey.CROUCH.getValue())
				{
					long now = System.currentTimeMillis();
					if (now <= lastHornKeyPressedTime + 1000)
					{
						RacingManagerImpl racingManager = raceService.getRacingManager();
						Racing racing = racingManager.getPlayerRacing(player);
						if (racing != null && racing.getStatus() == RacingStatus.WAITING)
						{
							new RacingDialog(player, shoebill, eventManager, null, raceService, racing).show();
						}
					}
					lastHornKeyPressedTime = System.currentTimeMillis();
				}
				else if (keyState.getKeys() == PlayerKey.ANALOG_DOWN.getValue())
				{
					long now = System.currentTimeMillis();
					if (now <= lastAnalogDownKeyPressedTime + 1000)
					{
						new RacingListDialog(player, shoebill, eventManager, null, raceService).show();
					}
					lastAnalogDownKeyPressedTime = System.currentTimeMillis();
				}
			}
		}
	};
}
