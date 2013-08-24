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
	
	public void setAllowAutoRepair(boolean isAllowAutoRepair)
	{
		this.isAllowAutoRepair = isAllowAutoRepair;
	}
	
	public boolean isAllowInfiniteNitrous()
	{
		return isAllowInfiniteNitrous;
	}
	
	public void setAllowInfiniteNitrous(boolean isAllowInfiniteNitrous)
	{
		this.isAllowInfiniteNitrous = isAllowInfiniteNitrous;
	}
	
	public boolean isAllowAutoFlip()
	{
		return isAllowAutoFlip;
	}
	
	public void setAllowAutoFlip(boolean isAllowAutoFlip)
	{
		this.isAllowAutoFlip = isAllowAutoFlip;
	}
	
	public boolean isAllowChangeVehicle()
	{
		return isAllowChangeVehicle;
	}
	
	public void setAllowChangeVehicle(boolean isAllowChangeVehicle)
	{
		this.isAllowChangeVehicle = isAllowChangeVehicle;
	}
}
