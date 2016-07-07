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

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.Objects;

import org.dayflower.pathtracer.color.Color;

//TODO: Add Javadocs.
public final class Surface {
	private final Color emission;
	private final float perlinNoiseAmount;
	private final float perlinNoiseScale;
	private final Material material;
	private final Texture textureAlbedo;
	private final Texture textureNormal;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Surface(final Color emission, final float perlinNoiseAmount, final float perlinNoiseScale, final Material material, final Texture textureAlbedo, final Texture textureNormal) {
		this.emission = Objects.requireNonNull(emission, "emission == null");
		this.perlinNoiseAmount = perlinNoiseAmount;
		this.perlinNoiseScale = perlinNoiseScale;
		this.material = Objects.requireNonNull(material, "material == null");
		this.textureAlbedo = Objects.requireNonNull(textureAlbedo, "textureAlbedo == null");
		this.textureNormal = Objects.requireNonNull(textureNormal, "textureNormal == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Surface)) {
			return false;
		} else if(!Objects.equals(this.emission, Surface.class.cast(object).emission)) {
			return false;
		} else if(Float.compare(this.perlinNoiseAmount, Surface.class.cast(object).perlinNoiseAmount) != 0) {
			return false;
		} else if(Float.compare(this.perlinNoiseScale, Surface.class.cast(object).perlinNoiseScale) != 0) {
			return false;
		} else if(!Objects.equals(this.material, Surface.class.cast(object).material)) {
			return false;
		} else if(!Objects.equals(this.textureAlbedo, Surface.class.cast(object).textureAlbedo)) {
			return false;
		} else if(!Objects.equals(this.textureNormal, Surface.class.cast(object).textureNormal)) {
			return false;
		} else {
			return true;
		}
	}
	
//	TODO: Add Javadocs.
	public boolean isEmissive() {
		return !this.emission.isBlack();
	}
	
//	TODO: Add Javadocs.
	public Color getEmission() {
		return this.emission;
	}
	
//	TODO: Add Javadocs.
	public float getPerlinNoiseAmount() {
		return this.perlinNoiseAmount;
	}
	
//	TODO: Add Javadocs.
	public float getPerlinNoiseScale() {
		return this.perlinNoiseScale;
	}
	
//	TODO: Add Javadocs.
	@Override
	public int hashCode() {
		return Objects.hash(this.emission, Float.valueOf(this.perlinNoiseAmount), Float.valueOf(this.perlinNoiseScale), this.material, this.textureAlbedo, this.textureNormal);
	}
	
//	TODO: Add Javadocs.
	public Material getMaterial() {
		return this.material;
	}
	
//	TODO: Add Javadocs.
	public Texture getTextureAlbedo() {
		return this.textureAlbedo;
	}
	
//	TODO: Add Javadocs.
	public Texture getTextureNormal() {
		return this.textureNormal;
	}
}