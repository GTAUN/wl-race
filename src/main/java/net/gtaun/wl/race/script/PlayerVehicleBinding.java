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

import net.gtaun.shoebill.data.AngledLocation;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Velocity;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

public class PlayerVehicleBinding implements ScriptBinding
{
	private final Player player;
	
	public float health;
	public float x, y, z;
	public int interior;
	public float angle;
	public float vx, vy, vz;
	public float speed;
	
	public int color1, color2;
	
	
	public PlayerVehicleBinding(Player player)
	{
		this.player = player;
	}

	@Override
	public void update()
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle != null)
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
		else
		{
			health = 0.0f;
			
			AngledLocation location = player.getLocation();
			x = location.getX();
			y = location.getY();
			z = location.getZ();
			interior = location.getInteriorId();
			angle = location.getAngle();
			
			Velocity velocity = player.getVelocity();
			vx = velocity.getX();
			vy = velocity.getY();
			vz = velocity.getZ();
			speed = velocity.speed3d();
			
			color1 = 0;
			color2 = 0;
		}
	}
	
	public void setHealth(float health)
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		vehicle.setHealth(health);
		this.health = health;
	}
	
	public void setPos(float x, float y, float z)
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		vehicle.setLocation(x, y, z);
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setInterior(int interior)
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		vehicle.setInteriorId(interior);
		this.interior = interior;
	}
	
	public void setAngle(float angle)
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		vehicle.setAngle(angle);
		this.angle = angle;
	}
	
	public void setSpeed(float spd)
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		Velocity velocity = vehicle.getVelocity();
		float rate = spd / velocity.speed3d();
		velocity.set(velocity.getX()*rate, velocity.getY()*rate, velocity.getZ()*rate);
		vehicle.setVelocity(velocity);
		
		this.speed = spd;
	}
	
	public void setVelocity(float vx, float vy, float vz)
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		Velocity velocity = new Velocity(vx, vy, vz);
		vehicle.setVelocity(velocity);

		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
		this.speed = velocity.speed3d();
	}
	
	public void setAngularVelocity(float vx, float vy, float vz)
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		vehicle.setAngularVelocity(new Velocity(vx, vy, vz));

		Velocity velocity = vehicle.getVelocity();
		this.vx = velocity.getX();
		this.vy = velocity.getY();
		this.vz = velocity.getZ();
		this.speed = velocity.speed3d();
	}
	
	public void setVZ(float vz)
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		Velocity velocity = vehicle.getVelocity();
		velocity.set(velocity, vz);
		vehicle.setVelocity(velocity);
		
		this.vz = vz;
		this.speed = velocity.speed3d();
	}
	
	public void setColor1(int color)
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		vehicle.setColor(color1, color2);
		this.color1 = color;
	}
	
	public void setColor2(int color)
	{
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		vehicle.setColor(color1, color2);
		this.color2 = color;
	}
}
