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

//TODO: Add Javadocs.
public abstract class Texture {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SIZE = 1;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_TYPE = 0;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private int offset;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	protected Texture() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public abstract float[] toFloatArray();
	
//	TODO: Add Javadocs.
	public final int getOffset() {
		return this.offset;
	}
	
//	TODO: Add Javadocs.
	public abstract int size();
	
//	TODO: Add Javadocs.
	public final void setOffset(final int offset) {
		this.offset = offset;
	}
}