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
public final class CheckerboardTexture extends Texture {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_COLOR_0 = 2;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_COLOR_1 = 5;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_DEGREES = 8;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SCALE_U = 9;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SCALE_V = 10;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 11;
	
//	TODO: Add Javadocs.
	public static final int TYPE = 1;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final Color color0;
	private final Color color1;
	private final float degrees;
	private final float scaleU;
	private final float scaleV;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public CheckerboardTexture() {
		this(Color.GRAY);
	}
	
//	TODO: Add Javadocs.
	public CheckerboardTexture(final Color color0) {
		this(color0, 5.0F, 5.0F);
	}
	
//	TODO: Add Javadocs.
	public CheckerboardTexture(final Color color0, final Color color1) {
		this(color0, color1, 5.0F, 5.0F, 0.0F);
	}
	
//	TODO: Add Javadocs.
	public CheckerboardTexture(final Color color0, final Color color1, final float scaleU, final float scaleV) {
		this(color0, color1, scaleU, scaleV, 0.0F);
	}
	
//	TODO: Add Javadocs.
	public CheckerboardTexture(final Color color0, final Color color1, final float scaleU, final float scaleV, final float degrees) {
		this.color0 = Objects.requireNonNull(color0, "color0 == null");
		this.color1 = Objects.requireNonNull(color1, "color1 == null");
		this.scaleU = scaleU;
		this.scaleV = scaleV;
		this.degrees = degrees;
	}
	
//	TODO: Add Javadocs.
	public CheckerboardTexture(final Color color0, final float scaleU, final float scaleV) {
		this(color0, scaleU, scaleV, 0.0F);
	}
	
//	TODO: Add Javadocs.
	public CheckerboardTexture(final Color color0, final float scaleU, final float scaleV, final float degrees) {
		this(color0, color0, scaleU, scaleV, degrees);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Color getColor0() {
		return this.color0;
	}
	
//	TODO: Add Javadocs.
	public Color getColor1() {
		return this.color1;
	}
	
//	TODO: Add Javadocs.
	public float getDegrees() {
		return this.degrees;
	}
	
//	TODO: Add Javadocs.
	public float getScaleU() {
		return this.scaleU;
	}
	
//	TODO: Add Javadocs.
	public float getScaleV() {
		return this.scaleV;
	}
	
//	TODO: Add Javadocs.
	@Override
	public float[] toFloatArray() {
		return new float[] {
			TYPE,
			SIZE,
			getColor0().r,
			getColor0().g,
			getColor0().b,
			getColor1().r,
			getColor1().g,
			getColor1().b,
			getDegrees(),
			getScaleU(),
			getScaleV()
		};
	}
	
//	TODO: Add Javadocs.
	@Override
	public int size() {
		return SIZE;
	}
}