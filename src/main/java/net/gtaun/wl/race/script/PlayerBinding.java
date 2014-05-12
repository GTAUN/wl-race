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

import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;

public class PlayerBinding implements ScriptBinding
{
	private final Player player;
	
	public String name;
	
	
	public PlayerBinding(Player player)
	{
		this.player = player;
	}
	
	@Override
	public void update()
	{
		name = player.getName();
	}
	
	public void sendMessage(String message)
	{
		player.sendMessage(Color.WHITE, message);
	}
	
	public void setTime(int hours, int minutes)
	{
		player.setTime(hours, minutes);
	}
	
	public void setWeather(int weatherId)
	{
		player.setWeather(weatherId);
	}

	public void playAudioStream(String url)
	{
		player.playAudioStream(url);
	}

	public void stopAudioStream()
	{
		player.stopAudioStream();
	}
	
	public void playSound(int sound)
	{
		player.playSound(sound, player.getLocation());
	}
}
