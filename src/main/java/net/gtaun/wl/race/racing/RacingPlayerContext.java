package net.gtaun.wl.race.racing;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.race.script.ScriptExecutor;
import net.gtaun.wl.race.script.ScriptExecutorFactory;

public class RacingPlayerContext extends AbstractPlayerContext
{
	private ScriptExecutor scriptExecutor;
	
	
	public RacingPlayerContext(Shoebill shoebill, EventManager rootEventManager, Player player)
	{
		super(shoebill, rootEventManager, player);
	}
	
	@Override
	protected void onInit()
	{
		scriptExecutor = ScriptExecutorFactory.createCheckpointScriptExecutor(player);
		
	}
	
	@Override
	protected void onDestroy()
	{
		
	}
	
	public ScriptExecutor getScriptExecutor()
	{
		return scriptExecutor;
	}
}
