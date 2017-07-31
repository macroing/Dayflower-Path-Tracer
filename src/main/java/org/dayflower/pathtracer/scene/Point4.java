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

import static org.dayflower.pathtracer.math.Math2.abs;

import java.util.Objects;

public final class Point4 {
	public final float w;
	public final float x;
	public final float y;
	public final float z;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Point4() {
		this(0.0F, 0.0F, 0.0F);
	}
	
	public Point4(final float x, final float y, final float z) {
		this(x, y, z, 1.0F);
	}
	
	public Point4(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Point4)) {
			return false;
		} else if(Float.compare(this.x, Point4.class.cast(object).x) != 0) {
			return false;
		} else if(Float.compare(this.y, Point4.class.cast(object).y) != 0) {
			return false;
		} else if(Float.compare(this.z, Point4.class.cast(object).z) != 0) {
			return false;
		} else if(Float.compare(this.w, Point4.class.cast(object).w) != 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isInsideViewFrustum() {
		final boolean isInsideViewFrustumX = abs(this.x) <= abs(this.w);
		final boolean isInsideViewFrustumY = abs(this.y) <= abs(this.w);
		final boolean isInsideViewFrustumZ = abs(this.z) <= abs(this.w);
		
		return isInsideViewFrustumX && isInsideViewFrustumY && isInsideViewFrustumZ;
	}
	
	public float getW() {
		return this.w;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getZ() {
		return this.z;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z), Float.valueOf(this.w));
	}
	
	public Point4 perspectiveDivide() {
		return new Point4(this.x / this.w, this.y / this.w, this.z / this.w, this.w);
	}
	
	public Point4 transform(final Matrix44 m) {
		final float x = m.e11 * this.x + m.e12 * this.y + m.e13 * this.z + m.e14 * this.w;
		final float y = m.e21 * this.x + m.e22 * this.y + m.e23 * this.z + m.e24 * this.w;
		final float z = m.e31 * this.x + m.e32 * this.y + m.e33 * this.z + m.e34 * this.w;
		final float w = m.e41 * this.x + m.e42 * this.y + m.e43 * this.z + m.e44 * this.w;
		
		return new Point4(x, y, z, w);
	}
}