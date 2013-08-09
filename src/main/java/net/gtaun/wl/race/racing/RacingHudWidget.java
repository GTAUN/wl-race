/**
 * Copyright (C) 2013 MK124
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.gtaun.wl.race.racing;

import net.gtaun.shoebill.SampObjectFactory;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.constant.TextDrawAlign;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerTextdraw;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Timer.TimerCallback;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.textdraw.TextDrawUtils;

public class RacingHudWidget extends AbstractPlayerContext
{
	private final RacingPlayerContext racingPlayerContext;
	
	private Timer timer;

	private PlayerTextdraw checkpointNumber;
	private PlayerTextdraw otherInfo;
	
	
	public RacingHudWidget(Shoebill shoebill, EventManager rootEventManager, Player player, RacingPlayerContext racingPlayerContext)
	{
		super(shoebill, rootEventManager, player);
		this.racingPlayerContext = racingPlayerContext;
	}

	@Override
	protected void onInit()
	{
		SampObjectFactory factory = shoebill.getSampObjectFactory();
		
		checkpointNumber = TextDrawUtils.createPlayerText(factory, player, 0, 440, "0/0");
		checkpointNumber.setAlignment(TextDrawAlign.LEFT);
		checkpointNumber.setFont(TextDrawFont.FONT2);
		checkpointNumber.setLetterSize(0.75f, 2.4f);
		checkpointNumber.setShadowSize(2);
		checkpointNumber.show();
		
		otherInfo = TextDrawUtils.createPlayerText(factory, player, 0, 468, "-");
		otherInfo.setAlignment(TextDrawAlign.LEFT);
		otherInfo.setFont(TextDrawFont.FONT2);
		otherInfo.setLetterSize(0.25f, 0.8f);
		otherInfo.setShadowSize(1);
		otherInfo.show();
		
		timer = factory.createTimer(500);
		timer.setCallback(new TimerCallback()
		{
			@Override
			public void onTick(int factualInterval)
			{
				update();
			}
		});
		timer.start();

		addDestroyable(otherInfo);
		addDestroyable(checkpointNumber);
		addDestroyable(timer);
		
		update();
	}

	@Override
	protected void onDestroy()
	{
		
	}
	
	private void update()
	{
		int passedCheckpoints = racingPlayerContext.getPassedCheckpoints();
		int checkpoints = racingPlayerContext.getTrackCheckpoints();
		
		final String checkpointNumberformat = "%1$d/%2$d";
		checkpointNumber.setText(String.format(checkpointNumberformat, passedCheckpoints + 1, checkpoints - 1));

		float completionPercent = racingPlayerContext.getCompletionPercent() * 100.0f;
		
		final String otherInfoformat = "Completed %1$1.1f%%";
		otherInfo.setText(String.format(otherInfoformat, completionPercent));
	}
}

