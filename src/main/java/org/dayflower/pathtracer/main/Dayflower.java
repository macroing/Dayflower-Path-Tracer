/**
 * Copyright 2009 - 2017 J&#246;rgen Lundgren
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

final class Dayflower {
	private static final Properties SETTINGS = doReadSettings();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Dayflower() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static int getHeight() {
		return doReadSettingAsInt("height", 768);
	}
	
	public static int getHeightScale() {
		return doReadSettingAsInt("height.scale", 1);
	}
	
	public static int getWidth() {
		return doReadSettingAsInt("width", 1024);
	}
	
	public static int getWidthScale() {
		return doReadSettingAsInt("width.scale", 1);
	}
	
	public static String getModelFilename(final String name) {
		return String.format("%s/model/%s", doGetDirectory(), name);
	}
	
	public static String getSceneFilename(final String name) {
		return String.format("%s/scene/%s", doGetDirectory(), name);
	}
	
	public static String getSceneName() {
		return SETTINGS.getProperty("scene.name", "Material_Showcase_Scene");
	}
	
	public static String getTextureFilename(final String name) {
		return String.format("%s/texture/%s", doGetDirectory(), name);
	}
	
	public static String getVersion() {
		final Package package_ = Dayflower.class.getPackage();
		
		if(package_ != null) {
			final String implementationVersion = package_.getImplementationVersion();
			
			if(implementationVersion != null) {
				return String.format("v.%s", implementationVersion);
			}
		}
		
		return "v.0.0.0";
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static int doReadSettingAsInt(final String key, final int defaultValue) {
		try {
			return Math.abs(Integer.parseInt(SETTINGS.getProperty(key, Integer.toString(defaultValue))));
		} catch(final NumberFormatException e) {
			return defaultValue;
		}
	}
	
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
		final Package package_ = Dayflower.class.getPackage();
		
		if(package_ != null) {
			final String implementationVersion = package_.getImplementationVersion();
			
			if(implementationVersion != null) {
				return "./resources";
			}
		}
		
		return "./resources/distribution/resources";
	}
}