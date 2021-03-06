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
