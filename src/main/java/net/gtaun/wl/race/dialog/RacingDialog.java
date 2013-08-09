package net.gtaun.wl.race.dialog;

import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.racing.Racing;
import net.gtaun.wl.race.racing.Racing.RacingStatus;
import net.gtaun.wl.race.racing.RacingManagerImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;

public class RacingDialog extends AbstractListDialog
{
	private final RaceServiceImpl raceService;
	private final Racing racing;
	
	
	public RacingDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final RaceServiceImpl raceService, final Racing racing)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.raceService = raceService;
		this.racing = racing;
		this.caption = String.format("%1$s: 查看比赛 %2$s 的信息", "赛车系统", racing.getName());
	}
	
	@Override
	public void show()
	{
		final Track track = racing.getTrack();
		final RacingManagerImpl racingManager = raceService.getRacingManager();

		dialogListItems.clear();
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("比赛名称: %1$s", racing.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("赛道: %1$s", track.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new TrackDialog(player, shoebill, eventManager, RacingDialog.this, raceService, track).show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("举办者: %1$s", racing.getSponsor().getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("参加比赛")
		{
			@Override
			public boolean isEnabled()
			{
				if (racing.getStatus() != RacingStatus.WAITING) return false;
				if (racingManager.getPlayerRacing(player) == racing) return false;
				return true;
			}
			
			private void joinRacing()
			{
				racing.join(player);
				
				List<TrackCheckpoint> checkpoints = track.getCheckpoints();
				if (checkpoints.isEmpty()) return;
				
				Location startLoc = checkpoints.get(0).getLocation();
				Location location = new Location(startLoc);
				location.setZ(location.getZ() + 2.0f);
				
				player.setLocation(location);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				if (racingManager.isPlayerInRacing(player))
				{
					final Racing nowRacing = racingManager.getPlayerRacing(player);
					String text = String.format("当前正在参加 %1$s 比赛，您确定要退出并参加另一个比赛 %1$s 吗？", nowRacing.getName(), racing.getName());
					new MsgboxDialog(player, shoebill, eventManager, RacingDialog.this, "参加比赛", text)
					{
						@Override
						protected void onClickOk()
						{
							player.playSound(1083, player.getLocation());
							nowRacing.leave(player);
							joinRacing();
						}
					}.show();
				}
				else joinRacing();
			}
		});
		
		dialogListItems.add(new DialogListItem("开始比赛")
		{
			@Override
			public boolean isEnabled()
			{
				if (racing.getStatus() != RacingStatus.WAITING) return false;
				if (racingManager.getPlayerRacing(player) != racing) return false;
				if (racing.getSponsor() != player) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				racing.begin();
			}
		});
		
		dialogListItems.add(new DialogListItem("退出比赛")
		{
			@Override
			public boolean isEnabled()
			{
				if (racingManager.getPlayerRacing(player) != racing) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				if (racingManager.getPlayerRacing(player) != racing) return ;
				
				String text = String.format("当前正在参加 %1$s 比赛，您确定要退出吗？", racing.getName());
				new MsgboxDialog(player, shoebill, eventManager, RacingDialog.this, "退出比赛", text)
				{
					@Override
					protected void onClickOk()
					{
						player.playSound(1083, player.getLocation());
						racing.leave(player);
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});

		List<Player> players = racing.getPlayers();
		for (final Player joinedPlayer : players)
		{
			String item = String.format("参赛者: %1$s", joinedPlayer.getName());
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					if (player != racing.getSponsor() || player == joinedPlayer)
					{
						show();
					}
					else
					{
						String text = String.format("您确定要踢出参赛者 %1$s 吗？", joinedPlayer.getName());
						new MsgboxDialog(player, shoebill, rootEventManager, RacingDialog.this, "踢出参赛者", text)
						{
							protected void onClickOk()
							{
								player.playSound(1083, player.getLocation());
								racing.kick(joinedPlayer);
								show();
							}
						}.show();
					}
				}
			});
		}
		
		super.show();
	}
}
