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
package org.dayflower.pathtracer.scene;

import java.util.Objects;

//TODO: Add Javadocs.
public final class Primitive {
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SHAPE_OFFSET = 1;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SHAPE_TYPE = 0;
	
//	TODO: Add Javadocs.
	public static final int RELATIVE_OFFSET_SURFACE_OFFSET = 2;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 3;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Shape shape;
	private Surface surface;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Primitive(final Shape shape, final Surface surface) {
		this.shape = Objects.requireNonNull(shape, "shape == null");
		this.surface = Objects.requireNonNull(surface, "surface == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Shape getShape() {
		return this.shape;
	}
	
//	TODO: Add Javadocs.
	@Override
	public String toString() {
		return String.format("new Primitive(%s, %s)", this.shape, this.surface);
	}
	
//	TODO: Add Javadocs.
	public Surface getSurface() {
		return this.surface;
	}
	
//	TODO: Add Javadocs.
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Primitive)) {
			return false;
		} else if(!Objects.equals(this.shape, Primitive.class.cast(object).shape)) {
			return false;
		} else if(!Objects.equals(this.surface, Primitive.class.cast(object).surface)) {
			return false;
		} else {
			return true;
		}
	}
	
//	TODO: Add Javadocs.
	@Override
	public int hashCode() {
		return Objects.hash(this.shape, this.surface);
	}
	
//	TODO: Add Javadocs.
	public void setShape(final Shape shape) {
		this.shape = Objects.requireNonNull(shape, "shape == null");
	}
	
//	TODO: Add Javadocs.
	public void setSurface(final Surface surface) {
		this.surface = Objects.requireNonNull(surface, "surface == null");
	}
}