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
package org.dayflower.pathtracer.scene;

import java.lang.reflect.Field;//TODO: Add Javadocs.

//TODO: Add Javadocs!
public final class Point2 {
//	TODO: Add Javadocs!
	public final float x;
	
//	TODO: Add Javadocs!
	public final float y;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public Point2() {
		this(0.0F, 0.0F);
	}
	
//	TODO: Add Javadocs!
	public Point2(final float x, final float y) {
		this.x = x;
		this.y = y;
	}
}