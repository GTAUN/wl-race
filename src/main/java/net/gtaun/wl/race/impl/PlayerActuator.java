package net.gtaun.wl.race.impl;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackEditor;

public class PlayerActuator extends AbstractPlayerContext
{
	private final RaceServiceImpl raceService;
	
	private TrackEditor trackEditor;
	
	
	public PlayerActuator(Shoebill shoebill, EventManager rootEventManager, Player player, RaceServiceImpl raceService)
	{
		super(shoebill, rootEventManager, player);
		this.raceService = raceService;
	}

	@Override
	protected void onInit()
	{

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
			
			trackEditor.destroy();
			trackEditor = null;
		}
		else
		{
			if (trackEditor != null) return;

			trackEditor = new TrackEditor(shoebill, rootEventManager, player, raceService, track);
			trackEditor.init();
		}
	}

	public Track getEditingTrack()
	{
		if (trackEditor == null) return null;
		return trackEditor.getTrack();
	}
}
