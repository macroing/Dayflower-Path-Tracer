/**
 * Copyright 2015 - 2019 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.scene.shape;

import java.util.Objects;

import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Surface;

public final class Terrain extends Shape {
	private final float maximum;
	private final float minimum;
	private final float persistence;
	private final float scale;
	private final int octaves;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Terrain(final Surface surface, final float persistence, final float scale, final float minimum, final float maximum, final int octaves) {
		super(surface);
		
		this.persistence = persistence;
		this.scale = scale;
		this.minimum = minimum;
		this.maximum = maximum;
		this.octaves = octaves;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Terrain)) {
			return false;
		} else if(!Objects.equals(getSurface(), Terrain.class.cast(object).getSurface())) {
			return false;
		} else if(Float.compare(this.maximum, Terrain.class.cast(object).maximum) != 0) {
			return false;
		} else if(Float.compare(this.minimum, Terrain.class.cast(object).minimum) != 0) {
			return false;
		} else if(Float.compare(this.persistence, Terrain.class.cast(object).persistence) != 0) {
			return false;
		} else if(Float.compare(this.scale, Terrain.class.cast(object).scale) != 0) {
			return false;
		} else if(this.octaves != Terrain.class.cast(object).octaves) {
			return false;
		} else {
			return true;
		}
	}
	
	public float getMaximum() {
		return this.maximum;
	}
	
	public float getMinimum() {
		return this.minimum;
	}
	
	public float getPersistence() {
		return this.persistence;
	}
	
	public float getScale() {
		return this.scale;
	}
	
	public int getOctaves() {
		return this.octaves;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getSurface(), Float.valueOf(this.maximum), Float.valueOf(this.minimum), Float.valueOf(this.persistence), Float.valueOf(this.scale), Integer.valueOf(this.octaves));
	}
}