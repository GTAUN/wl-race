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
