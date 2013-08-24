package net.gtaun.wl.race.racing;

public class RacingLimit
{
	private boolean isAllowAutoRepair;
	private boolean isAllowInfiniteNitrous;
	private boolean isAllowAutoFlip;
	private boolean isAllowChangeVehicle;
	
	
	public RacingLimit()
	{
		isAllowAutoRepair = true;
		isAllowInfiniteNitrous = true;
		isAllowAutoFlip = false;
		isAllowChangeVehicle = true;
	}

	public boolean isAllowAutoRepair()
	{
		return isAllowAutoRepair;
	}
	
	public boolean isAllowInfiniteNitrous()
	{
		return isAllowInfiniteNitrous;
	}
	
	public boolean isAllowAutoFlip()
	{
		return isAllowAutoFlip;
	}
	
	public boolean isAllowChangeVehicle()
	{
		return isAllowChangeVehicle;
	}
}
