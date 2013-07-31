package net.gtaun.wl.race.script;

import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.object.Vehicle;

public class ScriptVehicleBinding implements ScriptBinding
{
	private final Vehicle vehicle;
	
	public float health;
	public float x, y, z;
	public int interior;
	public float angle;
	public float vx, vy, vz;
	public float speed;
	
	public int color1, color2;
	
	
	public ScriptVehicleBinding(Vehicle vehicle)
	{
		this.vehicle = vehicle;
	}

	@Override
	public void update()
	{
		health = vehicle.getHealth();
		
		Location location = vehicle.getLocation();
		x = location.getX();
		y = location.getY();
		z = location.getZ();
		interior = location.getInteriorId();
		
		angle = vehicle.getAngle();
		
		Velocity velocity = vehicle.getVelocity();
		vx = velocity.getX();
		vy = velocity.getY();
		vz = velocity.getZ();
		speed = velocity.speed3d();
		
		color1 = vehicle.getColor1();
		color2 = vehicle.getColor2();
	}
	
	public void setHealth(float health)
	{
		vehicle.setHealth(health);
		this.health = health;
	}
	
	public void setPos(float x, float y, float z)
	{
		vehicle.setLocation(x, y, z);
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setInterior(int interior)
	{
		vehicle.setInteriorId(interior);
		this.interior = interior;
	}
	
	public void setAngle(float angle)
	{
		vehicle.setAngle(angle);
		this.angle = angle;
	}
	
	public void setSpeed(float spd)
	{
		Velocity velocity = vehicle.getVelocity();
		float rate = spd / velocity.speed3d();
		velocity.set(velocity.getX()*rate, velocity.getY()*rate, velocity.getZ()*rate);
		vehicle.setVelocity(velocity);
		
		this.speed = spd;
	}
	
	public void setVelocity(float vx, float vy, float vz)
	{
		Velocity velocity = new Velocity(vx, vy, vz);
		vehicle.setVelocity(velocity);

		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
		this.speed = velocity.speed3d();
	}
	
	public void setAngularVelocity(float vx, float vy, float vz)
	{
		vehicle.setAngularVelocity(new Velocity(vx, vy, vz));

		Velocity velocity = vehicle.getVelocity();
		this.vx = velocity.getX();
		this.vy = velocity.getY();
		this.vz = velocity.getZ();
		this.speed = velocity.speed3d();
	}
	
	public void setVZ(float vz)
	{
		Velocity velocity = vehicle.getVelocity();
		velocity.set(velocity, vz);
		vehicle.setVelocity(velocity);
		
		this.vz = vz;
		this.speed = velocity.speed3d();
	}
	
	public void setColor1(int color)
	{
		vehicle.setColor(color1, color2);
		this.color1 = color;
	}
	
	public void setColor2(int color)
	{
		vehicle.setColor(color1, color2);
		this.color2 = color;
	}
}
