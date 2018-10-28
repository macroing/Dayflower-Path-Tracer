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
package org.dayflower.pathtracer.scene.texture;

import java.util.Objects;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Texture;

public final class FractionalBrownianMotionTexture implements Texture {
	private final Color addend;
	private final Color multiplier;
	private final float persistence;
	private final float scale;
	private final int octaves;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public FractionalBrownianMotionTexture(final Color addend, final Color multiplier, final float persistence, final float scale, final int octaves) {
		this.addend = Objects.requireNonNull(addend, "addend == null");
		this.multiplier = Objects.requireNonNull(multiplier, "multiplier == null");
		this.persistence = persistence;
		this.scale = scale;
		this.octaves = octaves;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Color getAddend() {
		return this.addend;
	}
	
	public Color getMultiplier() {
		return this.multiplier;
	}
	
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof FractionalBrownianMotionTexture)) {
			return false;
		} else if(!Objects.equals(this.addend, FractionalBrownianMotionTexture.class.cast(object).addend)) {
			return false;
		} else if(!Objects.equals(this.multiplier, FractionalBrownianMotionTexture.class.cast(object).multiplier)) {
			return false;
		} else if(Float.compare(this.persistence, FractionalBrownianMotionTexture.class.cast(object).persistence) != 0) {
			return false;
		} else if(Float.compare(this.scale, FractionalBrownianMotionTexture.class.cast(object).scale) != 0) {
			return false;
		} else if(this.octaves != FractionalBrownianMotionTexture.class.cast(object).octaves) {
			return false;
		} else {
			return true;
		}
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
		return Objects.hash(this.addend, this.multiplier, Float.valueOf(this.persistence), Float.valueOf(this.scale), Integer.valueOf(this.octaves));
	}
}