package net.gtaun.wl.race.racing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.AbstractShoebillContext;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerDisconnectEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.wl.race.RacingManager;
import net.gtaun.wl.race.exception.AlreadyJoinedException;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.track.Track;

import com.google.code.morphia.Datastore;

public class RacingManagerImpl extends AbstractShoebillContext implements RacingManager
{
	private List<Racing> racings;
	private Map<Player, Racing> playerRacings;
	

	public RacingManagerImpl(Shoebill shoebill, EventManager rootEventManager, Datastore datastore)
	{
		super(shoebill, rootEventManager);
		racings = new ArrayList<>();
		playerRacings = new HashMap<>();
	}

	@Override
	protected void onInit()
	{
		eventManager.registerHandler(PlayerDisconnectEvent.class, playerEventHandler, HandlerPriority.NORMAL);
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
		Racing racing = new Racing(shoebill, rootEventManager, this, track, sponsor, name);
		racings.add(racing);
		racing.join(sponsor);
		sponsor.sendMessage(Color.LIGHTBLUE, "%1$s: 在等待比赛开始的时候，在车上按两下喇叭 (H键或Caps Lock键) 可以呼出比赛菜单。", "赛车系统");
		
		Collection<Player> players = shoebill.getSampObjectStore().getPlayers();
		for (Player player : players)
		{
			if (player == sponsor) continue;
			player.sendMessage(Color.LIGHTBLUE, "%1$s: %2$s 举办了比赛 %3$s (赛道 %4$s)，请在车上按下两下 End 键可以参加比赛。", "赛车系统", sponsor.getName(), racing.getName(), track.getName());
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
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerDisconnect(PlayerDisconnectEvent event)
		{
			Player player = event.getPlayer();
			if (isPlayerInRacing(player)) getPlayerRacing(player).leave(player);
		}
	};
}
