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
package org.dayflower.pathtracer.scene.texture;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.Objects;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Texture;

//TODO: Add Javadocs.
public final class SolidTexture extends Texture {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_COLOR = 2;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 5;
	
//	TODO: Add Javadocs.
	public static final int TYPE = 2;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Color color;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public SolidTexture() {
		this(Color.GREEN);
	}
	
//	TODO: Add Javadocs.
	public SolidTexture(final Color color) {
		this.color = Objects.requireNonNull(color, "color == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Color getColor() {
		return this.color;
	}
	
//	TODO: Add Javadocs.
	@Override
	public float[] toFloatArray() {
		return new float[] {
			TYPE,
			SIZE,
			getColor().r,
			getColor().g,
			getColor().b
		};
	}
	
//	TODO: Add Javadocs.
	@Override
	public int size() {
		return SIZE;
	}
}