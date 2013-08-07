package net.gtaun.wl.race.script;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.gtaun.shoebill.object.Player;

public final class ScriptExecutorFactory
{
	public static ScriptExecutor createCheckpointScriptExecutor(Player player)
	{
		final ScriptEngine engine = createCheckpointScriptEngine(player);
		return new ScriptExecutor()
		{
			@Override
			public void execute(String script) throws ScriptException
			{
				Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
				for (Object obj : bindings.values())
				{
					if (obj instanceof ScriptBinding == false) continue;
					ScriptBinding binding = (ScriptBinding) obj;
					binding.update();
				}
				
				engine.eval(script);
			}
		};
	}
	
	private static ScriptEngine createCheckpointScriptEngine(Player player)
	{
		ScriptEngine engine = createEngine();
		
		Bindings bindings = engine.createBindings();
		bindings.put("player", new PlayerBinding(player));
		bindings.put("vehicle", new PlayerVehicleBinding(player));
		
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
