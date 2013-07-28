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

package net.gtaun.wl.race;

import java.io.File;

import net.gtaun.shoebill.common.ConfigurablePlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.mapping.DefaultCreator;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class RacePlugin extends ConfigurablePlugin
{
	public static final Logger LOGGER = LoggerFactory.getLogger(RacePlugin.class);
	
	
	private RaceConfig config;
	
	private MongoClient mongoClient;
	private Morphia morphia;
	private Datastore datastore;
	
	private RaceServiceImpl vehicleManagerSerivce;
	
	
	public RacePlugin()
	{
		
	}
	
	@Override
	protected void onEnable() throws Throwable
	{
		config = new RaceConfig(new File(getDataDir(), "config.yml"));
		
		mongoClient = new MongoClient(config.getDbHost());
		
		morphia = new Morphia();
		morphia.getMapper().getOptions().objectFactory = new DefaultCreator()
		{
            @Override
            protected ClassLoader getClassLoaderForClass(String clazz, DBObject object)
            {
                return getClass().getClassLoader();
            }
        };
		
		if (config.getDbUser().isEmpty() || config.getDbPass().isEmpty())
		{
			datastore = morphia.createDatastore(mongoClient, config.getDbName());
		}
		else
		{
			datastore = morphia.createDatastore(mongoClient, config.getDbName(), config.getDbUser(), config.getDbPass().toCharArray());
		}
		
		vehicleManagerSerivce = new RaceServiceImpl(getShoebill(), getEventManager(), this, datastore);
		registerService(RaceService.class, vehicleManagerSerivce);
		
		LOGGER.info(getDescription().getName() + " " + getDescription().getVersion() + " Enabled.");
	}
	
	@Override
	protected void onDisable() throws Throwable
	{
		unregisterService(RaceService.class);
		
		vehicleManagerSerivce.destroy();
		vehicleManagerSerivce = null;
		
		datastore = null;
		morphia = null;
		
		mongoClient.close();
		mongoClient = null;
		
		LOGGER.info(getDescription().getName() + " " + getDescription().getVersion() + " Disabled.");
	}
}
