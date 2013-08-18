package net.gtaun.wl.race.script;

public class ScriptTimeoutException extends RuntimeException
{
	private static final long serialVersionUID = 1601322737761500413L;

	
	public ScriptTimeoutException()
	{
	}
	
	public ScriptTimeoutException(String message)
	{
		super(message);
	}
	
	public ScriptTimeoutException(Throwable cause)
	{
		super(cause);
	}
}
