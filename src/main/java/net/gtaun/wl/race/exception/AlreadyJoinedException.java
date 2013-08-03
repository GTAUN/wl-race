package net.gtaun.wl.race.exception;

public class AlreadyJoinedException extends RuntimeException
{
	private static final long serialVersionUID = -4396265516911213536L;
	

	public AlreadyJoinedException()
	{
		
	}
	
	public AlreadyJoinedException(String message)
	{
		super(message);
	}
	
	public AlreadyJoinedException(Throwable cause)
	{
		super(cause);
	}
}
