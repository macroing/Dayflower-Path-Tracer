/**
 * Copyright 2009 - 2016 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.util;

import java.lang.reflect.Field;//TODO: Add Javadocs.

//TODO: Add Javadocs.
public final class Ranges {
	private Ranges() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static float clamp(final float value, final float minimum, final float maximum) {
		if(value < minimum) {
			return minimum;
		} else if(value > maximum) {
			return maximum;
		} else {
			return value;
		}
	}
	
//	TODO: Add Javadocs.
	public static float requireRange(final float value, final float minimum, final float maximum) {
		return requireRange(value, minimum, maximum, null);
	}
	
//	TODO: Add Javadocs.
	public static float requireRange(final float value, final float minimum, final float maximum, final String message) {
		if(value >= minimum && value <= maximum) {
			return value;
		}
		
		throw new IllegalArgumentException(message);
	}
}