package net.gtaun.wl.race.script;

public class ScriptInstructionCountLimitException extends RuntimeException
{
	private static final long serialVersionUID = -665290691288360724L;

	
	public ScriptInstructionCountLimitException()
	{

	}
	
	public ScriptInstructionCountLimitException(String message)
	{
		super(message);
	}
	
	public ScriptInstructionCountLimitException(Throwable cause)
	{
		super(cause);
	}
}
