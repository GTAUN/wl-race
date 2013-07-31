package net.gtaun.wl.race.script;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

public final class ScriptExecutorFactory
{
	public static ScriptExecutor createCheckpointScriptExecutor(Player player, Vehicle vehicle)
	{
		final ScriptEngine engine = createCheckpointScriptEngine(player, vehicle);
		return new ScriptExecutor()
		{
			@Override
			public void execute(String script) throws ScriptException
			{
				engine.eval(script);
			}
		};
	}
	
	private static ScriptEngine createCheckpointScriptEngine(Player player, Vehicle vehicle)
	{
		ScriptEngine engine = createEngine();
		
		Bindings bindings = engine.createBindings();
		bindings.put("player", new ScriptPlayerBinding(player));
		bindings.put("vehicle", new ScriptVehicleBinding(vehicle));
		
		engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
		return engine;
	}
	
	private static ScriptEngine createEngine()
	{
		ScriptEngineManager manager = new ScriptEngineManager();
		return manager.getEngineByName("JavaScript");	
	}
	
	private ScriptExecutorFactory()
	{
		
	}
}
