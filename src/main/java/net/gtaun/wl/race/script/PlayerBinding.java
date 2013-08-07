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
