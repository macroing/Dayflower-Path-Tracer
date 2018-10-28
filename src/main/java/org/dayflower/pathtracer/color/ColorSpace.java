/**
 * Copyright 2015 - 2018 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.color;

/**
 * The {@code ColorSpace} class represents a color space.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class ColorSpace {
	/**
	 * Constructs a new {@code ColorSpace} instance.
	 */
	protected ColorSpace() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Call this method to redo Gamma Correction on {@code value}.
	 * <p>
	 * Returns {@code value} with Gamma Correction.
	 * 
	 * @param value a {@code float} value
	 * @return {@code value} with Gamma Correction
	 */
	public abstract float redoGammaCorrection(final float value);
	
	/**
	 * Call this method to undo Gamma Correction on {@code value}.
	 * <p>
	 * Returns {@code value} without Gamma Correction.
	 * 
	 * @param value a {@code float} value
	 * @return {@code value} without Gamma Correction
	 */
	public abstract float undoGammaCorrection(final float value);
}