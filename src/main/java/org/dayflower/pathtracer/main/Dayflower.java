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

final class Dayflower {
	private Dayflower() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static String getModelFilename(final String name) {
		return String.format("%s/model/%s", doGetDirectory(), name);
	}
	
	public static String getSceneFilename(final String name) {
		return String.format("%s/scene/%s", doGetDirectory(), name);
	}
	
	public static String getTextureFilename(final String name) {
		return String.format("%s/texture/%s", doGetDirectory(), name);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
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