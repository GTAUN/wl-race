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
