package net.gtaun.wl.race.util;

import org.apache.commons.lang3.StringUtils;

public final class TrackUtil
{
	public static final int NAME_MIN_LENGTH = 3;
	public static final int NAME_MAX_LENGTH = 40;
	
	
	public static boolean isVaildName(String name)
	{
		if (name.length() < NAME_MIN_LENGTH || name.length() > NAME_MAX_LENGTH) return false;
		if (name.contains("%") || name.contains("\t") || name.contains("\n")) return false;
		if (!StringUtils.trimToEmpty(name).equals(name)) return false;
		return true;
	}
	
	public static String filterName(String name)
	{
		name = StringUtils.trimToEmpty(name);
		name = StringUtils.replace(name, "%", "#");
		name = StringUtils.replace(name, "\t", " ");
		name = StringUtils.replace(name, "\n", " ");
		return name;
	}
	
	private TrackUtil()
	{
		
	}
}
