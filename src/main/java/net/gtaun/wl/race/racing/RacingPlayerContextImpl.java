/**
 * WL Race Plugin
 * Copyright (C) 2013 MK124
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.gtaun.wl.race.racing;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.ColorUtils;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.constant.MapIconStyle;
import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerDeathEvent;
import net.gtaun.shoebill.event.player.PlayerSpawnEvent;
import net.gtaun.shoebill.event.player.PlayerStateChangeEvent;
import net.gtaun.shoebill.event.player.PlayerUpdateEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerMapIcon.MapIcon;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.wl.race.racing.Racing.DeathRule;
import net.gtaun.wl.race.script.ScriptExecutor;
import net.gtaun.wl.race.script.ScriptExecutorFactory;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.vehicle.PlayerOverrideLimit;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;

public class RacingPlayerContextImpl extends AbstractPlayerContext implements RacingPlayerContext
{
	private final Racing racing;
	
	private ScriptExecutor scriptExecutor;
	private TrackCheckpoint currentCheckpoint;
	
	private RacingHudWidget hudWidget;
	private Map<TrackCheckpoint, MapIcon> mapIcons;
	
	private Date startTime;
	private PlayerOverrideLimit vehicleManagerLimit;
	
	private int lastVehicleModel;
	private AngledLocation lastPassLocation;
	private Velocity lastPassVelocity;
	
	private Vehicle tempVehicle;
	
	
	public RacingPlayerContextImpl(Shoebill shoebill, EventManager rootEventManager, Player player, final Racing racing, TrackCheckpoint startCheckpoint)
	{
		super(shoebill, rootEventManager, player);
		this.racing = racing;
		this.currentCheckpoint = startCheckpoint;
		this.mapIcons = new HashMap<>();
		
		vehicleManagerLimit = new PlayerOverrideLimit()
		{
			@Override
			public boolean isInfiniteNitrous(boolean previous)
			{
				return previous && racing.getSetting().getLimit().isAllowInfiniteNitrous();
			}
			
			@Override
			public boolean isAutoRepair(boolean previous)
			{
				return previous && racing.getSetting().getLimit().isAllowAutoRepair();
			}
			
			@Override
			public boolean isAutoFlip(boolean previous)
			{
				return previous && racing.getSetting().getLimit().isAllowAutoFlip();
			}
			
			@Override
			public boolean isAutoCarryPassengers(boolean previous)
			{
				return previous;
			}
		};
	}
	
	@Override
	protected void onInit()
	{
		scriptExecutor = ScriptExecutorFactory.createCheckpointScriptExecutor(player);
		
		hudWidget = new RacingHudWidget(shoebill, rootEventManager, null, player, this);
		hudWidget.init();
		addDestroyable(hudWidget);
		
		if (player.isInAnyVehicle()) lastVehicleModel = player.getVehicle().getModelId();
		
		eventManager.registerHandler(PlayerUpdateEvent.class, player, playerEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(PlayerDeathEvent.class, player, playerEventHandler, HandlerPriority.NORMAL);
		eventManager.registerHandler(PlayerSpawnEvent.class, player, playerEventHandler, HandlerPriority.BOTTOM);
		eventManager.registerHandler(PlayerStateChangeEvent.class, player, playerEventHandler, HandlerPriority.NORMAL);
	}
	
	@Override
	protected void onDestroy()
	{
		scriptExecutor = null;
		
		VehicleManagerService service = shoebill.getServiceStore().getService(VehicleManagerService.class);
		if (service != null)
		{
			service.endRacingStatistic(player);
			service.removeOverrideLimit(player, vehicleManagerLimit);
		}
		
		if (tempVehicle != null) tempVehicle.destroy();
		
		for (MapIcon icon : mapIcons.values()) icon.destroy();
		mapIcons.clear();
	}
	
	void onPassCheckpoint(TrackCheckpoint checkpoint)
	{
		currentCheckpoint = checkpoint;
		
		Vehicle vehicle = player.getVehicle();
		if (vehicle != null)
		{
			lastPassLocation = vehicle.getLocation();
			lastPassVelocity = vehicle.getVelocity();
		}
		else
		{
			lastPassLocation = player.getLocation();
			lastPassVelocity = player.getVelocity();
		}
		
		updateMapIcons();
	}
	
	public void begin()
	{
		startTime = new Date();
		VehicleManagerService service = shoebill.getServiceStore().getService(VehicleManagerService.class);
		if (service != null)
		{
			service.startRacingStatistic(player);
			service.addOverrideLimit(player, vehicleManagerLimit);
		}
	}
	
	@Override
	public Date getStartTime()
	{
		return startTime;
	}

	@Override
	public Racing getRacing()
	{
		return racing;
	}

	@Override
	public ScriptExecutor getScriptExecutor()
	{
		return scriptExecutor;
	}
	
	@Override
	public boolean isCompleted()
	{
		return false;
	}
	
	@Override
	public int getPassedCheckpoints()
	{
		return currentCheckpoint.getNumber();
	}
	
	@Override
	public int getTrackCheckpoints()
	{
		return racing.getTrack().getCheckpoints().size();
	}
	
	@Override
	public float getRemainingDistance()
	{
		TrackCheckpoint next = currentCheckpoint.getNext();
		float distance = 0.0f, cpDistance = currentCheckpoint.getNextDistance();
		
		float nextTotalDistance = (next != null) ? next.getTotalDistance() : 0.0f;
		if (next != null) distance = next.getLocation().distance(player.getLocation());
		if (distance > cpDistance) distance = cpDistance;
		
		return nextTotalDistance + distance;
	}
	
	@Override
	public float getCompletionPercent()
	{
		TrackCheckpoint first = racing.getTrack().getCheckpoints().get(0);
		return 1.0f - (getRemainingDistance() / first.getTotalDistance());
	}
	
	@Override
	public int getRankingNumber()
	{
		return racing.getRacingRankingNumber(this);
	}
	
	@Override
	public String getRankingString()
	{
		int num = getRankingNumber();
		String str;
		
		switch (num)
		{
		case 0:		str = "N/A";		break;
		case 1:		str = "1st";		break;
		case 2:		str = "2nd";		break;
		case 3:		str = "3rd";		break;
		default:	str = num + "th";	break;
		}
		
		return str;
	}
	
	@Override
	public float getTimeDiff()
	{
		List<RacingPlayerContext> rankedList = racing.getRacingRankedList();
		int index = rankedList.indexOf(this);
		if (index <= 0) return 0.0f;
		
		float speed;
		VehicleManagerService service = shoebill.getServiceStore().getService(VehicleManagerService.class);
		if (service == null) speed = player.getVelocity().speed3d() * 50;
		else
		{
			OncePlayerVehicleStatistic stat = service.getPlayerCurrentOnceStatistic(player);
			speed = (float) (stat.getDriveOdometer() / stat.getDriveSecondCount());
		}
		
		RacingPlayerContext prev = rankedList.get(index-1);
		float distanceDiff = getRemainingDistance() - prev.getRemainingDistance();
		
		float diff = distanceDiff / speed;
		return diff >= 0.0f ? diff : 0.0f;
	}
	
	private void updateMapIcons()
	{
		Map<TrackCheckpoint, MapIcon> lastMapIcons = mapIcons;
		mapIcons = new HashMap<>();

		int count = 0;
		TrackCheckpoint next = currentCheckpoint.getNext();
		TrackCheckpoint next2 = next == null ? null : next.getNext();
		for (TrackCheckpoint checkpoint = next2; checkpoint != null && count < 2; checkpoint = checkpoint.getNext())
		{
			MapIcon icon = lastMapIcons.get(checkpoint);
			
			if (icon == null) icon = player.getMapIcon().createIcon();
			else lastMapIcons.remove(checkpoint);

			final float fadeOutDistance = 300.0f;
			float distance = player.getLocation().distance(next.getLocation()) + next2.getDistance(checkpoint);
			if (distance > fadeOutDistance) distance = fadeOutDistance;
			else if (distance < 0.0f) distance = 0.0f;
			int alpha = (int) (255 * distance / fadeOutDistance);
			
			Color subColor = new Color(Color.GREEN);
			subColor.setA(0);
			Color color = ColorUtils.colorBlend(Color.RED, subColor, alpha);
			
			icon.update(checkpoint.getLocation(), 0, color, MapIconStyle.GLOBAL_CHECKPOINT);
			mapIcons.put(checkpoint, icon);
			
			count++;
		}

		for (MapIcon icon : lastMapIcons.values()) icon.destroy();
	}
	
	private void createTempVehicle(int modelId)
	{
		AngledLocation location = player.getLocation();
		Velocity velocity = player.getVelocity();
		
		if (tempVehicle != null) tempVehicle.destroy();
		tempVehicle = shoebill.getSampObjectFactory().createVehicle(lastVehicleModel, player.getLocation(), 0, 0, 3600);
		tempVehicle.putPlayer(player, 0);
		
		tempVehicle.setLocation(location);
		tempVehicle.setVelocity(velocity);
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerUpdate(PlayerUpdateEvent event)
		{
			if (player.getUpdateFrameCount() % 40 != 0) return;
			updateMapIcons();
		}
		
		protected void onPlayerDeath(PlayerDeathEvent event)
		{
			if (racing.getSetting().getDeathRule() == DeathRule.KNOCKOUT) racing.leave(player);
		}
		
		protected void onPlayerSpawn(PlayerSpawnEvent event)
		{
			if (lastVehicleModel != 0)
			{
				createTempVehicle(lastVehicleModel);
				tempVehicle.setLocation(lastPassLocation);
				tempVehicle.setVelocity(lastPassVelocity);
			}
			else
			{
				player.setLocation(lastPassLocation);
			}
		}
		
		protected void onPlayerStateChange(PlayerStateChangeEvent event)
		{
			if (player.isInAnyVehicle()) lastVehicleModel = player.getVehicle().getModelId();
		}
	};
}
