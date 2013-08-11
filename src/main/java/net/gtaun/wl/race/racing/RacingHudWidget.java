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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.gtaun.shoebill.SampObjectFactory;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.constant.TextDrawAlign;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerTextdraw;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Timer.TimerCallback;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.textdraw.TextDrawUtils;
import net.gtaun.wl.race.track.Track;

public class RacingHudWidget extends AbstractPlayerContext
{
	private final RacingPlayerContext racingPlayerContext;
	
	private Timer timer;

	private PlayerTextdraw checkpointNumber;
	private PlayerTextdraw rankingNumber;
	private PlayerTextdraw timeDiffDraw;
	private PlayerTextdraw otherInfo;

	private PlayerTextdraw progressBarBg;
	private List<PlayerTextdraw> progressBarTextdraws;
	
	
	public RacingHudWidget(Shoebill shoebill, EventManager rootEventManager, Player player, RacingPlayerContext racingPlayerContext)
	{
		super(shoebill, rootEventManager, player);
		this.racingPlayerContext = racingPlayerContext;
		progressBarTextdraws = new ArrayList<>();
	}

	@Override
	protected void onInit()
	{
		SampObjectFactory factory = shoebill.getSampObjectFactory();
		
		checkpointNumber = TextDrawUtils.createPlayerText(factory, player, 0, 435, "0/0");
		checkpointNumber.setAlignment(TextDrawAlign.LEFT);
		checkpointNumber.setFont(TextDrawFont.FONT2);
		checkpointNumber.setLetterSize(0.75f, 2.4f);
		checkpointNumber.setShadowSize(1);
		checkpointNumber.show();

		rankingNumber = TextDrawUtils.createPlayerText(factory, player, 635, 360, "-");
		rankingNumber.setAlignment(TextDrawAlign.RIGHT);
		rankingNumber.setFont(TextDrawFont.FONT2);
		rankingNumber.setLetterSize(1.2f, 3.75f);
		rankingNumber.setShadowSize(2);
		rankingNumber.show();
		
		timeDiffDraw = TextDrawUtils.createPlayerText(factory, player, 320, 440, "-");
		timeDiffDraw.setAlignment(TextDrawAlign.CENTER);
		timeDiffDraw.setFont(TextDrawFont.PRICEDOWN);
		timeDiffDraw.setLetterSize(1.2f, 3.75f);
		timeDiffDraw.setShadowSize(2);
		
		otherInfo = TextDrawUtils.createPlayerText(factory, player, 0, 460, "-");
		otherInfo.setAlignment(TextDrawAlign.LEFT);
		otherInfo.setFont(TextDrawFont.FONT2);
		otherInfo.setLetterSize(0.25f, 0.8f);
		otherInfo.setShadowSize(1);
		otherInfo.show();
		
		progressBarBg = TextDrawUtils.createPlayerTextBG(factory, player, 2, 240, 10, 200);
		progressBarBg.setBoxColor(new Color(0, 0, 0, 128));
		progressBarBg.show();
		
		timer = factory.createTimer(100);
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
		addDestroyable(rankingNumber);
		addDestroyable(checkpointNumber);
		addDestroyable(timeDiffDraw);
		addDestroyable(progressBarBg);
		addDestroyable(timer);
		
		update();
	}

	@Override
	protected void onDestroy()
	{
		for (PlayerTextdraw textdraw : progressBarTextdraws) textdraw.destroy();
		progressBarTextdraws.clear();
	}
	
	private void update()
	{
		int passedCheckpoints = racingPlayerContext.getPassedCheckpoints();
		int checkpoints = racingPlayerContext.getTrackCheckpoints();
		
		final String checkpointNumberformat = "%1$d/%2$d";
		checkpointNumber.setText(String.format(checkpointNumberformat, passedCheckpoints + 1, checkpoints - 1));


		Racing racing = racingPlayerContext.getRacing();
		Track track = racing.getTrack();
		
		String rankingStr = racingPlayerContext.getRankingString();
		rankingNumber.setText(rankingStr);
		
		
		float timeDiff = racingPlayerContext.getTimeDiff();
		if (timeDiff != 0.0f)
		{
			int diff = (int) (timeDiff * 1000);
			long milliseconds = diff % 1000;
			long seconds = (diff / 1000) % 60;
			long minutes = diff / 1000 / 60;
			
			String format = "+%1$02d:%2$02d.%3$03d";
			if (minutes == 0) format = "+%2$02d.%3$03d";
			String formatedTime = String.format(format, minutes, seconds, milliseconds);
			timeDiffDraw.setText(formatedTime);
			
			if (!timeDiffDraw.isShowed()) timeDiffDraw.show();
		}
		else
		{
			if (timeDiffDraw.isShowed()) timeDiffDraw.hide();
		}
		
		
		float completionPercent = racingPlayerContext.getCompletionPercent();
		
		Date now = new Date();
		long time = now.getTime() - racing.getStartTime().getTime();
		
		long milliseconds = time % 1000;
		long seconds = (time / 1000) % 60;
		long minutes = time / 1000 / 60;
		String formatedTime = String.format("%1$02d:%2$02d.%3$03d", minutes, seconds, milliseconds);
		
		final String otherInfoformat = "Completed: ~b~~h~%3$1.1f%%~w~ - Time: %4$s~n~Racing: ~y~~h~%1$s~w~ - Track: ~g~~h~%2$s~w~";
		otherInfo.setText(String.format(otherInfoformat, racing.getName(), track.getName(), completionPercent * 100.0f, formatedTime));
		
		
		SampObjectFactory factory = shoebill.getSampObjectFactory();
		
		for (PlayerTextdraw textdraw : progressBarTextdraws) textdraw.destroy();
		progressBarTextdraws.clear();
		
		List<RacingPlayerContext> rankedList = racing.getRacingRankedList();
		for (int i=0; i<rankedList.size(); i++)
		{
			RacingPlayerContext context = rankedList.get(i);
			float percent = context.getCompletionPercent();
			
			try
			{
				PlayerTextdraw draw = TextDrawUtils.createPlayerTextBG(factory, player, 2, 240+189*(1.0f-percent), 15, 4);
				draw.setBoxColor(new Color(context.getPlayer().getColor().getValue() & 0xFFFFFF00 | 0x7F));
				draw.show();
				progressBarTextdraws.add(draw);
			}
			catch (CreationFailedException e)
			{
				System.err.println("PlayerTextdraw CreationFailed.");
			}
			
			try
			{
				PlayerTextdraw text = TextDrawUtils.createPlayerText(factory, player, 18, 240+189*(1.0f-percent)-4, context.getPlayer().getName());
				text.setAlignment(TextDrawAlign.LEFT);
				text.setFont(TextDrawFont.FONT2);
				text.setLetterSize(0.25f, 0.8f);
				text.setShadowSize(1);
				text.show();
				progressBarTextdraws.add(text);
			}
			catch (Exception e)
			{
				System.err.println("PlayerTextdraw CreationFailed.");
			}
		}
	}
}

