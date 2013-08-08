package net.gtaun.wl.race.dialog;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.RaceCheckpointType;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Radius;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractInputDialog;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;

public class TrackCheckpointEditDialog extends AbstractListDialog
{
	private final TrackCheckpoint checkpoint;
	private final Track track;
	
	
	public TrackCheckpointEditDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final TrackCheckpoint checkpoint)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.checkpoint = checkpoint;
		this.track = checkpoint.getTrack();
		
		if (track.getCheckpoints().contains(checkpoint) == false) player.setLocation(checkpoint.getLocation());

		dialogListItems.add(new DialogListItem("保存")
		{
			@Override
			public boolean isEnabled()
			{
				return track.getCheckpoints().contains(checkpoint) == false;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				track.addCheckpoint(checkpoint);
				showParentDialog();
			}
		});
		
		dialogListItems.add(new DialogListItem("传送到这个检查点")
		{
			@Override
			public boolean isEnabled()
			{
				if (player.getLocation().equals(checkpoint.getLocation())) return false;
				return track.getCheckpoints().contains(checkpoint);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				player.setLocation(checkpoint.getLocation());
				show();
			}
		});

		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				Radius loc = checkpoint.getLocation();
				String item = String.format("坐标: x=%1$1.2f, y=%2$1.2f, z=%3$1.2f, interior=%4$d", loc.getX(), loc.getY(), loc.getZ(), loc.getInteriorId());
				return item;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				final Radius oldLoc = checkpoint.getLocation();
				String msg = String.format("当前坐标值为: x=%1$1.2f, y=%2$1.2f z=%3$1.2f interior=%4$d\n请输入新坐标值，格式: [x] [y] [z] [interior]", oldLoc.getX(), oldLoc.getY(), oldLoc.getZ(), oldLoc.getInteriorId());
				new AbstractInputDialog(player, shoebill, eventManager, "编辑检查点坐标", msg, TrackCheckpointEditDialog.this)
				{
					public void onClickOk(String inputText)
					{
						player.playSound(1083, player.getLocation());
						
						try (Scanner scanner = new Scanner(inputText))
						{
							Radius loc = new Radius(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat(), scanner.nextInt(), oldLoc.getWorldId(), oldLoc.getRadius());
							checkpoint.setLocation(loc);
							showParentDialog();
						}
						catch (NoSuchElementException e)
						{
							append = "{FF0000}* 请按照正确的格式输入坐标值。";
							show();
						}
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItemSwitch("类型:", "赛车检查点", "飞行检查点")
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				if (checkpoint.getType() != RaceCheckpointType.NORMAL) checkpoint.setType(RaceCheckpointType.NORMAL);
				else checkpoint.setType(RaceCheckpointType.AIR);
				
				show();
			}
			
			@Override
			public boolean isSwitched()
			{
				return checkpoint.getType() == RaceCheckpointType.NORMAL;
			}
		});

		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String item = String.format("大小: %1$1.1f", checkpoint.getSize());
				return item;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String msg = String.format("当前大小值为: %1$1.1f\n请输入新坐标值:", checkpoint.getSize());
				new AbstractInputDialog(player, shoebill, eventManager, "编辑检查点大小", msg, TrackCheckpointEditDialog.this)
				{
					private String append;
					
					public void onClickOk(String inputText)
					{
						player.playSound(1083, player.getLocation());
						
						try (Scanner scanner = new Scanner(inputText))
						{
							checkpoint.setSize(scanner.nextFloat());
							showParentDialog();
						}
						catch (NoSuchElementException e)
						{
							append = "{FF0000}* 请按照正确的格式输入坐标值。";
							show();
						}
					}
					
					protected void show(String text)
					{
						if (append != null) super.show(this.message + "\n\n" + append);
						else super.show(text);
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem()
		{
			@Override
			public String toItemString()
			{
				String code = checkpoint.getScript();
				int lines = StringUtils.countMatches(code, "\n");
				return String.format("触发代码: %1$d 行 (%2$d 个字符)", lines, code.length());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String title = String.format("检查点 %1$d", checkpoint.getNumber()+1);
				String code = checkpoint.getScript();
				new CodeEditorDialog(player, shoebill, eventManager, TrackCheckpointEditDialog.this, title, code)
				{
					@Override
					protected void onComplete(String code)
					{
						checkpoint.setScript(code);
						showParentDialog();
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItem("更新检查点位置")
		{
			@Override
			public boolean isEnabled()
			{
				if (player.getLocation().equals(checkpoint.getLocation())) return false;
				return track.getCheckpoints().contains(checkpoint);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				checkpoint.setLocation(player.getLocation());
				player.sendMessage(Color.LIGHTBLUE, "赛车系统: 检查点位置已更新。");
				show();
			}
		});

		dialogListItems.add(new DialogListItem("删除这个检查点")
		{
			@Override
			public boolean isEnabled()
			{
				return track.getCheckpoints().contains(checkpoint);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				track.removeChechpoint(checkpoint);
				showParentDialog();
			}
		});
	}
	
	@Override
	public void show()
	{
		String format = "%1$s: 编辑赛道: %2$s: 编辑检查点 %3$d";
		int number = checkpoint.getNumber();
		if (number == -1) format = "%1$s: 编辑赛道: %2$s: 创建新检查点";
		
		this.caption = String.format(format, "赛车系统", track.getName(), number);
		super.show();
	}
}
