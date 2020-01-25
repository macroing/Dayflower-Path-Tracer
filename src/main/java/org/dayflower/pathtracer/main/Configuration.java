/**
 * Copyright 2015 - 2020 J&#246;rgen Lundgren
 * 
 * This file is part of Dayflower.
 * 
 * Dayflower is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Dayflower is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dayflower. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dayflower.pathtracer.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class Configuration {
	private final Properties settings;
	private final String imageDirectory;
	private final String rootDirectory;
	private final String version;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Configuration() {
		this.settings = doReadSettings();
		this.imageDirectory = doGetImageDirectory();
		this.rootDirectory = doGetDirectory();
		this.version = doGetVersion();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getImageDirectory() {
		return this.imageDirectory;
	}
	
	public String getRootDirectory() {
		return this.rootDirectory;
	}
	
	public String getSceneName() {
		return this.settings.getProperty("scene.name", "Material_Showcase_Scene");
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public boolean getSceneCompile() {
		return doReadSettingAsBoolean("scene.compile", false);
	}
	
	public int getCanvasHeight() {
		return doReadSettingAsInt("canvas.height", 800);
	}
	
	public int getCanvasWidth() {
		return doReadSettingAsInt("canvas.width", 800);
	}
	
	public int getKernelHeight() {
		return doReadSettingAsInt("kernel.height", 800);
	}
	
	public int getKernelWidth() {
		return doReadSettingAsInt("kernel.width", 800);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean doReadSettingAsBoolean(final String key, final boolean defaultValue) {
		return Boolean.parseBoolean(this.settings.getProperty(key, Boolean.toString(defaultValue)));
	}
	
	private int doReadSettingAsInt(final String key, final int defaultValue) {
		try {
			return Math.abs(Integer.parseInt(this.settings.getProperty(key, Integer.toString(defaultValue))));
		} catch(final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Properties doReadSettings() {
		final Properties properties = new Properties();
		
		try(final InputStream inputStream = new FileInputStream(new File(doGetDirectory(), "settings.properties"))) {
			properties.load(inputStream);
		} catch(final IOException e) {
//			Do nothing.
		}
		
		return properties;
	}
	
	private static String doGetDirectory() {
		final Package package_ = Configuration.class.getPackage();
		
		if(package_ != null) {
			final String implementationVersion = package_.getImplementationVersion();
			
			if(implementationVersion != null) {
				return "./resources";
			}
		}
		
		return "./resources/distribution/resources";
	}
	
	private static String doGetImageDirectory() {
		return String.format("%s/image", doGetDirectory());
	}
	
	private static String doGetVersion() {
		final Package package_ = Configuration.class.getPackage();
		
		if(package_ != null) {
			final String implementationVersion = package_.getImplementationVersion();
			
			if(implementationVersion != null) {
				return String.format("v.%s", implementationVersion);
			}
		}
		
		return "v.0.0.0";
	}
}