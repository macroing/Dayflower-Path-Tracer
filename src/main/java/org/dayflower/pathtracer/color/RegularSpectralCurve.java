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
package org.dayflower.pathtracer.color;

import java.lang.reflect.Field;//TODO: Add Javadocs.

//TODO: Add Javadocs!
public final class RegularSpectralCurve extends SpectralCurve {
	private final float delta;
	private final float deltaReciprocal;
	private final float lambdaMax;
	private final float lambdaMin;
	private final float[] spectrum;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public RegularSpectralCurve(final float lambdaMin, final float lambdaMax, final float[] spectrum) {
		this.lambdaMin = lambdaMin;
		this.lambdaMax = lambdaMax;
		this.spectrum = spectrum.clone();
		this.delta = (this.lambdaMax - this.lambdaMin) / (this.spectrum.length - 1);
		this.deltaReciprocal = 1.0F / this.delta;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	@Override
	public float sample(final float lambda) {
		if(lambda < this.lambdaMin || lambda > this.lambdaMax) {
			return 0.0F;
		}
		
		final float x = (lambda - this.lambdaMin) * this.deltaReciprocal;
		
		final int index0 = (int)(x);
		final int index1 = Math.min(index0 + 1, this.spectrum.length - 1);
		
		final float deltaX = x - index0;
		final float sample = (1.0F - deltaX) * this.spectrum[index0] + deltaX * this.spectrum[index1];
		
		return sample;
	}
}