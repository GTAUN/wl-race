package net.gtaun.wl.race.racing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.Datastore;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.race.RacingManager;
import net.gtaun.wl.race.exception.AlreadyJoinedException;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.track.Track;

public class RacingManagerImpl extends AbstractShoebillContext implements RacingManager
{
	private List<Racing> racings;
	private Map<Player, Racing> playerRacings;
	

	public RacingManagerImpl(Shoebill shoebill, EventManager rootEventManager, Datastore datastore)
	{
		super(shoebill, rootEventManager);
		racings = new ArrayList<>();
	}

	@Override
	protected void onInit()
	{
		
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
	public Racing createRacing(Track track, Player sponsor)
	{
		Racing racing = new Racing(shoebill, rootEventManager, this, track, sponsor);
		racings.add(racing);
		return racing;
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
	
	public void joinRacing(Racing racing, Player player) throws AlreadyJoinedException
	{
		if (playerRacings.containsKey(player)) throw new AlreadyJoinedException();
		if (!racings.contains(racing)) throw new IllegalArgumentException("Unknown Racing");
		
		playerRacings.put(player, racing);
	}
	
	public void leaveRacing(Racing racing, Player player)
	{
		if (playerRacings.get(player) != racing) throw new IllegalStateException("Invaild Racing or Player");
		playerRacings.remove(player);
	}
}
