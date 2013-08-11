package net.gtaun.wl.race.importer;

import java.io.File;

import net.gtaun.shoebill.data.Radius;
import net.gtaun.wl.race.TrackManager;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.Track.TrackStatus;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

public class SraceImporter extends Importer
{
	public SraceImporter(TrackManager trackManager, File baseDir)
	{
		super(trackManager, baseDir);
	}

	@Override
	public void importAll()
	{
		File trackDir = new File(baseDir, "tracks");
		if (trackDir.exists() && trackDir.isDirectory()) importTracks(trackDir);
	}
	
	@Override
	public void importTrack(File file) throws Throwable
	{
		Ini ini = new Ini(file);
		
		Section trackInfo = ini.get("TrackInfo");
		String name = file.getName().replace(".ini", "");
		String desc = trackInfo.get("Description");
		String author = trackInfo.get("Designer");
		
		Section checkpointInfo = ini.get("CheckpointInfo");
		int checkpointCount = Integer.parseInt(checkpointInfo.get("Count"));
		
		Radius[] checkpointPos = new Radius[checkpointCount];
		for (int i=0; i<checkpointCount; i++)
		{
			String line = checkpointInfo.get("Checkpoint" + (i+1));
			String[] splits = StringUtils.split(line, ',');
			checkpointPos[i] = new Radius(Float.parseFloat(splits[0]), Float.parseFloat(splits[1]), Float.parseFloat(splits[2]), Float.parseFloat(splits[3]));
		}
		
		Track track = trackManager.createTrack(author, name);
		track.setDesc(desc);
		for (int i=0; i<checkpointCount; i++)
		{
			TrackCheckpoint checkpoint = track.createCheckpoint(checkpointPos[i]);
			checkpoint.setSize(checkpointPos[i].getRadius());
		}
		
		track.setStatus(TrackStatus.COMPLETED);
	}
}
