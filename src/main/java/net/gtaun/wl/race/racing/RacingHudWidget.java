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

package net.gtaun.wl.race.racing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.gtaun.shoebill.common.player.PlayerLifecycleObject;
import net.gtaun.shoebill.constant.TextDrawAlign;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerTextdraw;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.textdraw.TextDrawUtils;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;

public class RacingHudWidget extends PlayerLifecycleObject
{
	private final RaceServiceImpl raceService;
	private final RacingPlayerContext racingPlayerContext;

	private Timer timer;
	private Timer progressBarTimer;

	private PlayerTextdraw checkpointNumber;
	private PlayerTextdraw rankingNumber;
	private PlayerTextdraw timeDiffDraw;
	private PlayerTextdraw bottomInfo;

	private PlayerTextdraw progressBarBg;
	private List<PlayerTextdraw> progressBarTextdraws;
	
	
	public RacingHudWidget(EventManager rootEventManager, RaceServiceImpl raceService, Player player, RacingPlayerContext racingPlayerContext)
	{
		super(rootEventManager, player);
		this.raceService = raceService;
		this.racingPlayerContext = racingPlayerContext;
		progressBarTextdraws = new ArrayList<>();
	}

	@Override
	protected void onInit()
	{
		
		checkpointNumber = TextDrawUtils.createPlayerText(player, 0, 435, "0/0");
		checkpointNumber.setAlignment(TextDrawAlign.LEFT);
		checkpointNumber.setFont(TextDrawFont.FONT2);
		checkpointNumber.setLetterSize(0.75f, 2.4f);
		checkpointNumber.setShadowSize(1);
		checkpointNumber.show();

		rankingNumber = TextDrawUtils.createPlayerText(player, 635, 360, "-");
		rankingNumber.setAlignment(TextDrawAlign.RIGHT);
		rankingNumber.setFont(TextDrawFont.FONT2);
		rankingNumber.setLetterSize(1.2f, 3.75f);
		rankingNumber.setShadowSize(2);
		rankingNumber.show();
		
		timeDiffDraw = TextDrawUtils.createPlayerText(player, 320, 440, "-");
		timeDiffDraw.setAlignment(TextDrawAlign.CENTER);
		timeDiffDraw.setFont(TextDrawFont.PRICEDOWN);
		timeDiffDraw.setLetterSize(1.2f, 3.75f);
		timeDiffDraw.setShadowSize(2);
		
		bottomInfo = TextDrawUtils.createPlayerText(player, 0, 460, "-");
		bottomInfo.setAlignment(TextDrawAlign.LEFT);
		bottomInfo.setFont(TextDrawFont.FONT2);
		bottomInfo.setLetterSize(0.25f, 0.8f);
		bottomInfo.setShadowSize(1);
		bottomInfo.show();
		
		progressBarBg = TextDrawUtils.createPlayerTextBG(player, 2, 240, 10, 200);
		progressBarBg.setBoxColor(new Color(0, 0, 0, 128));
		progressBarBg.show();

		timer = Timer.create(100, (factualInterval) -> update());
		timer.start();
		
		progressBarTimer = Timer.create(500, (factualInterval) -> updateProgressBar());
		progressBarTimer.start();

		addDestroyable(bottomInfo);
		addDestroyable(rankingNumber);
		addDestroyable(checkpointNumber);
		addDestroyable(timeDiffDraw);
		addDestroyable(progressBarBg);
		addDestroyable(timer);
		addDestroyable(progressBarTimer);
		
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
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
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
		Date startTime = racingPlayerContext.getStartTime();
		if (startTime == null) startTime = now;
		long time = now.getTime() - startTime.getTime();
		
		long milliseconds = time % 1000;
		long seconds = (time / 1000) % 60;
		long minutes = time / 1000 / 60;
		String formatedTime = String.format("%1$02d:%2$02d.%3$03d", minutes, seconds, milliseconds);
		
		bottomInfo.setText(stringSet.format(player, "Textdraw.RacingHudWidget.BottomInfoFormat", racing.getName(), track.getName(), completionPercent * 100.0f, formatedTime));
	}
	
	private void updateProgressBar()
	{
		Racing racing = racingPlayerContext.getRacing();
		
		for (PlayerTextdraw textdraw : progressBarTextdraws) textdraw.destroy();
		progressBarTextdraws.clear();
		
		List<RacingPlayerContext> rankedList = racing.getRacingRankedList();
		for (int i=0; i<rankedList.size(); i++)
		{
			RacingPlayerContext context = rankedList.get(i);
			float percent = context.getCompletionPercent();
			
			try
			{
				PlayerTextdraw draw = TextDrawUtils.createPlayerTextBG(player, 2, 240+189*(1.0f-percent), 15, 4);
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
				PlayerTextdraw text = TextDrawUtils.createPlayerText(player, 18, 240+189*(1.0f-percent)-4, context.getPlayer().getName());
				text.setAlignment(TextDrawAlign.LEFT);
				text.setFont(TextDrawFont.FONT2);
				text.setLetterSize(0.25f, 0.8f);
				text.setShadowSize(1);
				text.show();
				progressBarTextdraws.add(text);
			}
			catch (Throwable e)
			{
				System.err.println("PlayerTextdraw CreationFailed.");
			}
		}
	}
}

