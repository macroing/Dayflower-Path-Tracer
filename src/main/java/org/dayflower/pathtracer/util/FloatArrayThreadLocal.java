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
package org.dayflower.pathtracer.util;

/**
 * A {@code FloatArrayThreadLocal} is a {@code ThreadLocal} that provides thread-local {@code float} arrays.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public class FloatArrayThreadLocal extends ThreadLocal<float[]> {
	private final int length;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code FloatArrayThreadLocal} instance.
	 * 
	 * @param length the length of the {@code float} array to create
	 */
	public FloatArrayThreadLocal(final int length) {
		this.length = length;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a new {@code float} array.
	 * 
	 * @return a new {@code float} array
	 */
	@Override
	protected float[] initialValue() {
		return new float[this.length];
	}
}