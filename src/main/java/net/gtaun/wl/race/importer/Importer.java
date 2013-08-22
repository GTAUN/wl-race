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

package net.gtaun.wl.race.importer;

import java.io.File;

import net.gtaun.wl.race.TrackManager;

public abstract class Importer
{
	protected final TrackManager trackManager;
	protected final File baseDir;
	
	
	protected Importer(TrackManager trackManager, File baseDir)
	{
		this.trackManager = trackManager;
		this.baseDir = baseDir;
	}
	
	public abstract void importAll() throws Throwable;
	
	public void importTracks(File dir)
	{
		File[] files = dir.listFiles();
		for (File file : files)
		{
			System.out.println(String.format("%1$s Import Track: %2$s", getClass().getSimpleName(), file.getPath()));
			try
			{
				importTrack(file);
				file.delete();
			}
			catch (Throwable e)
			{
				System.out.println("Import file \"" + file.getName() + "\" Failed.");
			}
		}
	}
	
	public abstract void importTrack(File file) throws Throwable;
}
