package net.gtaun.wl.race.importer;

import java.io.File;

import net.gtaun.wl.race.TrackManager;

public abstract class Importer
{
	protected final TrackManager trackManager;
	
	
	protected Importer(TrackManager trackManager)
	{
		this.trackManager = trackManager;	
	}
	
	public void importTracks(File dir)
	{
		File[] files = dir.listFiles();
		for (File file : files)
		{
			System.out.println(String.format("%1$s Import Track: %2$s", getClass().getSimpleName(), file.getPath()));
			try
			{
				importTrack(file);
			}
			catch (Throwable e)
			{
				System.out.println("Cannot import this file.");
			}
		}
	}
	
	public abstract void importTrack(File file) throws Throwable;
}
