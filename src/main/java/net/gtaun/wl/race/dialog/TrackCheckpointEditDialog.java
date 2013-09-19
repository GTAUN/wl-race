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
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.race.impl.RaceServiceImpl;
import net.gtaun.wl.race.track.Track;
import net.gtaun.wl.race.track.TrackCheckpoint;

public class TrackCheckpointEditDialog extends AbstractListDialog
{
	private final RaceServiceImpl raceService;
	private final TrackCheckpoint checkpoint;
	private final Track track;
	
	
	public TrackCheckpointEditDialog(final Player player, final Shoebill shoebill, final EventManager eventManager, final AbstractDialog parentDialog, final RaceServiceImpl raceService, final TrackCheckpoint checkpoint)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.raceService = raceService;
		this.checkpoint = checkpoint;
		this.track = checkpoint.getTrack();
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		if (track.getCheckpoints().contains(checkpoint) == false) player.setLocation(checkpoint.getLocation());

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Common.OK"))
		{
			@Override
			public boolean isEnabled()
			{
				return parentDialog instanceof TrackEditDialog == false;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				showParentDialog();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackCheckpointEditDialog.Teleport"))
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
				String item = stringSet.format(player, "Dialog.TrackCheckpointEditDialog.Position", loc.getX(), loc.getY(), loc.getZ(), loc.getInteriorId());
				return item;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				final Radius oldLoc = checkpoint.getLocation();
				String caption = stringSet.get(player, "Dialog.TrackCheckpointEditPositionDialog.Caption");
				String text = stringSet.format(player, "Dialog.TrackCheckpointEditPositionDialog.Text", oldLoc.getX(), oldLoc.getY(), oldLoc.getZ(), oldLoc.getInteriorId());
				new AbstractInputDialog(player, shoebill, eventManager, TrackCheckpointEditDialog.this, caption, text)
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
							append = stringSet.get(player, "Dialog.TrackCheckpointEditPositionDialog.IllegalFormatAppendMessage");
							show();
						}
					}
				}.show();
			}
		});
		
		dialogListItems.add(new DialogListItemRadio(stringSet.get(player, "Dialog.TrackCheckpointEditDialog.Type"))
		{
			{
				addItem(new RadioItem(stringSet.get(player, "Track.Checkpoint.Type.Normal"), Color.RED)
				{
					@Override
					public void onSelected()
					{
						checkpoint.setType(RaceCheckpointType.NORMAL);
					}
				});
				
				addItem(new RadioItem(stringSet.get(player, "Track.Checkpoint.Type.Air"), Color.BLUE)
				{
					@Override
					public void onSelected()
					{
						checkpoint.setType(RaceCheckpointType.AIR);
					}
				});
			}
			
			@Override
			public int getSelected()
			{
				return checkpoint.getType() == RaceCheckpointType.NORMAL ? 0 : 1;
			}
			
			@Override
			public void onItemSelect(RadioItem item, int index)
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
				String item = stringSet.format(player, "Dialog.TrackCheckpointEditDialog.Size", checkpoint.getSize());
				return item;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String caption = stringSet.get(player, "Dialog.TrackCheckpointEditSizeDialog.Caption");
				String text = stringSet.format(player, "Dialog.TrackCheckpointEditSizeDialog.Text", checkpoint.getSize());
				new AbstractInputDialog(player, shoebill, eventManager, TrackCheckpointEditDialog.this, caption, text)
				{
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
							append = stringSet.get(player, "Dialog.TrackCheckpointEditSizeDialog.IllegalFormatAppendMessage");
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
				String code = checkpoint.getScript();
				int lines = StringUtils.countMatches(code, "\n");
				return stringSet.format(player, "Dialog.TrackCheckpointEditDialog.Script", lines, code.length());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				String title = stringSet.format(player, "Dialog.TrackCheckpointEditDialog.CheckpointFormat", checkpoint.getNumber()+1);
				String code = checkpoint.getScript();
				new CodeEditorDialog(player, shoebill, eventManager, TrackCheckpointEditDialog.this, raceService, title, code)
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
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackCheckpointEditDialog.UpdatePosition"))
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
				player.sendMessage(Color.LIGHTBLUE, stringSet.get(player, "Dialog.TrackCheckpointEditDialog.UpdatePositionMessage"));
				show();
			}
		});

		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.TrackCheckpointEditDialog.Delete"))
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
		final LocalizedStringSet stringSet = raceService.getLocalizedStringSet();
		
		this.caption = stringSet.format(player, "Dialog.TrackCheckpointEditDialog.Caption", track.getName(), checkpoint.getNumber());
		super.show();
	}
}
