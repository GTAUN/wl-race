package net.gtaun.wl.race.script;

public class ScriptException extends Exception
{
	private static final long serialVersionUID = 7057253799387140933L;
	
	
	private final String sourceName;
    private final int lineNumber;
    private final String lineSource;
    private final int columnNumber;
    
    
	public ScriptException(String detail, String sourceName, int lineNumber, String lineSource, int columnNumber)
	{
		super(detail);
		this.sourceName = sourceName;
		this.lineNumber = lineNumber;
		this.lineSource = lineSource;
		this.columnNumber = columnNumber;
	}
	
	public String getSourceName()
	{
		return sourceName;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	public String getLineSource()
	{
		return lineSource;
	}
	
	public int getColumnNumber()
	{
		return columnNumber;
	}
}
