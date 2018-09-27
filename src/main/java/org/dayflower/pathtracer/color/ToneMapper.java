/**
 * Copyright 2009 - 2018 J&#246;rgen Lundgren
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

import static org.dayflower.pathtracer.math.Math2.max;

/**
 * A {@code ToneMapper} is an abstraction of a Tone Mapping operator to be applied on a {@link Color}.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public interface ToneMapper {
	/**
	 * Applies Tone Mapping to {@code color}.
	 * <p>
	 * Returns a {@link Color} instance denoting the Tone Mapped version of {@code color}.
	 * 
	 * @param color the {@code Color} to apply Tone Mapping to
	 * @return a {@code Color} instance denoting the Tone Mapped version of {@code color}
	 */
	Color applyToneMapping(final Color color);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code ToneMapper} that implements the Filmic Curve Tone Mapping operator.
	 * <p>
	 * This Tone Mapping operator takes care of Gamma Correction.
	 * 
	 * @return a {@code ToneMapper} that implements the Filmic Curve Tone Mapping operator
	 */
	static ToneMapper filmicCurve() {
		return color -> {
			final float rMaximum = max(color.r - 0.004F, 0.0F);
			final float gMaximum = max(color.g - 0.004F, 0.0F);
			final float bMaximum = max(color.b - 0.004F, 0.0F);
			
			final float r = (rMaximum * (6.2F * rMaximum + 0.5F)) / (rMaximum * (6.2F * rMaximum + 1.7F) + 0.06F);
			final float g = (gMaximum * (6.2F * gMaximum + 0.5F)) / (gMaximum * (6.2F * gMaximum + 1.7F) + 0.06F);
			final float b = (bMaximum * (6.2F * bMaximum + 0.5F)) / (bMaximum * (6.2F * bMaximum + 1.7F) + 0.06F);
			
			return new Color(r, g, b);
		};
	}
	
	/**
	 * Returns a {@code ToneMapper} that implements the Linear Tone Mapping operator.
	 * <p>
	 * This Tone Mapping operator does not take care of Gamma Correction.
	 * 
	 * @return a {@code ToneMapper} that implements the Linear Tone Mapping operator
	 */
	static ToneMapper linear() {
		return color -> color.max() > 1.0F ? color.divide(color.max()) : color;
	}
	
	/**
	 * Returns a {@code ToneMapper} that implements the Reinhard Tone Mapping operator.
	 * <p>
	 * This Tone Mapping operator does not take care of Gamma Correction.
	 * 
	 * @return a {@code ToneMapper} that implements the Reinhard Tone Mapping operator
	 */
	static ToneMapper reinhard() {
		return color -> color.divide(color.r + 1.0F, color.g + 1.0F, color.b + 1.0F);
	}
}