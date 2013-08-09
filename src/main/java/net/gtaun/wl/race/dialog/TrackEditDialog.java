package net.gtaun.wl.race.dialog;

import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.exception.AlreadyExistException;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.common.dialog.MsgboxDialog;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.Track.TrackStatus;
import net.gtaun.wl.race.track.TrackCheckpoint;
import net.gtaun.wl.race.track.TrackManagerImpl;

import org.apache.commons.lang3.StringUtils;

public class TrackEditDialog extends AbstractListDialog
{
	private final RaceServiceImpl raceService;
	private final Track track;
	

	public TrackEditDialog
	(final Player player, final Shoebill shoebill, EventManager eventManager, AbstractDialog parentDialog, RaceServiceImpl raceService, final Track track)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.raceService = raceService;
		this.track = track;
	}
	
	@Override
	public void show()
	{
		dialogListItems.clear();
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				return String.format("赛道名: %1$s", track.getName());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = String.format("%1$s: 编辑赛道名: %2$s", "赛车系统", track.getName());
				String message = String.format("原始赛道名 %1$s ，请输入新的赛道名: ", track.getName());
				new TrackNamingDialog(player, shoebill, rootEventManager, caption, message, TrackEditDialog.this)
				{
					protected void onNaming(String name)
					{
						try
						{
							TrackManagerImpl trackManager = raceService.getTrackManager();
							trackManager.renameTrack(track, name);
							showParentDialog();
						}
						catch (AlreadyExistException e)
						{
							append = String.format("{FF0000}* 赛道名 {FFFFFF}\"%1$s\" {FF0000}已被使用，请重新命名。", name);
							show();
						}
						catch (IllegalArgumentException e)
						{
							append = String.format("{FF0000}* 赛道名 {FFFFFF}\"%1$s\" {FF0000}不合法，请重新命名。", name);
							show();
						}
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String desc = track.getDesc();
				if (StringUtils.isBlank(desc)) desc = "空";
				return String.format("描述: %1$s", desc);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String messageFormat =
						"赛道 %1$s 原描述信息为:\n\n" +
						"%2$s\n\n" +
						"请输入新的描述信息:";
				
				String caption = String.format("%1$s: 编辑赛道描述: %2$s", "赛车系统", track.getName());
				String message = String.format(messageFormat, track.getName(), track.getDesc());
				new AbstractInputDialog(player, shoebill, rootEventManager, TrackEditDialog.this, caption, message)
				{
					public void onClickOk(String inputText)
					{
						String desc = StringUtils.trimToEmpty(inputText);
						desc = StringUtils.replace(desc, "%", "#");
						desc = StringUtils.replace(desc, "\t", " ");
						desc = StringUtils.replace(desc, "\n", " ");
						
						track.setDesc(desc);
						showParentDialog();
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem("添加当前地点为新检查点")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				TrackCheckpoint checkpoint = new TrackCheckpoint(track, player.getLocation());
				new TrackCheckpointEditDialog(player, shoebill, eventManager, null, checkpoint).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("测试本赛道")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new StartNewRacingDialog(player, shoebill, eventManager, TrackEditDialog.this, raceService, track).show();
			}
		});
		
		dialogListItems.add(new DialogListItem("停止编辑赛道")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				raceService.stopEditingTrack(player);
			}
		});
		
		dialogListItems.add(new DialogListItem("完成赛道编辑")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String format =
					"赛道在编辑完成以后，将开始统计比赛成绩信息。\n" +
					"尝试再次编辑赛道将会清空当前所有的比赛成绩。\n" +
					"被管理员通过审核(Ranking)的赛道，将无法再次编辑。\n\n" +
					"您确定要完成赛道 %1$s 吗？";
				String message = String.format(format, track.getName());
				new MsgboxDialog(player, shoebill, rootEventManager, TrackEditDialog.this, "完成赛道确认", message)
				{
					protected void onClickOk()
					{
						player.playSound(1083, player.getLocation());
						
						raceService.stopEditingTrack(player);
						track.setStatus(TrackStatus.COMPLETED);
						
						TrackEditDialog.this.showParentDialog();
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem("删除赛道")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String format =
					"警告！删除赛道将无法还原，所有的比赛记录也都将会消除。\n" +
					"请您务必谨慎操作，有任何疑问，请先联系管理员解决。\n\n" +
					"请您再次输入赛道名 %1$s 以确认删除操作：";
				String message = String.format(format, track.getName());
				new AbstractInputDialog(player, shoebill, rootEventManager, TrackEditDialog.this, "！！！删除赛道！！！", message)
				{
					public void onClickOk(String inputText)
					{
						player.playSound(1083, player.getLocation());
						if (!track.equals(inputText))
						{
							String format = "已取消删除赛道 %1$s 。";
							String message = String.format(format, track.getName());
							new MsgboxDialog(player, shoebill, rootEventManager, parentDialog, "取消删除赛道", message)
							{
								protected void onClickOk()
								{
									onClickCancel();
								}
							}.show();
						}
						
						TrackManagerImpl trackManager = raceService.getTrackManager();
						trackManager.deleteTrack(track);
						
						String format = "赛道 %1$s 已经成功删除。";
						String message = String.format(format, track.getName());
						new MsgboxDialog(player, shoebill, rootEventManager, parentDialog, "删除赛道成功", message)
						{
							protected void onClickOk()
							{
								onClickCancel();
							}
						}.show();
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public boolean isEnabled()
			{
				List<TrackCheckpoint> checkpoints = track.getCheckpoints();
				return !checkpoints.isEmpty();
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		List<TrackCheckpoint> checkpoints = track.getCheckpoints();
		for (int i=0; i<checkpoints.size(); i++)
		{
			final TrackCheckpoint checkpoint = checkpoints.get(i);
			float distance = player.getLocation().distance(checkpoint.getLocation());
			String item = String.format("检查点 %1$d (距离 %2$1.1f)", i+1, distance);
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1083, player.getLocation());
					new TrackCheckpointEditDialog(player, shoebill, eventManager, TrackEditDialog.this, checkpoint).show();
				}
			});
		}
		
		this.caption = String.format("%1$s: 编辑赛道: %2$s", "赛车系统", track.getName());
		super.show();
	}
}
