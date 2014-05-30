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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.race.RacingManager;
import net.gtaun.wl.race.exception.AlreadyJoinedException;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.track.Track;

import org.mongodb.morphia.Datastore;

public class RacingManagerImpl extends AbstractShoebillContext implements RacingManager
{
	private final RaceServiceImpl raceService;

	private List<Racing> racings;
	private Map<Player, Racing> playerRacings;


	public RacingManagerImpl(EventManager rootEventManager, RaceServiceImpl raceService, Datastore datastore)
	{
		super(rootEventManager);
		this.raceService = raceService;

		racings = new ArrayList<>();
		playerRacings = new HashMap<>();
	}

	@Override
	protected void onInit()
	{
		eventManagerNode.registerHandler(PlayerDisconnectEvent.class, (e) ->
		{
			Player player = e.getPlayer();
			if (isPlayerInRacing(player)) getPlayerRacing(player).leave(player);
		});
	}

	@Override
	protected void onDestroy()
	{

	}

	@Override
	public List<Racing> getRacings()
	{
		return Collections.unmodifiableList(racings);
	}

	@Override
	public List<Racing> getRacings(RacingStatus status)
	{
		List<Racing> list = new ArrayList<>();
		for (Racing racing : racings) if (racing.getStatus() == status) list.add(racing);
		return list;
	}

	@Override
	public Racing createRacing(Track track, Player sponsor, String name) throws AlreadyJoinedException
	{
		if (isPlayerInRacing(sponsor)) throw new AlreadyJoinedException();
		Racing racing = new Racing(rootEventManager, raceService, this, track, sponsor, name);
		racings.add(racing);
		racing.join(sponsor);

		for (Player player : Player.get())
		{
			if (player == sponsor) continue;
			PlayerStringSet stringSet = raceService.getLocalizedStringSet().getStringSet(player);
			stringSet.sendMessage(Color.LIGHTBLUE, "Racing.Message.NewRacingMessage", sponsor.getName(), racing.getName(), track.getName());
		}

		return racing;
	}

	public void destroyRacing(Racing racing)
	{
		racings.remove(racing);
		for (Iterator<Entry<Player, Racing>> it = playerRacings.entrySet().iterator(); it.hasNext();)
		{
			Entry<Player, Racing> entry = it.next();
			if (entry.getValue() == racing) it.remove();
		}
	}

	@Override
	public boolean isPlayerInRacing(Player player)
	{
		return playerRacings.containsKey(player);
	}

	@Override
	public Racing getPlayerRacing(Player player)
	{
		return playerRacings.get(player);
	}

	@Override
	public PlayerRacingStatus getPlayerRacingStatus(Player player)
	{
		Racing racing = getPlayerRacing(player);
		if (racing == null) return PlayerRacingStatus.NONE;
		return racing.getStatus() == RacingStatus.WAITING ? PlayerRacingStatus.WAITING : PlayerRacingStatus.RACING;
	}

	void joinRacing(Racing racing, Player player) throws AlreadyJoinedException
	{
		if (playerRacings.containsKey(player)) throw new AlreadyJoinedException();
		if (!racings.contains(racing)) throw new IllegalArgumentException("Unknown Racing");

		playerRacings.put(player, racing);
	}

	void leaveRacing(Racing racing, Player player)
	{
		if (playerRacings.get(player) != racing) throw new IllegalStateException("Invaild Racing or Player");
		playerRacings.remove(player);
	}
}
