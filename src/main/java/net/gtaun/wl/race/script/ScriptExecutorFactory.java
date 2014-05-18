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

package net.gtaun.wl.race.script;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import net.gtaun.shoebill.object.Player;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public final class ScriptExecutorFactory
{
	private static class TimeLimitContext extends Context
	{
		private int timeLimit;
		private int instructionCountLimit;
		
		private long startTime;
		private int instructionCount;
		
		public TimeLimitContext()
		{
			super(CONTEXT_FACTORY);
		}
		
		public void setTimeLimit(int timeLimit)
		{
			this.timeLimit = timeLimit;
		}
		
		public void setInstructionCountLimit(int instructionCountLimit)
		{
			this.instructionCountLimit = instructionCountLimit;
		}
		
		public void start()
		{
			startTime = System.currentTimeMillis();
			instructionCount = 0;
		}
		
		@Override
		protected void observeInstructionCount(int count)
		{
			instructionCount += count;
			
			if (timeLimit != 0 && System.currentTimeMillis() - startTime > timeLimit) throw new ScriptTimeoutException();
			if (instructionCount > instructionCountLimit) throw new ScriptInstructionCountLimitException();
		}
	}
	
	private static class ScriptContextFactory extends ContextFactory
	{
		@Override
		protected TimeLimitContext makeContext()
		{
			TimeLimitContext context = new TimeLimitContext();
			context.setWrapFactory(new WrapFactory()
			{
				@Override
				public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, java.lang.Class<?> staticType)
				{
					return new NativeJavaObject(scope, javaObject, getClass())
					{
						private static final long serialVersionUID = 1L;

						@Override
						public Object get(String name, Scriptable start)
						{
							if (name.equals("getClass")) return NOT_FOUND;
							return super.get(name, start);
						}
					};
				}
			});
			context.setClassShutter(new ClassShutter()
			{
				@Override
				public boolean visibleToScripts(String fullClassName)
				{
					Class<?> clz;
					try
					{
						clz = Class.forName(fullClassName);
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
						return false;
					}
					if(ScriptBinding.class.isAssignableFrom(clz)) return true;
					if(clz.equals(String.class)) return true;
					return false;
				}
			});
			return context;
		}

		@Override
		public TimeLimitContext enterContext()
		{
			return (TimeLimitContext) super.enterContext();
		}
	};
	
	public static final ScriptContextFactory CONTEXT_FACTORY = new ScriptContextFactory();
	
	
	public static ScriptExecutor createCheckpointScriptExecutor(Player player)
	{
		final Deque<ScriptException> exceptions = new LinkedList<>();
		final TimeLimitContext context = CONTEXT_FACTORY.enterContext();
		context.setTimeLimit(1000);
		context.setInstructionCountLimit(1000);
		context.setInstructionObserverThreshold(10);
		context.setErrorReporter(new ErrorReporter()
		{
			@Override
			public void warning(String message, String sourceName, int line, String lineSource, int lineOffset)
			{
				exceptions.add(new ScriptException(message, sourceName, line, lineSource, lineOffset));
			}
			
			@Override
			public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset)
			{
				exceptions.add(new ScriptException(message, sourceName, line, lineSource, lineOffset));
				return new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
			}
			
			@Override
			public void error(String message, String sourceName, int line, String lineSource, int lineOffset)
			{
				exceptions.add(new ScriptException(message, sourceName, line, lineSource, lineOffset));
			}
		});
		Context.exit();
		
		final Scriptable scope = context.initStandardObjects();
		
		final List<ScriptBinding> bindings = new ArrayList<>();
		
		PlayerBinding playerBinding = new PlayerBinding(player);
		scope.put("player", scope, playerBinding);
		bindings.add(playerBinding);
		
		PlayerVehicleBinding playerVehicleBinding = new PlayerVehicleBinding(player);
		scope.put("vehicle", scope, playerVehicleBinding);
		bindings.add(playerVehicleBinding);
		
		return new ScriptExecutor()
		{
			@Override
			public void execute(final String script) throws ScriptException
			{
				for (Object obj : bindings)
				{
					if (obj instanceof ScriptBinding == false) continue;
					ScriptBinding binding = (ScriptBinding) obj;
					binding.update();
				}
				
				try
				{
					CONTEXT_FACTORY.enterContext(context);
					context.start();
					context.evaluateString(scope, script, "UNKNOWN", 0, null);
				}
				catch (EvaluatorException e)
				{
					exceptions.clear();
					throw new ScriptException(e.details(), e.sourceName(), e.lineNumber(), e.lineSource(), e.columnNumber());
				}
				catch (ScriptTimeoutException | ScriptInstructionCountLimitException e)
				{
					throw e;
				}
				finally
				{
					Context.exit();
				}
				
				if (!exceptions.isEmpty())
				{
					ScriptException exception = exceptions.pollLast();
					exceptions.clear();
					throw exception;
				}
			}
		};
	}
	
	private ScriptExecutorFactory()
	{
		
	}
}
